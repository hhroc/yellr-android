package yellr.net.yellr_android.intent_services.stories;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import yellr.net.yellr_android.BuildConfig;
import yellr.net.yellr_android.utils.YellrUtils;

public class StoriesIntentService extends IntentService {
    public static final String ACTION_GET_STORIES =
            "yellr.net.yellr_android.action.GET_STORIES";
    public static final String ACTION_NEW_STORIES =
            "yellr.net.yellr_android.action.NEW_STORIES";

    //public static final String PARAM_CUID = "clientId";
    public static final String PARAM_STORIES_JSON = "storiesJson";

    public StoriesIntentService() {
        super("StoriesIntentService");
        //Log.d("StoriesIntentService()","Constructor.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("StoriesIntentService.onHandleIntent()","Requesting new stories ...");

        //String cuid = intent.getStringExtra(PARAM_CUID);
        //if ( YellrUtils.isHomeLocationSet(getApplicationContext()) != null )
            handleActionGetStories(); //cuid);
    }

    /**
     * Handles get Stories
     */
    private void handleActionGetStories() {

        String storiesJson = "{}";

        String baseUrl = BuildConfig.BASE_URL + "/get_stories.json";
        String url = YellrUtils.buildUrl(getApplicationContext(),baseUrl);
        if (url != null) {
            storiesJson = YellrUtils.downloadJson(getApplicationContext(),url);

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(StoriesIntentService.ACTION_NEW_STORIES);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_STORIES_JSON, storiesJson);
            sendBroadcast(broadcastIntent);

        }
    }
}
