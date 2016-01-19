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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import uk.co.sarahjohnston.museoglasgow.view.SlidingTabLayout;


public class MainActivity extends FragmentActivity {

    private SlidingTabLayout slidingTabLayout;
    private NonSwipeableViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private HomeTabsViewPagerAdapter myViewPageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //show logo in action bar
        ActionBar actionBar = getActionBar();

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.logo_actionbar, null);

        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setElevation(0);

        // Define SlidingTabLayout
        slidingTabLayout = (SlidingTabLayout)findViewById(R.id.tab);
        //slidingTabLayout.setCustomTabView(R.layout.tab_item, R.id.tab_name);
        viewPager = (NonSwipeableViewPager)findViewById(R.id.viewpager);

        // create a fragment list in order.
        fragments = new ArrayList<>();

        fragments.add(new HomeFragment());
        fragments.add(new TodayFragment());

        // use FragmentPagerAdapter to connect slidingTabLayout and NonSwipeableViewPager
        myViewPageAdapter =new HomeTabsViewPagerAdapter(getFragmentManager(),
                fragments);
        viewPager.setAdapter(myViewPageAdapter);

        // make tabs even spaced
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
