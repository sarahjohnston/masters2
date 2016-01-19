package uk.co.sarahjohnston.museoglasgow;

import org.json.JSONObject;

/**
 * Created by sarahjohnston on 17/11/2015.
 */
public class JsonNewsItem {
    private int NewsId;
    private JSONObject NewsObject;

    public int getNewsId() {
        return NewsId;
    }

    public void setNewsId(int newsId) {
        NewsId = newsId;
    }

    public JSONObject getNewsObject() {
        return NewsObject;
    }

    public void setNewsObject(JSONObject newsObject) {
        NewsObject = newsObject;
    }

}
