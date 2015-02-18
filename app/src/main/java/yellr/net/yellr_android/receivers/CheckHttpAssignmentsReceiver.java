package yellr.net.yellr_android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Arrays;

import yellr.net.yellr_android.intent_services.assignments.AssignmentsIntentService;
import yellr.net.yellr_android.intent_services.assignments.AssignmentsResponse;
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * Created by TDuffy on 2/18/2015.
 */
public class CheckHttpAssignmentsReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("CheckHttpAssignmentsReceiver.onReceive()","Handling new assignments ...");

        String assignmentsJson = intent.getStringExtra(AssignmentsIntentService.PARAM_ASSIGNMENTS_JSON);
        Gson gson = new Gson();
        AssignmentsResponse response = gson.fromJson(assignmentsJson, AssignmentsResponse.class);

        boolean newAssignments = false;
        if (response.success) {

            String[] currentAssignmentIds = YellrUtils.getCurrentAssignmentIds(context);

            //for(int i = 0; i< currentAssignmentIds.length; i++) {
            //    Log.d("CheckHttpAssignmentsReceiver.onReceive()","currentAssigmentIds[" + String.valueOf(i) + "]: " + currentAssignmentIds[i]);
            //}

            for (int i = 0; i < response.assignments.length; i++) {
                //Log.d("CheckHttpAssignmentsReceiver.onReceive()","assignment_id: " + String.valueOf(response.assignments[i].assignment_id));
                if (!Arrays.asList(currentAssignmentIds).contains(String.valueOf(response.assignments[i].assignment_id))) {
                    newAssignments = true;
                    break;
                }
            }

            if (newAssignments) {
                // TODO: create android notification that there is a new assignment
                Log.d("CheckHttpAssignmentsReceiver.onReceive()", "New Assignments!");
            }

            // update current Ids list
            currentAssignmentIds = new String[response.assignments.length];
            for(int i = 0; i < response.assignments.length; i++) {
                currentAssignmentIds[i] = String.valueOf(response.assignments[i].assignment_id);
            }
            YellrUtils.setCurrentAssignmentIds(context, currentAssignmentIds);
        }
        else {
            Log.d("CheckHttpAssignmentsReceiver.onReceive()","ERROR: Success was false");
        }
    }
}
