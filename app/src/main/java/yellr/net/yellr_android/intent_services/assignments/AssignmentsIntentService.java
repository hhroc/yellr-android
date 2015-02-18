package yellr.net.yellr_android.intent_services.assignments;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import yellr.net.yellr_android.activities.HomeActivity;
import yellr.net.yellr_android.fragments.AssignmentsFragment;

public class AssignmentsIntentService extends IntentService {
    public static final String ACTION_GET_ASSIGNMENTS =
            "yellr.net.yellr_android.action.GET_ASSIGNMENTS";
    public static final String ACTION_NEW_ASSIGNMENTS =
            "yellr.net.yellr_android.action.NEW_ASSIGNMENTS";

    public static final String PARAM_CUID = "cuid";
    public static final String PARAM_ASSIGNMENTS_JSON = "assignmentsJson";

    public AssignmentsIntentService() {
        super("AssignmentsIntentService");
        //Log.d("AssignmentsIntentService()","Constructor.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //Log.d("AssignmentsIntentService.onHandleIntent()","Decoding intent action ...");

        String cuid = intent.getStringExtra(PARAM_CUID);
        handleActionGetAssignments(cuid);
    }

    /**
     * Handles get assignments
     */
    private void handleActionGetAssignments(String cuid) {

        //Log.d("AssignmentsIntentService.UpdateData()", "Starting UpdateData() ...");

        // get location data

        // via: http://stackoverflow.com/a/2227299
        // TODO: check for last known good, and if null then
        //       poll to get a fresh location.  This will be
        //       okay for now though, even if it takes a while
        //       to complete (since it's in the service)

        String baseUrl = "http://yellr.mycodespace.net/get_assignments.json";

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
            Log.d("AssignmentsIntentService.handleActionGetAssignments()","No location available, defaulting to Rochester, NY");
        }

        String lat = String.valueOf(latitude);
        String lng = String.valueOf(longitude);

        String languageCode = Locale.getDefault().getLanguage();

        String url =  baseUrl
                + "?cuid=" + cuid
                + "&language_code=" + languageCode
                + "&lat=" + lat
                + "&lng=" + lng;

        //Log.d("AssignmentsIntentService.UpdateData()","URL: " + url);

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

            //Log.d("AssignmentsIntentService.UpdateData()","Broadcasting result ...");

            Log.d("AssignmentsIntentService.UpdateData()","JSON: " + assignmentsJson);

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(ACTION_NEW_ASSIGNMENTS);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_ASSIGNMENTS_JSON, assignmentsJson);
            sendBroadcast(broadcastIntent);

        } catch( Exception e) {

            Log.d("AssignmentsIntentService.UpdateData()","Error: " + e.toString());

            //e.printStackTrace();
        }
    }
}
