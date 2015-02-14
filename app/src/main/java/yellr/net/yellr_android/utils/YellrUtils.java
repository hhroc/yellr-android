package yellr.net.yellr_android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by TDuffy on 2/7/2015.
 */
public class YellrUtils {

    public static Date PrettifyDateTime(String rawDateTime){

        Date date = new Date();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
            date = dateFormat.parse(rawDateTime);
        } catch( Exception e) {
            // todo: report
        }
        return date;
    }

    public static String calcTimeBetween(Date start, Date end){
        int SECOND = 1000;
        int MINUTE = SECOND * 60;
        int HOUR = MINUTE * 60;
        int DAY = HOUR * 24;
        int milliSeconds = Math.round(end.getTime() - start.getTime());
        if(milliSeconds > DAY){
            return String.format("%d days", Math.round(milliSeconds / DAY));
        }
        if(milliSeconds < DAY && milliSeconds > HOUR){
            return String.format("%d hours", Math.round(milliSeconds / HOUR));
        }
        if(milliSeconds < HOUR  && milliSeconds > 15 * MINUTE){
            return String.format("%d minutes", Math.round(milliSeconds / MINUTE));
        }
        if(milliSeconds < 15 * MINUTE){
            return "Moments";
        }
        //Throw an exception instead?
        return null;
    }

    public static String getUUID(Context context) {
        String clientId;
        SharedPreferences sharedPref = context.getSharedPreferences("clientId", Context.MODE_PRIVATE);
        clientId = sharedPref.getString("clientId", "");

        // check to see if there is a clientId on the device, if not created one
        if (clientId.equals("")){

            clientId = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("clientId", clientId);
            editor.commit();
        }
        return clientId;
    }

    public static void resetUUID(Context context){
        String clientId;
        SharedPreferences sharedPref = context.getSharedPreferences("clientId", Context.MODE_PRIVATE);

        clientId = UUID.randomUUID().toString();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("clientId", clientId);
        editor.commit();
    }
}
