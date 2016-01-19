package uk.co.sarahjohnston.museoglasgow;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sarahjohnston on 09/12/2015.
 */
public class ReviewsXmlParser {

    private static final String ns = null;


    public List<Review> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            return readFeed(parser);

        }   finally {
            in.close();
        }
    }

    public Double parseRatings(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            return readRatings(parser);

        }   finally {
            in.close();
        }
    }


    private List<Review> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Review> reviews = new ArrayList<Review>();

        //find root tag
        parser.require(XmlPullParser.START_TAG, ns, "ArrayOfreview");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            //look for review tag
            if (name.equals("review")) {
                reviews.add(readReview(parser));
            }
            else {
                skip(parser);
            }


        }
        return reviews;
    }

    private Double readRatings(XmlPullParser parser) throws XmlPullParserException, IOException {
        Double rating;
        Double numReviews = 0.0;
        Double totalRatings = 0.0;

        //find root tag
        parser.require(XmlPullParser.START_TAG, ns, "place");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            //look for review tag
            if (name.equals("NumberReviews")) {
                numReviews = Double.valueOf(readText(parser));
            }
            else if (name.equals("TotalRatings")) {
                totalRatings = Double.valueOf(readText(parser));
            }
            else {
                skip(parser);
            }


        }
        rating = totalRatings / numReviews;
        return rating;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }



    public static class Review {
        public final int reviewId;
        public final int rating;
        public final String headline;
        public final String reviewText;
        public final String reviewDate;

        private Review(int reviewId, int rating, String headline, String reviewText, String reviewDate) {
            this.reviewId = reviewId;
            this.rating = rating;
            this.headline = headline;
            this.reviewText = reviewText;
            this.reviewDate = reviewDate;
        }

    }



    private Review readReview(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "review");
        int reviewId = 0;
        int rating = 0;
        String headline = null;
        String reviewText = null;
        String reviewDate = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "Id":
                    reviewId = Integer.valueOf(readText(parser));
                    break;
                case "Rating":
                    rating = Integer.valueOf(readText(parser));
                    break;
                case "Headline":
                    headline = readText(parser);
                    break;
                case "ReviewText":
                    reviewText = readText(parser);
                    break;
                case "Date":
                    reviewDate = readText(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new Review(reviewId, rating, headline, reviewText, reviewDate);

    }

    private String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
