package uk.co.sarahjohnston.museoglasgow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static uk.co.sarahjohnston.museoglasgow.MyUtil.dateConvert;

/**
 * Created by sarahjohnston on 16/11/2015.
 */
public class NewsAdapter extends ArrayAdapter<JsonNewsItem> {


    public NewsAdapter(Context context, List<JsonNewsItem> aListings) {
        super(context, 0, aListings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        JsonNewsItem news_item = getItem(position);
        JSONObject json_string = news_item.getNewsObject();
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.news_item, null);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.newsItem);
        TextView tvMuseum = (TextView) convertView.findViewById(R.id.newsLocation);
        TextView tvDate = (TextView) convertView.findViewById(R.id.newsDate);
        ImageView ivThumbImage = (ImageView) convertView.findViewById(R.id.newsImage);
        // Populate the data into the template view using the data object
        try {
            tvName.setText(json_string.getString("exhibition_name"));
            tvMuseum.setText(json_string.getString("museum_name"));
            tvDate.setText(dateConvert(json_string.getString("start_date")) + " - " + dateConvert(json_string.getString("end_date")));
            Picasso.with(getContext()).load(json_string.getString("images")).into(ivThumbImage);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Return the completed view to render on screen
        return convertView;
    }


}

