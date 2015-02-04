package yellr.net.yellr_android.intent_services.publish_post;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

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

public class PublishPostIntentService extends IntentService {
    public static final String ACTION_GET_PUBLISH_POST =
            "yellr.net.yellr_android.action.GET_PUBLISH_POST";

    public static final String PARAM_CLIENT_ID = "clientId";
    public static final String PARAM_ASSIGNMENT_ID = "assignmentId";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_MEDIA_OBJECT_DEFINITIONS_JSON = "mediaObjectDefinitionsJson";
    public static final String PARAM_PUBLISH_POST_JSON = "PublishPostJson";

    public PublishPostIntentService() {
        super("PublishPostIntentService");
        Log.d("PublishPostIntentService()","Constructor.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("PublishPostIntentService.onHandleIntent()","Decoding intent action ...");

        String clientId = intent.getStringExtra(PARAM_CLIENT_ID);
        int assignmentId = intent.getIntExtra(PARAM_ASSIGNMENT_ID,0);
        String title = intent.getStringExtra(PARAM_TITLE);
        String mediaObjectDefinitionsJson = intent.getStringExtra(PARAM_MEDIA_OBJECT_DEFINITIONS_JSON);

        Gson gson = new Gson();
        MediaObjectDefinition[] mediaObjectDefinitions =
                gson.fromJson(mediaObjectDefinitionsJson, MediaObjectDefinition[].class);

        handleActionGetPublishPost(clientId, assignmentId, title, mediaObjectDefinitions);
    }

    /**
     * Handles get PublishPost
     */
    private void handleActionGetPublishPost(String clientId,
                                            int assignmentId,
                                            String title,
                                            MediaObjectDefinition[] mediaObjectDefinitions) {

        Log.d("PublishPostIntentService.handleActionGetPublishPost()", "Starting handleActionGetPublishPost() ...");

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

        double latitude = 43.2;
        double longitude = -77.5;

        String publishPostUrl = "http://yellr.mycodespace.net/publish_post.json";

        String languageCode = Locale.getDefault().getLanguage();

        Log.d("PublishPostIntentService.handleActionGetPublishPost()","Uploading media objects ...");

        Gson gson = new Gson();

        String[] mediaObjectIds = new String[mediaObjectDefinitions.length];
        for(int i = 0; i<mediaObjectDefinitions.length; i++) {

            String mediaObjectResponseJson = uploadMedia(
                    clientId,
                    mediaObjectDefinitions[i].mediaType,
                    mediaObjectDefinitions[i].mediaFilename,
                    mediaObjectDefinitions[i].mediaText,
                    mediaObjectDefinitions[i].mediaCaption);

            mediaObjectIds[i] =
                    gson.fromJson(mediaObjectResponseJson,MediaObjectResponse.class).media_id;

        }

        Log.d("PublishPostIntentService.handleActionGetPublishPost()","Publishing post ...");


        String mediaObjectIdsJson = gson.toJson(mediaObjectIds);
        String publishPostJson = publishPost(
                clientId,
                assignmentId,
                languageCode,
                title,
                latitude,
                longitude,
                mediaObjectIdsJson);


    }

    private String publishPost(String clientId,
                               int assignmentId,
                               String languageCode,
                               String title,
                               double lat,
                               double lng,
                               String mediaObjects){

        String uploadMediaUrl = "http://yellr.mycodespace.net/publish_post.json";

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("client_id", clientId));
        params.add(new BasicNameValuePair("assignment_id", String.valueOf(assignmentId)));
        params.add(new BasicNameValuePair("language_code", languageCode));
        params.add(new BasicNameValuePair("title", title));
        params.add(new BasicNameValuePair("lat", String.valueOf(lat)));
        params.add(new BasicNameValuePair("lng", String.valueOf(lng)));
        params.add(new BasicNameValuePair("media_objects", mediaObjects));

        //
        // derived from
        //  http://stackoverflow.com/a/2937140
        //

        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(uploadMediaUrl);

        String publishPostJson = "{}";

        try {

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            for(int index=0; index < params.size(); index++) {

                entityBuilder.addPart(params.get(index).getName(),
                        new StringBody(params.get(index).getValue(), ContentType.TEXT_PLAIN));

            }

            HttpEntity entity = entityBuilder.build();
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost, localContext);

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

            Log.d("PublishPostIntentService.publishPost()","JSON: " + publishPostJson);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return publishPostJson;

    }

    private String uploadMedia(String clientId,
                               String mediaType,
                               String mediaFilename,
                               String mediaText,
                               String mediaCaption) {

        String uploadMediaUrl = "http://yellr.mycodespace.net/upload_media.json";

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("client_id", clientId));
        params.add(new BasicNameValuePair("media_type", mediaType));
        params.add(new BasicNameValuePair("media_file", mediaFilename));
        params.add(new BasicNameValuePair("media_text", mediaText));
        params.add(new BasicNameValuePair("media_caption", mediaCaption));

        //
        // derived from
        //  http://stackoverflow.com/a/2937140
        //

        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(uploadMediaUrl);

        String uploadMediaJson = "{}";

        try {

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            for(int index=0; index < params.size(); index++) {
                if(params.get(index).getName().equalsIgnoreCase("image")) {
                    // we only need to add a file if our media object type is not text
                    if(mediaType != "text" ) {
                        // If the key equals to "media_file", we use FileBody to transfer the data

                        entityBuilder.addPart(params.get(index).getName(),
                                new FileBody(new File(params.get(index).getValue())));
                    }
                } else {
                    // Normal string data
                    entityBuilder.addPart(params.get(index).getName(),
                            new StringBody(params.get(index).getValue(), ContentType.TEXT_PLAIN));
                }
            }

            HttpEntity entity = entityBuilder.build();
            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost, localContext);

            //
            InputStream content = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));

            //
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            uploadMediaJson = builder.toString();

            Log.d("PublishPostIntentService.uploadMedia()","JSON: " + uploadMediaJson);

        } catch (IOException e) {
            Log.d("PublishPostIntentService.uploadMedia()","ERROR: " + e.toString());
            e.printStackTrace();
        }

        return uploadMediaJson;

    }

}
