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

        handleActionGetAssignments(); //cuid);
    }

    /**
     * Handles get assignments
     */
    private void handleActionGetAssignments() {

        String assignmentsJson = "{}";

        String basicUrl = BuildConfig.BASE_URL + "/get_assignments.json";
        String url = YellrUtils.buildUrl(getApplicationContext(), basicUrl);
        if (url != null) {

            assignmentsJson = YellrUtils.downloadJson(getApplicationContext(),url);

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(AssignmentsIntentService.ACTION_NEW_ASSIGNMENTS);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(AssignmentsIntentService.PARAM_ASSIGNMENTS_JSON, assignmentsJson);
            sendBroadcast(broadcastIntent);

        }


    }
}
