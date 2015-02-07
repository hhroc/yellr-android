package yellr.net.yellr_android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import yellr.net.yellr_android.R;

/**
 * Created by TDuffy on 2/7/2015.
 */
public class ViewAssignmentFragment extends Fragment {

    public static final String ARG_ASSIGNMENT_QUESTION = "assignmentQuestion";
    public static final String ARG_ASSIGNMENT_DESCRIPTION = "assignmentDescription";

    private TextView assignmentQuestion;
    private TextView assignmentDescription;

    public ViewAssignmentFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_story, container, false);

        assignmentQuestion = (TextView) view.findViewById(R.id.frag_view_assignment_question);
        assignmentDescription = (TextView) view.findViewById(R.id.frag_view_assignment_description);

        Intent intent = getActivity().getIntent();

        String question = intent.getStringExtra(ViewAssignmentFragment.ARG_ASSIGNMENT_QUESTION);
        String description = intent.getStringExtra(ViewAssignmentFragment.ARG_ASSIGNMENT_DESCRIPTION);

        assignmentQuestion.setText(question);
        assignmentDescription.setText(description);

        return view;
    }
}
