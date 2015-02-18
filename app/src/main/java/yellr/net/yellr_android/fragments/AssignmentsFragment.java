package yellr.net.yellr_android.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import yellr.net.yellr_android.activities.PostActivity;
import yellr.net.yellr_android.intent_services.assignments.Assignment;
import yellr.net.yellr_android.intent_services.assignments.AssignmentsIntentService;
import yellr.net.yellr_android.intent_services.assignments.AssignmentsResponse;
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AssignmentsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AssignmentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AssignmentsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private String cuid;
    private AssignmentsArrayAdapter assignmentsArrayAdapter;

    private Assignment[] assignments;
    //private AssignmentsArrayAdapter assignmentsArrayAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AssignmentsFragment.
     */
    public static AssignmentsFragment newInstance() {
        AssignmentsFragment fragment = new AssignmentsFragment();
        Bundle args = new Bundle();
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
        }

        // get the cuid
        this.cuid = YellrUtils.getCUID(getActivity().getApplicationContext());

        // init new assignments receiver
        Context context = getActivity().getApplicationContext();
        IntentFilter assignmentsFilter = new IntentFilter(AssignmentsReceiver.ACTION_NEW_ASSIGNMENTS);
        assignmentsFilter.addCategory(Intent.CATEGORY_DEFAULT);
        AssignmentsReceiver assignmentsReceiver = new AssignmentsReceiver();
        context.registerReceiver(assignmentsReceiver, assignmentsFilter);

        assignmentsArrayAdapter = new AssignmentsArrayAdapter(getActivity(), new ArrayList<Assignment>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_assignments, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.frag_home_assignment_swipe_refresh_layout);
        listView = (ListView)view.findViewById(R.id.assignmentsList);

        listView.setAdapter(assignmentsArrayAdapter);
        listView.setOnItemClickListener(new AssignmentListOnClickListener());

        // This appear to be a linting error the Android Docs call for a Color Resource to be used here...
        // https://developer.android.com/reference/android/support/v4/widget/SwipeRefreshLayout.html#setProgressBackgroundColor(int)
        swipeRefreshLayout.setProgressBackgroundColor(R.color.yellow);
        swipeRefreshLayout.setColorSchemeResources(R.color.black);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAssignmentData();
            }
        });

        return view;
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
        Log.d("AssignmentsFragment.onResume()", "Starting assignments intent service ...");
        refreshAssignmentData();
        super.onResume();
    }

    private void refreshAssignmentData() {
        // init service
        Context context = getActivity().getApplicationContext();
        Intent assignmentsWebIntent = new Intent(context, AssignmentsIntentService.class);
        assignmentsWebIntent.putExtra(AssignmentsIntentService.PARAM_CUID, cuid);
        assignmentsWebIntent.setAction(AssignmentsIntentService.ACTION_GET_ASSIGNMENTS);
        context.startService(assignmentsWebIntent);
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

        public AssignmentsReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            String assignmentsJson = intent.getStringExtra(AssignmentsIntentService.PARAM_ASSIGNMENTS_JSON);

            Gson gson = new Gson();
            AssignmentsResponse response = gson.fromJson(assignmentsJson, AssignmentsResponse.class);

            if (response.success) {
                assignmentsArrayAdapter.clear();
                assignments = new Assignment[response.assignments.length];
                for (int i = 0; i < response.assignments.length; i++) {
                    Assignment assignment = response.assignments[i];
                    assignmentsArrayAdapter.add(assignment);
                    assignments[i] = assignment;
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    class AssignmentListOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            Intent intent;
            intent = new Intent(getActivity().getApplicationContext(), PostActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra(PostFragment.ARG_ASSIGNMENT_QUESTION, assignments[position].question_text);
            intent.putExtra(PostFragment.ARG_ASSIGNMENT_DESCRIPTION, assignments[position].description);
            intent.putExtra(PostFragment.ARG_ASSIGNMENT_ID, assignments[position].assignment_id);

            startActivity(intent);
        }

    }

    class AssignmentsArrayAdapter extends ArrayAdapter<Assignment> {

        private ArrayList<Assignment> assignments;

        public AssignmentsArrayAdapter(Context context, ArrayList<Assignment> assignments) {
            super(context, R.layout.fragment_assignment_row, R.id.frag_home_assignment_question_text, assignments);
            this.assignments = assignments;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = super.getView(position, convertView, parent);

            TextView textViewQuestionText = (TextView) row.findViewById(R.id.frag_home_assignment_question_text);
            TextView textViewOrganization = (TextView) row.findViewById(R.id.frag_home_assignment_organization);
            TextView textViewPostCount = (TextView) row.findViewById(R.id.frag_home_assignment_post_count);

            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");
            textViewPostCount.setTypeface(font);

            textViewQuestionText.setText(this.assignments.get(position).question_text);
            textViewOrganization.setText("Organization: " + this.assignments.get(position).organization);
            textViewPostCount.setText(getString(R.string.fa_comments) + " " + String.valueOf(this.assignments.get(position).post_count));

            return row;
        }
    }
}
