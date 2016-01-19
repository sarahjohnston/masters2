package uk.co.sarahjohnston.museoglasgow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.List;


public class TodayFragment extends Fragment {


    int ID = 99999;
    private NewsAdapter adapter;
    ListView lvResults;
    List<JsonNewsItem> newsArrayList = new ArrayList<>();
    public static final String NEWS_ITEM = "news_item";
    LinearLayout errorLayout;
    Button tryAgainButton;
    public String CONNECTED = "connnected";
    public String DISCONNECTED = "disconnected";
    public String CONNECTION_STATUS = null;
    public Long lastUpdated;
    public int updateInterval = 1800;

    public TodayFragment() {
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
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        lvResults = (ListView)view.findViewById(R.id.newsListView);
        errorLayout = (LinearLayout)view.findViewById(R.id.errorBox);
        tryAgainButton = (Button)view.findViewById(R.id.tryAgainButton);

        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshNews();
            }
        });


        adapter = new NewsAdapter(getActivity(), newsArrayList);
        lvResults.setAdapter(adapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        if (lastUpdated != null) {
            savedInstanceState.putLong("lastUpdated", lastUpdated);
        }
    }

    private class FetchExhibitionsTask extends AsyncTask<Void,Void,List<JsonNewsItem>> {
        @Override
        protected List<JsonNewsItem> doInBackground(Void... params) {
            Long now = System.currentTimeMillis()/1000;
            if (lastUpdated != null) {
                //check for last updated and check if already have results
                if ((now - lastUpdated) < updateInterval) {
                    return newsArrayList;
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
        protected void onPostExecute(List<JsonNewsItem> items) {
            newsArrayList = items;
            if (items == null) {
                Log.d("Result", "Bugger off no results here");}
            adapter.addAll(newsArrayList);
            lastUpdated = System.currentTimeMillis()/1000;
            Log.d("Last updated:", String.valueOf(lastUpdated));
            setupItemSelectedListener();
        }
    }

    public void setupItemSelectedListener() {
        lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View item, int position, long rowId) {
                //Toast.makeText(getBaseContext(), "List item clicked: " + String.valueOf(position), Toast.LENGTH_SHORT).show();
                JsonNewsItem jsonItem = adapter.getItem(position);
                String passItem = jsonItem.getNewsObject().toString();
                //Log.d("JSON to send in extra", passItem);
                Intent i = new Intent(getActivity(), NewsDetailActivity.class);
                i.putExtra(NEWS_ITEM, passItem);
                startActivity(i);
            }
        });
    }

    public boolean checkForNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (newsArrayList.isEmpty()) {
            refreshNews();
        }

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
            Log.d("Connection status", "not connected");
            CONNECTION_STATUS = DISCONNECTED;
            lvResults.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);

        }

    }



}
