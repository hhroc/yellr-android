package yellr.net.yellr_android.intent_services.data;

import yellr.net.yellr_android.intent_services.assignments.Assignment;
import yellr.net.yellr_android.intent_services.messages.Message;
import yellr.net.yellr_android.intent_services.notifications.Notification;
import yellr.net.yellr_android.intent_services.stories.Story;

/**
 * Created by TDuffy on 2/21/2015.
 */
public class DataResponse {

    Assignment[] assignments;
    Story[] stories;
    Notification[] notifications;
    Message[] messages;

    public boolean success;

}
