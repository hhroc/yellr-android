package yellr.net.yellr_android.intent_services.verify_user;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import yellr.net.yellr_android.intent_services.verify_user.VerifyUserResponse;

public class VerifyUserIntentService extends IntentService {
    public static final String ACTION_GET_VERIFY_USER =
            "yellr.net.yellr_android.action.GET_VERIFY_USER";

    public static final String PARAM_CLIENT_ID = "clientId";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_FIRST_NAME = "firstName";
    public static final String PARAM_LAST_NAME = "lastName";
    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_VERIFY_USER_JSON = "verifyUserJson";

    public VerifyUserIntentService() {
        super("VerifyUserIntentService");
        //Log.d("VerifyUserIntentService()","Constructor.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("VerifyUserIntentService.onHandleIntent()","Decoding intent action ...");

        String clientId = intent.getStringExtra(PARAM_CLIENT_ID);
        String username = intent.getStringExtra(PARAM_USERNAME);
        String password = intent.getStringExtra(PARAM_PASSWORD);
        String first_name = intent.getStringExtra(PARAM_FIRST_NAME);
        String last_name = intent.getStringExtra(PARAM_LAST_NAME);
        String email = intent.getStringExtra(PARAM_EMAIL);


        handleActionVerifyUser(clientId, username, password, first_name, last_name, email);
    }

    /**
     * Handles get verifyUser
     */
    private void handleActionVerifyUser(String clientId,
                                        String username,
                                        String password,
                                        String first_name,
                                        String last_name,
                                        String email) {

        String baseUrl = "http://yellr.mycodespace.net/verify_user.json";

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("client_id", clientId));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("first_name", first_name));
        params.add(new BasicNameValuePair("last_name", last_name));
        if( !email.isEmpty() ) {
            params.add(new BasicNameValuePair("email", email));
        }
        String url =  baseUrl
                + "?client_id=" + clientId;

        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);

        String verifyUserJson = "{}";

        Log.d("VerifyUserIntentService.onHandleIntent()","Verifying client ...");

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

            //
            InputStream content = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));

            //
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            verifyUserJson = builder.toString();

            Log.d("VerifyUserIntentService.publishPost()", "JSON: " + verifyUserJson);

            Gson gson = new Gson();
            VerifyUserResponse verifyUserresponse = gson.fromJson(verifyUserJson, VerifyUserResponse.class);

            if ( !verifyUserresponse.success ) {
                // TODO: report failure back to calling fragment
            }

        } catch (Exception e) {
            Log.d("VerifyUserIntentService.onHandleIntent()","ERROR: " + e.toString());
            //e.printStackTrace();
        }
    }
}
