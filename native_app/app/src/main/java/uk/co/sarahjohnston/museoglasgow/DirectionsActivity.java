package uk.co.sarahjohnston.museoglasgow;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

public class DirectionsActivity extends Activity implements AdapterView.OnItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        loc = getIntent().getDoubleArrayExtra("location");
        museumName = getIntent().getStringExtra("museumName");

        if (loc.length < 2) {
            Log.d("Error", "No location co-ordinates");
            finish();
        }

        currentLocationChoice = (RadioGroup)findViewById(R.id.currentLocationChoice);
        currentLocation_yes = (RadioButton)findViewById(R.id.currentLocation_yes);
        currentLocation_no = (RadioButton)findViewById(R.id.currentLocation_no);
        originLabel = (TextView)findViewById(R.id.originLabel);
        originName = (EditText)findViewById(R.id.origin_choice);

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



        destination = (TextView)findViewById(R.id.destination);
        destination.setText("To: " + museumName);

        transport_mode = (Spinner)findViewById(R.id.transport_mode);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.transport_mode, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transport_mode.setAdapter(adapter);
        transport_mode.setOnItemSelectedListener(this);

        directionsButton = (Button)findViewById(R.id.directionsButton);
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

        Button cancelButton = (Button)findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

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

    public boolean isValidString(String in) {
        if (in != null && in.length() > 2) {
            return true;
        }
        return false;
    }

    public void submitDirectionForm() {

        boolean errors = false;
        String errorLoc = "";
        String errorText = "";

        //sort out origin for directions
        if (!currentLocation) {
            //get origin from edittext field
            origin_string = originName.getText().toString().trim();
            if (!isValidString(origin_string)) {
                //return error please fill out all fields
                originName.setHint("Enter your starting point");
                errors = true;
                errorText = "Please fill out all form fields. ";
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
                origin_string = String.valueOf(mLastLocation.getLatitude()) + "," + String.valueOf(mLastLocation.getLongitude());
            }
            else {
                //error GPS failed
                Log.d("Error", "No GPS found");
                errors = true;
                errorLoc = "Sorry their was a problem getting your current location. ";

            }

        }

        //Log.d("MODE", mMode);
        if(!isValidString(mMode)) {
            errors = true;
            errorText = "Please fill out all form fields. ";

        }
        String destination = String.valueOf(loc[0]) + "," + String.valueOf(loc[1]);
        //fetch directions
        //fetchDirections(origin, destination, mode);

        if (errors) {
            //show error toast
            Toast toast = Toast.makeText(this, errorText + errorLoc, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
        else {
            Intent mIntent = new Intent(this, DirectionsResultsActivity.class);
            mIntent.putExtra("origin", origin_string);
            mIntent.putExtra("museumName", museumName);
            mIntent.putExtra("destination", destination);
            mIntent.putExtra("mode", mMode);
            startActivity(mIntent);
        }



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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
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
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
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
