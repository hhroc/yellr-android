package yellr.net.yellr_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

public class StoriesReceiver extends BroadcastReceiver {
    public static final String ACTION_NEW_STORIES =
            "yellr.net.yellr_android.action.NEW_STORIES";

    public StoriesReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("StoriesReceiver.onReceive()", "onReceive called.");

        String Stories_json = intent.getStringExtra(StoriesIntentService.PARAM_STORIES_JSON);

        Log.d("StoriesReceiver.onReceive()", "JSON: " + Stories_json);

        Gson gson = new Gson();
        StoriesResponse response = gson.fromJson(Stories_json, StoriesResponse.class);

        if ( response.success ) {

            // TODO: populate GUI with array of Stories

            for(int i=0; i<response.stories.length; i++) {

                Story story = response.stories[i];

                Log.d("StoriesReceiver.onReceive()", "Assignment: " + story.title);

            }

        }

        // TODO: update MainActivity with new Stories
    }
}
