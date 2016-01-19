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
 * Created by sarahjohnston on 01/01/16.
 */
public class DirectionsAPI {

    public String getUrlString(String urlSpec) throws IOException {
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
            return new String(out.toByteArray());
        }   finally {
            connection.disconnect();
        }
    }

    public List<DirectionStep> fetchDirections(String origin, String destination, String mode) {

        List<DirectionStep> directions = new ArrayList<>();
        try {
            String url;

            //replace dummywebservice.com with the url of your actual web service
            url = Uri.parse("http://dummywebservice.com/directions.php")
                    .buildUpon()
                    .appendQueryParameter("origin", origin)
                    .appendQueryParameter("destination", destination)
                    .appendQueryParameter("mode", mode)
                    .build().toString();
            Log.d("URL", url);
            String jsonString = getUrlString(url);
            //Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonResult = new JSONObject(jsonString);
            parseDirections(directions, jsonResult);
        }   catch (JSONException je) {
            Log.e("Error", "Failed to parse JSON", je);
        }   catch (IOException ioe) {
            Log.e("Error", "Failed to fetch directions", ioe);
        }

        return directions;

    }

    public void parseDirections(List<DirectionStep> directions, JSONObject jsonResult) throws IOException, JSONException {
        String resultStatus = jsonResult.getString("status");
        Log.d("Status", String.valueOf(resultStatus));
        if (resultStatus.equals("OK")) {
            //ok have results
            Log.d("Directions Result", "Got results");
            JSONObject leg = jsonResult.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
            String to_from = "From: " + leg.getString("start_address") + "<br>To: " + leg.getString("end_address");
            String totalDuration = leg.getJSONObject("duration").getString("text");
            String totalDistance = leg.getJSONObject("distance").getString("text");

            DirectionStep totalStep = new DirectionStep(totalDistance, totalDuration, to_from);
            directions.add(totalStep);

            JSONArray steps = leg.getJSONArray("steps");

            for (int i = 0; i < steps.length(); i++) {
                JSONObject currentStep = steps.getJSONObject(i);
                JSONObject distanceObj = currentStep.getJSONObject("distance");
                String distance = distanceObj.getString("text");
                JSONObject durationObj = currentStep.getJSONObject("duration");
                String duration = durationObj.getString("text");
                String instructions = currentStep.getString("html_instructions");

                DirectionStep step = new DirectionStep(distance, duration, instructions);

                directions.add(step);

            }

        }
        else if (resultStatus.equals("NOT_FOUND")) {

        }
        else {
            //error
            Log.d("Error", "Problem retrieving from web service");
            return;
        }
    }

    public class DirectionStep {
        String distance;
        String duration;
        String instructions;

        DirectionStep(String distance, String duration, String instructions) {
            this.distance = distance;
            this.duration = duration;
            this.instructions = instructions;
        }
    }
}
