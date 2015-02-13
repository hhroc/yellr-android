package yellr.net.yellr_android.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.intent_services.profile.ProfileIntentService;
import yellr.net.yellr_android.intent_services.profile.ProfileResponse;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String clientId;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        Log.d("ProfileFragment.onResume()", "Starting profile intent service ...");
        refreshProfileData();
        super.onResume();
    }

    private void refreshProfileData() {
        // init service
        Context context = getActivity().getApplicationContext();
        Intent profileWebIntent = new Intent(context, ProfileIntentService.class);
        profileWebIntent.putExtra(ProfileIntentService.PARAM_CLIENT_ID, clientId);
        profileWebIntent.setAction(ProfileIntentService.ACTION_GET_PROFILE);
        context.startService(profileWebIntent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Log.d("ProfileFragment.onCreate()","Setting up IntentService receiver ...");

        // get clientId
        SharedPreferences sharedPref = getActivity().getSharedPreferences("clientId", Context.MODE_PRIVATE);
        clientId = sharedPref.getString("clientId", "");

        // init new profile receiver
        Context context = getActivity().getApplicationContext();
        IntentFilter profileFilter = new IntentFilter(ProfileReceiver.ACTION_NEW_PROFILE);
        profileFilter.addCategory(Intent.CATEGORY_DEFAULT);
        ProfileReceiver profileReceiver = new ProfileReceiver();
        context.registerReceiver(profileReceiver, profileFilter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

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

            /*
            {
                "first_name": "",
                "last_name": "",
                "verified": false,
                "success": true,
                "post_count": 1,
                "post_view_count": 0,
                "organization": "",
                "post_used_count": 0,
                "email": ""
            }
             */

            }

        }
    }
    
}