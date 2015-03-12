package yellr.net.yellr_android.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import yellr.net.yellr_android.BuildConfig;
import yellr.net.yellr_android.R;
import yellr.net.yellr_android.activities.PostActivity;
import yellr.net.yellr_android.intent_services.local_posts.LocalPost;
import yellr.net.yellr_android.intent_services.local_posts.LocalPostsIntentService;
import yellr.net.yellr_android.intent_services.local_posts.LocalPostsResponse;
import yellr.net.yellr_android.utils.PostImageView;
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalPostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalPostsFragment extends Fragment {

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

        // get the cuid
        //this.cuid = YellrUtils.getCUID(getActivity().getApplicationContext());

        // init new localPosts receiver
        Context context = getActivity().getApplicationContext();
        IntentFilter localPostsFilter = new IntentFilter(LocalPostsIntentService.ACTION_NEW_LOCAL_POSTS);
        localPostsFilter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalPostsReceiver localPostsReceiver = new LocalPostsReceiver();
        context.registerReceiver(localPostsReceiver, localPostsFilter);

        Log.d("LocalPostsFragment.onCreate()","LocalPostsReciever registered.");

        localPostsArrayAdapter = new LocalPostsArrayAdapter(getActivity(), new ArrayList<LocalPost>());
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



    public class LocalPostsArrayAdapter extends ArrayAdapter<LocalPost> {

        private ArrayList<LocalPost> localPosts;

        private LayoutInflater inflater;

        public LocalPostsArrayAdapter(Context context, ArrayList<LocalPost> localPosts) {
            super(context, R.layout.fragment_local_post_row, R.id.frag_home_local_post_user, localPosts);
            this.localPosts = localPosts;

            this.inflater = LayoutInflater.from(getContext());

        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Log.d("LocalPostsArrayAdapter.getView()", "Position: " + String.valueOf(position) + ", Media Type: " + this.localPosts.get(position).media_objects[0].media_type_name + ", " );

            View row = null;
            //if( null == convertView ) {
                //row = super.getView(position, convertView, parent);

                // TODO: Figure out how to re-use convertView
                //       This causes the images to be re-drawn every time
                //       this function is called.
                //
                //       If I do not do this, and I just use the convertView,
                //       the images jump around as I scroll.  I suspect this
                //       is because Android re-uses the view as convertView.
                //

                row = LayoutInflater.from(getContext()).inflate(R.layout.fragment_local_post_row, parent, false);

            //} else {
            //    row = convertView;
            //}

            //View row = convertView;

            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");

            //
            // Question Text
            //

            TextView textViewPostQuestion = (TextView) row.findViewById(R.id.frag_home_local_post_question);
            textViewPostQuestion.setTypeface(font);

            String questionText = this.localPosts.get(position).question_text;
            boolean assignmentResponse = questionText == null || questionText.equals(null) || questionText.equals("") ? false : true;

            if ( assignmentResponse ) {
                textViewPostQuestion.setText(getString(R.string.fa_question_circle) + "  " + questionText);
            } else {
                //textViewPostQuestion.setTypeface(null, Typeface.ITALIC);
                //textViewPostQuestion.setText("Free Post");
                //textViewPostQuestion.setVisibility(View.GONE);
                textViewPostQuestion.setText(getString(R.string.fa_question_circle) + "  " + "Free Post");
                textViewPostQuestion.setTypeface(font, Typeface.ITALIC);
            }


            //
            // User Text
            //

            TextView textViewPostUser = (TextView) row.findViewById(R.id.frag_home_local_post_user);
            textViewPostUser.setTypeface(font); //, Typeface.BOLD_ITALIC);

            boolean verifiedUser = this.localPosts.get(position).verified_user;
            String firstName = this.localPosts.get(position).first_name;
            String lastName = this.localPosts.get(position).last_name;

            if ( verifiedUser ) {
                textViewPostUser.setText(getString(R.string.fa_user) + "  " + firstName + " " + lastName);
            } else {
                textViewPostUser.setText(getString(R.string.fa_user) + "  " + getString(R.string.anonymous_user));
            }


            //
            // Post DateTime
            //

            TextView textViewPostDateTime = (TextView) row.findViewById(R.id.frag_home_local_post_datetime);
            textViewPostDateTime.setTypeface(font);

            Date postDateTime = YellrUtils.prettifyDateTime(this.localPosts.get(position).post_datetime);
            String postAuthoredAgo = YellrUtils.calcTimeBetween(postDateTime, new Date()) + getString(R.string.time_ago) + ".";

            textViewPostDateTime.setText(getString(R.string.fa_pencil) + "  " + postAuthoredAgo);

            //
            // Post Text
            //

            TextView textViewPostText = (TextView) row.findViewById(R.id.frag_home_local_post_text);

            String mediaType = this.localPosts.get(position).media_objects[0].media_type_name;
            String mediaText = this.localPosts.get(position).media_objects[0].media_text;
            String mediaCaption = this.localPosts.get(position).media_objects[0].caption;

            if ( mediaType.equals("text") ) {
                textViewPostText.setText(mediaText);
            } else {
                textViewPostText.setText(mediaCaption);
            }

            //
            // Image View (optional)
            //

            PostImageView imageViewPostImage = (PostImageView) row.findViewById(R.id.frag_home_local_post_image);

            //String mediaType = this.localPosts.get(position).media_objects[0].media_type_name;

            //if ( !imageViewPostImage.imageSet ) {

                if (mediaType.equals("image")) {
                    try {

                        //
                        // TODO: This needs to be moved to a background task.
                        //       Unsure how to do this with respect to the list ..
                        //       I suspect it has something to do with
                        //       position, however I'm not sure.

                        //URL url = new URL(BuildConfig.BASE_URL + this.localPosts.get(position).media_objects[0].file_name);
                        //InputStream content = (InputStream) url.getContent();
                        //Drawable drawable = Drawable.createFromStream(content, "src");
                        //imageViewPostImage.setImageDrawable(drawable);

                        Log.d("LocalPostArrayAdapter.getView()","Position: " + String.valueOf(position));
                        Log.d("LocalPostArrayAdapter.getView()","    Media Text: " + mediaText);
                        Log.d("LocalPostArrayAdapter.getView()","    Media Caption: " + mediaCaption);

                        String url = BuildConfig.BASE_URL + "/media/" + YellrUtils.getPreviewImageName(this.localPosts.get(position).media_objects[0].file_name);
                        imageViewPostImage.setImage(url, position);

                    } catch (Exception e) {
                        Log.d("LocalPostsArrayAdapter.getView()", "ERROR: " + e.toString());
                    }
                } else if (mediaType.equals("text")) {
                    imageViewPostImage.setVisibility(View.GONE);
                }

            //}


            //
            // User Actions (up vote, down vote, favorite)
            //

            TextView textViewPostUpVote = (TextView) row.findViewById(R.id.frag_home_local_post_up_vote);
            textViewPostUpVote.setTypeface(font);
            //TextView textViewPostDownVote = (TextView) row.findViewById(R.id.frag_home_local_post_down_vote);
            //textViewPostDownVote.setTypeface(font);
            //TextView textViewPostFavorite = (TextView) row.findViewById(R.id.frag_home_local_post_favorite);
            //textViewPostFavorite.setTypeface(font);


            //TextView textViewQuestionText = (TextView) row.findViewById(R.id.frag_home_local_post_question_text);
            //TextView textViewOrganization = (TextView) row.findViewById(R.id.frag_home_local_post_organization);
            //TextView textViewPostCount = (TextView) row.findViewById(R.id.frag_home_local_post_post_count);

            //
            //textViewOrganization.setTypeface(font);
            //textViewPostCount.setTypeface(font);

            //textViewQuestionText.setText(this.localPosts.get(position).question_text);
            //textViewOrganization.setText(getString(R.string.fa_user) + "   " + YellrUtils.shortenString(this.localPosts.get(position).organization));
            //textViewPostCount.setText(getString(R.string.fa_comments) + " " + String.valueOf(this.localPosts.get(position).post_count));

            return row;
        }
    }


}
