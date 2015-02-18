package yellr.net.yellr_android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Created by TDuffy on 2/7/2015.
 */
public class YellrUtils {

    public static Date PrettifyDateTime(String rawDateTime) {

        Date date = new Date();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
            date = dateFormat.parse(rawDateTime);
        } catch (Exception e) {
            // todo: report
        }
        return date;
    }

    public static String calcTimeBetween(Date start, Date end) {
        int SECOND = 1000;
        int MINUTE = SECOND * 60;
        int HOUR = MINUTE * 60;
        int DAY = HOUR * 24;
        int milliSeconds = Math.round(end.getTime() - start.getTime());
        if (milliSeconds > DAY) {
            return String.format("%d days", Math.round(milliSeconds / DAY));
        }
        if (milliSeconds < DAY && milliSeconds > HOUR) {
            return String.format("%d hours", Math.round(milliSeconds / HOUR));
        }
        if (milliSeconds < HOUR && milliSeconds > 15 * MINUTE) {
            return String.format("%d minutes", Math.round(milliSeconds / MINUTE));
        }
        if (milliSeconds < 15 * MINUTE) {
            return "Moments";
        }
        //Throw an exception instead?
        return null;
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
        int count = Integer.parseInt(currentAssignmentIdsCountPref.getString("current_assignment_ids_count","0"));

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