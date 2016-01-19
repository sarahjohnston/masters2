package uk.co.sarahjohnston.museoglasgow;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.WallpaperManager;
import android.graphics.drawable.Drawable;
import android.support.v13.app.FragmentPagerAdapter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import java.util.ArrayList;


public class ActionTabsViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments;

    public static final int INFO = 0;
    public static final int HOURS = 1;
    public static final int FIND = 2;
    public static final int EXHIBITIONS = 3;
    public static final String UI_TAB_INFO = "INFO";
    public static final String UI_TAB_HOURS = "HOURS";
    public static final String UI_TAB_FIND = "MAP";
    //public static final String UI_TAB_EXHIBITIONS = "EXHIBITIONS";
    public static final String UI_TAB_EXHIBITIONS = "WHAT'S ON";

    public ActionTabsViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments){
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

            case INFO:
                return UI_TAB_INFO;
            case HOURS:
                return UI_TAB_HOURS;
            case FIND:
                return UI_TAB_FIND;
            case EXHIBITIONS:
                return UI_TAB_EXHIBITIONS;
            default:
                break;
        }
        return null;


    }



}
