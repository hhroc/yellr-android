package yellr.net.yellr_android.intent_services.assignments;

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

import yellr.net.yellr_android.activities.HomeActivity;
import yellr.net.yellr_android.fragments.AssignmentsFragment;

public class AssignmentsIntentService extends IntentService {
    public static final String ACTION_GET_ASSIGNMENTS =
            "yellr.net.yellr_android.action.GET_ASSIGNMENTS";

    public static final String PARAM_CLIENT_ID = "clientId";
    public static final String PARAM_ASSIGNMENTS_JSON = "assignmentsJson";

    public AssignmentsIntentService() {
        super("AssignmentsIntentService");
        Log.d("AssignmentsIntentService()","Constructor.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("AssignmentsIntentService.onHandleIntent()","Decoding intent action ...");

        String clientId = intent.getStringExtra(PARAM_CLIENT_ID);
        handleActionGetAssignments(clientId);
    }

    /**
     * Handles get assignments
     */
    private void handleActionGetAssignments(String clientId) {

        Log.d("AssignmentsIntentService.UpdateData()", "Starting UpdateData() ...");

        // get location data

        // via: http://stackoverflow.com/a/2227299
        // TODO: check for last known good, and if null then
        //       poll to get a fresh location.  This will be
        //       okay for now though, even if it takes a while
        //       to complete (since it's in the service)
        //LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //double latitude = location.getLatitude();
        //double longitude = location.getLongitude();

        double latitude = 43.2;
        double longitude = -77.5;

        String baseUrl = "http://yellr.mycodespace.net/get_assignments.json";

        String lat = String.valueOf(latitude);
        String lng = String.valueOf(longitude);

        String languageCode = Locale.getDefault().getLanguage();

        String url =  baseUrl
                + "?client_id=" + clientId
                + "&language_code=" + languageCode
                + "&lat=" + lat
                + "&lng=" + lng;

        Log.d("AssignmentsIntentService.UpdateData()","URL: " + url);

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

            String assignmentsJson = builder.toString();

            Log.d("AssignmentsIntentService.UpdateData()","Broadcasting result ...");

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(AssignmentsFragment.AssignmentsReceiver.ACTION_NEW_ASSIGNMENTS);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_ASSIGNMENTS_JSON, assignmentsJson);
            sendBroadcast(broadcastIntent);

        } catch( Exception e) {

            Log.d("AssignmentsIntentService.UpdateData()","Error: " + e.toString());

            //e.printStackTrace();
        }
    }
}
