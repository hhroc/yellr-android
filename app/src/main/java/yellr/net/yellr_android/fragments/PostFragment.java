package yellr.net.yellr_android.fragments;

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

import org.json.JSONObject;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.intent_services.publish_post.MediaObjectDefinition;
import yellr.net.yellr_android.intent_services.publish_post.PublishPostIntentService;

/**
 * Created by Andy on 2/6/2015.
 */
public class PostFragment extends Fragment {

    // Edit Text
    EditText caption;

    // Buttons
    Button imageButton;
    Button videoButton;
    Button audioButton;

    // Post Details
    String clientId;
    int assignmentId;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AssignmentsFragment.
     */
    // TODO: Need AssigmentID Param
    public static PostFragment newInstance() {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
        }

        // read the clientId from the device.
        // TODO: null pointer check
        SharedPreferences sharedPref = getActivity().getSharedPreferences("clientId", Context.MODE_PRIVATE);
        clientId = sharedPref.getString("clientId", "");
        // TODO grab from PARAM
        assignmentId = 1;
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
                Gson gson = new Gson();
                MediaObjectDefinition mod = new MediaObjectDefinition();
                mod.mediaType = "text";
                mod.mediaText = caption.getText().toString();
                String mediaObject = "[" + gson.toJson(mod) + "]"; //Hack to make it an array


                Intent postIntent = new Intent(getActivity(), PublishPostIntentService.class);
                postIntent.putExtra("clientId", clientId);
                //TODO grab this from fragment PARAM
                postIntent.putExtra("assignmentId", assignmentId);
                postIntent.putExtra("title", "test");
                postIntent.putExtra("mediaObjectDefinitionsJson", mediaObject);
                getActivity().startService(postIntent);

                break;
            default:
                break;
        }
        return true;
    }
}
