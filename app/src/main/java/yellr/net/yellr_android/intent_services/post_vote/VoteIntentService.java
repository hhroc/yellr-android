package yellr.net.yellr_android.intent_services.post_vote;

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
import yellr.net.yellr_android.utils.YellrUtils;

public class VoteIntentService extends IntentService {

    private Handler handler;

    public static final String ACTION_REGISTER_VOTE =
            "yellr.net.yellr_android.action.REGISTER_VOTE";

    public static final String PARAM_POST_ID =
            "yellr.net.yellr_android.params.POST_ID";
    public static final String PARAM_IS_UP_VOTE =
            "yellr.net.yellr_android.params.IS_UP_VOTE";

    public VoteIntentService() {
        super("VoteIntentService");
        //Log.d("VoteIntentService()","Constructor.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int postId = intent.getIntExtra(VoteIntentService.PARAM_POST_ID, 0);
        boolean isUpVote = intent.getBooleanExtra(VoteIntentService.PARAM_IS_UP_VOTE, true);

        handleActionGetVote(postId, isUpVote);
    }

    /**
     * Handles submitting Vote
     */
    private void handleActionGetVote(int postId, boolean isUpVote) {

        Log.d("VoteIntentService.handleActionGetVote()", "Submitting vote ...");

        String baseUrl = BuildConfig.BASE_URL + "/register_vote.json";

        // get the location, but if the user has turned off location services,
        // it will come back null.  If it's null, just dump out.
        // TODO: pop-up a dialog maybe??
        double latLng[] = YellrUtils.getLocation(getApplicationContext());
        if (latLng == null )
            return;
        String lat = String.valueOf(latLng[0]);
        String lng = String.valueOf(latLng[1]);

        String languageCode = Locale.getDefault().getLanguage();

        String url =  baseUrl
                + "?cuid=" + YellrUtils.getCUID(getApplicationContext()) //cuid
                + "&language_code=" + languageCode
                + "&lat=" + lat
                + "&lng=" + lng;

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("post_id",String.valueOf(postId)));
        params.add(new BasicNameValuePair("is_up_vote",YellrUtils.booleanToString(isUpVote)));

        //params.add(new BasicNameValuePair("cuid", cuid));
        //params.add(new BasicNameValuePair("assignment_id", String.valueOf(assignmentId)));
        //params.add(new BasicNameValuePair("language_code", languageCode));
        //params.add(new BasicNameValuePair("title", title));
        //params.add(new BasicNameValuePair("lat", String.valueOf(lat)));
        //params.add(new BasicNameValuePair("lng", String.valueOf(lng)));
        //params.add(new BasicNameValuePair("media_objects", "[\"" + mediaId + "\"]"));

        //
        // derived from
        //  http://stackoverflow.com/a/2937140
        //

        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);

        String voteJson = "{}";

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
            //final String toastMsg;
            //if(response.getStatusLine().getStatusCode() == 200){
            //    toastMsg = "Vote submitted!";
            //} else {
            //    toastMsg = "Problem submitting vote.";
            //}

            //handler.post(new Runnable() {
            //    @Override
            //    public void run() {
            //        Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
            //    }
            //});


            //
            InputStream content = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));

            //
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            voteJson = builder.toString();

            Log.d("VoteIntentService.vote()", "JSON: " + voteJson);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}