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

        //Log.d("StoriesIntentService.onHandleIntent()","Decoding intent action ...");

        //String cuid = intent.getStringExtra(PARAM_CUID);
        if ( YellrUtils.isHomeLocationSet(getApplicationContext()) )
            handleActionGetStories(); //cuid);
    }

    /**
     * Handles get Stories
     */
    private void handleActionGetStories() {

        String baseUrl = BuildConfig.BASE_URL + "/get_stories.json";

        // get the location, but if the user has turned off location services,
        // it will come back null.  If it's null, just dump out.
        // TODO: pop-up a dialog maybe??
        double latLng[] = YellrUtils.getLocation(getApplicationContext());
        if (latLng == null )
            return;
        String lat = String.valueOf(latLng[0]);
        String lng = String.valueOf(latLng[1]);

        String languageCode = Locale.getDefault().getLanguage();

        String url =  baseUrl
                + "?cuid=" + YellrUtils.getCUID(getApplicationContext()) // cuid
                + "&language_code=" + languageCode
                + "&lat=" + lat
                + "&lng=" + lng;
        //Log.d("StoriesIntentService.UpdateData()","URL: " + url);

        //
        // TODO: need to check for exceptions better, this bombs out sometimes
        //
        try {

            //Log.d("StoriesIntentService.UpdateData()","Attempting HTTP connection ...");

            //
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();

            //
            InputStream content = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));

            //
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            String storiesJson = builder.toString();

            Log.d("StoriesIntentService.publishPost()","JSON: " + storiesJson);

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(StoriesIntentService.ACTION_NEW_STORIES);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_STORIES_JSON, storiesJson);
            sendBroadcast(broadcastIntent);

        } catch( Exception e) {

            Log.d("StoriesIntentService.UpdateData()","Error: " + e.toString());

            //e.printStackTrace();
        }
    }
}
