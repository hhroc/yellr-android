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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

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
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalPostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalPostsFragment extends Fragment {

    private Map<String, Bitmap> bitmapDictionary;

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

        bitmapDictionary = new HashMap();

        // get the cuid
        //this.cuid = YellrUtils.getCUID(getActivity().getApplicationContext());

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
                    //if ( localPosts[i].media_objects[0].media_type_name.equals("image") &&
                    //        localPosts[i].media_objects[0].displayImage != null ) {
                    //    localPosts[i].media_objects[0].downloadDisplayImage();
                    //}
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
        public final TextView textViewPostUpVote;
        public final TextView textViewPostVoteCount;

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
            this.textViewPostUpVote = (TextView) row.findViewById(R.id.frag_home_local_post_up_vote);

            this.textViewPostVoteCount = (TextView) row.findViewById(R.id.frag_home_local_post_vote_count);
            this.textViewPostUpVote.setTypeface(font);
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

            LocalPostViewHolder localPostViewHolder;

            if ( convertView == null ) {

                // get the convertView (reused between getView() calls)
                LayoutInflater vi = LayoutInflater.from(getContext()); //getLayoutInflater(); //(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.fragment_local_post_row, null);

                // create our view holder, and set it as the tag of the convertView.
                localPostViewHolder = new LocalPostViewHolder(position, convertView);
                convertView.setTag(localPostViewHolder);

            } else {

                // ge tour view holder from the convertView
                localPostViewHolder = (LocalPostViewHolder)convertView.getTag();

                // remove any previous image on reuse
                localPostViewHolder.imageViewPostImage.setImageBitmap(null);

                // re hide the view if there is no image, so we need to default it to showing
                localPostViewHolder.imageViewPostImage.setVisibility(View.VISIBLE);

            }

            LocalPost localPost = localPosts.get(position);

            //
            // Question Text
            //

            String questionText = localPost.question_text;
            boolean assignmentResponse = questionText == null || questionText.equals(null) || questionText.equals("") ? false : true;

            if ( assignmentResponse ) {
                localPostViewHolder.textViewPostQuestion.setText(getString(R.string.fa_question_circle) + "  " + questionText);
            } else {
                localPostViewHolder.textViewPostQuestion.setText(getString(R.string.fa_question_circle) + "  " + "Free Post");
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
            String postAuthoredAgo = YellrUtils.calcTimeBetween(postDateTime, new Date()) + getString(R.string.time_ago) + ".";

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

                    // note: if image is already downloaded, the BitmapDownloaderTask will just pull
                    //       the image from the HashMap rather than re-downloading it.
                    String url = BuildConfig.BASE_URL + "/media/" + YellrUtils.getPreviewImageName(localPost.media_objects[0].file_name);
                    BitmapDownloaderTask bitmapDownloaderTask = new BitmapDownloaderTask(localPostViewHolder.imageViewPostImage, url);
                    bitmapDownloaderTask.execute();

                } catch (Exception e) {
                    Log.d("LocalPostsArrayAdapter.getView()", "ERROR: " + e.toString());
                }
            } else if (mediaType.equals("text")) {
                localPostViewHolder.imageViewPostImage.setVisibility(View.GONE);
            }


            //
            // Voting (up vote, vote count)
            //



            return convertView;
        }
    }

    class BitmapDownloaderTask extends AsyncTask<Void, Void, Bitmap> {

        private WeakReference<ImageView> imageViewWeakReference;
        private String url;

        public BitmapDownloaderTask(ImageView imageView, String url) {
            imageView.setTag(url);
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap retBitmap = null;
            ImageView imageView = imageViewWeakReference.get();
            if (imageView == null || !url.equals(imageView.getTag())) {
                // not us, returns null
            } else {

                // see if we have already downloaded the bitmap, and
                // if we haven't download it.
                retBitmap = bitmapDictionary.get(this.url);
                if ( retBitmap == null ) {
                    retBitmap = YellrUtils.downloadBitmap(this.url);
                    bitmapDictionary.put(this.url, retBitmap);
                }

            }
            return retBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView = this.imageViewWeakReference.get();
            if (imageView == null || bitmap == null || !this.url.equals(imageView.getTag())) {
                // something went wrong ...
            } else {
                // success!
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}
