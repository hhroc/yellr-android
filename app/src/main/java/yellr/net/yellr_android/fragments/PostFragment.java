package yellr.net.yellr_android.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.activities.HomeActivity;
import yellr.net.yellr_android.intent_services.publish_post.PublishPostIntentService;

/**
 * Created by Andy on 2/6/2015.
 */
public class PostFragment extends Fragment {

    // Edit Text
    EditText postText;

    public static final String ARG_ASSIGNMENT_ID = "assignmentId";
    public static final String ARG_ASSIGNMENT_QUESTION = "assignmentQuestion";
    public static final String ARG_ASSIGNMENT_DESCRIPTION = "assignmentDescription";

    // Buttons
    Button imageButton;
    Button videoButton;
    Button audioButton;

    static final int REQUEST_IMAGE_CAPTURE = 1234;

    // Preview
    ImageView imagePreview;

    // Post Details
    String clientId;
    int assignmentId;
    String questionText;
    String questionDescription;

    TextView assignmentQuestion;
    TextView assignmentDescription;

    String mediaType = "text";
    String imageFilename = "";
    String proposedImageFilename = "";
    String audioFilename = "";
    String videoFilename = "";

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

        postText = (EditText)view.findViewById(R.id.frag_post_edittext);

        imageButton = (Button)view.findViewById(R.id.frag_post_photo_button);
        videoButton = (Button)view.findViewById(R.id.frag_post_video_button);
        audioButton = (Button)view.findViewById(R.id.frag_post_audio_button);

        imagePreview = (ImageView)view.findViewById(R.id.frag_post_imagepreview);

        assignmentQuestion = (TextView)view.findViewById(R.id.frag_post_assignment_question);
        assignmentDescription = (TextView)view.findViewById(R.id.frag_post_assignment_description);

        if(questionText == null){
            assignmentQuestion.setText(R.string.fragment_post_assignment_title);
            assignmentDescription.setText(R.string.fragment_post_assignment_description);
        } else {
            assignmentQuestion.setText(questionText);
            assignmentDescription.setText(questionDescription);
        }

        imageButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if(getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) == false){
                    Toast.makeText(getActivity(), "This device does not have a camera.", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                    //Log.d("PostFragment.onActivityResult()","Launching Camera Intent ...");

                    File imageFile = null;
                    try {

                        final File path = new File( Environment.getExternalStorageDirectory(), getActivity().getApplicationContext().getPackageName() );
                        if(!path.exists()){
                            path.mkdir();
                        }
                        imageFile =  new File(path, "yellr.jpg");

                        //Toast toast2 = Toast.makeText(getActivity(), "Temp file created.", Toast.LENGTH_SHORT);
                        //toast2.show();

                        Log.d("PostFragment.onActivityResult()","Setting proposedImageFilename ...");

                        if ( imageFile != null ) {

                            proposedImageFilename = imageFile.getAbsolutePath();

                            //Toast toast2 = Toast.makeText(getActivity(), proposedImageFilename, Toast.LENGTH_SHORT);
                            //toast2.show();

                            Log.d("PostFragment.onActivityResult()","Configuring Intent, and starting Activity ...");

                            Uri fileUri = Uri.fromFile(imageFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                        } else {
                            Toast toast2 = Toast.makeText(getActivity(), "Image capture not supported on device.", Toast.LENGTH_SHORT);
                            toast2.show();
                        }


                    } catch (Exception ex) { //(IOException ex) {
                        // Error occurred while creating the File
                        Toast toast = Toast.makeText(getActivity(), ex.toString(), Toast.LENGTH_SHORT);
                        toast.show();

                        Log.d("imageButton.setOnClickListener().OnClick()", "Exception: " + ex.toString());

                    }


                }
            }
        });

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");
        imageButton.setTypeface(font);
        videoButton.setTypeface(font);
        audioButton.setTypeface(font);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(getActivity(), "We're back from taking a picture", Toast.LENGTH_SHORT).show();
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if ( resultCode == Activity.RESULT_OK ) {

                //Toast.makeText(getActivity(), "We event we successful with RESULT_OK", Toast.LENGTH_SHORT).show();

                Log.d("PostFragment.onActivityResult()", "Attempting to display image thumbnail ...");

                //Bundle extras = data.getExtras();
                //Bitmap imageBitmap = (Bitmap) extras.get("data");

                this.mediaType = "image";
                this.imageFilename = proposedImageFilename;

                Bitmap imageBitmap = BitmapFactory.decodeFile(this.imageFilename);
                imagePreview.setImageBitmap(imageBitmap);

            } else {
                Toast.makeText(getActivity(), "result code was not RESULT_OK", Toast.LENGTH_SHORT).show();
            }
        }
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
        //Gson gson = new Gson();
        //MediaObjectDefinition mod = new MediaObjectDefinition();
        //mod.mediaType = "text";
        //mod.mediaText = postText.getText().toString();
        //String mediaObject = "[" + gson.toJson(mod) + "]"; //Hack to make it an array

        Intent postIntent = new Intent(getActivity(), PublishPostIntentService.class);
        postIntent.putExtra("clientId", clientId);
        postIntent.putExtra("assignmentId", assignmentId);
        postIntent.putExtra("text", postText.getText().toString());
        postIntent.putExtra("mediaType", this.mediaType);
        postIntent.putExtra(PublishPostIntentService.PARAM_IMAGE_FILENAME, this.imageFilename);
        postIntent.putExtra(PublishPostIntentService.PARAM_AUDIO_FILENAME, this.audioFilename);
        postIntent.putExtra(PublishPostIntentService.PARAM_VIDEO_FILENAME, this.videoFilename);

        Log.d("SubmitPostToYellr()","Starting PublishPostIntentService intent ...");

        Toast.makeText(getActivity(), "Sending " + this.imageFilename, Toast.LENGTH_SHORT).show();

        //postIntent.putExtra("title", "_");
        //postIntent.putExtra("mediaObjectDefinitionsJson", mediaObject);
        getActivity().startService(postIntent);

        postText.setText("");
        assignmentQuestion.setText(R.string.fragment_post_assignment_title);
        assignmentDescription.setText(R.string.fragment_post_assignment_description);



        Intent homeIntent = new Intent(getActivity(), HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }
}
