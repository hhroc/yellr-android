package yellr.net.yellr_android.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.activities.PostActivity;
import yellr.net.yellr_android.intent_services.assignments.Assignment;
import yellr.net.yellr_android.intent_services.assignments.AssignmentsIntentService;
import yellr.net.yellr_android.intent_services.assignments.AssignmentsResponse;
import yellr.net.yellr_android.receivers.CheckHttpAssignmentsReceiver;
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AssignmentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AssignmentsFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    //private String cuid;
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
        //this.cuid = YellrUtils.getCUID(getActivity().getApplicationContext());

        // init new assignments receiver
        Context context = getActivity().getApplicationContext();
        IntentFilter assignmentsFilter = new IntentFilter(AssignmentsIntentService.ACTION_NEW_ASSIGNMENTS);
        assignmentsFilter.addCategory(Intent.CATEGORY_DEFAULT);
        AssignmentsReceiver assignmentsReceiver = new AssignmentsReceiver();
        context.registerReceiver(assignmentsReceiver, assignmentsFilter);

        // init background service stories reciever
        //IntentFilter checkHttpAssignmentsFilter = new IntentFilter(AssignmentsIntentService.ACTION_NEW_ASSIGNMENTS);
        //checkHttpAssignmentsFilter.addCategory(Intent.CATEGORY_DEFAULT);
        //CheckHttpAssignmentsReceiver checkHttpAssignmentsReceiver = new CheckHttpAssignmentsReceiver();
        //context.registerReceiver(checkHttpAssignmentsReceiver, checkHttpAssignmentsFilter);

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

        // display pin wheel until there is something to show
        //if ( this.assignments == null || this.assignments.length == 0 ) {
        //    swipeRefreshLayout.setRefreshing(true);
        //}

        return view;
    }

    @Override
    public void onResume() {
        Log.d("AssignmentsFragment.onResume()", "Starting assignments intent service ...");
        refreshAssignmentData();

        // pop-up informing user about yellr
        if ( YellrUtils.isFirstBoot(getActivity()) ) {
            AboutMainFragment aboutMainFragment = new AboutMainFragment();
            aboutMainFragment.show(getFragmentManager(), AboutMainFragment.DIALOG_SHOW_ABOUT_MAIN);
        }

        super.onResume();
    }

    private void refreshAssignmentData() {
        // init service
        Context context = getActivity().getApplicationContext();
        Intent assignmentsWebIntent = new Intent(context, AssignmentsIntentService.class);
        //assignmentsWebIntent.putExtra(AssignmentsIntentService.PARAM_CUID, cuid);
        assignmentsWebIntent.setAction(AssignmentsIntentService.ACTION_GET_ASSIGNMENTS);
        context.startService(assignmentsWebIntent);
    }


    public class AssignmentsReceiver extends BroadcastReceiver {
        //public static final String ACTION_NEW_ASSIGNMENTS =
        //        "yellr.net.yellr_android.action.NEW_ASSIGNMENTS";

        public AssignmentsReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            String assignmentsJson = intent.getStringExtra(AssignmentsIntentService.PARAM_ASSIGNMENTS_JSON);

            assignments = YellrUtils.decodeAssignmentJson(context, assignmentsJson);

            assignmentsArrayAdapter.clear();

            // make sure that there are actually assignments to populate
            if ( assignments != null ) {
                for (int i = 0; i < assignments.length; i++) {
                    assignmentsArrayAdapter.add(assignments[i]);
                }
            }

            // remove the refreshing pin wheel.
            swipeRefreshLayout.setRefreshing(false);
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
            super(context, R.layout.fragment_assignment_row , R.id.frag_home_assignment_question_text, assignments);
            this.assignments = assignments;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = super.getView(position, convertView, parent);

            //LinearLayout linearLayoutWrapper = (LinearLayout)row.findViewById(R.id.frag_home_assignment_wrapper);
            if (this.assignments.get(position).has_responded) {
                row.setBackgroundColor(getResources().getColor(R.color.assignment_response_grey));
            } else {
                row.setBackgroundColor(getResources().getColor(R.color.background));
            }

            TextView textViewQuestionText = (TextView) row.findViewById(R.id.frag_home_assignment_question_text);
            TextView textViewOrganization = (TextView) row.findViewById(R.id.frag_home_assignment_organization);
            TextView textViewPostCount = (TextView) row.findViewById(R.id.frag_home_assignment_post_count);

            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");
            textViewOrganization.setTypeface(font);
            textViewPostCount.setTypeface(font);

            textViewQuestionText.setText(this.assignments.get(position).question_text);
            textViewOrganization.setText(getString(R.string.fa_user) + "   " + YellrUtils.shortenString(this.assignments.get(position).organization));
            textViewPostCount.setText(getString(R.string.fa_comments) + "  " + String.valueOf(this.assignments.get(position).post_count));

            return row;
        }
    }
}
