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
import yellr.net.yellr_android.activities.ViewPostActivity;
import yellr.net.yellr_android.intent_services.local_posts.LocalPostsIntentService;
import yellr.net.yellr_android.intent_services.local_posts.LocalPostsResponse;
import yellr.net.yellr_android.intent_services.local_posts.Post;
import yellr.net.yellr_android.intent_services.post_vote.VoteIntentService;
import yellr.net.yellr_android.intent_services.publish_post.PublishPostIntentService;
import yellr.net.yellr_android.utils.LocalPostViewHolder;
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

    private Post[] localPosts;
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

        localPostsArrayAdapter = new LocalPostsArrayAdapter(getActivity(), R.layout.fragment_local_post_row, new ArrayList<Post>());
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
                localPosts = new Post[response.posts.length];
                for (int i = 0; i < response.posts.length; i++) {
                    Post localPost = response.posts[i];
                    localPostsArrayAdapter.add(localPost);
                    localPosts[i] = localPost;
                }
            }

            swipeRefreshLayout.setRefreshing(false);
        }
    }



    class LocalPostListOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Log.d("LocalPostListOnClickListener.onItemClick()", "Click!");

            Intent intent;
            intent = new Intent(getActivity().getApplicationContext(), ViewPostActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Post post = localPosts[position];

            intent.putExtra(ViewPostFragment.ARG_POST_LIST_POSITION, position);

            intent.putExtra(ViewPostFragment.ARG_POST_DATETIME, localPosts[position].post_datetime);
            intent.putExtra(ViewPostFragment.ARG_POST_QUESTION_TEXT, localPosts[position].question_text);
            intent.putExtra(ViewPostFragment.ARG_POST_UP_VOTE_COUNT, localPosts[position].up_vote_count);
            intent.putExtra(ViewPostFragment.ARG_POST_DOWN_VOTE_COUNT, localPosts[position].down_vote_count);
            intent.putExtra(ViewPostFragment.ARG_POST_HAS_VOTED, localPosts[position].has_voted);
            intent.putExtra(ViewPostFragment.ARG_POST_IS_UP_VOTE, localPosts[position].is_up_vote);

            String text = localPosts[position].media_objects[0].media_text;
            if (localPosts[position].media_objects[0].media_type_name.toLowerCase().equals("image"))
                text = localPosts[position].media_objects[0].caption;
            intent.putExtra(ViewPostFragment.ARG_POST_MEDIA_OBJECT_TEXT, text);
            intent.putExtra(ViewPostFragment.ARG_POST_MEDIA_OBJECT_FILENAME, localPosts[position].media_objects[0].file_name);
            intent.putExtra(ViewPostFragment.ARG_POST_MEDIA_OBJECT_MEDIA_TYPE, localPosts[position].media_objects[0].media_type_name);


            startActivity(intent);


        }

    }



    public class LocalPostsArrayAdapter extends ArrayAdapter<Post> {

        private ArrayList<Post> localPosts;

        public LocalPostsArrayAdapter(Context context, int listViewId, ArrayList<Post> localPosts) {
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
                localPostViewHolder = new LocalPostViewHolder(getContext(), position, convertView, false);
                convertView.setTag(localPostViewHolder);

            } else {

                // get our view holder from the convertView
                localPostViewHolder = (LocalPostViewHolder) convertView.getTag();

            }

            final Post localPost = localPosts.get(position);

            localPostViewHolder.build(getContext(), localPost);

            return convertView;



        }
    }

}
