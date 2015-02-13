/*
package yellr.net.yellr_android.intent_services.profile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

public class ProfileReceiver extends BroadcastReceiver {
    public static final String ACTION_NEW_PROFILE =
            "yellr.net.yellr_android.action.NEW_PROFILE";

    public ProfileReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //Log.d("ProfileReceiver.onReceive()", "onReceive called.");

        String profileJson = intent.getStringExtra(ProfileIntentService.PARAM_PROFILE_JSON);

        //Log.d("ProfileReceiver.onReceive()", "JSON: " + profileJson);

        Gson gson = new Gson();
        ProfileResponse response = gson.fromJson(profileJson, ProfileResponse.class);

        if ( response.success ) {

            // TODO: populate GUI with array of Profile


//            {
//                "first_name": "",
//                "last_name": "",
//                "verified": false,
//                "success": true,
//                "post_count": 1,
//                "post_view_count": 0,
//                "organization": "",
//                "post_used_count": 0,
//                "email": ""
//            }


        }

    }
}
*/
