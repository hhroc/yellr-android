package yellr.net.yellr_android.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Arrays;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.activities.PostActivity;
import yellr.net.yellr_android.fragments.PostFragment;
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

        Log.d("CheckHttpAssignmentsReceiver.onReceiver()","Assignments JSON: " + assignmentsJson);

        Gson gson = new Gson();
        AssignmentsResponse response = new AssignmentsResponse();
        try{
            response = gson.fromJson(assignmentsJson, AssignmentsResponse.class);
        } catch (Exception e){
            Log.d("CheckHttpAssignmentsReceiver.onReceive", "GSON puked");
        }
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
                Intent assignmentIntent;
                assignmentIntent = new Intent(context, PostActivity.class);
                assignmentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if(response.assignments.length >0) {
                    int aIndex = 0;
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.icon)
                                    .setContentTitle(response.assignments[aIndex].question_text)
                                    .setContentText(response.assignments[aIndex].description);

                    assignmentIntent.putExtra(PostFragment.ARG_ASSIGNMENT_QUESTION, response.assignments[aIndex].question_text);
                    assignmentIntent.putExtra(PostFragment.ARG_ASSIGNMENT_DESCRIPTION, response.assignments[aIndex].description);
                    assignmentIntent.putExtra(PostFragment.ARG_ASSIGNMENT_ID, response.assignments[aIndex].assignment_id);

                    PendingIntent pendingAssignmentIntent = PendingIntent.getActivity(context, 0, assignmentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    mBuilder.setContentIntent(pendingAssignmentIntent);
                    mBuilder.setAutoCancel(true); // clear notification after click
                    int assignmentNotificationId = 2;
                    NotificationManager mNotificationMgr =
                            (NotificationManager)  context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationMgr.notify(assignmentNotificationId, mBuilder .build());
                }
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
