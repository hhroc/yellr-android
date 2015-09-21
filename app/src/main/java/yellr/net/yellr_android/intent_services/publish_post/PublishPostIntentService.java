package yellr.net.yellr_android.intent_services.publish_post;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import yellr.net.yellr_android.BuildConfig;
import yellr.net.yellr_android.utils.YellrUtils;

public class PublishPostIntentService extends IntentService {

    private Handler handler;

    public static final String ACTION_GET_PUBLISH_POST =
            "yellr.net.yellr_android.action.GET_PUBLISH_POST";

    //public static final String PARAM_CUID = "cuid";
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

        Log.d("PublishPostIntentService.onHandleIntent()","Decoding intent action ...");

        //String cuid = intent.getStringExtra(PARAM_CUID);
        int assignmentId = intent.getIntExtra(PARAM_ASSIGNMENT_ID, 0);
        //String title = intent.getStringExtra(PARAM_TITLE);
        String text = intent.getStringExtra(PARAM_TEXT);
        String mediaType = intent.getStringExtra(PARAM_MEDIA_TYPE);
        String imageFilename = intent.getStringExtra(PARAM_IMAGE_FILENAME);
        String audioFilename = intent.getStringExtra(PARAM_AUDIO_FILENAME);
        String videoFilename = intent.getStringExtra(PARAM_VIDEO_FILENAME);

        Log.d("2.SubmitPostToYellr()Post()",videoFilename);

        //String mediaObjectDefinitionsJson = intent.getStringExtra(PARAM_MEDIA_OBJECT_DEFINITIONS_JSON);

        //Gson gson = new Gson();
        //MediaObjectDefinition[] mediaObjectDefinitions =
        //        gson.fromJson(mediaObjectDefinitionsJson, MediaObjectDefinition[].class);

        //handleActionGetPublishPost(clientId, assignmentId, title, mediaObjectDefinitions);

        //String[] imageFilenames

        // TODO: we really cant get here unless the home locsation is set, so we prob dont need to check tyhis

        //if ( YellrUtils.isHomeLocationSet(getApplicationContext()) )
            handleActionGetPublishPost(assignmentId, mediaType, text, imageFilename, audioFilename, videoFilename);

    }

    /**
     * Handles get PublishPost
     */
    private void handleActionGetPublishPost(int assignmentId,
                                            String mediaType,
                                            String text,
                                            String imageFilename,
                                            String audioFilename,
                                            String videoFilename) {

        Log.d("PublishPostIntentService.handleActionGetPublishPost()", "Uploading media objects ...");

        //String mediaType = "text";
        String mediaFilename = "";
        String mediaText = text;
        String mediaCaption = text;

        switch (mediaType) {
            case "text":
                //mediaType = "text";
                //Log.d("PublishPostIntentService.Case", "Text");
                mediaFilename = "";
                mediaText = text;
                mediaCaption = "";
                break;
            case "image":
                //mediaType = "image";
                //Log.d("PublishPostIntentService.Case", "Image");
                mediaFilename = imageFilename;
                Log.d("PublishPostIntentService.Case", imageFilename);

                Bitmap fileToUploadBitmap = BitmapFactory.decodeFile(mediaFilename);
                double scaleToResizeAspectRatio = (double)fileToUploadBitmap.getWidth() / (double)fileToUploadBitmap.getHeight();
                int desiredHeight = 600;
                int desiredWidth = (int) (desiredHeight*scaleToResizeAspectRatio);

                Log.d("PublishPostIntentService.Case.AspectRatio - ", String.valueOf(scaleToResizeAspectRatio));
                Log.d("PublishPostIntentService.Case.AspectRatio - ", String.valueOf(fileToUploadBitmap.getWidth()));
                Log.d("PublishPostIntentService.Case.AspectRatio - ", String.valueOf(fileToUploadBitmap.getHeight()));

                Bitmap outFile = Bitmap.createScaledBitmap(fileToUploadBitmap, desiredWidth, desiredHeight, false);

                File outputDir = getApplicationContext().getCacheDir(); // context being the Activity pointer
                try {

                    File outputFile = File.createTempFile("fileToUploadYellr", "jpg", outputDir);
                    File file = new File(outputFile.getAbsolutePath());
                    FileOutputStream fOut;

                    try {
                        fOut = new FileOutputStream(file);
                        outFile.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                        fileToUploadBitmap.recycle();
                        outFile.recycle();

                        mediaFilename = outputFile.getAbsolutePath();

                    } catch (Exception e) {}

                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaText = "";
                mediaCaption = text;
                break;
            case "audio":
                //mediaType = "image";
                //Log.d("PublishPostIntentService.Case", "Audio");
                mediaFilename = audioFilename;
                mediaText = "";
                mediaCaption = text;
                break;
            case "video":
                //mediaType = "image";
                //Log.d("PublishPostIntentService.Case", "Video");
                mediaFilename = videoFilename;
                mediaText = "";
                mediaCaption = text;
                break;
            default:
                // uh ..
                break;
        }

        // TODO: switch on media type

        Log.d("PublishPostIntentService.MediaFileName", mediaFilename);

        String mediaObjectResponseJson = uploadMedia(
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
                assignmentId,
                //languageCode,
                //title,
                //latitude,
                //longitude,
                mediaId);
        //mediaObjectIdsJson);

    }

    private String publishPost(int assignmentId,
                               //String languageCode,
                               //String title,
                               //double lat,
                               //double lng,
                               String mediaId) {
        //String mediaObjects){

        /*

        String baseUrl = BuildConfig.BASE_URL + "/publish_post.json";

        // get the location, but if the user has turned off location services,
        // it will come back null.  If it's null, just dump out.
        // TODO: pop-up a dialog maybe??
        double latLng[] = YellrUtils.getLocation(getApplicationContext());
        if (latLng == null )
            return null;
        String lat = String.valueOf(latLng[0]);
        String lng = String.valueOf(latLng[1]);

        String languageCode = Locale.getDefault().getLanguage();

        String url =  baseUrl
                + "?cuid=" + YellrUtils.getCUID(getApplicationContext()) //cuid
                + "&language_code=" + languageCode
                + "&lat=" + lat
                + "&lng=" + lng;

        */

        String publishPostJson = "{}";

        String baseUrl = BuildConfig.BASE_URL + "/publish_post.json";
        String url = YellrUtils.buildUrl(getApplicationContext(),baseUrl);
        if (url != null) {

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
                if (response.getStatusLine().getStatusCode() == 200) {
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

        } else {
            Log.d("PublishPostIntentService.publishPost()","URL was null!!!");
        }

        return publishPostJson;

    }

    private String uploadMedia(String mediaType,
                               String mediaFilename,
                               String mediaText,
                               String mediaCaption) {

        //Log.d("uploadMedia()", "param name: " + params.get(index).getName());

        /*

        String baseUrl = BuildConfig.BASE_URL + "/upload_media.json";

        double latLng[] = YellrUtils.getLocation(getApplicationContext());
        String lat = String.valueOf(latLng[0]);
        String lng = String.valueOf(latLng[1]);

        String languageCode = Locale.getDefault().getLanguage();

        String url =  baseUrl
                + "?cuid=" + YellrUtils.getCUID(getApplicationContext()) //cuid
                + "&language_code=" + languageCode
                + "&lat=" + lat
                + "&lng=" + lng;

        */

        String uploadMediaJson = "{}";

        String baseUrl = BuildConfig.BASE_URL + "/upload_media.json";
        String url = YellrUtils.buildUrl(getApplicationContext(),baseUrl);
        if (url != null) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            //params.add(new BasicNameValuePair("cuid", cuid));
            params.add(new BasicNameValuePair("media_type", mediaType));
            //params.add(new BasicNameValuePair("media_type", mediaType));

            if (!mediaFilename.equals("")) {
                params.add(new BasicNameValuePair("media_file", mediaFilename));
            }

            if (!mediaText.equals("")) {
                params.add(new BasicNameValuePair("media_text", mediaText));
            }

            if (!mediaCaption.equals("")) {
                params.add(new BasicNameValuePair("caption", mediaCaption));
            }

            //
            // derived from
            //  http://stackoverflow.com/a/2937140
            //

            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            for (int index = 0; index < params.size(); index++) {

                if (params.get(index).getName().equalsIgnoreCase("media_file") && !mediaType.equalsIgnoreCase("text")) {
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
        }

        return uploadMediaJson;

    }

}