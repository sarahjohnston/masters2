package uk.co.sarahjohnston.museoglasgow;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import uk.co.sarahjohnston.museoglasgow.view.SlidingTabLayout;


public class PlaceActivity extends FragmentActivity {

    private SlidingTabLayout slidingTabLayout;
    private NonSwipeableViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private ActionTabsViewPagerAdapter myViewPageAdapter;

    int singleID = 9999;

    private DataBaseSQLHelper mDataBaseSQLHelper = null;
    Museum currentMuseum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        /* get ID from previous page */
        Bundle extras = getIntent().getExtras();

        if(extras != null && extras.containsKey("MUSEUM_ID"))
        {
            singleID = getIntent().getExtras().getInt("MUSEUM_ID");
            Log.d("Database ID: ", String.valueOf(singleID));

        }

        mDataBaseSQLHelper = new DataBaseSQLHelper(this);

        if (singleID > 0 && singleID != 9999) {
            currentMuseum = mDataBaseSQLHelper.getMuseum(singleID);
            ActionBar ab = getActionBar();
            ab.setTitle(currentMuseum.get_museumName());
            ab.setElevation(0);

            slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tab);
            viewPager = (NonSwipeableViewPager) findViewById(R.id.viewpager);

            // create a fragment list in order.
            fragments = new ArrayList<>();

            fragments.add(new InfoFragment());
            fragments.add(new HoursFragment());
            fragments.add(new FindFragment());
            fragments.add(new ExhibitionsFragment());

            // use FragmentPagerAdapter to bind the slidingTabLayout (tabs with different titles)
            // and ViewPager (different pages of fragment) together.
            myViewPageAdapter =new ActionTabsViewPagerAdapter(getFragmentManager(),
                    fragments);
            viewPager.setAdapter(myViewPageAdapter);

            // make sure the tabs are equally spaced.
            slidingTabLayout.setDistributeEvenly(true);
            slidingTabLayout.setViewPager(viewPager);

        }
        else {
            Log.d("Error", "Had no ID to lookup");
            Toast toast= Toast.makeText(this, R.string.error_no_museum, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Log.d("save instance", "SAVING");
        outState.putInt("singleID", singleID);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Log.d("LIFECYCLE", "ON RESTORE");
        // Restore id from saved instance
        singleID = savedInstanceState.getInt("singleID");
        Log.d("Restored", String.valueOf(singleID));
    }

    public Museum getCurrentMuseum() {
        return currentMuseum;
    }

    public int getFragPosition() {return viewPager.getCurrentItem();}
}
