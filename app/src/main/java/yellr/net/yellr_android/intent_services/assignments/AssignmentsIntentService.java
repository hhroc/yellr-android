package yellr.net.yellr_android.intent_services.assignments;

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

public class AssignmentsIntentService extends IntentService {
    public static final String ACTION_GET_ASSIGNMENTS =
            "yellr.net.yellr_android.action.GET_ASSIGNMENTS";
    public static final String ACTION_NEW_ASSIGNMENTS =
            "yellr.net.yellr_android.action.NEW_ASSIGNMENTS";

    //public static final String PARAM_CUID = "cuid";
    public static final String PARAM_ASSIGNMENTS_JSON = "assignmentsJson";

    public AssignmentsIntentService() {
        super("AssignmentsIntentService");
        //Log.d("AssignmentsIntentService()","Constructor.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //Log.d("AssignmentsIntentService.onHandleIntent()","Decoding intent action ...");

        //String cuid = intent.getStringExtra(PARAM_CUID);
        handleActionGetAssignments(); //cuid);
    }

    /**
     * Handles get assignments
     */
    private void handleActionGetAssignments() {



        // get the location, but if the user has turned off location services,
        // it will come back null.  If it's null, we won't ping the server
        // and just return a blank json list

        /*
        double latLng[] = YellrUtils.getLocation(getApplicationContext());
        if (latLng != null ) {

            String lat = String.valueOf(latLng[0]);
            String lng = String.valueOf(latLng[1]);

            String languageCode = Locale.getDefault().getLanguage();

            String url = baseUrl
                    + "?cuid=" + YellrUtils.getCUID(getApplicationContext())//cuid
                    + "&language_code=" + languageCode
                    + "&lat=" + lat
                    + "&lng=" + lng;
        */

        String assignmentsJson = "[]";

        String baseUrl = BuildConfig.BASE_URL + "/get_assignments.json";
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

                assignmentsJson = builder.toString();

                Log.d("AssignmentsIntentService.UpdateData()", "Successfully got new assignments list from server.");

            } catch (Exception e) {
                Log.d("AssignmentsIntentService.UpdateData()", "Error: " + e.toString());
            }
        }

        Log.d("AssignmentsIntentService.UpdateData()", "JSON: " + assignmentsJson);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_NEW_ASSIGNMENTS);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_ASSIGNMENTS_JSON, assignmentsJson);
        sendBroadcast(broadcastIntent);
    }
}
