package yellr.net.yellr_android.intent_services.messages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

public class MessagesReceiver extends BroadcastReceiver {
    public static final String ACTION_NEW_MESSAGES =
            "yellr.net.yellr_android.action.NEW_MESSAGES";

    public MessagesReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //Log.d("MessagesReceiver.onReceive()", "onReceive called.");

        String messagesJson = intent.getStringExtra(MessagesIntentService.PARAM_MESSAGES_JSON);

        //Log.d("MessagesReceiver.onReceive()", "JSON: " + messagesJson);

        Gson gson = new Gson();
        MessagesResponse response = gson.fromJson(messagesJson, MessagesResponse.class);

        if ( response.success ) {

            // TODO: populate GUI with array of messages

            for(int i=0; i<response.messages.length; i++) {

                Message message = response.messages[i];

                //Log.d("MessagesReceiver.onReceive()", "Message Subject: " + message.message_subject);

            }

        }

    }
}
