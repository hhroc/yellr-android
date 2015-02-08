package yellr.net.yellr_android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.activities.PostActivity;

/**
 * Created by TDuffy on 2/7/2015.
 */
public class ViewAssignmentFragment extends Fragment {

    public static final String ARG_ASSIGNMENT_QUESTION = "assignmentQuestion";
    public static final String ARG_ASSIGNMENT_DESCRIPTION = "assignmentDescription";
    public static final String ARG_ASSIGNMENT_ID = "assignmentID";

    private TextView assignmentQuestion;
    private TextView assignmentDescription;
    private Button assignmentContribute;

    public ViewAssignmentFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_assignment, container, false);

        assignmentQuestion = (TextView) view.findViewById(R.id.frag_view_assignment_question);
        assignmentDescription = (TextView) view.findViewById(R.id.frag_view_assignment_description);
        assignmentContribute = (Button) view.findViewById(R.id.frag_view_assignment_contribute_button);

        Intent intent = getActivity().getIntent();

        String question = intent.getStringExtra(ViewAssignmentFragment.ARG_ASSIGNMENT_QUESTION);
        String description = intent.getStringExtra(ViewAssignmentFragment.ARG_ASSIGNMENT_DESCRIPTION);
        int rawAssignmentId = (int)intent.getIntExtra(ViewAssignmentFragment.ARG_ASSIGNMENT_ID,0);
        final String assignmentId = String.valueOf(rawAssignmentId);

        assignmentQuestion.setText(question);
        assignmentDescription.setText(description);
        assignmentContribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                intent = new Intent(getActivity().getApplicationContext(), PostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // TODO: puExtra for assignment ID
                intent.putExtra(PostFragment.ARG_ASSIGNMENT_ID, assignmentId); // save assignmentId from getStringExtra in onCreateView()

                startActivity(intent);

            }
        });

        // TODO: set assignmentContribute onClick to launch PostActivity

        return view;
    }

}
