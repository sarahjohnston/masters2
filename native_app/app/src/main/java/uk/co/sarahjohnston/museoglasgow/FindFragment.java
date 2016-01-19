package uk.co.sarahjohnston.museoglasgow;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class FindFragment extends Fragment {


    public FindFragment() {
        // Required empty public constructor
    }

    MapView mMapView;
    private GoogleMap googleMap;
    Museum thisMuseum;
    Button directionsButton;
    double[] loc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout
        View v = inflater.inflate(R.layout.fragment_find, container,
                false);

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        // latitude and longitude
        PlaceActivity activity = (PlaceActivity) getActivity();
        thisMuseum = activity.getCurrentMuseum();
        loc = thisMuseum.get_location();
        LatLng museumLocation = new LatLng(loc[0], loc[1]);

        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                museumLocation).title(thisMuseum.get_museumName());

        // adding marker
        googleMap.addMarker(marker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(museumLocation, 15));

        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Perform any camera updates here

        directionsButton = (Button)v.findViewById(R.id.directionsButton);
        directionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "direction button pressed", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getActivity(), DirectionsActivity.class);
                i.putExtra("museumName", thisMuseum.get_museumName());
                i.putExtra("location", loc);
                startActivity(i);
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}