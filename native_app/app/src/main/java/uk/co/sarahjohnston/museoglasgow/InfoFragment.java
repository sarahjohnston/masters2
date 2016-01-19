package uk.co.sarahjohnston.museoglasgow;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    Museum thisMuseum;
    TextView placeName;
    TextView address;
    TextView description;
    ImageView mainImage;
    RatingBar ratingBar;
    float currentRate = -1;
    int museum_id;
    Button reviewsButton;

    public String CONNECTED = "connnected";
    public String DISCONNECTED = "disconnected";
    public String CONNECTION_STATUS = null;

    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        mainImage = (ImageView)view.findViewById(R.id.mainImage);
        placeName = (TextView)view.findViewById(R.id.placeName);
        address = (TextView)view.findViewById(R.id.address);
        description = (TextView)view.findViewById(R.id.description);
        ratingBar = (RatingBar)view.findViewById(R.id.setRating);
        ratingBar.setAlpha(.2f);
        reviewsButton = (Button)view.findViewById(R.id.reviewButton);

        PlaceActivity activity = (PlaceActivity) getActivity();
        thisMuseum = activity.getCurrentMuseum();
        museum_id = thisMuseum.getId();

        reviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to reviews
                Intent i = new Intent(getActivity(), ReviewsActivity.class);
                i.putExtra("placeID", thisMuseum.getId());
                i.putExtra("museumName", thisMuseum.get_museumName());
                startActivity(i);
            }
        });


        placeName.setText(thisMuseum.get_museumName());
        address.setText(thisMuseum.get_AddressText());
        description.setText(Html.fromHtml(thisMuseum.get_description()));

        //Log.d("Picture name", currentMuseum.get_mainImage());
        int resID = getResources().getIdentifier(thisMuseum.get_mainImage(),"drawable",activity.getPackageName());
        //int resID = R.drawable.kelvingrove;
        mainImage.setImageResource(resID);

        if (checkForNetwork()) {
            //Log.d("Connection status", "connected");
            CONNECTION_STATUS = CONNECTED;
            new FetchRatingsTask().execute();
        }
        else {
            Log.d("Connection status", "not connected");
            CONNECTION_STATUS = DISCONNECTED;
            //ratingBar.setVisibility(View.GONE);


        }

        //getRating(thisMuseum.getId());

        return view;
    }

    public boolean checkForNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private class FetchRatingsTask extends AsyncTask<Void,Void,Double> {
        @Override
        protected Double doInBackground(Void... params) {
            try {
                return new ReviewsAPI().loadRatingFromNetwork(museum_id);
            } catch (IOException e) {
                Log.e("Rating Service", "Failed to fetch rating", e);
                return -1.0;
            } catch (XmlPullParserException e) {
                Log.e("XML", "Failed to parse XML", e);
                return -1.0;
            }


        }
        @Override
        protected void onPostExecute(Double rating) {
            currentRate = rating.floatValue();
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            currentRate = Float.valueOf(decimalFormat.format(rating));
            setRating(currentRate);
        }
    }

    public void setRating(float currentRate) {

        if (currentRate == -1) {
            //error condition
            ratingBar.setAlpha(.2f);
        }
        else {
            ratingBar.setRating(currentRate);
            ratingBar.setAlpha(1);

        }
    }





}


