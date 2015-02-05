package yellr.net.yellr_android.intent_services.notifications;

/**
 * Created by TDuffy on 2/4/2015.
 */
public class NotificationPayload {

    // post_successful, post_viewed, post_used,
    public int post_id = 0;
    public String post_title = "";

    // post_viewed, post_used, new_message
    public String organization = "";

    // post_used
    public int story_id;
    public String story_title;

    // new_message, message_sent
    public String message_subject = "";
    public int message_id = 0;

}
