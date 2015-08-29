package yellr.net.yellr_android.intent_services.local_posts;

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

public class LocalPostsIntentService extends IntentService {
    public static final String ACTION_GET_LOCAL_POSTS =
            "yellr.net.yellr_android.action.GET_LOCAL_POSTS";
    public static final String ACTION_NEW_LOCAL_POSTS =
            "yellr.net.yellr_android.action.NEW_LOCAL_POSTS";

    //public static final String PARAM_CUID = "cuid";
    public static final String PARAM_LOCAL_POSTS_JSON =
            "yellr.net.yellr_android.params.LOCAL_POSTS_JSON";

    public LocalPostsIntentService() {
        super("LocalPostsIntentService");
        Log.d("LocalPostsIntentService()","Constructor.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("LocalPostsIntentService.onHandleIntent()","Decoding intent action ...");

        //String cuid = intent.getStringExtra(PARAM_CUID);
        handleActionGetLocalPosts(); //cuid);
    }

    /**
     * Handles get localPosts
     */
    private void handleActionGetLocalPosts() {

        Log.d("LocalPostsIntentService.handleActionGetLocalPosts()","Attempting to get local posts ...");

        String localPostsJson = "[]";

        String baseUrl = BuildConfig.BASE_URL + "/get_local_posts.json";
        String url = YellrUtils.buildUrl(getApplicationContext(),baseUrl);
        if (url != null) {

            //
            // TODO: need to check for exceptions better, this bombs out sometimes
            //
            try {

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

                localPostsJson = builder.toString();

                Log.d("LocalPostsIntentService.handleActionGetLocalPosts()", "Successfully got new local posts list from server.");
                Log.d("LocalPostsIntentService.handleActionGetLocalPosts()All()", localPostsJson);

            } catch (Exception e) {
                Log.d("LocalPostsIntentService.handleActionGetLocalPosts()", "Error: " + e.toString());
            }
        }

        Log.d("LocalPostsIntentService.handleActionGetLocalPosts()", "JSON: " + localPostsJson);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(LocalPostsIntentService.ACTION_NEW_LOCAL_POSTS);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(LocalPostsIntentService.PARAM_LOCAL_POSTS_JSON, localPostsJson);
        sendBroadcast(broadcastIntent);
    }
}
