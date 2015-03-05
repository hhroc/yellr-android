package yellr.net.yellr_android.intent_services.data;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.gson.Gson;

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

public class DataIntentService extends IntentService {
    public static final String ACTION_GET_DATA =
            "yellr.net.yellr_android.action.GET_DATA";
    public static final String ACTION_NEW_DATA =
            "yellr.net.yellr_android.action.NEW_DATA";

    public static final String PARAM_CUID = "cuid";
    public static final String PARAM_DATA_JSON = "dataJson";

    public DataIntentService() {
        super("DataIntentService");
        //Log.d("DataIntentService()","Constructor.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //Log.d("DataIntentService.onHandleIntent()","Decoding intent action ...");

        //String cuid = intent.getStringExtra(PARAM_CUID);
        if ( YellrUtils.isHomeLocationSet(getApplicationContext()) )
            handleActionGetData(); //cuid);
    }

    /**
     * Handles get data
     */
    private void handleActionGetData() {

        String baseUrl = BuildConfig.BASE_URL + "/get_data.json";

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
                + "?cuid=" + YellrUtils.getCUID(getApplicationContext())//cuid
                + "&language_code=" + languageCode
                + "&lat=" + lat
                + "&lng=" + lng;

        //Log.d("DataIntentService.UpdateData()","URL: " + url);

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

            String dataJson = builder.toString();

            //Log.d("DataIntentService.UpdateData()","Broadcasting result ...");

            Log.d("DataIntentService.UpdateData()","JSON: " + dataJson);

            //Gson gson

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(DataIntentService.ACTION_NEW_DATA);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(DataIntentService.PARAM_DATA_JSON, dataJson);
            sendBroadcast(broadcastIntent);

        } catch( Exception e) {

            Log.d("DataIntentService.UpdateData()","Error: " + e.toString());

            //e.printStackTrace();
        }
    }
}
