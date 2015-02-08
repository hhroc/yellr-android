package yellr.net.yellr_android.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.activities.HomeActivity;
import yellr.net.yellr_android.activities.ViewAssignmentActivity;
import yellr.net.yellr_android.intent_services.IntentServicesHelper;
import yellr.net.yellr_android.intent_services.assignments.Assignment;
import yellr.net.yellr_android.intent_services.assignments.AssignmentsIntentService;
import yellr.net.yellr_android.intent_services.assignments.AssignmentsResponse;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AssignmentsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AssignmentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AssignmentsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Assignment[] assignments;
    //private AssignmentsArrayAdapter assignmentsArrayAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AssignmentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AssignmentsFragment newInstance(String param1, String param2) {
        AssignmentsFragment fragment = new AssignmentsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AssignmentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // init new assignments receiver
        Context context = getActivity().getApplicationContext();
        IntentFilter assignmentsFilter = new IntentFilter(AssignmentsReceiver.ACTION_NEW_ASSIGNMENTS);
        assignmentsFilter.addCategory(Intent.CATEGORY_DEFAULT);
        AssignmentsReceiver assignmentsReceiver = new AssignmentsReceiver();
        context.registerReceiver(assignmentsReceiver, assignmentsFilter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assignments, container, false);
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

    @Override
    public void onResume() {

        // get clientId
        SharedPreferences sharedPref = getActivity().getSharedPreferences("clientId", Context.MODE_PRIVATE);
        String clientId = sharedPref.getString("clientId", "");

        Log.d("AssignmentsFragment.onResume()", "Starting assignments intent service ...");

        // init service
        Context context = getActivity().getApplicationContext();
        Intent assignmentsWebIntent = new Intent(context, AssignmentsIntentService.class);
        assignmentsWebIntent.putExtra(AssignmentsIntentService.PARAM_CLIENT_ID, clientId);
        assignmentsWebIntent.setAction(AssignmentsIntentService.ACTION_GET_ASSIGNMENTS);
        context.startService(assignmentsWebIntent);

        super.onResume();
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

    public class AssignmentsReceiver extends BroadcastReceiver {
        public static final String ACTION_NEW_ASSIGNMENTS =
                "yellr.net.yellr_android.action.NEW_ASSIGNMENTS";

        private ListView listView;

        public AssignmentsReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            listView = (ListView)getView().findViewById(R.id.assignmentsList);

            //Log.d("AssignmentsReceiver.onReceive()", "onReceive called.");

            String assignmentsJson = intent.getStringExtra(AssignmentsIntentService.PARAM_ASSIGNMENTS_JSON);

            //Log.d("AssignmentsReceiver.onReceive()", "JSON: " + assignmentsJson);

            Gson gson = new Gson();
            AssignmentsResponse response = gson.fromJson(assignmentsJson, AssignmentsResponse.class);

            if (response.success) {

                AssignmentsArrayAdapter assignmentsArrayAdapter = new AssignmentsArrayAdapter(getActivity(), new ArrayList<Assignment>());

                assignments = new Assignment[response.assignments.length];
                for (int i = 0; i < response.assignments.length; i++) {
                    Assignment assignment = response.assignments[i];
                    assignmentsArrayAdapter.add(assignment);
                    assignments[i] = assignment;
                }

                //Log.d("AssignmentsReceiver.onReceive()", "Setting listView adapter ...");

                listView.setAdapter(assignmentsArrayAdapter);
                listView.setOnItemClickListener(new AssignmentListOnClickListener());

            }

        }
    }

    class AssignmentListOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            Intent intent;
            intent = new Intent(getActivity().getApplicationContext(), ViewAssignmentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra(ViewAssignmentFragment.ARG_ASSIGNMENT_QUESTION, assignments[position].question_text);
            intent.putExtra(ViewAssignmentFragment.ARG_ASSIGNMENT_DESCRIPTION, assignments[position].description);
            intent.putExtra(ViewAssignmentFragment.ARG_ASSIGNMENT_ID, assignments[position].assignment_id);

            startActivity(intent);
        }

    }

    class AssignmentsArrayAdapter extends ArrayAdapter<Assignment> {

        private ArrayList<Assignment> assignments;

        public AssignmentsArrayAdapter(Context context, ArrayList<Assignment> assignments) {
            super(context, R.layout.fragment_assignment_row, R.id.frag_home_assignment_question_text, assignments);
            this.assignments = assignments;

            //Log.d("AssignmentsArrayAdapter.AssignmentsArrayAdapter()","Constructor.");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = super.getView(position, convertView, parent);

            //Log.d("AssignmentsArrayAdapter.getView()","Setting values for view.");

            TextView textViewQuestionText = (TextView) row.findViewById(R.id.frag_home_assignment_question_text);
            TextView textViewOrganization = (TextView) row.findViewById(R.id.frag_home_assignment_organization);
            TextView textViewPostCount = (TextView) row.findViewById(R.id.frag_home_assignment_post_count);

            textViewQuestionText.setText(this.assignments.get(position).question_text);
            textViewOrganization.setText("Organization: " + this.assignments.get(position).organization);
            textViewPostCount.setText("Responses: " + String.valueOf(this.assignments.get(position).post_count));

            return row;
        }

    }


}
