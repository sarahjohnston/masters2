package uk.co.sarahjohnston.museoglasgow;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sarahjohnston on 05/11/15.
 * Code adapted from Android Programming - The Big Nerd Ranch Guide
 */
public class ExhibitionAPIFetch {

    String TAG = "ExhibitionAPIFetch";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        }   finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<JsonNewsItem> fetchExhibitions(int museum_id) {

        List<JsonNewsItem> items = new ArrayList<>();
        try {
            String url;
            if (museum_id == 99999) {
                //replace dummywebservice.com with the url of your actual web service
                url = "http://dummywebservice.com/index.php";

            }
            else {
                //replace dummywebservice.com with the url of your actual web service
                url = Uri.parse("http://dummywebservice.com/index.php")
                        .buildUpon()
                        .appendQueryParameter("museum_id", String.valueOf(museum_id))
                        .build().toString();
            }
            String jsonString = getUrlString(url);
            //Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonResult = new JSONObject(jsonString);
            parseExhibitions(items, jsonResult);
        }   catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }   catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch exhibitions", ioe);
        }

        return items;

    }

    public void parseExhibitions(List<JsonNewsItem> items, JSONObject jsonResult)
            throws IOException, JSONException {
        int resultStatus = jsonResult.getInt("status");
        Log.d("Status", String.valueOf(resultStatus));
        if (resultStatus == 200) {
            //ok have results
            Log.d("Web Service Result", "Got results");
            JSONArray exhibitionsArray = jsonResult.getJSONArray("exhibitions");
            for (int i = 0; i < exhibitionsArray.length(); i++) {

                JsonNewsItem item = new JsonNewsItem();
                item.setNewsId(i);
                //Log.d("Name", exhibitionObject.getString("exhibition_name"));
                item.setNewsObject(exhibitionsArray.getJSONObject(i));

                items.add(item);

            }

        }
        else {
            //error
            Log.d("Error:", "Problem retrieving from web service");
        }
    }



}
