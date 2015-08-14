package yellr.net.yellr_android.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import yellr.net.yellr_android.BuildConfig;
import yellr.net.yellr_android.R;
import yellr.net.yellr_android.intent_services.local_posts.MediaObject;
import yellr.net.yellr_android.intent_services.local_posts.Post;
import yellr.net.yellr_android.intent_services.post_vote.VoteIntentService;
import yellr.net.yellr_android.intent_services.report_post.ReportPostIntentService;
import yellr.net.yellr_android.utils.LocalPostViewHolder;
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * Created by TDuffy on 4/16/2015.
 */
public class ViewPostFragment extends Fragment {

    public static final String ARG_POST_LIST_POSITION = "postListPosition";

    public static final String ARG_POST_ID = "postId";
    public static final String ARG_POST_DATETIME = "postDateTime";
    public static final String ARG_POST_QUESTION_TEXT = "postQuestionText";
    public static final String ARG_POST_UP_VOTE_COUNT = "postUpVoteCount";
    public static final String ARG_POST_DOWN_VOTE_COUNT = "postDownVoteCount";
    public static final String ARG_POST_HAS_VOTED = "postHasVoted";
    public static final String ARG_POST_IS_UP_VOTE = "postIsUpVote";

    public static final String ARG_POST_MEDIA_OBJECT_TEXT = "postMediaObjectText";
    public static final String ARG_POST_MEDIA_OBJECT_FILENAME = "postMediaObjectFilename";
    public static final String ARG_POST_MEDIA_OBJECT_MEDIA_TYPE = "postMediaObjectMediaType";

    // declare the dialog as a member field of your activity
    static ProgressDialog mProgressDialog;
    public static LinearLayout videoViewPostVideo;
    public static VideoView videoViewPostVideoInner;
    Button reportButton;

    public ViewPostFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);

        final Post post = new Post();

        this.reportButton = (Button)view.findViewById(R.id.report_this_post);
        this.videoViewPostVideo = (LinearLayout) view.findViewById(R.id.frag_view_post_video);
        this.videoViewPostVideoInner = (VideoView) view.findViewById(R.id.frag_view_post_video_inner);

        Intent intent = getActivity().getIntent();

        int position = intent.getIntExtra(ViewPostFragment.ARG_POST_LIST_POSITION, 0);

        post.post_id = intent.getIntExtra(ViewPostFragment.ARG_POST_ID, 0);
        post.post_datetime = intent.getStringExtra(ViewPostFragment.ARG_POST_DATETIME);
        post.question_text = intent.getStringExtra(ViewPostFragment.ARG_POST_QUESTION_TEXT);
        post.up_vote_count = intent.getIntExtra(ViewPostFragment.ARG_POST_UP_VOTE_COUNT,0);
        post.down_vote_count = intent.getIntExtra(ViewPostFragment.ARG_POST_DOWN_VOTE_COUNT,0);
        post.has_voted = intent.getIntExtra(ViewPostFragment.ARG_POST_HAS_VOTED,0);
        post.is_up_vote = intent.getIntExtra(ViewPostFragment.ARG_POST_IS_UP_VOTE,0);

        post.media_objects = new MediaObject[1];
        post.media_objects[0] = new MediaObject();

        post.media_objects[0].media_text = intent.getStringExtra(ViewPostFragment.ARG_POST_MEDIA_OBJECT_TEXT);
        post.media_objects[0].caption = intent.getStringExtra(ViewPostFragment.ARG_POST_MEDIA_OBJECT_TEXT);
        post.media_objects[0].file_name = intent.getStringExtra(ViewPostFragment.ARG_POST_MEDIA_OBJECT_FILENAME);
        post.media_objects[0].media_type_name = intent.getStringExtra(ViewPostFragment.ARG_POST_MEDIA_OBJECT_MEDIA_TYPE);

        //hide video/audio view
        if (post.media_objects[0].media_type_name.equals("image")) {
            this.videoViewPostVideo.setVisibility(View.GONE);
        } else if (post.media_objects[0].media_type_name.equals("audio")) {
            this.videoViewPostVideo.setVisibility(View.GONE);
        } else if (post.media_objects[0].media_type_name.equals("video")) {
            this.videoViewPostVideo.setVisibility(View.VISIBLE);
        }

        LocalPostViewHolder localPostViewHolder = new LocalPostViewHolder(getActivity().getApplicationContext(), position, view, true);

        localPostViewHolder.build(getActivity().getApplicationContext(), post);

        this.reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getActivity(), "Report Received!", Toast.LENGTH_SHORT);
                toast.show();

                Intent reportPostIntent = new Intent(getActivity().getApplicationContext(), ReportPostIntentService.class);
                reportPostIntent.putExtra(ReportPostIntentService.PARAM_POST_ID, post.post_id);
                getActivity().startService(reportPostIntent);

            }

        });

        //instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("A message");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        //video / audio url
        String url = BuildConfig.BASE_URL + "/media/" + post.media_objects[0].file_name;
        //url = url.replace("p.mp4",".mp4");

        // execute this when the downloader must be fired
        final DownloadTask downloadTask = new DownloadTask(getActivity().getApplicationContext());
        downloadTask.execute(url);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });

        return view;
    }

    // usually, subclasses of AsyncTask are declared inside the activity class.
    // that way, you can easily modify the UI thread from here
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream("/sdcard/file_name_1.mp4");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else {

                String url = "/sdcard/file_name_1.mp4";
//                    MediaController mediaController = new MediaController(context);
//                    mediaController.setAnchorView(this.videoViewPostVideo);
//                    Uri video = Uri.parse(url);
//                    this.videoViewPostVideo.setMediaController(mediaController);
//                    this.videoViewPostVideo.setVideoURI(video);
//                    this.videoViewPostVideo.start();
                ViewPostFragment.videoViewPostVideoInner.setVideoPath(url);
                ViewPostFragment.videoViewPostVideoInner.start();

                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
