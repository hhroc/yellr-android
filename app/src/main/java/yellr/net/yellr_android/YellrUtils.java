package yellr.net.yellr_android;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TDuffy on 2/7/2015.
 */
public class YellrUtils {

    public static String PrettifyDateTime(String rawDateTime){

        String cleanDateTime = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss.S");
            Date date = dateFormat.parse(rawDateTime);
            cleanDateTime = date.toString();
        } catch( Exception e) {
            // todo: report
        }
        return cleanDateTime;
    }


}
