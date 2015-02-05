package yellr.net.yellr_android.intent_services.notifications;

/**
 * Created by TDuffy on 2/4/2015.
 */
public class Notification {

    public int notification_id;

    // post_successful, post_viewed, post_used, new_message, message_sent
    public String notification_type;

    public String notification_datetime;

    // different contents based on notification_type
    public NotificationPayload payload;

}
