package yellr.net.yellr_android.intent_services.stories;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

public class StoriesReceiver extends BroadcastReceiver {
    public static final String ACTION_NEW_STORIES =
            "yellr.net.yellr_android.action.NEW_STORIES";

    public StoriesReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //Log.d("StoriesReceiver.onReceive()", "onReceive called.");

        String storiesJson = intent.getStringExtra(StoriesIntentService.PARAM_STORIES_JSON);

        //Log.d("StoriesReceiver.onReceive()", "JSON: " + storiesJson);

        Gson gson = new Gson();
        StoriesResponse response = gson.fromJson(storiesJson, StoriesResponse.class);

        if ( response.success ) {

            // TODO: populate GUI with array of Stories

            for(int i=0; i<response.stories.length; i++) {

                Story story = response.stories[i];

                //Log.d("StoriesReceiver.onReceive()", "Assignment: " + story.title);

            }

        }

    }
}

