package yellr.net.yellr_android.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Arrays;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.activities.ViewStoryActivity;
import yellr.net.yellr_android.fragments.ViewStoryFragment;
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
        StoriesResponse response = new StoriesResponse();
        try{
            response = gson.fromJson(storiesJson, StoriesResponse.class);
        } catch (Exception e){
            Log.d("CheckHttpStoriesReceiver.onReceive", "GSON puked");
        }

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

                Intent storyIntent;
                storyIntent = new Intent(context, ViewStoryActivity.class);
                storyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if(response.stories.length >0) {
                    int sIndex = 0;
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.icon)
                                    .setContentTitle(response.stories[sIndex].title)
                                    .setContentText(response.stories[sIndex].contents);

                    storyIntent.putExtra(ViewStoryFragment.ARG_STORY_TITLE, response.stories[sIndex].title);
                    storyIntent.putExtra(ViewStoryFragment.ARG_STORY_AUTHOR, response.stories[sIndex].author_first_name + " " + response.stories[sIndex].author_last_name);
                    storyIntent.putExtra(ViewStoryFragment.ARG_STORY_PUBLISHED_DATETIME, response.stories[sIndex].publish_datetime);
                    storyIntent.putExtra(ViewStoryFragment.ARG_STORY_BANNER_MEDIA_FILE_NAME, response.stories[sIndex].banner_media_file_name);
                    storyIntent.putExtra(ViewStoryFragment.ARG_STORY_TOP_TEXT, response.stories[sIndex].top_text);
                    //intent.putExtra(ViewStoryFragment.ARG_STORY_CONTENTS,response.stories[position].contents);
                    storyIntent.putExtra(ViewStoryFragment.ARG_STORY_CONTENTS_RENDERED, response.stories[sIndex].contents_rendered);

                    PendingIntent pendingStoryIntent = PendingIntent.getActivity(context, 0, storyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    mBuilder.setContentIntent(pendingStoryIntent);
                    mBuilder.setAutoCancel(true); // closes notification after click
                    int storyNotificationId = 1;
                    NotificationManager mNotificationMgr =
                            (NotificationManager)  context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationMgr.notify(storyNotificationId, mBuilder .build());
                }


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
