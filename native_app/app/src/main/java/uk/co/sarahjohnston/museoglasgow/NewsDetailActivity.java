package uk.co.sarahjohnston.museoglasgow;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;



public class NewsDetailActivity extends Activity {

    ImageView detailImage;
    TextView detailName;
    TextView detailMuseum;
    TextView detailDate;
    TextView detailDescription;
    String linkUrl;
    private Intent mShareIntent = new Intent(Intent.ACTION_SEND);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        detailImage = (ImageView)findViewById(R.id.detailImage);
        detailName = (TextView)findViewById(R.id.detailName);
        detailMuseum = (TextView)findViewById(R.id.detailMuseum);
        detailDate = (TextView)findViewById(R.id.detailDate);
        detailDescription = (TextView)findViewById(R.id.detailDescription);

        // Load listing JSON string from previous activity
        String jsonListing = getIntent().getStringExtra("news_item");
        //Log.d("EXTRA contained", jsonListing);
        JSONObject listing = null;
        try {
            listing = new JSONObject(jsonListing);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (listing == null) {
            //TODO Add action for if no data passed into activity
            Log.d("Error", "No object found");
        }
        else {
            loadNews(listing);
        }
    }

    public void loadNews(JSONObject item) {

        try {
            // Populate data
            String itemName = item.getString("exhibition_name");
            detailName.setText(itemName);
            String museumName = item.getString("museum_name");
            detailMuseum.setText(museumName);
            String exhibitionDate = MyUtil.dateConvert(item.getString("start_date")) + " - " + MyUtil.dateConvert(item.getString("end_date"));
            detailDate.setText(exhibitionDate);
            detailDescription.setText(item.getString("description"));
            linkUrl = item.getString("exhibition_url");
            Picasso.with(this).load(item.getString("images")).into(detailImage);
            ActionBar ab = getActionBar();
            if (ab != null) {
                ab.setTitle(itemName);
            }
            /*Picasso.with(this).load(item.getItem_image()).
                placeholder(R.drawable.placeholder_image).
                into(detailImage);*/
            String shareBody = itemName + " at " + museumName + " from ";
            shareBody += exhibitionDate + " " + linkUrl;
            mShareIntent.setType("text/plain");
            mShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

            /*final Button more_info = (Button)findViewById(R.id.detailMore);
            more_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri webpage = Uri.parse(linkUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    startActivity(intent);
                }
            });*/

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //menu code starts//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_news_detail, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * On selecting action bar icons
     * */


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                //go back
                super.onBackPressed();
                return true;
            case R.id.action_share:
                // do share
                //startActivity(mShareIntent);
                startActivityForResult(mShareIntent, 111);
                //return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //menu ends//

}
