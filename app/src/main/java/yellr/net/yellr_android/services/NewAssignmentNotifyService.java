package yellr.net.yellr_android.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

import yellr.net.yellr_android.BuildConfig;
import yellr.net.yellr_android.intent_services.assignments.Assignment;
import yellr.net.yellr_android.utils.YellrUtils;

public class NewAssignmentNotifyService extends Service {

    Handler mHandler;
    static final int INTERVAL_DURATION_OF_POLLING = 6*60*60*1000;  // 6*60*60*1000 - 6 hours - 1000 milliseconds

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreate() {

        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mHandler = new Handler();
        Runnable r = new Runnable() {
            //@override
            public void run() {
                doPoll();
                mHandler.postDelayed(this, INTERVAL_DURATION_OF_POLLING);
            }
        };
        mHandler.postDelayed(r, INTERVAL_DURATION_OF_POLLING);
    }

    public void doPoll(){
        //Toast t = Toast.makeText(this, "Service is still running", Toast.LENGTH_SHORT);
        //t.show();

        Context context = getApplicationContext();

        String assignmentsJson = "{}";

        String basicUrl = BuildConfig.BASE_URL + "/get_assignments.json";
        String url = YellrUtils.buildUrl(context, basicUrl);

        if (url != null) {

            try {
                Log.d("NewAssignmentNotifyService.doPoll()", "Checking for new assignments ( url = '" + url + "' )");
                assignmentsJson = YellrUtils.downloadJson(context, url);
                Log.d("NewAssignmentNotifyService.doPoll()", "Assignments JSON: " +assignmentsJson);
                Assignment[] assignments = YellrUtils.decodeAssignmentJson(context, assignmentsJson);
                String[] currentAssignmentIds = YellrUtils.getCurrentAssignmentIds(context);
                for (int i = 0; i < assignments.length; i++) {
                    if (!Arrays.asList(currentAssignmentIds).contains(String.valueOf(assignments[i].assignment_id))) {
                        YellrUtils.buildNewAssignmentNotification(context, assignments[i]);
                        Log.d("NewAssignmentNotifyService.doPoll()","NewAssignment()");
                    }
                }
                YellrUtils.setCurrentAssignmentIds(context, assignments);
            } catch( Exception ex) {
                Log.d("NewAssignmentNotifyService.doPoll()", "ERROR: " + ex.toString());
            }
        }

    }

}