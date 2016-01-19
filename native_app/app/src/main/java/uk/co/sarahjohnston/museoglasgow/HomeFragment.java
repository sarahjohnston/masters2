package uk.co.sarahjohnston.museoglasgow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    Intent intent;

    Context mContext;

    private DataBaseSQLHelper mDataBaseSQLHelper = null;

    private Cursor mCursor = null;

    //private MuseumAdapter mMuseumAdapter = null;

    List<Museum> Museums = new ArrayList<Museum>();
    GridView museumListView;
    ArrayAdapter<Museum> museumAdapter;
    int gridItemW;
    int thumbnailHeight;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        museumListView = (GridView) view.findViewById(R.id.gridView);

        mDataBaseSQLHelper = new DataBaseSQLHelper(getActivity());

        mDataBaseSQLHelper.createDatabase();

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        //float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        //Log.d("SCREEN SIZE", "Width: " + String.valueOf(dpWidth) + "dp, Height: " + String.valueOf(dpHeight) + "dp");

        float gridItemWidth = dpWidth / 2;
        gridItemW = (int)gridItemWidth;
        thumbnailHeight = gridItemW/2;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        int NumberMuseums = mDataBaseSQLHelper.getDatabaseCount();
        Log.d("Number of DB entries", String.valueOf(NumberMuseums));

        if (NumberMuseums > 0) {
            Museums.clear();
            Museums.addAll(mDataBaseSQLHelper.getAllMuseums());

            museumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    view.setAlpha(.8f);
                    Museum clickedMuseum = Museums.get(position);
                    int singleID = clickedMuseum.getId();
                    //Log.d("clicked on:", String.valueOf(singleID));
                    Intent i = new Intent(getActivity(), PlaceActivity.class);
                    i.putExtra("MUSEUM_ID", singleID);
                    startActivity(i);

                }
            });


        }

        else {

            Toast toast= Toast.makeText(getActivity(), "No properties in database", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
        populateList();
    }

    private void populateList() {
        museumAdapter = new MuseumListAdapter();
        museumListView.setAdapter(museumAdapter);
    }


    private class MuseumListAdapter extends ArrayAdapter<Museum> {
        public MuseumListAdapter() {
            super (getActivity(), R.layout.gridview_item, Museums);
        }


        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getActivity().getLayoutInflater().inflate(R.layout.gridview_item, parent, false);

            Museum currentMuseum = Museums.get(position);

            int resID = getResources().getIdentifier("text_" + currentMuseum.get_mainImage(), "drawable", getActivity().getPackageName());
            ImageView thumbnail = (ImageView)view.findViewById(R.id.museum_thumbnail);
            thumbnail.setImageResource(resID);

            return view;

        }
    }




}
