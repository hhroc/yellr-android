package yellr.net.yellr_android.activities;

import java.util.UUID;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;

import com.google.gson.Gson;

import yellr.net.yellr_android.intent_services.assignments.AssignmentsReceiver;
import yellr.net.yellr_android.intent_services.assignments.AssignmentsIntentService;

import yellr.net.yellr_android.intent_services.publish_post.MediaObjectDefinition;
import yellr.net.yellr_android.intent_services.publish_post.PublishPostIntentService;
import yellr.net.yellr_android.intent_services.publish_post.PublishPostReceiver;
import yellr.net.yellr_android.intent_services.stories.StoriesReceiver;
import yellr.net.yellr_android.intent_services.stories.StoriesIntentService;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        // Create Client ID
        //
        String clientId = UUID.randomUUID().toString();

        ////////////////////////////////////////////
        //
        // ASSIGNMENTS

        Log.d("HomeActivity.onCreate()","Setting up assignments intent service ...");

        // init new assignments receiver
        IntentFilter assignmentsFilter = new IntentFilter(AssignmentsReceiver.ACTION_NEW_ASSIGNMENTS);
        assignmentsFilter.addCategory(Intent.CATEGORY_DEFAULT);
        AssignmentsReceiver assignmentsReceiver = new AssignmentsReceiver();
        registerReceiver(assignmentsReceiver, assignmentsFilter);

        // init service
        Intent assignmentsWebIntent = new Intent(this, AssignmentsIntentService.class);
        assignmentsWebIntent.putExtra(AssignmentsIntentService.PARAM_CLIENT_ID, clientId);
        assignmentsWebIntent.setAction(AssignmentsIntentService.ACTION_GET_ASSIGNMENTS);
        startService(assignmentsWebIntent);

        ////////////////////////////////////////////
        //
        // STORIES

        Log.d("HomeActivity.onCreate()","Setting up stories intent service ...");

        // init new stories receiver
        IntentFilter storiesFilter = new IntentFilter(StoriesReceiver.ACTION_NEW_STORIES);
        storiesFilter.addCategory(Intent.CATEGORY_DEFAULT);
        StoriesReceiver storiesReceiver = new StoriesReceiver();
        registerReceiver(storiesReceiver, storiesFilter);

        // init service
        Intent storiesWebIntent = new Intent(this, StoriesIntentService.class);
        storiesWebIntent.putExtra(StoriesIntentService.PARAM_CLIENT_ID, clientId);
        storiesWebIntent.setAction(StoriesIntentService.ACTION_GET_STORIES);
        startService(storiesWebIntent);

        ////////////////////////////////////////////
        //
        // PUBLISH POST

        Log.d("HomeActivity.onCreate()","Setting up stories intent service ...");

        // init new stories receiver
        IntentFilter publishPostFilter = new IntentFilter(PublishPostReceiver.ACTION_NEW_PUBLISH_POST);
        publishPostFilter.addCategory(Intent.CATEGORY_DEFAULT);
        PublishPostReceiver publishPostReceiver = new PublishPostReceiver();
        registerReceiver(storiesReceiver, publishPostFilter);

        // init service

        int assignmentId = 0;
        String title = "mah post";
        MediaObjectDefinition[] mediaObjectDefinitions = new MediaObjectDefinition[1];
        mediaObjectDefinitions[0] = new MediaObjectDefinition();
        mediaObjectDefinitions[0].mediaType = "text";
        mediaObjectDefinitions[0].mediaText = "Hi there.";
        mediaObjectDefinitions[0].mediaCaption = "";
        mediaObjectDefinitions[0].mediaFilename = "";
        Gson gson = new Gson();
        String mediaObjectDefinitionsJson = gson.toJson(mediaObjectDefinitions);

        Intent publishPostWebIntent = new Intent(this, PublishPostIntentService.class);
        publishPostWebIntent.putExtra(PublishPostIntentService.PARAM_CLIENT_ID, clientId);
        publishPostWebIntent.putExtra(PublishPostIntentService.PARAM_ASSIGNMENT_ID, assignmentId);
        publishPostWebIntent.putExtra(PublishPostIntentService.PARAM_TITLE, title);
        publishPostWebIntent.putExtra(PublishPostIntentService.PARAM_MEDIA_OBJECT_DEFINITIONS_JSON, mediaObjectDefinitionsJson);
        startService(publishPostWebIntent);

    }

}
