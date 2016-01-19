package uk.co.sarahjohnston.museoglasgow;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DirectionsActivityFragment extends Fragment implements AdapterView.OnItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    double[] loc;
    String museumName;
    TextView destination;
    TextView originLabel;
    EditText originName;
    Spinner transport_mode;
    Button directionsButton;
    RadioGroup currentLocationChoice;
    RadioButton currentLocation_yes;
    RadioButton currentLocation_no;

    Boolean currentLocation = true;
    String origin_string = "";
    String mMode;
    List<DirectionsAPI.DirectionStep> mDirectionSteps = new ArrayList<>();


    public String CONNECTED = "connnected";
    public String DISCONNECTED = "disconnected";
    public String CONNECTION_STATUS = null;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public DirectionsActivityFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_directions, container, false);

        loc = getActivity().getIntent().getDoubleArrayExtra("location");
        museumName = getActivity().getIntent().getStringExtra("museumName");

        if (loc.length < 2) {
            Log.d("Error", "No location co-ordinates");
            getActivity().finish();
        }

        currentLocationChoice = (RadioGroup)view.findViewById(R.id.currentLocationChoice);
        currentLocation_yes = (RadioButton)view.findViewById(R.id.currentLocation_yes);
        currentLocation_no = (RadioButton)view.findViewById(R.id.currentLocation_no);
        originLabel = (TextView)view.findViewById(R.id.originLabel);
        originName = (EditText)view.findViewById(R.id.origin_choice);

        currentLocationChoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (i == R.id.currentLocation_yes) {
                    currentLocation = true;
                    originName.setVisibility(View.GONE);
                    originLabel.setVisibility(View.GONE);
                } else {
                    currentLocation = false;
                    originName.setVisibility(View.VISIBLE);
                    originLabel.setVisibility(View.VISIBLE);

                }
                //Log.d("Radio", String.valueOf(currentLocation));
            }
        });



        destination = (TextView)view.findViewById(R.id.destination);
        destination.setText("Location: " + museumName);

        transport_mode = (Spinner)view.findViewById(R.id.transport_mode);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.transport_mode, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transport_mode.setAdapter(adapter);
        transport_mode.setOnItemSelectedListener(this);

        directionsButton = (Button)view.findViewById(R.id.directionsButton);
        directionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //process form
                submitDirectionForm();

            }
        });

        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }

        Button cancelButton = (Button)view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void submitDirectionForm() {

        //sort out origin for directions
        if (!currentLocation) {
            //get origin from edittext field
            origin_string = originName.getText().toString().trim();
            if (origin_string.length() < 4) {
                //return error please fill out all fields
            }
            Log.d("ORIGIN", origin_string);
        }
        else {
            //get GPS
            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                //Log.d("Lat", String.valueOf(mLastLocation.getLatitude()));
                //Log.d("Lon", String.valueOf(mLastLocation.getLongitude()));
                origin_string = String.valueOf(mLastLocation.getLatitude()) + String.valueOf(mLastLocation.getLongitude());
            }
            else {
                //error GPS failed
                Log.d("Error", "No GPS found");
            }

        }

        Log.d("MODE", mMode);
        //fetch directions
        //fetchDirections(origin, destination, mode);
        //new FetchDirectionsTask().execute();


    }

    private class FetchDirectionsTask extends AsyncTask<Void,Void,List<DirectionsAPI.DirectionStep>> {

        @Override
        protected List<DirectionsAPI.DirectionStep> doInBackground(Void... params) {

            return new DirectionsAPI().fetchDirections(origin_string, museumName, mMode);

        }
        @Override
        protected void onPostExecute(List<DirectionsAPI.DirectionStep> directions) {
            //set list to adapter
            if (mDirectionSteps.size() > 0) {
                mDirectionSteps.clear();
            }
            mDirectionSteps.addAll(directions);
            populateList();
        }
    }

    private void populateList() {
        //reviewAdapter = new ReviewListAdapter();
        //reviewListView.setAdapter(reviewAdapter);
    }

    public boolean checkForNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        mMode = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    //Runs when a GoogleApiClient object successfully connects.
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("Google Maps", "onConnected has fired");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("Google Maps", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // Connection to Google Play services was lost for some reason - call connect() to
        // attempt to re-establish the connection.
        Log.i("Google Maps", "Connection suspended");
        mGoogleApiClient.connect();
    }


}
