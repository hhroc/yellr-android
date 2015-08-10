package yellr.net.yellr_android.intent_services.report_post;

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

public class ReportPostIntentService extends IntentService {

    public static final String PARAM_POST_ID =
            "yellr.net.yellr_android.params.POST_ID";

    public ReportPostIntentService() {

        super("ReportPostIntentService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int postId = intent.getIntExtra(ReportPostIntentService.PARAM_POST_ID, 0);
        reportPost(postId);
    }

    private void reportPost(int postId) {

        Log.d("ReportPostIntentService.reportPost()", "Sending report for post ...");

        String baseUrl = BuildConfig.BASE_URL + "/flag_post.json";
        String url = YellrUtils.buildUrl(getApplicationContext(),baseUrl);
        if (url != null) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("post_id", String.valueOf(postId)));
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
                InputStream content = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


}