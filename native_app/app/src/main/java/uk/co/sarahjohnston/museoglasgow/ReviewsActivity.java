package uk.co.sarahjohnston.museoglasgow;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReviewsActivity extends Activity {

    int placeID;
    String museumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        placeID = getIntent().getIntExtra("placeID", 0);
        museumName = getIntent().getStringExtra("museumName");

        Log.d("Review Activity:", "id: " + String.valueOf(placeID) + ", name: " + museumName);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("placeID", placeID);
        outState.putString("museumName", museumName);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore placeID and museumName from saved instance
        placeID = savedInstanceState.getInt("placeID");
        museumName = savedInstanceState.getString("museumName");
        Log.d("Review Activity:", "id: " + String.valueOf(placeID) + ", name: " + museumName);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_reviews, menu);

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
