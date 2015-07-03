package yellr.net.yellr_android.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.activities.HomeActivity;
import yellr.net.yellr_android.activities.PostActivity;
import yellr.net.yellr_android.intent_services.publish_post.PublishPostIntentService;
import yellr.net.yellr_android.utils.YellrUtils;

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
    static final int SELECT_FILE = 12345;

    // Preview
    ImageView imagePreview;

    // Post Details
    String cuid;
    int assignmentId;
    String questionText;
    String questionDescription;

    TextView assignmentQuestion;
    TextView assignmentDescription;

    Button submitButton;

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

        // get the cuid
        //this.cuid = YellrUtils.getCUID(getActivity().getApplicationContext());



    }

    @Override
    public void onStop() {
        // unregister any receivers here.
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        submitButton = (Button)view.findViewById(R.id.frag_post_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitPostToYellr();
            }
        });

        postText = (EditText)view.findViewById(R.id.frag_post_edit_text);

        postText.setBackground(null);

        imageButton = (Button)view.findViewById(R.id.frag_post_photo_button);
        videoButton = (Button)view.findViewById(R.id.frag_post_video_button);
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getActivity(), "Coming Soon", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        audioButton = (Button)view.findViewById(R.id.frag_post_audio_button);
        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getActivity(), "Coming Soon", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        imagePreview = (ImageView)view.findViewById(R.id.frag_post_image_preview);

        assignmentQuestion = (TextView)view.findViewById(R.id.frag_post_assignment_question);
        assignmentDescription = (TextView)view.findViewById(R.id.frag_post_assignment_description);

        if(questionText == null){
            assignmentQuestion.setText(R.string.frag_post_assignment_title);
            assignmentDescription.setText(R.string.frag_post_assignment_description);
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

                final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals("Take Photo")) {

                            // Create an image file name
                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            String imageFileName = "JPEG_" + timeStamp + "_";
                            File storageDir = Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES);
                            File imageFile = null;
                            Log.d("image create","file: " + storageDir + "/" + imageFileName + ".jpg");
                            try {
                                imageFile = File.createTempFile(
                                        imageFileName,  /* prefix */
                                        ".jpg",         /* suffix */
                                        storageDir      /* directory */
                                );
                            } catch (IOException e) {
                                //e.printStackTrace();
                                Log.d("image create", "ERROR:" + e.toString());
                            }

                            proposedImageFilename = imageFile.getAbsolutePath();

                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(imageFile));//new File(proposedImageFilename)));
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                            }

                        } else if (items[item].equals("Choose from Library")) {

                            Intent takePictureIntent = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            takePictureIntent.setType("image/*");
//                            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//                            }
                            startActivityForResult(Intent.createChooser(takePictureIntent, "Select File"), SELECT_FILE);

                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();

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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            try {
                Bitmap imageBitmap;
                //if (data.getData() == null) {
                //    imageBitmap = (Bitmap) data.getExtras().get("data");
                //} else {
                //    imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                //}

                imageBitmap = BitmapFactory.decodeFile(this.proposedImageFilename);
                imagePreview.setImageBitmap(imageBitmap);

                //getBitmapFromCameraData()

                //
                // This code taken from here:
                //     https://github.com/rexstjohn/UltimateAndroidCameraGuide/blob/master/camera/src/main/java/com/ultimate/camera/fragments/SimpleAndroidImagePickerFragment.java

                //PostActivity activity = (PostActivity)getActivity();
                //Bitmap bitmap = getBitmapFromCameraData(data, activity);
                //imagePreview.setImageBitmap(bitmap);

                /*
                //
                // This code taken from here:
                //     https://github.com/rexstjohn/UltimateAndroidCameraGuide/blob/master/camera/src/main/java/com/ultimate/camera/fragments/SimpleAndroidImagePickerFragment.java

                // Get the dimensions of the View
                int targetW = imagePreview.getWidth();
                int targetH = imagePreview.getHeight();

                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(this.proposedImageFilename, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Determine how much to scale down the image
                int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;
                Bitmap bitmap = BitmapFactory.decodeFile(this.proposedImageFilename, bmOptions);
                imagePreview.setImageBitmap(bitmap);
                */

            }catch (Exception e) {
                // todo: display error
            }

            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //

            Log.d("PostFragment.onActivityResult()", "Attempting to display image thumbnail ...");



            // Save a file: path for use with ACTION_VIEW intents
            //proposedImageFilename = "file:" + imageFile.getAbsolutePath();


            //Toast.makeText(getActivity(), proposedImageFilename, Toast.LENGTH_SHORT).show();

            this.mediaType = "image";
            this.imageFilename = proposedImageFilename;

        } else if (requestCode == SELECT_FILE && resultCode == Activity.RESULT_OK && data != null) {

            try {

                Uri photoUri = data.getData();
                // Do something with the photo based on Uri
                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);

                imagePreview.setImageBitmap(selectedImage);

                File chosenPhotoFile = new File(photoUri.getPath());

                this.mediaType = "image";
                this.imageFilename = chosenPhotoFile.getAbsolutePath();

            }catch (Exception e) {
                // todo: display error
            }

            Log.d("PostFragment.onActivityResult()", "Attempting to display image thumbnail ...");

        }
    }

    public static Bitmap getBitmapFromCameraData(Intent data, Context context){
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return BitmapFactory.decodeFile(picturePath);
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

    private void SubmitPostToYellr() {

        if(postText.getText().toString().isEmpty()){
            Toast toast = Toast.makeText(getActivity(), "Please enter a message.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Intent postIntent = new Intent(getActivity(), PublishPostIntentService.class);
        //postIntent.putExtra(PublishPostIntentService.PARAM_CUID, cuid);
        postIntent.putExtra(PublishPostIntentService.PARAM_ASSIGNMENT_ID, assignmentId);
        postIntent.putExtra(PublishPostIntentService.PARAM_TEXT, postText.getText().toString());
        postIntent.putExtra(PublishPostIntentService.PARAM_MEDIA_TYPE, this.mediaType);
        postIntent.putExtra(PublishPostIntentService.PARAM_IMAGE_FILENAME, this.imageFilename);
        postIntent.putExtra(PublishPostIntentService.PARAM_AUDIO_FILENAME, this.audioFilename);
        postIntent.putExtra(PublishPostIntentService.PARAM_VIDEO_FILENAME, this.videoFilename);

        Log.d("SubmitPostToYellr()", "Starting PublishPostIntentService intent ...");

        Toast.makeText(getActivity(), R.string.frag_post_toast_sending_post, Toast.LENGTH_SHORT).show();

        // launch intent service
        getActivity().startService(postIntent);

        // reset display
        postText.setText("");
        assignmentQuestion.setText(getString(R.string.frag_post_assignment_title));
        assignmentDescription.setText(getString(R.string.frag_post_assignment_description));

        // go back to home screen
        Intent homeIntent = new Intent(getActivity(), HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);

        // remove from history stack
        this.getActivity().finish();
    }
}
