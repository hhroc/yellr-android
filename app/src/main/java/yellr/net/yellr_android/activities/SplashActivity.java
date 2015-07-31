package yellr.net.yellr_android.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.KeyEvent;

import yellr.net.yellr_android.BuildConfig;
import yellr.net.yellr_android.R;
import yellr.net.yellr_android.intent_services.assignments.AssignmentsIntentService;
import yellr.net.yellr_android.intent_services.stories.StoriesIntentService;
import yellr.net.yellr_android.receivers.CheckHttpAssignmentsReceiver;
import yellr.net.yellr_android.receivers.CheckHttpReceiver;
import yellr.net.yellr_android.services.NewAssignmentNotifyService;
import yellr.net.yellr_android.receivers.CheckHttpStoriesReceiver;
import yellr.net.yellr_android.utils.YellrUtils;

public class SplashActivity extends Activity{

    static int CHECK_FOR_NEW_DATA_INTERVAL = BuildConfig.UPDATE_RATE * 1000; // - testing, every 10 seconds    //5 * 60 * 1000; // Check every 5 minutes ( 288 times a day  )

    private PendingIntent checkHttpPendingIntent;
    private AlarmManager checkHttpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        // TODO: hook up reciever for messages

        // TODO: hook up reciever for notifications

        // create alarm intent
        Intent alarmIntent = new Intent(this, CheckHttpReceiver.class);
        checkHttpPendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        // start alarm manager
        checkHttpManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        checkHttpManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), CHECK_FOR_NEW_DATA_INTERVAL, checkHttpPendingIntent);

        //start the Yellr Assignment Fetch Service
        startService(new Intent(getBaseContext(), NewAssignmentNotifyService.class));

        /*
        // setup receiver for assignments
        IntentFilter checkHttpAssignmentsFilter = new IntentFilter(AssignmentsIntentService.ACTION_NEW_ASSIGNMENTS);
        checkHttpAssignmentsFilter.addCategory(Intent.CATEGORY_DEFAULT);
        CheckHttpAssignmentsReceiver checkHttpAssignmentsReceiver = new CheckHttpAssignmentsReceiver();
        getApplicationContext().registerReceiver(checkHttpAssignmentsReceiver, checkHttpAssignmentsFilter);

        // set up receiver for stories
        IntentFilter checkHttpStoriesFilter = new IntentFilter(StoriesIntentService.ACTION_NEW_STORIES);
        checkHttpStoriesFilter.addCategory(Intent.CATEGORY_DEFAULT);
        CheckHttpStoriesReceiver checkHttpStoriesReceiver = new CheckHttpStoriesReceiver();
        getApplicationContext().registerReceiver(checkHttpStoriesReceiver, checkHttpStoriesFilter);
        */
    }

    @Override
    protected void onResume() {
        super.onResume();

        YellrUtils.resetHomeLocation(getApplicationContext());

        /*
        if (YellrUtils.isHomeLocationSet(getApplicationContext())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent;
                    intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }, 500);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent;
                    intent = new Intent(getApplicationContext(), LocationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }, 500);
        }
        */

        //while(YellrUtils.getLocation(getApplicationContext()) == null) {

        //}
        if (YellrUtils.getLocation(getApplicationContext()) == null) {
            try {
                showSettingsAlert();
            } catch(Exception ex) {

            }

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent;
                    intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }, 500);
        }

    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Yikes!");

        // Setting Dialog Message
        alertDialog.setMessage("Looks like Location Services isn't turned on.\n\nYellr requires a location to work correctly.  Please enable location services to continue.");

        // On pressing Settings button
        alertDialog.setPositiveButton(
                "Location Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        finish();
                    }
                });

        alertDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                }
                return false;
            }
        });

        alertDialog.show();
    }
}
