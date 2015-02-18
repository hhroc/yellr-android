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

public class MessagesIntentService extends IntentService {
    public static final String ACTION_GET_MESSAGES =
            "yellr.net.yellr_android.action.GET_MESSAGES";

    public static final String PARAM_CUID = "cuid";
    public static final String PARAM_MESSAGES_JSON = "messagesJson";

    public MessagesIntentService() {
        super("MessagesIntentService");
        //Log.d("MessagesIntentService()","Constructor.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //Log.d("MessagesIntentService.onHandleIntent()","Decoding intent action ...");

        String cuid = intent.getStringExtra(PARAM_CUID);
        handleActionGetMessages(cuid);
    }

    /**
     * Handles get messages
     */
    private void handleActionGetMessages(String cuid) {

        //Log.d("MessagesIntentService.UpdateData()", "Starting UpdateData() ...");

        String baseUrl = "http://yellr.mycodespace.net/get_messages.json";

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String bestProvider = lm.getBestProvider(criteria, true);
        Location location = lm.getLastKnownLocation(bestProvider); //LocationManager.GPS_PROVIDER);
        // default to center of Rochester, NY
        double latitude = 43.1656;
        double longitude = -77.6114;
        // if we have a location available, then set it
        if ( location != null ){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } else {
            Log.d("MessagesIntentService.handleActionGetAssignments()","No location available, defaulting to Rochester, NY");
        }

        String lat = String.valueOf(latitude);
        String lng = String.valueOf(longitude);

        String languageCode = Locale.getDefault().getLanguage();

        String url =  baseUrl
                + "?cuid=" + cuid
                + "&language_code=" + languageCode
                + "&lat=" + lat
                + "&lng=" + lng;

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

            Log.d("MessagesIntentService.UpdateData()","JSON: " + messagesJson);

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MessagesReceiver.ACTION_NEW_MESSAGES);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_MESSAGES_JSON, messagesJson);
            sendBroadcast(broadcastIntent);

        } catch( Exception e) {

            Log.d("MessagesIntentService.UpdateData()","Error: " + e.toString());

            //e.printStackTrace();
        }
    }
}
