package uk.co.sarahjohnston.museoglasgow;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static uk.co.sarahjohnston.museoglasgow.MyUtil.dateConvert;

public class DirectionsResultsActivity extends Activity {

    String origin;
    String destination;
    String mode;
    String museumName;
    ArrayList<DirectionsAPI.DirectionStep> mDirectionSteps = new ArrayList<>();
    ListView directionsListView;
    DirectionsAdapter directionsAdapter;
    TextView to_from;
    TextView totals;
    ProgressDialog dialog;
    LinearLayout errorLayout;
    LinearLayout listHeader;
    Button tryAgainButton;
    public String CONNECTED = "connnected";
    public String DISCONNECTED = "disconnected";
    public String CONNECTION_STATUS = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions_results);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        museumName = getIntent().getStringExtra("museumName");
        origin = getIntent().getStringExtra("origin");
        destination = getIntent().getStringExtra("destination");
        mode = getIntent().getStringExtra("mode");
        directionsListView = (ListView)findViewById(R.id.directionsListView);
        to_from = (TextView)findViewById(R.id.to_from);
        totals = (TextView)findViewById(R.id.totals);
        errorLayout = (LinearLayout)findViewById(R.id.errorBox);
        listHeader = (LinearLayout)findViewById(R.id.listHeader);
        tryAgainButton = (Button)findViewById(R.id.tryAgainButton);

        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDirections();
            }
        });

        getDirections();
    }

    private class FetchDirectionsTask extends AsyncTask<Void,Void,List<DirectionsAPI.DirectionStep>> {

        @Override
        protected List<DirectionsAPI.DirectionStep> doInBackground(Void... params) {

            return new DirectionsAPI().fetchDirections(origin, destination, mode);

        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(DirectionsResultsActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Searching for directions...");
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        @Override
        protected void onPostExecute(List<DirectionsAPI.DirectionStep> directions) {
            //set list to adapter
            dialog.dismiss();
            if (mDirectionSteps.size() > 0) {
                mDirectionSteps.clear();
            }
            if (directions.size() > 0) {
                DirectionsAPI.DirectionStep currentTotals = directions.get(0);
                to_from.setText(Html.fromHtml(currentTotals.instructions));
                totals.setText("Distance: " + currentTotals.distance + ", Travel time: " + currentTotals.duration);
                directions.remove(0);
                //dialog.dismiss();
                mDirectionSteps.addAll(directions);
                populateList();
            }
            else {

                Toast toast = Toast.makeText(DirectionsResultsActivity.this, "No directions found. Check your from location.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                finish();
            }

        }
    }

    private void populateList() {
        directionsAdapter = new DirectionsAdapter(this, mDirectionSteps);
        directionsListView.setAdapter(directionsAdapter);
    }

    public boolean checkForNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void getDirections() {

        if (checkForNetwork()) {
            //Log.d("Connection status", "connected");
            CONNECTION_STATUS = CONNECTED;
            listHeader.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
            new FetchDirectionsTask().execute();

        }
        else {
            errorLayout.setVisibility(View.VISIBLE);
            listHeader.setVisibility(View.GONE);

        }
    }
    private class DirectionsAdapter extends ArrayAdapter<DirectionsAPI.DirectionStep> {

        public DirectionsAdapter(Context context, ArrayList<DirectionsAPI.DirectionStep> mDirectionSteps) {
            super (context, 0, mDirectionSteps);
        }


        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.direction_item, parent, false);

            DirectionsAPI.DirectionStep currentStep = mDirectionSteps.get(position);

            TextView instruction = (TextView) view.findViewById(R.id.instruction);
            instruction.setText(String.valueOf(position + 1) + ". " + Html.fromHtml(currentStep.instructions));
            TextView distance = (TextView) view.findViewById(R.id.distance);
            distance.setText("Distance: " + currentStep.distance + " | Duration: " + currentStep.duration);

            return view;

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
