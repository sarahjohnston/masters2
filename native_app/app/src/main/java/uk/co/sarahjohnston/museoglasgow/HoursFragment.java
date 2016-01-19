package uk.co.sarahjohnston.museoglasgow;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HoursFragment extends Fragment {

    Museum thisMuseum;
    private String sunday;
    private String monday;
    private String tuesday;
    private String wednesday;
    private String thursday;
    private String friday;
    private String saturday;
    private String openingNotes;
    private String closed = "closed";

    private List<String> openingHours;
    private static final String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    public HoursFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hours, container, false);

        PlaceActivity activity = (PlaceActivity)getActivity();
        thisMuseum = activity.getCurrentMuseum();
        openingHours = thisMuseum.get_openingHours();

        //TextView mondayText = (TextView)view.findViewById(R.id.monday);
        //TextView tuesdayText = (TextView)view.findViewById(R.id.tuesday);

        /*
        monday = "Monday: " + openingHours.get(2) + (openingHours.get(3).equalsIgnoreCase(closed) ? "" : " - " + openingHours.get(3));
        tuesday = "Tuesday: " + openingHours.get(4) + " - " + openingHours.get(5);
        TextView mondayText = (TextView)view.findViewById(R.id.monday);
        mondayText.setText(monday);
        TextView tuesdayText = (TextView)view.findViewById(R.id.tuesday);
        tuesdayText.setText(tuesday);
        */

        for (int i = 0; i < 7; i++) {

            String textHours = days[i] + ": " + openingHours.get(i*2) + (openingHours.get(i*2).equalsIgnoreCase(closed) ? "" : " - " + openingHours.get((i*2)+1));
            //Log.d("TEXT", textHours);
            String day = days[i];
            //Log.d("DAY", day);
            int resID = getResources().getIdentifier(day,"id",activity.getPackageName());
            //Log.d("ID", String.valueOf(resID));
            TextView hours = (TextView)view.findViewById(resID);
            hours.setText(textHours);

        }



        //display hours


        return view;
    }


}
