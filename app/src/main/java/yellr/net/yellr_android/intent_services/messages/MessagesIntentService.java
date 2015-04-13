package yellr.net.yellr_android.intent_services.messages;

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
import yellr.net.yellr_android.fragments.MessagesFragment;
import yellr.net.yellr_android.utils.YellrUtils;

public class MessagesIntentService extends IntentService {
    public static final String ACTION_GET_MESSAGES =
            "yellr.net.yellr_android.action.GET_MESSAGES";

    //public static final String PARAM_CUID = "cuid";
    public static final String PARAM_MESSAGES_JSON = "messagesJson";

    public MessagesIntentService() {
        super("MessagesIntentService");
        //Log.d("MessagesIntentService()","Constructor.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //Log.d("MessagesIntentService.onHandleIntent()","Decoding intent action ...");

        //String cuid = intent.getStringExtra(PARAM_CUID);
        //if ( YellrUtils.isHomeLocationSet(getApplicationContext()) )
            handleActionGetMessages(); //cuid);
    }

    /**
     * Handles get messages
     */
    private void handleActionGetMessages() {

        Log.d("MessagesIntentService.handleActionGetMessages()", "Attempting to get local posts ...");

        String localPostsJson = "[]";

        String baseUrl = BuildConfig.BASE_URL + "/get_local_posts.json";
        String url = YellrUtils.buildUrl(getApplicationContext(), baseUrl);
        if (url != null) {
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

                String messagesJson = builder.toString();

                //Log.d("MessagesIntentService.UpdateData()","Broadcasting result ...");

                Log.d("MessagesIntentService.handleActionGetMessages()", "JSON: " + messagesJson);


                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(MessagesFragment.MessagesReceiver.ACTION_NEW_MESSAGES);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra(PARAM_MESSAGES_JSON, messagesJson);
                sendBroadcast(broadcastIntent);


            } catch (Exception e) {

                Log.d("MessagesIntentService.handleActionGetMessages()", "Error: " + e.toString());

                //e.printStackTrace();
            }
        }
    }
}
