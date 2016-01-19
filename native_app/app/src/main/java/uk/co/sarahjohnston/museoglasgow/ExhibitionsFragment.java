package uk.co.sarahjohnston.museoglasgow;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExhibitionsFragment extends Fragment {

    Museum thisMuseum;
    PlaceActivity activity;
    int ID;
    private NewsAdapter adapter;
    ListView lvResults;
    List<JsonNewsItem> newsArrayList = new ArrayList<>();
    public Long lastUpdated;
    public int updateInterval = 1800;
    LinearLayout errorLayout;
    Button tryAgainButton;
    public static final String NEWS_ITEM = "news_item";
    public String CONNECTED = "connnected";
    public String DISCONNECTED = "disconnected";
    public String CONNECTION_STATUS = null;
    ProgressDialog dialog;
    public static Context appContext;


    public ExhibitionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            lastUpdated = savedInstanceState.getLong("lastUpdated");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exhibitions, container, false);

        activity = (PlaceActivity) getActivity();

        thisMuseum = activity.getCurrentMuseum();
        ID = thisMuseum.getId();
        Log.d("ID", String.valueOf(ID));

        lvResults = (ListView)view.findViewById(R.id.lvResults);
        errorLayout = (LinearLayout)view.findViewById(R.id.errorBox);
        tryAgainButton = (Button)view.findViewById(R.id.tryAgainButton);

        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshNews();
            }
        });

        adapter = new NewsAdapter(activity, newsArrayList);
        lvResults.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshNews();

    }

    private class FetchExhibitionsTask extends AsyncTask<Void,Void,List<JsonNewsItem>> {
        @Override
        protected List<JsonNewsItem> doInBackground(Void... params) {
            Long now = System.currentTimeMillis()/1000;
            if (lastUpdated != null) {
                //check for last updated and check if already have results
                if ((now - lastUpdated) < updateInterval && adapter.getCount() > 0) {
                    List<JsonNewsItem> emptyList = new ArrayList<>();
                    return emptyList;
                }
                else {
                    return new ExhibitionAPIFetch().fetchExhibitions(ID);
                }
            }
            else {
                return new ExhibitionAPIFetch().fetchExhibitions(ID);
            }


        }
        @Override
        protected void onPreExecute() {

            int pos = activity.getFragPosition();
            Log.d("POSITION", String.valueOf(pos));
            //if news tab visible
            if (pos == 3) {
                dialog = new ProgressDialog(getActivity());
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Fetching news...");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        }
        @Override
        protected void onPostExecute(List<JsonNewsItem> items) {
            newsArrayList = items;
            if (items == null) {
                Log.d("Result", "No results here");}
            adapter.addAll(newsArrayList);
            if (dialog != null) {dialog.dismiss();}
            lastUpdated = System.currentTimeMillis()/1000;
            Log.d("Last updated:", String.valueOf(lastUpdated));
            setupItemSelectedListener();
        }
    }

    public boolean checkForNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void setupItemSelectedListener() {
        lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View item, int position, long rowId) {
                JsonNewsItem jsonItem = adapter.getItem(position);
                String passItem = jsonItem.getNewsObject().toString();
                Log.d("JSON to send in extra", passItem);
                Intent i = new Intent(getActivity(), NewsDetailActivity.class);
                i.putExtra(NEWS_ITEM, passItem);
                startActivity(i);
            }
        });
    }

    public void refreshNews() {

        if (checkForNetwork()) {
            //Log.d("Connection status", "connected");
            CONNECTION_STATUS = CONNECTED;
            lvResults.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
            new FetchExhibitionsTask().execute();

        }

        else {
            //Log.d("Connection status", "not connected");
            CONNECTION_STATUS = DISCONNECTED;
            lvResults.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);

        }

    }






}
