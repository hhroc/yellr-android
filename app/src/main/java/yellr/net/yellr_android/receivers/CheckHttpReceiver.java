package yellr.net.yellr_android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import yellr.net.yellr_android.intent_services.assignments.AssignmentsIntentService;
import yellr.net.yellr_android.intent_services.notifications.NotificationsIntentService;
import yellr.net.yellr_android.intent_services.profile.ProfileIntentService;
import yellr.net.yellr_android.intent_services.stories.StoriesIntentService;
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * Created by TDuffy on 2/18/2015.
 */
public class CheckHttpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //try{

            Log.d("CheckHttpReceiver.onReceive()", "Calling all intent services to update HTTP data ...");

            String cuid = YellrUtils.getCUID(context);

            //Context context = getApplicationContext();

            // Assignments Intent Service
            Intent assignmentsWebIntent = new Intent(context, AssignmentsIntentService.class);
            assignmentsWebIntent.putExtra(AssignmentsIntentService.PARAM_CUID, cuid);
            assignmentsWebIntent.setAction(AssignmentsIntentService.ACTION_GET_ASSIGNMENTS);
            context.startService(assignmentsWebIntent);


            // Notifications Intent Service
            Intent notificationsWebIntent = new Intent(context, NotificationsIntentService.class);
            notificationsWebIntent.putExtra(NotificationsIntentService.PARAM_CUID, cuid);
            notificationsWebIntent.setAction(NotificationsIntentService.ACTION_GET_NOTIFICATIONS);
            context.startService(notificationsWebIntent);

            // Stories Intent Service
            Intent storiesWebIntent = new Intent(context, StoriesIntentService.class);
            storiesWebIntent.putExtra(StoriesIntentService.PARAM_CUID, cuid);
            storiesWebIntent.setAction(StoriesIntentService.ACTION_GET_STORIES);
            context.startService(storiesWebIntent);

            // Profile Intent Service
            Intent profileWebIntent = new Intent(context, ProfileIntentService.class);
            profileWebIntent.putExtra(ProfileIntentService.PARAM_CUID, cuid);
            profileWebIntent.setAction(ProfileIntentService.ACTION_GET_PROFILE);
            context.startService(profileWebIntent);

            //also call the same runnable
            //checkNewDataHandler.postDelayed(this, CHECK_FOR_NEW_DATA_INTERVAL);
        //}
        //catch (Exception e) {

        //    Log.d("CheckHttpReceiver.onReceive()","ERROR: " + e.toString());

            // TODO: handle exception
        //}
        //finally{

        //    Log.d("CheckHttpReceiver.onReceive()","re-launching postDelayed");

            //also call the same runnable
            //checkNewDataHandler.postDelayed(this, CHECK_FOR_NEW_DATA_INTERVAL);
        //}
    }


}
