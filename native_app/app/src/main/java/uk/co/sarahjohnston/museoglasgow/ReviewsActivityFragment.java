package uk.co.sarahjohnston.museoglasgow;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static uk.co.sarahjohnston.museoglasgow.MyUtil.dateConvert;

/**
 * A placeholder fragment containing a simple view.
 */
public class ReviewsActivityFragment extends Fragment {

    List<ReviewsXmlParser.Review> Reviews = new ArrayList<>();
    ListView reviewListView;
    ArrayAdapter<ReviewsXmlParser.Review> reviewAdapter;
    int placeID;
    String museumName;


    public String CONNECTED = "connnected";
    public String DISCONNECTED = "disconnected";
    public String CONNECTION_STATUS = null;
    LinearLayout errorLayout;
    Button tryAgainButton;
    ProgressDialog dialog;


    public ReviewsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        reviewListView = (ListView)view.findViewById(R.id.reviewListView);

        placeID = getActivity().getIntent().getIntExtra("placeID", 0);
        museumName = getActivity().getIntent().getStringExtra("museumName");
        Log.d("EXTRA contained", String.valueOf(placeID));

        if ((savedInstanceState != null) && (savedInstanceState.getInt("placeID", -1) != 0)) {
            placeID = savedInstanceState.getInt("placeID");
            museumName = savedInstanceState.getString("museumName");
        }

        ActionBar ab = getActivity().getActionBar();
        ab.setTitle(museumName);

        errorLayout = (LinearLayout)view.findViewById(R.id.errorBox);
        tryAgainButton = (Button)view.findViewById(R.id.tryAgainButton);

        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshReviews();;
            }
        });

        refreshReviews();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("placeID", placeID);
        outState.putString("museumName", museumName);
    }

    private class FetchReviewsTask extends AsyncTask<Void,Void,List<ReviewsXmlParser.Review>> {
        @Override
        protected List<ReviewsXmlParser.Review> doInBackground(Void... params) {
            try {
                return new ReviewsAPI().loadReviewsFromNetwork(placeID);
            } catch (IOException e) {
                Log.e("Rating Service", "Failed to fetch rating", e);
                return Reviews;
            } catch (XmlPullParserException e) {
                Log.e("XML", "Failed to parse XML", e);
                return Reviews;

            }


        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity());
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Fetching Reviews...");
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        @Override
        protected void onPostExecute(List<ReviewsXmlParser.Review> reviews) {
            //set list to adapter
            if (Reviews.size() > 0) {
                Reviews.clear();
            }
            dialog.dismiss();
            Reviews.addAll(reviews);
            populateList();
        }
    }

    public boolean checkForNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void populateList() {
        reviewAdapter = new ReviewListAdapter();
        reviewListView.setAdapter(reviewAdapter);
    }

    public void refreshReviews() {
        if (checkForNetwork()) {
            //Log.d("Connection status", "connected");
            CONNECTION_STATUS = CONNECTED;
            errorLayout.setVisibility(View.GONE);
            new FetchReviewsTask().execute();
        }
        else {
            //Log.d("Connection status", "not connected");
            CONNECTION_STATUS = DISCONNECTED;
            errorLayout.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_reviews_add, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_add_review:
                Intent i = new Intent(getActivity(), ReviewFormActivity.class);
                i.putExtra("museumName", museumName);
                i.putExtra("placeID", placeID);
                startActivityForResult(i, 1);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();

        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            //refresh reviews
            refreshReviews();

        }
    }

    private class ReviewListAdapter extends ArrayAdapter<ReviewsXmlParser.Review> {

        public ReviewListAdapter() {
            super (getActivity(), R.layout.review_list_item, Reviews);
        }


        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getActivity().getLayoutInflater().inflate(R.layout.review_list_item, parent, false);

            ReviewsXmlParser.Review currentReview = Reviews.get(position);

            TextView headline = (TextView) view.findViewById(R.id.headline);
            headline.setText("\"" + currentReview.headline + "\"");
            RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
            ratingBar.setRating(currentReview.rating);
            TextView reviewDate = (TextView) view.findViewById(R.id.date);
            //reviewDate.setText(currentReview.reviewDate);
            reviewDate.setText(dateConvert(currentReview.reviewDate));
            final TextView reviewText = (TextView) view.findViewById(R.id.reviewText);
            reviewText.setText(currentReview.reviewText);

            reviewText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (reviewText.getMaxLines() == 4) {
                        reviewText.setMaxLines(Integer.MAX_VALUE);
                        reviewText.setEllipsize(null);
                    } else {
                        reviewText.setMaxLines(4);
                        reviewText.setEllipsize(TextUtils.TruncateAt.END);
                    }
                }
            });

            return view;

        }
    }



}

