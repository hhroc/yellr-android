package yellr.net.yellr_android.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import yellr.net.yellr_android.BuildConfig;
import yellr.net.yellr_android.R;
import yellr.net.yellr_android.intent_services.local_posts.LocalPost;
import yellr.net.yellr_android.intent_services.local_posts.LocalPostsIntentService;
import yellr.net.yellr_android.intent_services.local_posts.LocalPostsResponse;
import yellr.net.yellr_android.intent_services.post_vote.VoteIntentService;
import yellr.net.yellr_android.intent_services.publish_post.PublishPostIntentService;
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalPostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalPostsFragment extends Fragment {

    //private Map<String, Bitmap> bitmapDictionary;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private String cuid;
    private LocalPostsArrayAdapter localPostsArrayAdapter;

    private LocalPost[] localPosts;
    //private LocalPostsArrayAdapter localPostsArrayAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LocalPostsFragment.
     */
    public static LocalPostsFragment newInstance() {
        LocalPostsFragment fragment = new LocalPostsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public LocalPostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        // init new localPosts receiver
        Context context = getActivity().getApplicationContext();
        IntentFilter localPostsFilter = new IntentFilter(LocalPostsIntentService.ACTION_NEW_LOCAL_POSTS);
        localPostsFilter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalPostsReceiver localPostsReceiver = new LocalPostsReceiver();
        context.registerReceiver(localPostsReceiver, localPostsFilter);

        Log.d("LocalPostsFragment.onCreate()","LocalPostsReciever registered.");

        localPostsArrayAdapter = new LocalPostsArrayAdapter(getActivity(), R.layout.fragment_local_post_row, new ArrayList<LocalPost>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_local_posts, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.frag_home_local_post_swipe_refresh_layout);
        listView = (ListView)view.findViewById(R.id.frag_home_local_posts_list);

        listView.setAdapter(localPostsArrayAdapter);
        listView.setOnItemClickListener(new LocalPostListOnClickListener());

        // This appear to be a linting error the Android Docs call for a Color Resource to be used here...
        // https://developer.android.com/reference/android/support/v4/widget/SwipeRefreshLayout.html#setProgressBackgroundColor(int)
        swipeRefreshLayout.setProgressBackgroundColor(R.color.yellow);
        swipeRefreshLayout.setColorSchemeResources(R.color.black);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLocalPostData();
            }
        });

        // display pin wheel until there is something to show.
        //if ( this.localPosts == null || this.localPosts.length == 0 ) {
        //    swipeRefreshLayout.setRefreshing(true);
        //}

        return view;
    }

    @Override
    public void onResume() {
        //Log.d("LocalPostsFragment.onResume()", "Starting localPosts intent service ...");
        refreshLocalPostData();
        super.onResume();
    }

    private void refreshLocalPostData() {

        Log.d("LocalPostsFragment.onResume()", "Starting localPosts intent service ...");

        // init service
        Context context = getActivity().getApplicationContext();
        Intent localPostsWebIntent = new Intent(context, LocalPostsIntentService.class);
        //localPostsWebIntent.putExtra(LocalPostsIntentService.PARAM_CUID, cuid);
        localPostsWebIntent.setAction(LocalPostsIntentService.ACTION_GET_LOCAL_POSTS);
        context.startService(localPostsWebIntent);

        Log.d("LocalPostsFragment.onResume()", "LocalPosts intent service dispatched.");
    }

    public class LocalPostsReceiver extends BroadcastReceiver {
        //public static final String ACTION_NEW_LOCAL_POSTS =
        //        "yellr.net.yellr_android.action.NEW_LOCAL_POSTS";

        public LocalPostsReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("LocalPostsFragment.onReceive", "New Local Posts for display.");

            String localPostsJson = intent.getStringExtra(LocalPostsIntentService.PARAM_LOCAL_POSTS_JSON);

            Gson gson = new Gson();
            LocalPostsResponse response = new LocalPostsResponse();
            try{
                response = gson.fromJson(localPostsJson, LocalPostsResponse.class);
            } catch(Exception e){
                Log.d("LocalPostsFragment.onReceive", "GSON puked");
            }

            if (response.success && response.posts != null) {
                localPostsArrayAdapter.clear();
                localPosts = new LocalPost[response.posts.length];
                for (int i = 0; i < response.posts.length; i++) {
                    LocalPost local_post = response.posts[i];
                    localPostsArrayAdapter.add(local_post);
                    localPosts[i] = local_post;
                }
            }

            swipeRefreshLayout.setRefreshing(false);
        }
    }

    class LocalPostListOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            //Intent intent;
            //intent = new Intent(getActivity().getApplicationContext(), PostActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //intent.putExtra(PostFragment.ARG_ASSIGNMENT_QUESTION, localPosts[position].question_text);
            //intent.putExtra(PostFragment.ARG_ASSIGNMENT_DESCRIPTION, localPosts[position].description);
            //intent.putExtra(PostFragment.ARG_ASSIGNMENT_ID, localPosts[position].local_post_id);

            //startActivity(intent);
        }

    }

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

        LocalPostViewHolder(int position, View row) {

            this.position = position;

            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");

            this.textViewPostQuestion = (TextView) row.findViewById(R.id.frag_home_local_post_question);
            this.textViewPostQuestion.setTypeface(font, Typeface.ITALIC);

            this.textViewPostUser = (TextView) row.findViewById(R.id.frag_home_local_post_user);
            this.textViewPostUser.setTypeface(font, Typeface.BOLD);

            this.textViewPostDateTime = (TextView) row.findViewById(R.id.frag_home_local_post_datetime);

            this.textViewPostDateTime.setTypeface(font);

            this.textViewPostText = (TextView) row.findViewById(R.id.frag_home_local_post_text);
            this.imageViewPostImage = (ImageView) row.findViewById(R.id.frag_home_local_post_image);

            this.buttonPostUpVote = (Button) row.findViewById(R.id.frag_home_local_post_button_up_vote);
            this.buttonPostUpVote.setTypeface(font);

            this.textViewPostUpVoteCount = (TextView) row.findViewById(R.id.frag_home_local_post_up_vote_count);

            this.textViewPostDownVoteCount = (TextView) row.findViewById(R.id.frag_home_local_post_down_vote_count);

            this.buttonPostDownVote = (Button) row.findViewById(R.id.frag_home_local_post_button_down_vote);
            this.buttonPostDownVote.setTypeface(font);
        }
    }

    public class LocalPostsArrayAdapter extends ArrayAdapter<LocalPost> {

        private ArrayList<LocalPost> localPosts;

        public LocalPostsArrayAdapter(Context context, int listViewId, ArrayList<LocalPost> localPosts) {
            super(context, listViewId, localPosts);
            this.localPosts = localPosts;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final LocalPostViewHolder localPostViewHolder;

            if ( convertView == null ) {

                // get the convertView (reused between getView() calls)
                LayoutInflater vi = LayoutInflater.from(getContext()); //getLayoutInflater(); //(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.fragment_local_post_row, null);

                // create our view holder, and set it as the tag of the convertView.
                localPostViewHolder = new LocalPostViewHolder(position, convertView);
                convertView.setTag(localPostViewHolder);

            } else {

                // get our view holder from the convertView
                localPostViewHolder = (LocalPostViewHolder)convertView.getTag();

            }

            final LocalPost localPost = localPosts.get(position);

            //
            // Question Text
            //

            String questionText = localPost.question_text;
            boolean assignmentResponse = questionText == null || questionText.equals(null) || questionText.equals("") ? false : true;

            if ( assignmentResponse ) {
                localPostViewHolder.textViewPostQuestion.setVisibility(View.VISIBLE);
                localPostViewHolder.textViewPostQuestion.setText(getString(R.string.fa_question_circle) + "  " + questionText);
            } else {
                //localPostViewHolder.textViewPostQuestion.setText(getString(R.string.fa_question_circle) + "  " + "Free Post");
                localPostViewHolder.textViewPostQuestion.setVisibility(View.GONE);
            }


            //
            // User Text
            //

            boolean verifiedUser = localPost.verified_user;
            String firstName = localPost.first_name;
            String lastName = localPost.last_name;

            if ( verifiedUser ) {
                localPostViewHolder.textViewPostUser.setText(getString(R.string.fa_user) + "  " + firstName + " " + lastName);
            } else {
                localPostViewHolder.textViewPostUser.setText(getString(R.string.fa_user) + "  " + getString(R.string.anonymous_user));
            }


            //
            // Post DateTime
            //

            Date postDateTime = YellrUtils.prettifyDateTime(localPost.post_datetime);
            String postAuthoredAgo = YellrUtils.calcTimeBetween(postDateTime, new Date());

            localPostViewHolder.textViewPostDateTime.setText(getString(R.string.fa_pencil) + "  " + postAuthoredAgo);


            //
            // Post Text
            //

            String mediaType = localPost.media_objects[0].media_type_name;
            String mediaText = localPost.media_objects[0].media_text;
            String mediaCaption = localPost.media_objects[0].caption;

            if ( mediaType.equals("text") ) {
                localPostViewHolder.textViewPostText.setText(mediaText);
            } else {
                localPostViewHolder.textViewPostText.setText(mediaCaption);
            }

            //
            // Image View (optional)
            //

            if (mediaType.equals("image")) {
                try {

                    String url = BuildConfig.BASE_URL + "/media/" + YellrUtils.getPreviewImageName(localPost.media_objects[0].file_name);

                    localPostViewHolder.imageViewPostImage.setVisibility(View.VISIBLE);
                    Picasso.with(getContext())
                            .load(url)
                            .into(localPostViewHolder.imageViewPostImage);

                } catch (Exception e) {
                    Log.d("LocalPostsArrayAdapter.getView()", "ERROR: " + e.toString());
                }
            } else if (mediaType.equals("text")) {
                localPostViewHolder.imageViewPostImage.setVisibility(View.GONE);
            }


            //
            // Voting (up vote, vote count)
            //

            localPostViewHolder.textViewPostUpVoteCount.setText(String.valueOf(localPost.up_vote_count));

            String downCount = String.valueOf(localPost.down_vote_count);
            if ( localPost.down_vote_count != 0 )
                downCount = "-" + downCount;
            localPostViewHolder.textViewPostDownVoteCount.setText(downCount);

            if (YellrUtils.intToBoolean(localPost.has_voted)) {
                if (YellrUtils.intToBoolean(localPost.is_up_vote)) {
                    localPostViewHolder.buttonPostUpVote.setTextColor(getResources().getColor(R.color.up_vote_green));
                    localPostViewHolder.buttonPostDownVote.setTextColor(getResources().getColor(R.color.light_grey));
                } else {
                    localPostViewHolder.buttonPostUpVote.setTextColor(getResources().getColor(R.color.light_grey));
                    localPostViewHolder.buttonPostDownVote.setTextColor(getResources().getColor(R.color.down_vote_red));
                }
            } else {
                localPostViewHolder.buttonPostUpVote.setTextColor(getResources().getColor(R.color.light_grey));
                localPostViewHolder.buttonPostDownVote.setTextColor(getResources().getColor(R.color.light_grey));
            }

            localPostViewHolder.buttonPostUpVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("localPostViewHolder.buttonPostUpVote.setOnClickListener", "Submitting Up Vote ...");


                    Intent voteIntent = new Intent(getActivity(), VoteIntentService.class);
                    voteIntent.putExtra(VoteIntentService.PARAM_POST_ID, localPost.post_id);
                    voteIntent.putExtra(VoteIntentService.PARAM_IS_UP_VOTE, true); // Up Vote = true
                    getActivity().startService(voteIntent);

                    Log.d("localPostViewHolder.buttonPostUpVote.setOnClickListener", "has_voted: " + String.valueOf((localPost.has_voted) + ", is_up_vote: " + String.valueOf(localPost.is_up_vote)));

                    if (YellrUtils.intToBoolean(localPost.has_voted)) {

                        if (YellrUtils.intToBoolean(localPost.is_up_vote)) {

                            // the client is removing their up vote

                            localPostViewHolder.buttonPostUpVote.setTextColor(getResources().getColor(R.color.light_grey));
                            localPostViewHolder.buttonPostDownVote.setTextColor(getResources().getColor(R.color.light_grey));

                            // remove vote
                            localPost.has_voted = 0;

                            // update up vote count
                            int count = Integer.valueOf(String.valueOf(localPostViewHolder.textViewPostUpVoteCount.getText()));
                            localPostViewHolder.textViewPostUpVoteCount.setText(String.valueOf(count - 1));

                        } else {

                            // the client is changing their down vote to an up vote.

                            localPostViewHolder.buttonPostUpVote.setTextColor(getResources().getColor(R.color.up_vote_green));
                            localPostViewHolder.buttonPostDownVote.setTextColor(getResources().getColor(R.color.light_grey));

                            // register up vote
                            localPost.is_up_vote = 1;

                            // update up vote count
                            int upCount = Integer.valueOf(String.valueOf(localPostViewHolder.textViewPostUpVoteCount.getText()));
                            localPostViewHolder.textViewPostUpVoteCount.setText(String.valueOf(upCount + 1));

                            // update down vote count
                            String downCountString = String.valueOf(localPostViewHolder.textViewPostDownVoteCount.getText());
                            localPostViewHolder.textViewPostDownVoteCount.setText(YellrUtils.lessDownVote(downCountString));
                        }
                    } else {

                        // the client is casting an up vote, with no previous vote on the post

                        localPostViewHolder.buttonPostUpVote.setTextColor(getResources().getColor(R.color.up_vote_green));
                        localPostViewHolder.buttonPostDownVote.setTextColor(getResources().getColor(R.color.light_grey));
                        localPost.has_voted = 1;
                        localPost.is_up_vote = 1;

                        // update up vote count
                        int count = Integer.valueOf(String.valueOf(localPostViewHolder.textViewPostUpVoteCount.getText()));
                        localPostViewHolder.textViewPostUpVoteCount.setText(String.valueOf(count + 1));
                    }

                }
            });

            localPostViewHolder.buttonPostDownVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent voteIntent = new Intent(getActivity(), VoteIntentService.class);
                    voteIntent.putExtra(VoteIntentService.PARAM_POST_ID, localPost.post_id);
                    voteIntent.putExtra(VoteIntentService.PARAM_IS_UP_VOTE, false); // Down Vote = false
                    getActivity().startService(voteIntent);

                    if (YellrUtils.intToBoolean(localPost.has_voted)) {
                        if (!YellrUtils.intToBoolean(localPost.is_up_vote)) {

                            // the client is removing their down vote

                            localPostViewHolder.buttonPostUpVote.setTextColor(getResources().getColor(R.color.light_grey));
                            localPostViewHolder.buttonPostDownVote.setTextColor(getResources().getColor(R.color.light_grey));

                            // remove vote
                            localPost.has_voted = 0;

                            // update down vote count
                            // update down vote count
                            String downCountString = String.valueOf(localPostViewHolder.textViewPostDownVoteCount.getText());
                            localPostViewHolder.textViewPostDownVoteCount.setText(YellrUtils.lessDownVote(downCountString));

                        } else {

                            // the client is changing their up vote to a down vote

                            localPostViewHolder.buttonPostUpVote.setTextColor(getResources().getColor(R.color.light_grey));
                            localPostViewHolder.buttonPostDownVote.setTextColor(getResources().getColor(R.color.down_vote_red));

                            // register down vote
                            localPost.is_up_vote = 0;

                            // update up vote count
                            int upCount = Integer.valueOf(String.valueOf(localPostViewHolder.textViewPostUpVoteCount.getText()));
                            localPostViewHolder.textViewPostUpVoteCount.setText(String.valueOf(upCount - 1));

                            // update down vote count
                            String downCountString = String.valueOf(localPostViewHolder.textViewPostDownVoteCount.getText());
                            localPostViewHolder.textViewPostDownVoteCount.setText(YellrUtils.moreDownVote(downCountString));
                        }
                    } else {

                        // the client is casting an up vote, with no previous vote on the post

                        localPostViewHolder.buttonPostUpVote.setTextColor(getResources().getColor(R.color.light_grey));
                        localPostViewHolder.buttonPostDownVote.setTextColor(getResources().getColor(R.color.down_vote_red));
                        localPost.has_voted = 1;
                        localPost.is_up_vote = 0;

                        // update down vote count
                        String downCountString = String.valueOf(localPostViewHolder.textViewPostDownVoteCount.getText());
                        localPostViewHolder.textViewPostDownVoteCount.setText(YellrUtils.moreDownVote(downCountString));

                    }

                }
            });


            return convertView;
        }
    }

}
