package uk.co.sarahjohnston.museoglasgow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReviewFormActivity extends Activity {

    int placeID;
    String museumName;
    TextView mLocation;
    EditText mHeadline;
    EditText mReview;
    RatingBar mRatingBar;
    TextView mRatingLabel;
    String headline;
    String review;
    float rating;
    int museumId;

    UserReview newReview;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_form);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        placeID = getIntent().getIntExtra("placeID", 0);
        museumId = (placeID * 10) - 9;
        museumName = getIntent().getStringExtra("museumName");

        mLocation = (TextView)findViewById(R.id.form_location);
        mLocation.setText("Location: " + museumName);
        mHeadline = (EditText)findViewById(R.id.form_headline);
        mReview = (EditText)findViewById(R.id.form_review);
        mRatingBar = (RatingBar)findViewById(R.id.form_ratingBar);
        mRatingLabel = (TextView)findViewById(R.id.form_ratingLabel);

        Button cancelButton = (Button)findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Button submitButton = (Button)findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check review and submit
                submitReview();
            }
        });

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mRatingLabel.setTextColor(Color.parseColor("#727272"));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    public void submitReview() {
        //validate input
        boolean errors = false;
        rating = mRatingBar.getRating();
        if (rating < 1) {
            mRatingLabel.setTextColor(Color.parseColor("#a30000"));
            errors = true;
        }

        headline = mHeadline.getText().toString().trim();
        if (!isValidString(headline)) {
            mHeadline.setHint("Enter your headline");
            errors = true;
        }

        review = mReview.getText().toString().trim();
        if (!isValidString(review)) {
            mReview.setHint("Enter your review");
            errors = true;
        }

        if (errors) {
            Toast toast = Toast.makeText(this, "Please fill out all form fields", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
        else {
            new PostReviewsTask().execute();
        }

    }

    private class PostReviewsTask extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            try {
                newReview = new UserReview(museumId, Math.round(rating), headline, review);
                String xmlString = ReviewsAPI.convertToXML(newReview);
                return new ReviewsAPI().postToReviewApi(xmlString);
            } catch (IOException e) {
                Log.e("Review Posting", "Failed to post review", e);
                Toast toast = Toast.makeText(ReviewFormActivity.this, "Sorry review submission failed. Check your internet connection and try again.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                return result;
            }


        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(ReviewFormActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Submitting review...");
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            Toast toast = Toast.makeText(ReviewFormActivity.this, "Review Sent", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();

            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", "ok");

            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }
    }

    public boolean isValidString(String in) {
        if (in != null && in.length() > 2) {
            return true;
        }
        return false;
    }

    public static class UserReview {
        int reviewPlace;
        int reviewRating;
        int reviewerId;
        String reviewText;
        String reviewHeadline;
        String reviewDate;

        public UserReview(int placeId, int rating, String headline, String reviewText) {
            this.reviewPlace = placeId;
            this.reviewRating = rating;
            this.reviewHeadline = headline;
            this.reviewText = reviewText;
            //arbitary figure for reviewerId as have not setup user authentication
            this.reviewerId = 200;
            Date date = new Date();
            this.reviewDate = new SimpleDateFormat("yyyy-MM-dd").format(date) + "T00:00:00";

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                //go back
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
