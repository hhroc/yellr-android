/*
package yellr.net.yellr_android.intent_services.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

public class NotificationsReceiver extends BroadcastReceiver {
    public static final String ACTION_NEW_NOTIFICATIONS =
            "yellr.net.yellr_android.action.NEW_NOTIFICATIONS";

    public NotificationsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //Log.d("NotificationsReceiver.onReceive()", "onReceive called.");

        String notificationsJson = intent.getStringExtra(NotificationsIntentService.PARAM_NOTIFICATIONS_JSON);

        //Log.d("NotificationsReceiver.onReceive()", "JSON: " + notificationsJson);

        Gson gson = new Gson();
        NotificationsResponse response = gson.fromJson(notificationsJson, NotificationsResponse.class);

        if ( response.success ) {

            // TODO: populate GUI with array of notifications

            for(int i=0; i<response.notifications.length; i++) {

                Notification notification = response.notifications[i];

                //Log.d("NotificationsReceiver.onReceive()", "Notification: " + notification.notification_type);

                int postId = notification.payload.post_id;
                String postTitle = notification.payload.post_title;
                String organization = notification.payload.organization;
                int storyId = notification.payload.story_id;
                String storyTitle = notification.payload.story_title;
                int messageId = notification.payload.message_id;
                String messageSubject = notification.payload.message_subject;

                switch( notification.notification_type) {

                    case "post_successful":

                        // postId, postTitle

                        // TODO: report successful posting

                        break;
                    case "post_viewed":

                        // postId, postTitle, organization

                        // TODO: report post was viewed by an organization

                        break;
                    case "post_used":

                        // postId, postTitle, organization, storyId, storyTitle

                        // TODO: report post was used in a story

                        break;
                    case "new_message":

                        // organization, messageId, messageSubject

                        // TODO: report new message

                        break;
                    case "message_sent":

                        // organization, messageId, messageSubject

                        // TODO: report message sent successfully.

                        break;
                    default:
                        break;

                };

            }

        }

    }
}
*/