package yellr.net.yellr_android.intent_services.notifications;

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

import yellr.net.yellr_android.fragments.NotificationsFragment;

public class NotificationsIntentService extends IntentService {
    public static final String ACTION_GET_NOTIFICATIONS =
            "yellr.net.yellr_android.action.GET_NOTIFICATIONS";

    public static final String PARAM_CUID = "cuid";
    public static final String PARAM_NOTIFICATIONS_JSON = "notificationsJson";

    public NotificationsIntentService() {
        super("NotificationsIntentService");
        //Log.d("NotificationsIntentService()","Constructor.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //Log.d("NotificationsIntentService.onHandleIntent()","Decoding intent action ...");

        String cuid = intent.getStringExtra(PARAM_CUID);
        handleActionGetNotifications(cuid);
    }

    /**
     * Handles get notifications
     */
    private void handleActionGetNotifications(String cuid) {

        Log.d("NotificationsIntentService.handleActionGetNotifications()", "Starting handleActionGetNotifications() ...");

        String baseUrl = "http://yellr.mycodespace.net/get_notifications.json";

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
            Log.d("NotificationsIntentService.handleActionGetAssignments()","No location available, defaulting to Rochester, NY");
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

            String notificationsJson = builder.toString();

            //Log.d("NotificationsIntentService.UpdateData()","Broadcasting result ...");

            Log.d("NotificationsIntentService.UpdateData()","JSON: " + notificationsJson);

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(NotificationsFragment.NotificationsReceiver.ACTION_NEW_NOTIFICATIONS);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_NOTIFICATIONS_JSON, notificationsJson);
            sendBroadcast(broadcastIntent);

        } catch( Exception e) {

            Log.d("NotificationsIntentService.UpdateData()","Error: " + e.toString());

            //e.printStackTrace();
        }
    }
}
