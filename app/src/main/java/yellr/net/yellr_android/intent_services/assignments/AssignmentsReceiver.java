package yellr.net.yellr_android.intent_services.assignments;

/*

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;

import yellr.net.yellr_android.R;

public class AssignmentsReceiver extends BroadcastReceiver {
    public static final String ACTION_NEW_ASSIGNMENTS =
            "yellr.net.yellr_android.action.NEW_ASSIGNMENTS";

    public AssignmentsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("AssignmentsReceiver.onReceive()", "onReceive called.");

        String assignmentsJson = intent.getStringExtra(AssignmentsIntentService.PARAM_ASSIGNMENTS_JSON);

        Log.d("AssignmentsReceiver.onReceive()", "JSON: " + assignmentsJson);

        Gson gson = new Gson();
        AssignmentsResponse response = gson.fromJson(assignmentsJson, AssignmentsResponse.class);

        if ( response.success ) {

            // TODO: populate GUI with array of assignments

            //AssignmentsFragment assignmentsFragment = getFra
            ListView listView = (ListView) findViewById(R.id.list);

            for(int i=0; i<response.assignments.length; i++) {

                Assignment assignment = response.assignments[i];

                Log.d("AssignmentsReceiver.onReceive()", "Assignment: " + assignment.question_text);

                if( assignment.question_type_id == 1 ) {

                    // free-text question

                } else if ( assignment.question_type_id == 2 ){

                    // multi choice question

                }

            }

        }

        // TODO: update MainActivity with new assignments
    }
}

*/