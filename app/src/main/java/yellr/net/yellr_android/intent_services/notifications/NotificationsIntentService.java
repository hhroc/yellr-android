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

import yellr.net.yellr_android.BuildConfig;
import yellr.net.yellr_android.fragments.NotificationsFragment;
import yellr.net.yellr_android.utils.YellrUtils;

public class NotificationsIntentService extends IntentService {
    public static final String ACTION_GET_NOTIFICATIONS =
            "yellr.net.yellr_android.action.GET_NOTIFICATIONS";

    //public static final String PARAM_CUID = "cuid";
    public static final String PARAM_NOTIFICATIONS_JSON = "notificationsJson";

    public NotificationsIntentService() {
        super("NotificationsIntentService");
        //Log.d("NotificationsIntentService()","Constructor.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //Log.d("NotificationsIntentService.onHandleIntent()","Decoding intent action ...");

        //String cuid = intent.getStringExtra(PARAM_CUID);
        //if (YellrUtils.isHomeLocationSet(getApplicationContext()))
            handleActionGetNotifications(); //cuid);
    }

    /**
     * Handles get notifications
     */
    private void handleActionGetNotifications() {

        Log.d("NotificationsIntentService.handleActionGetNotifications()", "Starting handleActionGetNotifications() ...");

        String baseUrl = BuildConfig.BASE_URL + "/get_notifications.json";

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
                + "?cuid=" + YellrUtils.getCUID(getApplicationContext()) //cuid
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
