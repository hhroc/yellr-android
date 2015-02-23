package yellr.net.yellr_android.intent_services.zipcode;

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
import yellr.net.yellr_android.utils.YellrUtils;

public class ZipcodeIntentService extends IntentService {
    public static final String ACTION_GET_ZIPCODE =
            "yellr.net.yellr_android.action.GET_ZIPCODE";
    public static final String ACTION_NEW_ZIPCODE =
            "yellr.net.yellr_android.action.NEW_ZIPCODE";

    //public static final String PARAM_CUID = "clientId";
    public static final String PARAM_ZIPCODE =
            "yellr.net.yellr_android.param.ZIPCODE";
    public static final String PARAM_ZIPCODE_JSON = "zipcodeJson";


    public ZipcodeIntentService() {
        super("ZipcodeIntentService");
        //Log.d("ZipcodeIntentService()","Constructor.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //Log.d("ZipcodeIntentService.onHandleIntent()","Decoding intent action ...");

        String zipcode = intent.getStringExtra(PARAM_ZIPCODE);
        handleActionGetZipcode(zipcode); //cuid);
    }

    /**
     * Handles get Zipcode
     */
    private void handleActionGetZipcode(String zipcode) {

        String baseUrl = BuildConfig.BASE_URL + "/zipcode_lookup.json";

        //double latLng[] = YellrUtils.getLocation(getApplicationContext());
        //String lat = String.valueOf(latLng[0]);
        //String lng = String.valueOf(latLng[1]);

        //String languageCode = Locale.getDefault().getLanguage();

        String url = baseUrl
                + "?cuid=" + YellrUtils.getCUID(getApplicationContext())//cuid
                + "&language_code=" + Locale.getDefault().getLanguage()
                + "&lat=" + 0
                + "&lng=" + 0
                + "&zipcode=" + zipcode;

        //Log.d("ZipcodeIntentService.UpdateData()","URL: " + url);

        //
        // TODO: need to check for exceptions better, this bombs out sometimes
        //
        try {

            //Log.d("ZipcodeIntentService.UpdateData()","Attempting HTTP connection ...");

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

            String zipcodeJson = builder.toString();

            Log.d("ZipcodeIntentService.handleActionGetZipcode()","JSON: " + zipcodeJson);

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(ZipcodeIntentService.ACTION_NEW_ZIPCODE);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(ZipcodeIntentService.PARAM_ZIPCODE_JSON, zipcodeJson);
            sendBroadcast(broadcastIntent);

        } catch( Exception e) {

            Log.d("ZipcodeIntentService.handleActionGetZipcode()","Error: " + e.toString());

            //e.printStackTrace();
        }
    }
}
