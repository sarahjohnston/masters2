package uk.co.sarahjohnston.museoglasgow;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import java.util.ArrayList;

/**
 * Created by sarahjohnston on 11/12/2015.
 */
public class HomeTabsViewPagerAdapter extends FragmentPagerAdapter{
    private ArrayList<Fragment> fragments;

    public static final int MUSEUMS = 0;
    public static final int TODAY = 1;

    public static final String UI_TAB_MUSEUMS = "MUSEUMS";
    public static final String UI_TAB_TODAY = "WHAT'S ON TODAY";


    public HomeTabsViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments){
        super(fm);
        this.fragments = fragments;
    }

    public Fragment getItem(int pos){
        return fragments.get(pos);
    }

    public int getCount(){
        return fragments.size();
    }

    public CharSequence getPageTitle(int position) {

        switch (position) {

            case MUSEUMS:
                return UI_TAB_MUSEUMS;
            case TODAY:
                return UI_TAB_TODAY;
            default:
                break;
        }
        return null;


    }
}







