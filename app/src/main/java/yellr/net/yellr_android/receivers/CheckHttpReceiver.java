package yellr.net.yellr_android.receivers;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Arrays;

import yellr.net.yellr_android.BuildConfig;
import yellr.net.yellr_android.intent_services.assignments.Assignment;
import yellr.net.yellr_android.intent_services.assignments.AssignmentsIntentService;
import yellr.net.yellr_android.intent_services.assignments.CheckAssignmentsIntentService;
import yellr.net.yellr_android.intent_services.stories.StoriesIntentService;
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * Created by TDuffy on 2/18/2015.
 */
public class CheckHttpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        try{

            // Check Assignments Intent Service
            Log.d("CheckHttpReceiver.onReceive()","Launching CheckAssignmentsIntentService ...");
            Intent checkAssignmentsIntent = new Intent(context, CheckAssignmentsIntentService.class);
            checkAssignmentsIntent.setAction(CheckAssignmentsIntentService.ACTION_CHECK_ASSIGNMENTS);
            context.startService(checkAssignmentsIntent);

            /*

            // Notifications Intent Service
            Intent notificationsWebIntent = new Intent(context, NotificationsIntentService.class);
            notificationsWebIntent.putExtra(NotificationsIntentService.PARAM_CUID, cuid);
            notificationsWebIntent.setAction(NotificationsIntentService.ACTION_GET_NOTIFICATIONS);
            context.startService(notificationsWebIntent);


            // Stories Intent Service
            //Intent storiesWebIntent = new Intent(context, StoriesIntentService.class);
            //storiesWebIntent.putExtra(StoriesIntentService.PARAM_CUID, cuid);
            //storiesWebIntent.setAction(StoriesIntentService.ACTION_GET_STORIES);
            //context.startService(storiesWebIntent);


            // Profile Intent Service
            Intent profileWebIntent = new Intent(context, ProfileIntentService.class);
            profileWebIntent.putExtra(ProfileIntentService.PARAM_CUID, cuid);
            profileWebIntent.setAction(ProfileIntentService.ACTION_GET_PROFILE);
            context.startService(profileWebIntent);



            // Data Intent Service
            Intent dataWebIntent = new Intent(context, DataIntentService.class);
            //dataWebIntent.putExtra(DataIntentService.PARAM_CUID, cuid);
            dataWebIntent.setAction(DataIntentService.ACTION_GET_DATA);
            context.startService(dataWebIntent);

            */

            //also call the same runnable
            //checkNewDataHandler.postDelayed(this, CHECK_FOR_NEW_DATA_INTERVAL);

        }
        catch (Exception ex) {
            Log.d("CheckHttpReceiver.onReceive()","ERROR: " + ex.toString());
        }

    }

}
