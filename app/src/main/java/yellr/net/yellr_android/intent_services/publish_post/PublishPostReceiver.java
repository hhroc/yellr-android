package yellr.net.yellr_android.intent_services.publish_post;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

public class PublishPostReceiver extends BroadcastReceiver {
    public static final String ACTION_NEW_PUBLISH_POST =
            "yellr.net.yellr_android.action.NEW_PUBLISH_POST";

    public PublishPostReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("PublishPostReceiver.onReceive()", "onReceive called.");

        String publishPostJson = intent.getStringExtra(PublishPostIntentService.PARAM_PUBLISH_POST_JSON);

        Log.d("PublishPostReceiver.onReceive()", "JSON: " + publishPostJson);

        Gson gson = new Gson();
        PublishPostResponse response = gson.fromJson(publishPostJson, PublishPostResponse.class);

        if ( response.success ) {

            Log.d("PublishPostReceiver.onReceive()", "Post ID: " + response.post_id);

        }

        // TODO: update MainActivity with new PublishPost
    }
}
