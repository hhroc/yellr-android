package yellr.net.yellr_android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import yellr.net.yellr_android.R;


public class PollFragment extends Fragment {

    public static final String ARG_ASSIGNMENT_ID = "assignmentId";
    public static final String ARG_POLL_TEXT = "pollText";
    public static final String ARG_POLL_OPTIONS = "pollOptions";

    LinearLayout pollOptionContainer;
    Button submitButton;

    String cuid;
    int assignmentId;
    String pollText;
    String pollOptions;

    public static PollFragment newInstance(int assignmentID, String pollText, String pollOptions) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ASSIGNMENT_ID, assignmentID);
        args.putSerializable(ARG_POLL_TEXT, pollText);
        args.putSerializable(ARG_POLL_OPTIONS, pollOptions);

        PollFragment fragment = new PollFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PollFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            assignmentId = (int)savedInstanceState.getSerializable(ARG_ASSIGNMENT_ID);
            pollText = (String)savedInstanceState.getSerializable(ARG_POLL_TEXT);
            pollOptions = (String)savedInstanceState.getSerializable(ARG_POLL_OPTIONS);
        } else {
            assignmentId = (int)getArguments().getSerializable(ARG_ASSIGNMENT_ID);
            pollText = (String)getArguments().getSerializable(ARG_POLL_TEXT);
            pollOptions = (String)getArguments().getSerializable(ARG_POLL_OPTIONS);
        }

        setHasOptionsMenu(true);

        // get the cuid
        //this.cuid = YellrUtils.getCUID(getActivity().getApplicationContext());



    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poll, container, false);

        pollOptionContainer = (LinearLayout) view.findViewById(R.id.frag_poll_container);

        final RadioButton[] rb = new RadioButton[5];
        RadioGroup rg = new RadioGroup(getActivity().getApplicationContext()); //create the RadioGroup
        rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
        for(int i=0; i<5; i++){
            rb[i]  = new RadioButton(getActivity().getApplicationContext());
            rg.addView(rb[i]);
            rb[i].setText(" AA BB ");
            rb[i].setTextColor(getResources().getColor(R.color.grey));
            rb[i].setId(i + 100);
            rb[i].setWidth(200);
            //rg.addView(rb[i]);
        }
        pollOptionContainer.addView(rg);//you add the whole RadioGroup to the layout

        submitButton = (Button) view.findViewById(R.id.frag_post_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitPollToYellr();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_post, menu);
        if(this.isAdded()){
            menu.findItem(R.id.action_post_submit).setTitle("POST");
            //.setIcon(
            //new IconDrawable(getActivity(), Iconify.IconValue.fa_upload)
            //        .colorRes(R.color.black)
            //        .actionBarSize()
            //);

        } else {
            Log.d("onCreateOptionsMenu()", "Fragment not added to Activity");
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            //case R.id.action_post_submit:
            //    SubmitPostToYellr();
            //    break;
            default:
                break;
        }
        return true;
    }

    private void SubmitPollToYellr() {

    }

}
