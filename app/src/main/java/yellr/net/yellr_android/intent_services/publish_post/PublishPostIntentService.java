package yellr.net.yellr_android.intent_services.publish_post;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import yellr.net.yellr_android.BuildConfig;

public class PublishPostIntentService extends IntentService {

    private Handler handler;

    public static final String ACTION_GET_PUBLISH_POST =
            "yellr.net.yellr_android.action.GET_PUBLISH_POST";

    public static final String PARAM_CUID = "cuid";
    public static final String PARAM_ASSIGNMENT_ID = "assignmentId";
    public static final String PARAM_TEXT = "text";
    public static final String PARAM_MEDIA_TYPE = "mediaType";
    public static final String PARAM_IMAGE_FILENAME = "imageFilename";
    public static final String PARAM_AUDIO_FILENAME = "audioFilename";
    public static final String PARAM_VIDEO_FILENAME = "videoFilename";
    //public static final String PARAM_MEDIA_OBJECT_DEFINITIONS_JSON = "mediaObjectDefinitionsJson";
    //public static final String PARAM_PUBLISH_POST_JSON = "PublishPostJson";

    public PublishPostIntentService() {
        super("PublishPostIntentService");
        //Log.d("PublishPostIntentService()","Constructor.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //Log.d("PublishPostIntentService.onHandleIntent()","Decoding intent action ...");

        String cuid = intent.getStringExtra(PARAM_CUID);
        int assignmentId = intent.getIntExtra(PARAM_ASSIGNMENT_ID, 0);
        //String title = intent.getStringExtra(PARAM_TITLE);
        String text = intent.getStringExtra(PARAM_TEXT);
        String mediaType = intent.getStringExtra(PARAM_MEDIA_TYPE);
        String imageFilename = intent.getStringExtra(PARAM_IMAGE_FILENAME);
        String audioFilename = intent.getStringExtra(PARAM_VIDEO_FILENAME);
        String videoFilename = intent.getStringExtra(PARAM_AUDIO_FILENAME);

        //String mediaObjectDefinitionsJson = intent.getStringExtra(PARAM_MEDIA_OBJECT_DEFINITIONS_JSON);

        //Gson gson = new Gson();
        //MediaObjectDefinition[] mediaObjectDefinitions =
        //        gson.fromJson(mediaObjectDefinitionsJson, MediaObjectDefinition[].class);

        //handleActionGetPublishPost(clientId, assignmentId, title, mediaObjectDefinitions);

        //String[] imageFilenames

        handleActionGetPublishPost(cuid, assignmentId, mediaType, text, imageFilename, audioFilename, videoFilename);

    }

    /**
     * Handles get PublishPost
     */
    private void handleActionGetPublishPost(String cuid,
                                            int assignmentId,
                                            String mediaType,
                                            String text,
                                            String imageFilename,
                                            String audioFilename,
                                            String videoFilename) {
        //String title,
        //MediaObjectDefinition[] mediaObjectDefinitions) {

        //Log.d("PublishPostIntentService.handleActionGetPublishPost()", "Starting handleActionGetPublishPost() ...");

        // get location data

        // via: http://stackoverflow.com/a/2227299
        // TODO: check for last known good, and if null then
        //       poll to get a fresh location.  This will be
        //       okay for now though, even if it takes a while
        //       to complete (since it's in the service)
        //LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //double latitude = location.getLatitude();
        //double longitude = location.getLongitude();

        //double latitude = 43.2;
        //double longitude = -77.5;

        Log.d("PublishPostIntentService.handleActionGetPublishPost()", "Uploading media objects ...");


        //String mediaType = "text";
        String mediaFilename = "";
        String mediaText = text;
        String mediaCaption = "";

        switch (mediaType) {
            case "text":
                //mediaType = "text";
                mediaFilename = "";
                mediaText = text;
                mediaCaption = "";
                break;
            case "image":
                //mediaType = "image";
                mediaFilename = imageFilename;
                mediaText = "";
                mediaCaption = text;
                break;
            default:
                // uh ..
                break;
        }

        // TODO: switch on media type

        String mediaObjectResponseJson = uploadMedia(
                cuid,
                mediaType,
                mediaFilename,
                mediaText,
                mediaCaption
        );
        Gson gson = new Gson();
        String mediaId = new String();
        try{
            mediaId = gson.fromJson(mediaObjectResponseJson, MediaObjectResponse.class).media_id;
        } catch (Exception e){
            Log.d("PublishPostIntentService.handleActionGetPublishedPost", "GSON puked");
        }


        //String mediaObjectIdsJson = gson.toJson(mediaObjectIds);
        String publishPostJson = publishPost(
                cuid,
                assignmentId,
                //languageCode,
                //title,
                //latitude,
                //longitude,
                mediaId);
        //mediaObjectIdsJson);

    }

    private String publishPost(String cuid,
                               int assignmentId,
                               //String languageCode,
                               //String title,
                               //double lat,
                               //double lng,
                               String mediaId) {
        //String mediaObjects){

        String baseUrl = BuildConfig.BASE_URL + "/publish_post.json";

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String bestProvider = lm.getBestProvider(criteria, true);
        Location location = lm.getLastKnownLocation(bestProvider); //LocationManager.GPS_PROVIDER);
        // default to center of Rochester, NY
        double latitude = 43.1656;
        double longitude = -77.6114;
        // if we have a location available, then set it
        if ( location != null ){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } else {
            Log.d("PublishPostIntentService.handleActionGetAssignments()","No location available, defaulting to Rochester, NY");
        }

        String lat = String.valueOf(latitude);
        String lng = String.valueOf(longitude);

        String languageCode = Locale.getDefault().getLanguage();

        String url =  baseUrl
                + "?cuid=" + cuid
                + "&language_code=" + languageCode
                + "&lat=" + lat
                + "&lng=" + lng;

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        //params.add(new BasicNameValuePair("cuid", cuid));
        params.add(new BasicNameValuePair("assignment_id", String.valueOf(assignmentId)));
        //params.add(new BasicNameValuePair("language_code", languageCode));
        //params.add(new BasicNameValuePair("title", title));
        //params.add(new BasicNameValuePair("lat", String.valueOf(lat)));
        //params.add(new BasicNameValuePair("lng", String.valueOf(lng)));
        params.add(new BasicNameValuePair("media_objects", "[\"" + mediaId + "\"]"));

        //
        // derived from
        //  http://stackoverflow.com/a/2937140
        //

        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);

        String publishPostJson = "{}";

        try {

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            for (int index = 0; index < params.size(); index++) {

                entityBuilder.addPart(params.get(index).getName(),
                        new StringBody(params.get(index).getValue(), ContentType.TEXT_PLAIN));

            }

            HttpEntity entity = entityBuilder.build();
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost, localContext);
            final String toastMsg;
            if(response.getStatusLine().getStatusCode() == 200){
                toastMsg = "Post Successful!";
            } else {
                toastMsg = "Problem Submitting Post.";
            }

            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
                }
            });


            //
            InputStream content = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));

            //
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            publishPostJson = builder.toString();

            Log.d("PublishPostIntentService.publishPost()", "JSON: " + publishPostJson);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return publishPostJson;

    }

    private String uploadMedia(String cuid,
                               String mediaType,
                               String mediaFilename,
                               String mediaText,
                               String mediaCaption) {

        Log.d("uploadMedia()", "media type: " + mediaType);
        //Log.d("uploadMedia()", "param name: " + params.get(index).getName());

        String baseUrl = BuildConfig.BASE_URL + "/upload_media.json";

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String bestProvider = lm.getBestProvider(criteria, true);
        Location location = lm.getLastKnownLocation(bestProvider); //LocationManager.GPS_PROVIDER);
        // default to center of Rochester, NY
        double latitude = 43.1656;
        double longitude = -77.6114;
        // if we have a location available, then set it
        if ( location != null ){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } else {
            Log.d("PublishPostIntentService.handleActionGetAssignments()","No location available, defaulting to Rochester, NY");
        }

        String lat = String.valueOf(latitude);
        String lng = String.valueOf(longitude);

        String languageCode = Locale.getDefault().getLanguage();

        String url =  baseUrl
                + "?cuid=" + cuid
                + "&language_code=" + languageCode
                + "&lat=" + lat
                + "&lng=" + lng;

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        //params.add(new BasicNameValuePair("cuid", cuid));
        params.add(new BasicNameValuePair("media_type", mediaType));

        if (!mediaFilename.equals("")) {
            params.add(new BasicNameValuePair("media_file", mediaFilename));
        }

        if (!mediaText.equals("")) {
            params.add(new BasicNameValuePair("media_text", mediaText));
        }

        if (!mediaCaption.equals("")) {
            params.add(new BasicNameValuePair("media_caption", mediaCaption));
        }

        //
        // derived from
        //  http://stackoverflow.com/a/2937140
        //

        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);

        String uploadMediaJson = "{}";


        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        for (int index = 0; index < params.size(); index++) {

            Log.d("uploadMedia()", "param name: " + params.get(index).getName());

            if (params.get(index).getName().equalsIgnoreCase("media_file") &&!mediaType.equalsIgnoreCase("text")) {
                // we only need to add a file if our media object type is not text
                //if (mediaType != "text") {
                // If the key equals to "media_file", we use FileBody to transfer the data

                Log.d("uploadMedia()", "adding binary file: " + params.get(index).getValue());

                entityBuilder.addPart(params.get(index).getName(),
                        new FileBody(new File(params.get(index).getValue())));
                //}
            } else {

                Log.d("uploadMedia()", "adding text field: " + params.get(index).getValue());

                // Normal string data
                entityBuilder.addPart(params.get(index).getName(),
                        new StringBody(params.get(index).getValue(), ContentType.TEXT_PLAIN));
            }
        }

        try {

            HttpEntity entity = entityBuilder.build();
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost, localContext);

            InputStream content = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));

            //
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            uploadMediaJson = builder.toString();

            Log.d("PublishPostIntentService.uploadMedia()", "JSON: " + uploadMediaJson);

        } catch (Exception e) {
            //Toast.makeText(this, "upload_media: " + e.toString(), Toast.LENGTH_SHORT).show();

            Log.d("PublishPostIntentService.uploadMedia()", "ERROR: " + e.toString());
            //e.printStackTrace();

        }
        return uploadMediaJson;

    }

}