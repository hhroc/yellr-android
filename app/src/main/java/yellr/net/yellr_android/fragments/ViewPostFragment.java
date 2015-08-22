package yellr.net.yellr_android.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

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
    Button reportButton;
    public String mediatype;

    LinearLayout videoViewPostVideo;
    public VideoView videoViewPostVideoInner;
    private SeekBar vseekbar;
    private ImageButton vmedia_pause, vmedia_play;
    public TextView vduration;
    private double vtimeElapsed = 0, vfinalTime = 0;

    LinearLayout audioContainer;
    private MediaPlayer mediaPlayer;
    public TextView duration;
    private double timeElapsed = 0, finalTime = 0;
    private Handler durationHandler = new Handler();
    private SeekBar seekbar;
    private ImageButton media_pause, media_play;


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
        this.audioContainer = (LinearLayout) view.findViewById(R.id.audio_container);

        Intent intent = getActivity().getIntent();

        int position = intent.getIntExtra(ViewPostFragment.ARG_POST_LIST_POSITION, 0);

        post.post_id = intent.getIntExtra(ViewPostFragment.ARG_POST_ID, 0);
        post.post_datetime = intent.getStringExtra(ViewPostFragment.ARG_POST_DATETIME);
        post.question_text = intent.getStringExtra(ViewPostFragment.ARG_POST_QUESTION_TEXT);
        post.up_vote_count = intent.getIntExtra(ViewPostFragment.ARG_POST_UP_VOTE_COUNT, 0);
        post.down_vote_count = intent.getIntExtra(ViewPostFragment.ARG_POST_DOWN_VOTE_COUNT,0);
        post.has_voted = intent.getIntExtra(ViewPostFragment.ARG_POST_HAS_VOTED,0);
        post.is_up_vote = intent.getIntExtra(ViewPostFragment.ARG_POST_IS_UP_VOTE,0);

        post.media_objects = new MediaObject[1];
        post.media_objects[0] = new MediaObject();

        post.media_objects[0].media_text = intent.getStringExtra(ViewPostFragment.ARG_POST_MEDIA_OBJECT_TEXT);
        post.media_objects[0].caption = intent.getStringExtra(ViewPostFragment.ARG_POST_MEDIA_OBJECT_TEXT);
        post.media_objects[0].file_name = intent.getStringExtra(ViewPostFragment.ARG_POST_MEDIA_OBJECT_FILENAME);
        post.media_objects[0].media_type_name = intent.getStringExtra(ViewPostFragment.ARG_POST_MEDIA_OBJECT_MEDIA_TYPE);

        this.mediatype = post.media_objects[0].media_type_name;
        //hide video/audio view
        if (this.mediatype.equals("image")) {

            this.videoViewPostVideo.setVisibility(View.GONE);
            this.audioContainer.setVisibility(View.GONE);

        } else if (this.mediatype.equals("audio")) {

            this.videoViewPostVideo.setVisibility(View.GONE);
            this.audioContainer.setVisibility(View.VISIBLE);

            media_pause = (ImageButton) view.findViewById(R.id.media_pause);
            media_play = (ImageButton) view.findViewById(R.id.media_play);
            duration = (TextView) view.findViewById(R.id.songDuration);
            seekbar = (SeekBar) view.findViewById(R.id.seekBar);
            seekbar.setClickable(false);

            media_pause.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       mediaPlayer.pause();
                   }
               }
            );

            media_play.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       mediaPlayer.start();
                       timeElapsed = mediaPlayer.getCurrentPosition();
                       seekbar.setProgress((int) timeElapsed);
                       durationHandler.postDelayed(updateSeekBarTime, 100);
                   }
               }
            );

        } else if (this.mediatype.equals("video")) {

            this.videoViewPostVideo.setVisibility(View.VISIBLE);
            this.audioContainer.setVisibility(View.GONE);

            vmedia_pause = (ImageButton) view.findViewById(R.id.vmedia_pause);
            vmedia_play = (ImageButton) view.findViewById(R.id.vmedia_play);
            //vduration = (TextView) view.findViewById(R.id.vidDuration);
            //vseekbar = (SeekBar) view.findViewById(R.id.vseekBar);
            //vseekbar.setClickable(false);

            vmedia_pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        videoViewPostVideoInner.pause();
                    }
                }
            );

            vmedia_play.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       videoViewPostVideoInner.start();
                       vtimeElapsed = videoViewPostVideoInner.getCurrentPosition();
                       //vseekbar.setProgress((int) vtimeElapsed);
                       durationHandler.postDelayed(vUpdateSeekBarTime, 100);
                   }
               }
            );

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

        if (this.mediatype.equals("audio") || this.mediatype.equals("video") ) {
            //video / audio url
            String url = BuildConfig.BASE_URL + "/media/" + post.media_objects[0].file_name;
            Log.d("ViewPostFragment.OnCreateView.DownloadMedia", url);

            //instantiate it within the onCreate method
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Getting " + this.mediatype);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(true);

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
        }

        return view;
    }

    // play mp3 song
    public void playAudio(String url) {
        mediaPlayer = MediaPlayer.create(getActivity().getApplicationContext(), Uri.parse(url));
        finalTime = mediaPlayer.getDuration();
        seekbar.setMax((int) finalTime);
        mediaPlayer.start();
        timeElapsed = mediaPlayer.getCurrentPosition();
        seekbar.setProgress((int) timeElapsed);
        durationHandler.postDelayed(updateSeekBarTime, 100);
    }

    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            timeElapsed = mediaPlayer.getCurrentPosition();
            //set seekbar progress
            seekbar.setProgress((int) timeElapsed);
            //set time remaing
            double timeRemaining = finalTime - timeElapsed;
            duration.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));

            //repeat yourself that again in 100 miliseconds
            durationHandler.postDelayed(this, 100);
        }
    };

    private Runnable vUpdateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            timeElapsed = videoViewPostVideoInner.getCurrentPosition();
            //set seekbar progress
            //vseekbar.setProgress((int) timeElapsed);
            //set time remaing
            double vtimeRemaining = vfinalTime - vtimeElapsed;
            //vduration.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) vtimeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) vtimeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) vtimeRemaining))));

            //repeat yourself that again in 100 miliseconds
            durationHandler.postDelayed(this, 100);
        }
    };

    //Play mp4 video
    public void playMedia(String url) {
        this.videoViewPostVideoInner.setVideoPath(url);

        vfinalTime = videoViewPostVideoInner.getDuration();
        //vseekbar.setMax((int) finalTime);

        vtimeElapsed = videoViewPostVideoInner.getCurrentPosition();
        //vseekbar.setProgress((int) timeElapsed);
        durationHandler.postDelayed(vUpdateSeekBarTime, 100);

        this.videoViewPostVideoInner.start();

    }

    // usually, subclasses of AsyncTask are declared inside the activity class.
    // that way, you can easily modify the UI thread from here
    public class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        public String dUrl;

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
                            + " " + connection.getResponseMessage() + ":" + sUrl[0];
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                this.dUrl = "/sdcard/" + YellrUtils.getFileName(sUrl[0]);
                output = new FileOutputStream(this.dUrl);

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
                if (mediatype.equals("video")) {
                    playMedia(this.dUrl);
                } else if (mediatype.equals("audio")) {
                    playAudio(this.dUrl);
                }
                //Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
