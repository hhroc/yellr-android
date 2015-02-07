package yellr.net.yellr_android.intent_services.notifications;

import android.app.IntentService;
import android.content.Intent;
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

public class NotificationsIntentService extends IntentService {
    public static final String ACTION_GET_NOTIFICATIONS =
            "yellr.net.yellr_android.action.GET_NOTIFICATIONS";

    public static final String PARAM_CLIENT_ID = "clientId";
    public static final String PARAM_NOTIFICATIONS_JSON = "notificationsJson";

    public NotificationsIntentService() {
        super("NotificationsIntentService");
        //Log.d("NotificationsIntentService()","Constructor.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //Log.d("NotificationsIntentService.onHandleIntent()","Decoding intent action ...");

        String clientId = intent.getStringExtra(PARAM_CLIENT_ID);
        handleActionGetNotifications(clientId);
    }

    /**
     * Handles get notifications
     */
    private void handleActionGetNotifications(String clientId) {

        //Log.d("NotificationsIntentService.UpdateData()", "Starting UpdateData() ...");

        String baseUrl = "http://yellr.mycodespace.net/get_notifications.json";

        String url =  baseUrl
                + "?client_id=" + clientId;

        //Log.d("NotificationsIntentService.UpdateData()","URL: " + url);

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
            broadcastIntent.setAction(NotificationsReceiver.ACTION_NEW_NOTIFICATIONS);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_NOTIFICATIONS_JSON, notificationsJson);
            sendBroadcast(broadcastIntent);

        } catch( Exception e) {

            Log.d("NotificationsIntentService.UpdateData()","Error: " + e.toString());

            //e.printStackTrace();
        }
    }
}
