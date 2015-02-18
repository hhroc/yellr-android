package yellr.net.yellr_android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Arrays;

import yellr.net.yellr_android.intent_services.stories.StoriesIntentService;
import yellr.net.yellr_android.intent_services.stories.StoriesResponse;
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * Created by TDuffy on 2/18/2015.
 */
public class CheckHttpStoriesReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("CheckHttpStoriesReceiver.onReceive()", "Handling new stories ...");

        String storiesJson = intent.getStringExtra(StoriesIntentService.PARAM_STORIES_JSON);
        Gson gson = new Gson();
        StoriesResponse response = gson.fromJson(storiesJson, StoriesResponse.class);

        boolean newStories = false;
        if (response.success) {

            String[] currentStoryIds = YellrUtils.getCurrentStoryIds(context);

            //for(int i = 0; i< currentStoryIds.length; i++) {
                //Log.d("CheckHttpStoriesReceiver.onReceive()","currentStoryIds[" + String.valueOf(i) + "]: " + currentStoryIds[i]);
            //}

            for (int i = 0; i < response.stories.length; i++) {
                //Log.d("CheckHttpStoriesReceiver.onReceive()","story_id: " + String.valueOf(response.stories[i].story_unique_id));
                if (!Arrays.asList(currentStoryIds).contains(String.valueOf(response.stories[i].story_unique_id))) {
                    newStories = true;
                    break;
                }
            }

            if (newStories) {
                // TODO: create android notification that there is a new story
                Log.d("CheckHttpStoriesReceiver.onReceive()", "New Stories!");
            }

            // update current Ids list
            currentStoryIds = new String[response.stories.length];
            for(int i = 0; i < response.stories.length; i++) {
                currentStoryIds[i] = String.valueOf(response.stories[i].story_unique_id);
            }
            YellrUtils.setCurrentStoryIds(context, currentStoryIds);
        }
        else {
            Log.d("CheckHttpStoriesReceiver.onReceive()","ERROR: Success was false");
        }
    }
}
