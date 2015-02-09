package yellr.net.yellr_android.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        if(milliSeconds < HOUR){
            return String.format("%d minutes", Math.round(milliSeconds / MINUTE));
        }
        //Throw an exception instead?
        return null;
    }


}
