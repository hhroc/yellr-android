package yellr.net.yellr_android.utils;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import yellr.net.yellr_android.BuildConfig;
import yellr.net.yellr_android.R;
import yellr.net.yellr_android.intent_services.local_posts.Post;
import yellr.net.yellr_android.intent_services.post_vote.VoteIntentService;

/**
 * Created by TDuffy on 4/16/2015.
 */
public class LocalPostViewHolder {

    public int position;

    public final TextView textViewPostQuestion;
    public final TextView textViewPostUser;
    public final TextView textViewPostDateTime;
    public final TextView textViewPostText;
    public final ImageView imageViewPostImage;
    public final Button buttonPostUpVote;
    public final TextView textViewPostUpVoteCount;
    public final TextView textViewPostDownVoteCount;
    public final Button buttonPostDownVote;
    public final TextView textViewFullDateTime;

    public final boolean isViewPost;

    public LocalPostViewHolder(Context context, int position, View row, boolean isViewPost) {

        this.position = position;
        this.isViewPost = isViewPost;

        Typeface font = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");

        if (isViewPost ) {
            this.textViewPostQuestion = (TextView) row.findViewById(R.id.frag_view_post_question);
            this.textViewPostUser = (TextView) row.findViewById(R.id.frag_view_post_user);
            this.textViewPostDateTime = (TextView) row.findViewById(R.id.frag_view_post_datetime);
            this.textViewPostText = (TextView) row.findViewById(R.id.frag_view_post_text);
            this.imageViewPostImage = (ImageView) row.findViewById(R.id.frag_view_post_image);
            this.buttonPostUpVote = (Button) row.findViewById(R.id.frag_view_post_button_up_vote);
            this.textViewPostUpVoteCount = (TextView) row.findViewById(R.id.frag_view_post_up_vote_count);
            this.textViewPostDownVoteCount = (TextView) row.findViewById(R.id.frag_view_post_down_vote_count);
            this.buttonPostDownVote = (Button) row.findViewById(R.id.frag_view_post_button_down_vote);
            this.textViewFullDateTime = (TextView) row.findViewById(R.id.frag_view_post_full_datetime);
            this.textViewFullDateTime.setTypeface(null, Typeface.ITALIC);
        }
        else {
            this.textViewPostQuestion = (TextView) row.findViewById(R.id.frag_home_local_post_question);
            this.textViewPostUser = (TextView) row.findViewById(R.id.frag_home_local_post_user);
            this.textViewPostDateTime = (TextView) row.findViewById(R.id.frag_home_local_post_datetime);
            this.textViewPostText = (TextView) row.findViewById(R.id.frag_home_local_post_text);
            this.imageViewPostImage = (ImageView) row.findViewById(R.id.frag_home_local_post_image);
            this.buttonPostUpVote = (Button) row.findViewById(R.id.frag_home_local_post_button_up_vote);
            this.textViewPostUpVoteCount = (TextView) row.findViewById(R.id.frag_home_local_post_up_vote_count);
            this.textViewPostDownVoteCount = (TextView) row.findViewById(R.id.frag_home_local_post_down_vote_count);
            this.buttonPostDownVote = (Button) row.findViewById(R.id.frag_home_local_post_button_down_vote);
            this.textViewFullDateTime = null;
        }

        this.textViewPostQuestion.setTypeface(font, Typeface.ITALIC);
        this.textViewPostUser.setTypeface(font, Typeface.BOLD);
        this.textViewPostDateTime.setTypeface(font);
        this.buttonPostUpVote.setTypeface(font);
        this.buttonPostDownVote.setTypeface(font);

    }
    
    public void build(final Context context, final Post post) {

        //
        // Question Text
        //

        String questionText = post.question_text;
        boolean assignmentResponse = questionText == null || questionText.equals(null) || questionText.equals("") ? false : true;

        if ( assignmentResponse ) {
            this.textViewPostQuestion.setVisibility(View.VISIBLE);
            this.textViewPostQuestion.setText(context.getString(R.string.fa_question_circle) + "  " + questionText);
        } else {
            //this.textViewPostQuestion.setText(context.getString(R.string.fa_question_circle) + "  " + "Free Post");
            this.textViewPostQuestion.setVisibility(View.GONE);
        }


        //
        // User Text
        //

        boolean verifiedUser = post.verified_user;
        String firstName = post.first_name;
        String lastName = post.last_name;

        if ( verifiedUser ) {
            this.textViewPostUser.setText(context.getString(R.string.fa_user) + "  " + firstName + " " + lastName);
        } else {
            this.textViewPostUser.setText(context.getString(R.string.fa_user) + "  " + context.getString(R.string.anonymous_user));
        }


        //
        // Post DateTime
        //

        Date postDateTime = YellrUtils.prettifyDateTime(post.post_datetime);
        String postAuthoredAgo = YellrUtils.calcTimeBetween(postDateTime, new Date());

        this.textViewPostDateTime.setText(context.getString(R.string.fa_pencil) + "  " + postAuthoredAgo);


        //
        // Post Text
        //

        String mediaType = post.media_objects[0].media_type_name;
        String mediaText = post.media_objects[0].media_text;
        String mediaCaption = post.media_objects[0].caption;

        if ( mediaType.equals("text") ) {
            this.textViewPostText.setText(mediaText);
        } else {
            this.textViewPostText.setText(mediaCaption);
        }

        //Video view / Audio view for videos & audios
        if (this.isViewPost) {
            if (mediaType.equals("video")) {


            } else if (mediaType.equals("audio")) {


            }
        }

        //
        // Image View (optional)
        //
        if (mediaType.equals("image")) {
            try {

                String url = BuildConfig.BASE_URL + "/media/" + YellrUtils.getPreviewImageName(post.media_objects[0].file_name);

                this.imageViewPostImage.setVisibility(View.VISIBLE);
                Picasso.with(context)
                        .load(url)
                        .into(this.imageViewPostImage);

            } catch (Exception e) {
                Log.d("LocalPostsArrayAdapter.getView()", "ERROR: " + e.toString());
            }
        } else if (mediaType.equals("text")) {
            this.imageViewPostImage.setVisibility(View.GONE);
        }


        //
        // Voting (up vote, vote count)
        //

        this.textViewPostUpVoteCount.setText(String.valueOf(post.up_vote_count));

        String downCount = String.valueOf(post.down_vote_count);
        if ( post.down_vote_count != 0 )
            downCount = "-" + downCount;
        this.textViewPostDownVoteCount.setText(downCount);

        if (YellrUtils.intToBoolean(post.has_voted)) {
            if (YellrUtils.intToBoolean(post.is_up_vote)) {
                this.buttonPostUpVote.setTextColor(context.getResources().getColor(R.color.up_vote_green));
                this.buttonPostDownVote.setTextColor(context.getResources().getColor(R.color.light_grey));
            } else {
                this.buttonPostUpVote.setTextColor(context.getResources().getColor(R.color.light_grey));
                this.buttonPostDownVote.setTextColor(context.getResources().getColor(R.color.down_vote_red));
            }
        } else {
            this.buttonPostUpVote.setTextColor(context.getResources().getColor(R.color.light_grey));
            this.buttonPostDownVote.setTextColor(context.getResources().getColor(R.color.light_grey));
        }

        this.buttonPostUpVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("this.buttonPostUpVote.setOnClickListener", "Submitting Up Vote ...");


                Intent voteIntent = new Intent(context, VoteIntentService.class);
                voteIntent.putExtra(VoteIntentService.PARAM_POST_ID, post.post_id);
                voteIntent.putExtra(VoteIntentService.PARAM_IS_UP_VOTE, true); // Up Vote = true
                context.startService(voteIntent);

                Log.d("this.buttonPostUpVote.setOnClickListener", "has_voted: " + String.valueOf((post.has_voted) + ", is_up_vote: " + String.valueOf(post.is_up_vote)));

                if (YellrUtils.intToBoolean(post.has_voted)) {

                    if (YellrUtils.intToBoolean(post.is_up_vote)) {

                        // the client is removing their up vote

                        buttonPostUpVote.setTextColor(context.getResources().getColor(R.color.light_grey));
                        buttonPostDownVote.setTextColor(context.getResources().getColor(R.color.light_grey));

                        // remove vote
                        post.has_voted = 0;

                        // update up vote count
                        int count = Integer.valueOf(String.valueOf(textViewPostUpVoteCount.getText()));
                        textViewPostUpVoteCount.setText(String.valueOf(count - 1));

                    } else {

                        // the client is changing their down vote to an up vote.

                        buttonPostUpVote.setTextColor(context.getResources().getColor(R.color.up_vote_green));
                        buttonPostDownVote.setTextColor(context.getResources().getColor(R.color.light_grey));

                        // register up vote
                        post.is_up_vote = 1;

                        // update up vote count
                        int upCount = Integer.valueOf(String.valueOf(textViewPostUpVoteCount.getText()));
                        textViewPostUpVoteCount.setText(String.valueOf(upCount + 1));

                        // update down vote count
                        String downCountString = String.valueOf(textViewPostDownVoteCount.getText());
                        textViewPostDownVoteCount.setText(YellrUtils.lessDownVote(downCountString));
                    }
                } else {

                    // the client is casting an up vote, with no previous vote on the post

                    buttonPostUpVote.setTextColor(context.getResources().getColor(R.color.up_vote_green));
                    buttonPostDownVote.setTextColor(context.getResources().getColor(R.color.light_grey));
                    post.has_voted = 1;
                    post.is_up_vote = 1;

                    // update up vote count
                    int count = Integer.valueOf(String.valueOf(textViewPostUpVoteCount.getText()));
                    textViewPostUpVoteCount.setText(String.valueOf(count + 1));
                }

            }
        });

        this.buttonPostDownVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent voteIntent = new Intent(context, VoteIntentService.class);
                voteIntent.putExtra(VoteIntentService.PARAM_POST_ID, post.post_id);
                voteIntent.putExtra(VoteIntentService.PARAM_IS_UP_VOTE, false); // Down Vote = false
                context.startService(voteIntent);

                if (YellrUtils.intToBoolean(post.has_voted)) {
                    if (!YellrUtils.intToBoolean(post.is_up_vote)) {

                        // the client is removing their down vote

                        buttonPostUpVote.setTextColor(context.getResources().getColor(R.color.light_grey));
                        buttonPostDownVote.setTextColor(context.getResources().getColor(R.color.light_grey));

                        // remove vote
                        post.has_voted = 0;

                        // update down vote count
                        // update down vote count
                        String downCountString = String.valueOf(textViewPostDownVoteCount.getText());
                        textViewPostDownVoteCount.setText(YellrUtils.lessDownVote(downCountString));

                    } else {

                        // the client is changing their up vote to a down vote

                        buttonPostUpVote.setTextColor(context.getResources().getColor(R.color.light_grey));
                        buttonPostDownVote.setTextColor(context.getResources().getColor(R.color.down_vote_red));

                        // register down vote
                        post.is_up_vote = 0;

                        // update up vote count
                        int upCount = Integer.valueOf(String.valueOf(textViewPostUpVoteCount.getText()));
                        textViewPostUpVoteCount.setText(String.valueOf(upCount - 1));

                        // update down vote count
                        String downCountString = String.valueOf(textViewPostDownVoteCount.getText());
                        textViewPostDownVoteCount.setText(YellrUtils.moreDownVote(downCountString));
                    }
                } else {

                    // the client is casting an up vote, with no previous vote on the post

                    buttonPostUpVote.setTextColor(context.getResources().getColor(R.color.light_grey));
                    buttonPostDownVote.setTextColor(context.getResources().getColor(R.color.down_vote_red));
                    post.has_voted = 1;
                    post.is_up_vote = 0;

                    // update down vote count
                    String downCountString = String.valueOf(textViewPostDownVoteCount.getText());
                    textViewPostDownVoteCount.setText(YellrUtils.moreDownVote(downCountString));

                }

            }
        });

        if ( this.textViewFullDateTime != null )
            textViewFullDateTime.setText(YellrUtils.prettifyDateTime(post.post_datetime).toString());


    }
}