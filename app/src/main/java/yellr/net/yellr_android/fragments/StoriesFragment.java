package yellr.net.yellr_android.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.activities.HomeActivity;
import yellr.net.yellr_android.activities.ViewStoryActivity;
import yellr.net.yellr_android.intent_services.IntentServicesHelper;
import yellr.net.yellr_android.intent_services.stories.Story;
import yellr.net.yellr_android.intent_services.stories.StoriesIntentService;
import yellr.net.yellr_android.intent_services.stories.StoriesResponse;
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StoriesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoriesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

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

        // init new stories receiver
        Context context = getActivity().getApplicationContext();
        IntentFilter storiesFilter = new IntentFilter(StoriesReceiver.ACTION_NEW_STORIES);
        storiesFilter.addCategory(Intent.CATEGORY_DEFAULT);
        StoriesReceiver storiesReceiver = new StoriesReceiver();
        context.registerReceiver(storiesReceiver, storiesFilter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stories, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {

        // get clientId
        SharedPreferences sharedPref = getActivity().getSharedPreferences("clientId", Context.MODE_PRIVATE);
        String clientId = sharedPref.getString("clientId", "");

        Log.d("StoriesFragment.onResume()", "Starting stories intent service ...");

        // init service
        Context context = getActivity().getApplicationContext();
        Intent storiesWebIntent = new Intent(context, StoriesIntentService.class);
        storiesWebIntent.putExtra(StoriesIntentService.PARAM_CLIENT_ID, clientId);
        storiesWebIntent.setAction(StoriesIntentService.ACTION_GET_STORIES);
        context.startService(storiesWebIntent);

        super.onResume();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public class StoriesReceiver extends BroadcastReceiver {
        public static final String ACTION_NEW_STORIES =
                "yellr.net.yellr_android.action.NEW_STORIES";

        private ListView listView;

        public StoriesReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            listView = (ListView)getView().findViewById(R.id.storiesList);

            //Log.d("StoriesReceiver.onReceive()", "onReceive called.");

            String storiesJson = intent.getStringExtra(StoriesIntentService.PARAM_STORIES_JSON);

            //Log.d("StoriesReceiver.onReceive()", "JSON: " + storiesJson);

            Gson gson = new Gson();
            StoriesResponse response = gson.fromJson(storiesJson, StoriesResponse.class);

            if (response.success) {

                StoriesArrayAdapter storiesArrayAdapter = new StoriesArrayAdapter(getActivity(), new ArrayList<Story>());

                stories = new Story[response.stories.length];

                for (int i = 0; i < response.stories.length; i++) {
                    Story story = response.stories[i];
                    storiesArrayAdapter.add(story);
                    stories[i] = story;
                }

                //Log.d("StoriesReceiver.onReceive()", "Setting listView adapter ...");

                listView.setAdapter(storiesArrayAdapter);
                listView.setOnItemClickListener(new StoryListOnClickListener());

            }

        }
    }

    class StoryListOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            //builder.setMessage("neat");

            Intent intent;
            intent = new Intent(getActivity().getApplicationContext(), ViewStoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra(ViewStoryFragment.ARG_STORY_TITLE,stories[position].title);
            intent.putExtra(ViewStoryFragment.ARG_STORY_AUTHOR,stories[position].author_first_name + " " + stories[position].author_last_name);
            intent.putExtra(ViewStoryFragment.ARG_STORY_PUBLISHED_DATETIME,stories[position].publish_datetime);
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

            //Log.d("StoriesArrayAdapter.StoriesArrayAdapter()","Constructor.");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = super.getView(position, convertView, parent);

            //Log.d("StoriesArrayAdapter.getView()","Setting values for view.");

            TextView textViewTitle = (TextView) row.findViewById(R.id.frag_home_story_title);
            TextView textViewPublishDateTime = (TextView) row.findViewById(R.id.frag_home_story_publish_datetime);
            TextView textViewPublishAuthor = (TextView) row.findViewById(R.id.frag_home_story_publish_author);

            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");
            textViewPublishAuthor.setTypeface(font);
            textViewPublishDateTime.setTypeface(font);

            textViewPublishAuthor.setText(getString(R.string.fa_user) + " Tim Duffy/HHROC");
            textViewTitle.setText(this.stories.get(position).title);

            Date pubDateStr = YellrUtils.PrettifyDateTime(this.stories.get(position).publish_datetime);
            String pubAgo = YellrUtils.calcTimeBetween(pubDateStr, new Date());

            Log.v("dateissue", this.stories.get(position).publish_datetime);
            textViewPublishDateTime.setText(getString(R.string.fa_pencil) + " " + pubAgo + " ago.");

            return row;
        }

    }


}
