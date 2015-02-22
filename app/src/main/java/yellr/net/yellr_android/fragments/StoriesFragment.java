package yellr.net.yellr_android.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.activities.ViewStoryActivity;
import yellr.net.yellr_android.intent_services.stories.Story;
import yellr.net.yellr_android.intent_services.stories.StoriesIntentService;
import yellr.net.yellr_android.intent_services.stories.StoriesResponse;
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoriesFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private String cuid;
    private StoriesArrayAdapter storiesArrayAdapter;

    private Story[] stories;
    //private StoriesArrayAdapter storiesArrayAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StoriesFragment.
     */
    public static StoriesFragment newInstance() {
        StoriesFragment fragment = new StoriesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public StoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        // get cuid
        this.cuid = YellrUtils.getCUID(getActivity().getApplicationContext());

        // init new stories receiver
        Context context = getActivity().getApplicationContext();
        IntentFilter storiesFilter = new IntentFilter(StoriesIntentService.ACTION_NEW_STORIES);
        storiesFilter.addCategory(Intent.CATEGORY_DEFAULT);
        StoriesReceiver storiesReceiver = new StoriesReceiver();
        context.registerReceiver(storiesReceiver, storiesFilter);

        storiesArrayAdapter = new StoriesArrayAdapter(getActivity(), new ArrayList<Story>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_stories, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.frag_home_story_swipe_refresh_layout);
        listView = (ListView)view.findViewById(R.id.storiesList);

        listView.setAdapter(storiesArrayAdapter);
        listView.setOnItemClickListener(new StoryListOnClickListener());

        // This appear to be a linting error the Android Docs call for a Color Resource to be used here...
        // https://developer.android.com/reference/android/support/v4/widget/SwipeRefreshLayout.html#setProgressBackgroundColor(int)
        swipeRefreshLayout.setProgressBackgroundColor(R.color.yellow);
        swipeRefreshLayout.setColorSchemeResources(R.color.black);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshStoryData();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        Log.d("StoriesFragment.onResume()", "Starting stories intent service ...");
        refreshStoryData();
        super.onResume();
    }

    private void refreshStoryData() {
        // init service
        Context context = getActivity().getApplicationContext();
        Intent storiesWebIntent = new Intent(context, StoriesIntentService.class);
        //storiesWebIntent.putExtra(StoriesIntentService.PARAM_CUID, cuid);
        storiesWebIntent.setAction(StoriesIntentService.ACTION_GET_STORIES);
        context.startService(storiesWebIntent);
    }

    public class StoriesReceiver extends BroadcastReceiver {

        public StoriesReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            String storiesJson = intent.getStringExtra(StoriesIntentService.PARAM_STORIES_JSON);

            Gson gson = new Gson();
            StoriesResponse response = new StoriesResponse();
            try{
                response = gson.fromJson(storiesJson, StoriesResponse.class);
            } catch (Exception e){
                Log.d("StoriesFragment.onReceive", "GSON puked");
            }

            if (response.success) {
                storiesArrayAdapter.clear();
                stories = new Story[response.stories.length];
                for (int i = 0; i < response.stories.length; i++) {
                    Story story = response.stories[i];
                    storiesArrayAdapter.add(story);
                    stories[i] = story;
                }
                swipeRefreshLayout.setRefreshing(false);
            }

        }
    }

    class StoryListOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            Intent intent;
            intent = new Intent(getActivity().getApplicationContext(), ViewStoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra(ViewStoryFragment.ARG_STORY_TITLE,stories[position].title);
            intent.putExtra(ViewStoryFragment.ARG_STORY_AUTHOR,stories[position].author_first_name + " " + stories[position].author_last_name);
            intent.putExtra(ViewStoryFragment.ARG_STORY_PUBLISHED_DATETIME,stories[position].publish_datetime);
            intent.putExtra(ViewStoryFragment.ARG_STORY_BANNER_MEDIA_FILE_NAME,stories[position].banner_media_file_name);
            intent.putExtra(ViewStoryFragment.ARG_STORY_TOP_TEXT,stories[position].top_text);
            //intent.putExtra(ViewStoryFragment.ARG_STORY_CONTENTS,stories[position].contents);
            intent.putExtra(ViewStoryFragment.ARG_STORY_CONTENTS_RENDERED,stories[position].contents_rendered);

            startActivity(intent);
        }

    }

    class StoriesArrayAdapter extends ArrayAdapter<Story> {

        private ArrayList<Story> stories;

        public StoriesArrayAdapter(Context context, ArrayList<Story> stories) {
            super(context, R.layout.fragment_story_row, R.id.frag_home_story_title, stories);
            this.stories = stories;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = super.getView(position, convertView, parent);

            TextView textViewTitle = (TextView) row.findViewById(R.id.frag_home_story_title);
            TextView textViewPublishDateTime = (TextView) row.findViewById(R.id.frag_home_story_publish_datetime);
            TextView textViewPublishAuthor = (TextView) row.findViewById(R.id.frag_home_story_publish_author);

            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");
            textViewPublishAuthor.setTypeface(font);
            textViewPublishDateTime.setTypeface(font);

            textViewPublishAuthor.setText(getString(R.string.fa_user) + " " + stories.get(position).author_first_name + " " + stories.get(position).author_last_name);
            textViewTitle.setText(this.stories.get(position).title);

            Date pubDateStr = YellrUtils.PrettifyDateTime(this.stories.get(position).publish_datetime);
            String pubAgo = YellrUtils.calcTimeBetween(pubDateStr, new Date());

            Log.v("dateissue", this.stories.get(position).publish_datetime);
            textViewPublishDateTime.setText(getString(R.string.fa_pencil) + " " + pubAgo + " ago.");

            return row;
        }

    }


}
