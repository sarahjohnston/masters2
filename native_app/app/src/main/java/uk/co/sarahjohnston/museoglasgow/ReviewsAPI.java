package uk.co.sarahjohnston.museoglasgow;

import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sarahjohnston on 01/01/16.
 */
public class ReviewsAPI {

    private InputStream downloadXML(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(15000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    public Double loadRatingFromNetwork(int museum_id) throws XmlPullParserException, IOException {

        if (museum_id > 1) {
            museum_id = (museum_id * 10) - 9;
        } // correcting for ClearDB's auto-incrementing of ids in 10s

        //replace dummyreviewservice.com with the url of your actual web service
        String url = "http://dummyreviewwebservice.com/api/places/" + String.valueOf(museum_id);
        InputStream stream = null;
        // Instantiate the parser
        ReviewsXmlParser reviewsXmlParser = new ReviewsXmlParser();
        Double rating = null;

        try {
            stream = downloadXML(url);
            rating = reviewsXmlParser.parseRatings(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return rating;
    }

    public List<ReviewsXmlParser.Review> loadReviewsFromNetwork(int museum_id) throws XmlPullParserException, IOException {

        if (museum_id > 1) {
            museum_id = (museum_id * 10) - 9;
        } // correcting for ClearDB's auto-incrementing of ids in 10s

        //replace dummyreviewservice.com with the url of your actual web service
        String url = "http://dummyreviewwebservice.com/api/reviews/place/" + String.valueOf(museum_id);
        InputStream stream = null;
        // Instantiate the parser
        ReviewsXmlParser reviewsXmlParser = new ReviewsXmlParser();
        List<ReviewsXmlParser.Review> reviews = new ArrayList<>();

        try {
            stream = downloadXML(url);
            reviews = reviewsXmlParser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return reviews;
    }

    public static String convertToXML(ReviewFormActivity.UserReview review) {
        String xmlString = "<review xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://schemas.datacontract.org/2004/07/ApiReviews.Models\">\n";
        xmlString += "<Date>" + review.reviewDate + "</Date>\n";
        xmlString += "<Headline>" + review.reviewHeadline + "</Headline>\n";
        xmlString += "<PlaceId>" + review.reviewPlace + "</PlaceId>\n";
        xmlString += "<Rating>" + review.reviewRating + "</Rating>\n";
        xmlString += "<ReviewText>" + review.reviewText + "</ReviewText>\n";
        xmlString += "<ReviewerId>" + review.reviewerId + "</ReviewerId>\n";
        xmlString += "</review>";

        Log.d("CREATED", xmlString);
        return xmlString;
    }

    public String postToReviewApi(String xmlString) throws IOException {

        String result = "";
        //replace dummyreviewservice.com with the url of your actual web service
        URL url = new URL("http://dummyreviewwebservice.com/api/reviews");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/xml");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream output = new BufferedOutputStream(conn.getOutputStream());
            output.write(xmlString.getBytes());
            output.flush();
            output.close();

            int status = conn.getResponseCode();
            Log.d("RESPONSE", String.valueOf(status));

            //Get Response
            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            result += response.toString();
            //Log.d("Log", result);
        }
        finally {
            conn.disconnect();
        }

        return result;
    }


}
