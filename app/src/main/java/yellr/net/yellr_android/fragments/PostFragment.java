package yellr.net.yellr_android.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import android.widget.EditText;

import com.google.gson.Gson;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.intent_services.publish_post.MediaObjectDefinition;
import yellr.net.yellr_android.intent_services.publish_post.PublishPostIntentService;

/**
 * Created by Andy on 2/6/2015.
 */
public class PostFragment extends Fragment {

    // Edit Text
    EditText caption;

    public static final String ARG_ASSIGNMENT_ID = "assignmentId";
    public static final String ARG_ASSIGNMENT_QUESTION = "assignmentQuestion";
    public static final String ARG_ASSIGNMENT_DESCRIPTION = "assignmentDescription";

    // Buttons
    Button imageButton;
    Button videoButton;
    Button audioButton;

    // Post Details
    String clientId;
    int assignmentId;
    String questionText;
    String questionDescription;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AssignmentsFragment.
     */
    public static PostFragment newInstance(int assignmentID, String questionText, String questionDescription) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ASSIGNMENT_ID, assignmentID);
        args.putSerializable(ARG_ASSIGNMENT_QUESTION, questionText);
        args.putSerializable(ARG_ASSIGNMENT_DESCRIPTION, questionDescription);

        PostFragment fragment = new PostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            assignmentId = (int)savedInstanceState.getSerializable(ARG_ASSIGNMENT_ID);
            questionText = (String)savedInstanceState.getSerializable(ARG_ASSIGNMENT_QUESTION);
            questionDescription = (String)savedInstanceState.getSerializable(ARG_ASSIGNMENT_DESCRIPTION);
        } else {
            assignmentId = (int)getArguments().getSerializable(ARG_ASSIGNMENT_ID);
            questionText = (String)getArguments().getSerializable(ARG_ASSIGNMENT_QUESTION);
            questionDescription = (String)getArguments().getSerializable(ARG_ASSIGNMENT_DESCRIPTION);
        }
        setHasOptionsMenu(true);

        // read the clientId from the device.
        // TODO: null pointer check
        SharedPreferences sharedPref = getActivity().getSharedPreferences("clientId", Context.MODE_PRIVATE);
        clientId = sharedPref.getString("clientId", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        caption = (EditText)view.findViewById(R.id.frag_post_edittext);

        imageButton = (Button)view.findViewById(R.id.frag_post_photo_button);
        videoButton = (Button)view.findViewById(R.id.frag_post_video_button);
        audioButton = (Button)view.findViewById(R.id.frag_post_audio_button);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");
        imageButton.setTypeface(font);
        videoButton.setTypeface(font);
        audioButton.setTypeface(font);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_post, menu);
        if(this.isAdded()){
                    /*New Story*/
            menu.findItem(R.id.action_post_upload).setIcon(
                    new IconDrawable(getActivity(), Iconify.IconValue.fa_upload)
                            .colorRes(R.color.black)
                            .actionBarSize()
            );
        } else {
            Log.d("onCreateOptionsMenu()", "Fragment not added to Activity");
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_post_upload:
                SubmitPostToYellr();
                break;
            default:
                break;
        }
        return true;
    }

    private void SubmitPostToYellr() {
        Gson gson = new Gson();
        MediaObjectDefinition mod = new MediaObjectDefinition();
        mod.mediaType = "text";
        mod.mediaText = caption.getText().toString();
        String mediaObject = "[" + gson.toJson(mod) + "]"; //Hack to make it an array

        Intent postIntent = new Intent(getActivity(), PublishPostIntentService.class);
        postIntent.putExtra("clientId", clientId);
        postIntent.putExtra("assignmentId", assignmentId);
        postIntent.putExtra("title", "_");
        postIntent.putExtra("mediaObjectDefinitionsJson", mediaObject);
        getActivity().startService(postIntent);

        caption.setText("");

        getActivity().finishActivity(Activity.RESULT_OK);
    }
}
