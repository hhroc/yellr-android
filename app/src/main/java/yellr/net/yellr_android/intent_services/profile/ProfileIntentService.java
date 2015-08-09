package yellr.net.yellr_android.intent_services.profile;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import yellr.net.yellr_android.BuildConfig;
import yellr.net.yellr_android.fragments.ProfileFragment;
import yellr.net.yellr_android.utils.YellrUtils;

public class ProfileIntentService extends IntentService {
    public static final String ACTION_GET_PROFILE =
            "yellr.net.yellr_android.action.GET_PROFILE";

    //public static final String PARAM_CUID = "cuid";
    public static final String PARAM_PROFILE_JSON = "profileJson";

    public ProfileIntentService() {
        super("ProfileIntentService");
        //Log.d("ProfileIntentService()","Constructor.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //Log.d("ProfileIntentService.onHandleIntent()","Decoding intent action ...");

        //String cuid = intent.getStringExtra(PARAM_CUID);
        //if ( YellrUtils.isHomeLocationSet(getApplicationContext()) )
        handleActionGetProfile(); //cuid);
    }

    /**
     * Handles get profile
     */
    private void handleActionGetProfile() {

        Log.d("ProfileIntentService.handleActionGetProfile()", "Getting profile data ...");

        /*
        String baseUrl = BuildConfig.BASE_URL + "/get_profile.json";

        double latLng[] = YellrUtils.getLocation(getApplicationContext());
        String lat = String.valueOf(latLng[0]);
        String lng = String.valueOf(latLng[1]);

        String languageCode = Locale.getDefault().getLanguage();

        String url =  baseUrl
                + "?cuid=" + YellrUtils.getCUID(getApplicationContext())
                + "&language_code=" + languageCode
                + "&lat=" + lat
                + "&lng=" + lng;
        */

        String baseUrl = BuildConfig.BASE_URL + "/get_profile.json";
        String url = YellrUtils.buildUrl(getApplicationContext(),baseUrl);
        if (url != null) {

            //
            // TODO: need to check for exceptions better, this bombs out sometimes
            //
            try {

                //
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                HttpResponse response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();

                //
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));

                //
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                String profileJson = builder.toString();

                //Log.d("ProfileIntentService.UpdateData()","Broadcasting result ...");

                Log.d("ProfileIntentService.UpdateData()", "JSON: " + profileJson);

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(ProfileFragment.ProfileReceiver.ACTION_NEW_PROFILE);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra(PARAM_PROFILE_JSON, profileJson);
                sendBroadcast(broadcastIntent);

            } catch (Exception e) {

                Log.d("ProfileIntentService.UpdateData()", "Error: " + e.toString());

                //e.printStackTrace();
            }
        }
    }
}
