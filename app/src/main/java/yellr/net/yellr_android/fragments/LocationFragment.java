package yellr.net.yellr_android.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.regex.Pattern;

import yellr.net.yellr_android.BuildConfig;
import yellr.net.yellr_android.R;
import yellr.net.yellr_android.activities.HomeActivity;
import yellr.net.yellr_android.activities.PostActivity;
import yellr.net.yellr_android.intent_services.assignments.AssignmentsIntentService;
import yellr.net.yellr_android.intent_services.profile.ProfileIntentService;
import yellr.net.yellr_android.intent_services.profile.ProfileResponse;
import yellr.net.yellr_android.intent_services.zipcode.ZipcodeIntentService;
import yellr.net.yellr_android.intent_services.zipcode.ZipcodeResponse;
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * Created by TDuffy on 2/22/2015.
 */
public class LocationFragment extends Fragment {

    private EditText zipcodeEditText;
    private Button nextButton;

    public LocationFragment() {
    }

    public static LocationFragment newInstance() {
        Bundle args = new Bundle();
        //args.putSerializable(ARG_ASSIGNMENT_ID, assignmentID);
        //args.putSerializable(ARG_ASSIGNMENT_QUESTION, questionText);
        // args.putSerializable(ARG_ASSIGNMENT_DESCRIPTION, questionDescription);

        LocationFragment fragment = new LocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);

//        /showZipcodeDialog();

        zipcodeEditText = (EditText)view.findViewById(R.id.frag_location_zipcode);

        zipcodeEditText.addTextChangedListener(new TextWatcher() {


            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {
                // if the user hit enter, then remove the \n and call the nextButton.onClick() function
                String text = s.toString();
                //Log.d("afterTextChanged()","text: " + text + ", text(-1): " + text.substring(text.length() - 1));
                if ( text.length() > 0 && text.substring(text.length() - 1).equals("\n") ) {
                    s.replace(s.length() - 1, s.length(), "");
                    nextButton.callOnClick();
                }
            }
        });

        nextButton = (Button)view.findViewById(R.id.frag_location_next_button);

        nextButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Log.d("LocationFragment.onCreateView().setOnClickListener()","Calling ZipcodeIntentService ...");

                String zipcode = String.valueOf(zipcodeEditText.getText()).trim();

                String regex = "^\\d{5}(-\\d{4})?$";
                if ( Pattern.matches(regex,zipcode) ) {

                    Context context = getActivity().getApplicationContext();
                    Intent zipcodeWebIntent = new Intent(context, ZipcodeIntentService.class);
                    zipcodeWebIntent.putExtra(ZipcodeIntentService.PARAM_ZIPCODE, zipcode);
                    zipcodeWebIntent.setAction(ZipcodeIntentService.ACTION_GET_ZIPCODE);
                    context.startService(zipcodeWebIntent);

                    nextButton.setEnabled(false);
                    nextButton.setBackground(getResources().getDrawable(R.drawable.yellr_button_deactivated));
                } else {

                    Toast.makeText(getActivity(), "Please enter a valid Zipcode.", Toast.LENGTH_SHORT).show();

                }
            }

        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        // init new profile receiver
        Context context = getActivity().getApplicationContext();
        IntentFilter zipcodeFilter = new IntentFilter(ZipcodeIntentService.ACTION_NEW_ZIPCODE);
        zipcodeFilter.addCategory(Intent.CATEGORY_DEFAULT);
        ZipcodeReceiver zipcodeReceiver = new ZipcodeReceiver();
        context.registerReceiver(zipcodeReceiver, zipcodeFilter);


    }

    @Override
    public void onResume() {
        //showZipcodeDialog();
        super.onResume();
    }

    public void showZipcodeDialog(String zipcode, String city, String stateCode, Float lat, Float lng){

        SetHomeLocationFragment setHomeLocationFragment = new SetHomeLocationFragment();
        Bundle setHomeLocationBundle = new Bundle();
        setHomeLocationBundle.putString("zipcode",zipcode);
        setHomeLocationBundle.putString("city",city);
        setHomeLocationBundle.putString("stateCode",stateCode);
        setHomeLocationBundle.putFloat("lat",lat);
        setHomeLocationBundle.putFloat("lng",lng);
        setHomeLocationFragment.setArguments(setHomeLocationBundle);
        setHomeLocationFragment.setTargetFragment(this, 1);
        setHomeLocationFragment.show(getFragmentManager(), SetHomeLocationFragment.DIALOG_SET_LOCATION);

        return;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Stuff to do, dependent on requestCode and resultCode
        //if(requestCode ==)  // 1 is an arbitrary number, can be any int
        //{
            Log.d("onActivityResult()", "requestCode: " + requestCode + ", resultCode: " + resultCode + ", Activity.RESULT_OK: " + Activity.RESULT_OK);

            // This is the return result of your DialogFragment
            if(resultCode == Activity.RESULT_OK) // 1 is an arbitrary number, can be any int
            {
                Intent intent;
                intent = new Intent(getActivity(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                nextButton.setEnabled(true);
                nextButton.setBackground(getResources().getDrawable( R.drawable.yellr_button));
            }
        //}
    }

    public class ZipcodeReceiver extends BroadcastReceiver {
        //public static final String ACTION_NEW_ZIPCODE =
        //        "yellr.net.yellr_android.action.NEW_PROFILE";

        public ZipcodeReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String zipcodeJson = intent.getStringExtra(ZipcodeIntentService.PARAM_ZIPCODE_JSON);

            Log.d("ZipcodeFragment.onReceive", "JSON: " + zipcodeJson);

            Gson gson = new Gson();
            ZipcodeResponse response = new ZipcodeResponse();
            try{
                response = gson.fromJson(zipcodeJson, ZipcodeResponse.class);
            } catch (Exception e){
                Log.d("ZipcodeFragment.onReceive", "GSON puked");
            }

            if ( response.success ) {

                showZipcodeDialog(response.zipcode, response.city, response.state_code, response.lat, response.lng);

            } else {

                Toast.makeText(getActivity(), "Please enter a valid Zipcode.", Toast.LENGTH_SHORT).show();

                nextButton.setEnabled(true);
                nextButton.setBackground(getResources().getDrawable( R.drawable.yellr_button));

            }

        }
    }
}
