package yellr.net.yellr_android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Created by TDuffy on 2/7/2015.
 */
public class YellrUtils {

    public static Date prettifyDateTime(String rawDateTime) {

        Date date = new Date();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = dateFormat.parse(rawDateTime);
        } catch (Exception e) {
            // todo: report
            Log.d("YellrUtils.PrettifyDateTime()", "Error: " + e.toString());
        }
        return date;
    }

    public static String calcTimeBetween(Date start, Date end) {

        int SECOND = 1000;
        int MINUTE = SECOND * 60;
        int HOUR = MINUTE * 60;
        int DAY = HOUR * 24;

        int milliSeconds = Math.round(end.getTime() - start.getTime());
        int t = 0;
        String retString = "";

        if (milliSeconds > DAY) {
            t = Math.round(milliSeconds / DAY);
            retString = String.format("%d day", t);
            if (t > 1) {
                retString += "s";
            }
            //return retString;
        } else if (milliSeconds < DAY && milliSeconds > HOUR) {
            t = Math.round(milliSeconds / HOUR);
            retString = String.format("%d hour", t);
            if (t > 1) {
                retString += "s";
            }
            //return retString;
        } else if (milliSeconds < HOUR && milliSeconds > 15 * MINUTE) {
            t = Math.round(milliSeconds / MINUTE);
            retString = String.format("%d minute", t);
            if (t > 1) {
                retString += "s";
            }
            //return retString;
        } else if (milliSeconds < 15 * MINUTE) {
            retString = "Moments";
        } else {
            //Throw an exception instead?
        }
        return retString;
    }

    public static String shortenString(String str) {
        String retString = str;
        if (str.length() > 20) {
            retString = str.substring(0, 20) + " ...";
        }
        return retString;
    }

    public static String getAppVersion(Context context) {
        String version = "Unknown";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (Exception ex) {
            Log.d("YellrUtils.GetAppVersion()", "Error: " + ex.toString());
        }
        return version;
    }

    public static String buildUrl(Context context, String baseUrl ) {

        String url = null;

        double latLng[] = YellrUtils.getLocation(context);
        if (latLng != null) {

            String languageCode = Locale.getDefault().getLanguage();

            url = baseUrl
                + "?cuid=" + YellrUtils.getCUID(context)
                + "&language_code=" + languageCode
                + "&lat=" + latLng[0]
                + "&lng=" + latLng[1]
                + "&platform=" + "Android"
                + "&app_version=" + YellrUtils.getAppVersion(context);
        }

        return url;
    }

    public static double[] getLocation(Context context) {

        /*
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String bestProvider = lm.getBestProvider(criteria, true);
        Location location = lm.getLastKnownLocation(bestProvider); //LocationManager.GPS_PROVIDER);
        */

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        List<String> providers = lm.getAllProviders();
        //providers.add(LocationManager.NETWORK_PROVIDER);
        Location location = null;
        Log.d("YellrUtils.getLocation()", "providers: " + providers.toString());
        try {
            for (int i = 0; i < providers.size(); i++) {
                Location l = lm.getLastKnownLocation(providers.get(i));
                if (null != l) {
                    location = l;
                    break;
                }
            }
        }catch (Exception ex) {
            Log.d("YellrUtils.getLocation()","Error: " + ex.toString());
        }

        // default to invalid lat/lng values
        double latitude = 1000; //43.1656;
        double longitude = 1000; //-77.6114;
        double[] latLng = null;
        // if we have a location available, then set it
        if (null != location) {

            latitude = location.getLatitude();
            longitude = location.getLongitude();

            latLng =  new double[2];
            latLng[0] = latitude;
            latLng[1] = longitude;
        }
        //} else {
        //    Log.d("YellrUtils.getLocation()", "No location available, defaulting to Home Location");
        //    Float[] latLng = YellrUtils.getHomeLocation(context);
        //    latitude = latLng[0];
        //    longitude = latLng[1];
        //}

        /*
        LocationDetector myloc = new LocationDetector(
                context);
        //double myLat = 0;
        //double myLong = 0;
        double latitude = 43.1656;
        double longitude = -77.6114;
        if (myloc.canGetLocation) {

            latitude = myloc.getLatitude();
            longitude = myloc.getLongitude();

            Log.v("get location values", Double.toString(latitude) + ", " + Double.toString(longitude));
        }
        */




        return latLng;
    }

    public static void resetHomeLocation(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("homeLocationSet", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("homeLocationSet", String.valueOf(false));
        editor.commit();
    }

    public static boolean isHomeLocationSet(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("homeLocationSet", Context.MODE_PRIVATE);
        String homeLocationSetString = sharedPref.getString("homeLocationSet", "false");
        return Boolean.valueOf(homeLocationSetString);
    }

    public static void setHomeLocation(Context context, String zipcode, String city, String stateCode, Float lat, Float lng) {
        SharedPreferences sharedPref = context.getSharedPreferences("homeLocationSet", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("zipcode", zipcode);
        editor.putString("city", city);
        editor.putString("stateCode", stateCode);
        editor.putString("lat", String.valueOf(lat));
        editor.putString("lng", String.valueOf(lng));
        editor.putString("homeLocationSet", String.valueOf(true));
        editor.commit();
        //String homeLocationSetString = sharedPref.getString("homeLocationSet", "false");
        //return Boolean.valueOf(homeLocationSetString);
    }

    public static Float[] getHomeLocation(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("homeLocationSet", Context.MODE_PRIVATE);
        Float lat = Float.valueOf(sharedPref.getString("lat", "0"));
        Float lng = Float.valueOf(sharedPref.getString("lng", "0"));
        Float[] latLng = new Float[2];
        latLng[0] = lat;
        latLng[1] = lng;
        return latLng;
    }

    public static String getCUID(Context context) {
        String cuid;
        SharedPreferences sharedPref = context.getSharedPreferences("cuid", Context.MODE_PRIVATE);
        cuid = sharedPref.getString("cuid", "");

        // check to see if there is a cuid on the device, if not created one
        if (cuid.equals("")) {

            cuid = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("cuid", cuid);
            editor.commit();
        }
        return cuid;
    }

    public static void resetCUID(Context context) {
        String cuid;
        SharedPreferences sharedPref = context.getSharedPreferences("cuid", Context.MODE_PRIVATE);

        cuid = UUID.randomUUID().toString();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("cuid", cuid);
        editor.commit();
    }

    public static String getPreviewImageName(String filename) {

        //String[] filenameParts = filename.split(".");

        StringTokenizer st = new StringTokenizer(filename, ".");
        String baseFilename = st.nextToken();
        String fileExtention = st.nextToken();

        Log.d("YellrUtils.getPreviewImageName()","baseFilename: " + baseFilename);
        Log.d("YellrUtils.getPreviewImageName()","fileExtention: " + fileExtention);

        String previewFileName = baseFilename + "p." + fileExtention;

        return previewFileName;
    }

    //
    // This function modified from here:
    //    http://android-developers.blogspot.com/2010/07/multithreading-for-performance.html
    ///
    public static Bitmap downloadBitmap(String url) {
        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);

        Bitmap bitmap = null;

        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url);
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    //final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    //return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or IllegalStateException
            getRequest.abort();
            Log.d("YellrUtils.downloadBitmap()", "Error while retrieving bitmap from " + url + ": " + e.toString());
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return bitmap;
    }

    public static void setCurrentAssignmentIds(Context context, String[] currentAssignmentIds) {
        SharedPreferences currentAssignmentIdsCountPref = context.getSharedPreferences("current_assignment_ids_count", Context.MODE_PRIVATE);
        SharedPreferences currentAssignmentIdsPref = context.getSharedPreferences("current_assignment_ids", Context.MODE_PRIVATE);

        StringBuilder currentAssignmentIdsCsv = new StringBuilder();
        for (int i = 0; i < currentAssignmentIds.length; i++) {
            currentAssignmentIdsCsv.append(currentAssignmentIds[i]).append(",");
        }

        SharedPreferences.Editor countEditor = currentAssignmentIdsCountPref.edit();
        countEditor.putString("current_assignment_ids_count", String.valueOf(currentAssignmentIds.length));
        countEditor.commit();

        SharedPreferences.Editor idsEditor = currentAssignmentIdsPref.edit();
        idsEditor.putString("current_assignment_ids", currentAssignmentIdsCsv.toString());
        idsEditor.commit();
    }

    public static String[] getCurrentAssignmentIds(Context context) {
        SharedPreferences currentAssignmentIdsCountPref = context.getSharedPreferences("current_assignment_ids_count", Context.MODE_PRIVATE);
        SharedPreferences currentAssignmentIdsPref = context.getSharedPreferences("current_assignment_ids", Context.MODE_PRIVATE);

        String currentAssignmentIdsCsv = currentAssignmentIdsPref.getString("current_assignment_ids", "");
        int count = Integer.parseInt(currentAssignmentIdsCountPref.getString("current_assignment_ids_count", "0"));

        StringTokenizer st = new StringTokenizer(currentAssignmentIdsCsv, ",");
        String[] currentAssignmentIds = new String[count];
        for (int i = 0; i < count; i++) {
            currentAssignmentIds[i] = st.nextToken();
        }
        return currentAssignmentIds;
    }

    public static void setCurrentStoryIds(Context context, String[] currentStoryIds) {
        SharedPreferences currentStoryIdsCountPref = context.getSharedPreferences("current_story_ids_count", Context.MODE_PRIVATE);
        SharedPreferences currentStoryIdsPref = context.getSharedPreferences("current_story_ids", Context.MODE_PRIVATE);

        StringBuilder currentStoryIdsCsv = new StringBuilder();
        for (int i = 0; i < currentStoryIds.length; i++) {
            currentStoryIdsCsv.append(currentStoryIds[i]).append(",");
        }

        SharedPreferences.Editor countEditor = currentStoryIdsCountPref.edit();
        countEditor.putString("current_story_ids_count", String.valueOf(currentStoryIds.length));
        countEditor.commit();

        SharedPreferences.Editor idsEditor = currentStoryIdsPref.edit();
        idsEditor.putString("current_story_ids", currentStoryIdsCsv.toString());
        idsEditor.commit();
    }

    public static String[] getCurrentStoryIds(Context context) {
        SharedPreferences currentStoryIdsCountPref = context.getSharedPreferences("current_story_ids_count", Context.MODE_PRIVATE);
        SharedPreferences currentStoryIdsPref = context.getSharedPreferences("current_story_ids", Context.MODE_PRIVATE);

        String currentStoryIdsCsv = currentStoryIdsPref.getString("current_story_ids", "");
        int count = Integer.parseInt(currentStoryIdsCountPref.getString("current_story_ids_count","0"));

        StringTokenizer st = new StringTokenizer(currentStoryIdsCsv, ",");
        String[] currentStoryIds = new String[count];
        for (int i = 0; i < count; i++) {
            currentStoryIds[i] = st.nextToken();
        }
        return currentStoryIds;
    }

    public static void setCurrentNotificationIds(Context context, String[] currentNotificationIds) {
        SharedPreferences currentNotificationIdsCountPref = context.getSharedPreferences("current_notification_ids_count", Context.MODE_PRIVATE);
        SharedPreferences currentNotificationIdsPref = context.getSharedPreferences("current_notification_ids", Context.MODE_PRIVATE);

        StringBuilder currentNotificationIdsCsv = new StringBuilder();
        for (int i = 0; i < currentNotificationIds.length; i++) {
            currentNotificationIdsCsv.append(currentNotificationIds[i]).append(",");
        }

        SharedPreferences.Editor countEditor = currentNotificationIdsCountPref.edit();
        countEditor.putString("current_notification_ids_count", String.valueOf(currentNotificationIds.length));
        countEditor.commit();

        SharedPreferences.Editor idsEditor = currentNotificationIdsPref.edit();
        idsEditor.putString("current_notification_ids", currentNotificationIdsCsv.toString());
        idsEditor.commit();
    }

    public static String[] getCurrentNotificationIds(Context context) {
        SharedPreferences currentNotificationIdsCountPref = context.getSharedPreferences("current_notification_ids_count", Context.MODE_PRIVATE);
        SharedPreferences currentNotificationIdsPref = context.getSharedPreferences("current_notification_ids", Context.MODE_PRIVATE);

        String currentNotificationIdsCsv = currentNotificationIdsPref.getString("current_notification_ids", "");
        int count = Integer.parseInt(currentNotificationIdsCountPref.getString("current_notification_ids_count","0"));

        StringTokenizer st = new StringTokenizer(currentNotificationIdsCsv, ",");
        String[] currentNotificationIds = new String[count];
        for (int i = 0; i < count; i++) {
            currentNotificationIds[i] = st.nextToken();
        }
        return currentNotificationIds;
    }

    public static void setCurrentMessageIds(Context context, String[] currentMessageIds) {
        SharedPreferences currentMessageIdsCountPref = context.getSharedPreferences("current_message_ids_count", Context.MODE_PRIVATE);
        SharedPreferences currentMessageIdsPref = context.getSharedPreferences("current_message_ids", Context.MODE_PRIVATE);

        StringBuilder currentMessageIdsCsv = new StringBuilder();
        for (int i = 0; i < currentMessageIds.length; i++) {
            currentMessageIdsCsv.append(currentMessageIds[i]).append(",");
        }

        SharedPreferences.Editor countEditor = currentMessageIdsCountPref.edit();
        countEditor.putString("current_message_ids_count", String.valueOf(currentMessageIds.length));
        countEditor.commit();

        SharedPreferences.Editor idsEditor = currentMessageIdsPref.edit();
        idsEditor.putString("current_message_ids", currentMessageIdsCsv.toString());
        idsEditor.commit();
    }

    public static String[] getCurrentMessageIds(Context context) {
        SharedPreferences currentMessageIdsCountPref = context.getSharedPreferences("current_message_ids_count", Context.MODE_PRIVATE);
        SharedPreferences currentMessageIdsPref = context.getSharedPreferences("current_message_ids", Context.MODE_PRIVATE);

        String currentMessageIdsCsv = currentMessageIdsPref.getString("current_message_ids", "");
        int count = Integer.parseInt(currentMessageIdsCountPref.getString("current_message_ids_count","0"));

        StringTokenizer st = new StringTokenizer(currentMessageIdsCsv, ",");
        String[] currentMessageIds = new String[count];
        for (int i = 0; i < count; i++) {
            currentMessageIds[i] = st.nextToken();
        }
        return currentMessageIds;
    }
}