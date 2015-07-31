package yellr.net.yellr_android.intent_services.assignments;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Arrays;

import yellr.net.yellr_android.BuildConfig;
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * Created by TDuffy on 4/15/2015.
 */
public class CheckAssignmentsIntentService extends IntentService {
    public static final String ACTION_CHECK_ASSIGNMENTS =
            "yellr.net.yellr_android.action.ACTION_CHECK_ASSIGNMENTS";

    public CheckAssignmentsIntentService() {
        super("CheckAssignmentsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Context context = getApplicationContext();

        String assignmentsJson = "{}";

        String basicUrl = BuildConfig.BASE_URL + "/get_assignments.json";
        String url = YellrUtils.buildUrl(context, basicUrl);
        if (url != null) {

            try {

                Log.d("CheckAssignmentsIntentService.onHandleIntent()", "Checking for new assignments ( url = '" + url + "' )");
                assignmentsJson = YellrUtils.downloadJson(context, url);
                //Log.d("CheckAssignmentsIntentService.onHandleIntent()", "Assignments JSON: " +assignmentsJson);
                Assignment[] assignments = YellrUtils.decodeAssignmentJson(context, assignmentsJson);
                String[] currentAssignmentIds = YellrUtils.getCurrentAssignmentIds(context);
                for (int i = 0; i < assignments.length; i++) {
                    if (!Arrays.asList(currentAssignmentIds).contains(String.valueOf(assignments[i].assignment_id))) {
                        YellrUtils.buildNewAssignmentNotification(context, assignments[i]);
                    }
                }
                YellrUtils.setCurrentAssignmentIds(context, assignments);
            } catch( Exception ex) {
                Log.d("CheckHttpReceiver.onReceive()", "ERROR: " + ex.toString());
            }
        }

    }
}
