package yellr.net.yellr_android.intent_services;

import android.content.Context;
import android.util.Log;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;

import com.google.gson.Gson;

import yellr.net.yellr_android.intent_services.assignments.AssignmentsReceiver;
import yellr.net.yellr_android.intent_services.assignments.AssignmentsIntentService;

import yellr.net.yellr_android.intent_services.stories.StoriesReceiver;
import yellr.net.yellr_android.intent_services.stories.StoriesIntentService;

import yellr.net.yellr_android.intent_services.notifications.NotificationsReceiver;
import yellr.net.yellr_android.intent_services.notifications.NotificationsIntentService;

import yellr.net.yellr_android.intent_services.messages.MessagesReceiver;
import yellr.net.yellr_android.intent_services.messages.MessagesIntentService;

import yellr.net.yellr_android.intent_services.publish_post.MediaObjectDefinition;
import yellr.net.yellr_android.intent_services.publish_post.PublishPostIntentService;
import yellr.net.yellr_android.intent_services.publish_post.PublishPostReceiver;

/**
 * Created by TDuffy on 2/5/2015.
 */
public class IntentServicesHelper {

    //
    // Create Client ID
    //
    String clientId = "2101b2d8-d3f9-421c-87b5-c6fd465c3394"; //UUID.randomUUID().toString();


    public static void getAssignments(Context context,
                                      String clientId) {

        ////////////////////////////////////////////
        //
        // ASSIGNMENTS

        Log.d("HomeActivity.onCreate()", "Setting up assignments intent service ...");

        // init new assignments receiver
        IntentFilter assignmentsFilter = new IntentFilter(AssignmentsReceiver.ACTION_NEW_ASSIGNMENTS);
        assignmentsFilter.addCategory(Intent.CATEGORY_DEFAULT);
        AssignmentsReceiver assignmentsReceiver = new AssignmentsReceiver();
        context.registerReceiver(assignmentsReceiver, assignmentsFilter);

        // init service
        Intent assignmentsWebIntent = new Intent(context, AssignmentsIntentService.class);
        assignmentsWebIntent.putExtra(AssignmentsIntentService.PARAM_CLIENT_ID, clientId);
        assignmentsWebIntent.setAction(AssignmentsIntentService.ACTION_GET_ASSIGNMENTS);
        context.startService(assignmentsWebIntent);
    }

    public static void getStories(Context context,
                                  String clientId) {

        ////////////////////////////////////////////
        //
        // STORIES

        Log.d("HomeActivity.onCreate()", "Setting up stories intent service ...");

        // init new stories receiver
        IntentFilter storiesFilter = new IntentFilter(StoriesReceiver.ACTION_NEW_STORIES);
        storiesFilter.addCategory(Intent.CATEGORY_DEFAULT);
        StoriesReceiver storiesReceiver = new StoriesReceiver();
        context.registerReceiver(storiesReceiver, storiesFilter);

        // init service
        Intent storiesWebIntent = new Intent(context, StoriesIntentService.class);
        storiesWebIntent.putExtra(StoriesIntentService.PARAM_CLIENT_ID, clientId);
        storiesWebIntent.setAction(StoriesIntentService.ACTION_GET_STORIES);
        context.startService(storiesWebIntent);

    }

    public static void getNotifications(Context context,
                                        String clientId) {

        ////////////////////////////////////////////
        //
        // NOTIFICATIONS

        Log.d("HomeActivity.onCreate()", "Setting up notifications intent service ...");

        // init new notifications receiver
        IntentFilter notificationsFilter = new IntentFilter(NotificationsReceiver.ACTION_NEW_NOTIFICATIONS);
        notificationsFilter.addCategory(Intent.CATEGORY_DEFAULT);
        NotificationsReceiver notificationsReceiver = new NotificationsReceiver();
        context.registerReceiver(notificationsReceiver, notificationsFilter);

        // init service
        Intent notificationsWebIntent = new Intent(context, NotificationsIntentService.class);
        notificationsWebIntent.putExtra(NotificationsIntentService.PARAM_CLIENT_ID, clientId);
        notificationsWebIntent.setAction(NotificationsIntentService.ACTION_GET_NOTIFICATIONS);
        context.startService(notificationsWebIntent);

    }

    public static void getMessages(Context context,
                                   String clientId) {

        ////////////////////////////////////////////
        //
        // MESSAGES

        Log.d("HomeActivity.onCreate()", "Setting up messages intent service ...");

        // init new messages receiver
        IntentFilter messagesFilter = new IntentFilter(MessagesReceiver.ACTION_NEW_MESSAGES);
        messagesFilter.addCategory(Intent.CATEGORY_DEFAULT);
        MessagesReceiver messagesReceiver = new MessagesReceiver();
        context.registerReceiver(messagesReceiver, messagesFilter);

        // init service
        Intent messagesWebIntent = new Intent(context, MessagesIntentService.class);
        messagesWebIntent.putExtra(MessagesIntentService.PARAM_CLIENT_ID, clientId);
        messagesWebIntent.setAction(MessagesIntentService.ACTION_GET_MESSAGES);
        context.startService(messagesWebIntent);
    }

    public static void publishPost(Context context,
                                   String clientId,
                                   String mediaType,
                                   String mediaText,
                                   String mediaCaption,
                                   String mediaFilename) {

        ////////////////////////////////////////////
        //
        // PUBLISH POST

        Log.d("HomeActivity.onCreate()", "Setting up stories intent service ...");

        // init new stories receiver
        IntentFilter publishPostFilter = new IntentFilter(PublishPostReceiver.ACTION_NEW_PUBLISH_POST);
        publishPostFilter.addCategory(Intent.CATEGORY_DEFAULT);
        PublishPostReceiver publishPostReceiver = new PublishPostReceiver();
        context.registerReceiver(publishPostReceiver, publishPostFilter);

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

        Intent publishPostWebIntent = new Intent(context, PublishPostIntentService.class);
        publishPostWebIntent.putExtra(PublishPostIntentService.PARAM_CLIENT_ID, clientId);
        publishPostWebIntent.putExtra(PublishPostIntentService.PARAM_ASSIGNMENT_ID, assignmentId);
        publishPostWebIntent.putExtra(PublishPostIntentService.PARAM_TITLE, title);
        publishPostWebIntent.putExtra(PublishPostIntentService.PARAM_MEDIA_OBJECT_DEFINITIONS_JSON, mediaObjectDefinitionsJson);
        context.startService(publishPostWebIntent);

    }
}
