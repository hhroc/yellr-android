package yellr.net.yellr_android.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
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
//import yellr.net.yellr_android.activities.ViewNotificationActivity;
import yellr.net.yellr_android.intent_services.notifications.NotificationsIntentService;
import yellr.net.yellr_android.intent_services.notifications.NotificationsResponse;
import yellr.net.yellr_android.intent_services.notifications.Notification;
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * Created by TDuffy on 2/8/2015.
 */
public class NotificationsFragment extends Fragment{

    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private String clientId;
    private NotificationsArrayAdapter notificationsArrayAdapter;

    private Notification[] notifications;
    //private NotificationsArrayAdapter notificationsArrayAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NotificationsFragment.
     */
    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        Log.d("NotificationsFragment.onCreate()", "Creating fragment ...");

        // get clientId
        SharedPreferences sharedPref = getActivity().getSharedPreferences("clientId", Context.MODE_PRIVATE);
        clientId = sharedPref.getString("clientId", "");

        // init new notifications receiver
        Context context = getActivity().getApplicationContext();
        IntentFilter notificationsFilter = new IntentFilter(NotificationsReceiver.ACTION_NEW_NOTIFICATIONS);
        notificationsFilter.addCategory(Intent.CATEGORY_DEFAULT);
        NotificationsReceiver notificationsReceiver = new NotificationsReceiver();
        context.registerReceiver(notificationsReceiver, notificationsFilter);

        notificationsArrayAdapter = new NotificationsArrayAdapter(getActivity(), new ArrayList<Notification>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_notifications, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.frag_home_notifications_swipe_refresh_layout);
        listView = (ListView)view.findViewById(R.id.notifications_list);

        listView.setAdapter(notificationsArrayAdapter);
        listView.setOnItemClickListener(new NotificationListOnClickListener());

        // This appear to be a linting error the Android Docs call for a Color Resource to be used here...
        // https://developer.android.com/reference/android/support/v4/widget/SwipeRefreshLayout.html#setProgressBackgroundColor(int)
        swipeRefreshLayout.setProgressBackgroundColor(R.color.yellow);
        swipeRefreshLayout.setColorSchemeResources(R.color.black);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNotificationData();
            }
        });
        return view;
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
        Log.d("NotificationsFragment.onResume()", "Starting notifications intent service ...");
        refreshNotificationData();
        super.onResume();
    }

    private void refreshNotificationData() {
        // init service
        Context context = getActivity().getApplicationContext();
        Intent notificationsWebIntent = new Intent(context, NotificationsIntentService.class);
        notificationsWebIntent.putExtra(NotificationsIntentService.PARAM_CLIENT_ID, clientId);
        notificationsWebIntent.setAction(NotificationsIntentService.ACTION_GET_NOTIFICATIONS);
        context.startService(notificationsWebIntent);
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

    public class NotificationsReceiver extends BroadcastReceiver {
        public static final String ACTION_NEW_NOTIFICATIONS =
                "yellr.net.yellr_android.action.NEW_NOTIFICATIONS";

        public NotificationsReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("NotificationsReceiver.onReceive()", "New notifications payload ...");

            try {

                String notificationsJson = intent.getStringExtra(NotificationsIntentService.PARAM_NOTIFICATIONS_JSON);

                Gson gson = new Gson();
                NotificationsResponse response = gson.fromJson(notificationsJson, NotificationsResponse.class);

                Log.d("NotificationsReceiver.onReceive()","Success: " + response.success);

                if (response.success) {
                    notificationsArrayAdapter.clear();
                    notifications = new Notification[response.notifications.length];
                    for (int i = 0; i < response.notifications.length; i++) {
                        Notification notification = response.notifications[i];
                        notificationsArrayAdapter.add(notification);
                        notifications[i] = notification;
                    }
                    swipeRefreshLayout.setRefreshing(false);

                    Log.d("NotificationsReceiver.onReceive()","notifications.length = " + notifications.length);

                }

            } catch ( Exception ex ){
                Log.d("NotificationsReceiver.onReceive()","ERROR: " + ex.toString());
            }

            /*


            */
        }
    }

    class NotificationListOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            /*
            Intent intent;
            intent = new Intent(getActivity().getApplicationContext(), ViewNotificationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra(ViewNotificationFragment.ARG_NOTIFICATION_TITLE,notifications[position].title);
            intent.putExtra(ViewNotificationFragment.ARG_NOTIFICATION_AUTHOR,notifications[position].author_first_name + " " + notifications[position].author_last_name);
            intent.putExtra(ViewNotificationFragment.ARG_NOTIFICATION_PUBLISHED_DATETIME,notifications[position].publish_datetime);
            intent.putExtra(ViewNotificationFragment.ARG_NOTIFICATION_BANNER_MEDIA_FILE_NAME,notifications[position].banner_media_file_name);
            intent.putExtra(ViewNotificationFragment.ARG_NOTIFICATION_TOP_TEXT,notifications[position].top_text);
            //intent.putExtra(ViewNotificationFragment.ARG_NOTIFICATION_CONTENTS,notifications[position].contents);
            intent.putExtra(ViewNotificationFragment.ARG_NOTIFICATION_CONTENTS_RENDERED,notifications[position].contents_rendered);

            startActivity(intent);
            */
        }

    }

    class NotificationsArrayAdapter extends ArrayAdapter<Notification> {

        private ArrayList<Notification> notifications;

        public NotificationsArrayAdapter(Context context, ArrayList<Notification> notifications) {
            super(context, R.layout.fragment_notification_row, R.id.frag_notification_text, notifications);
            this.notifications = notifications;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = super.getView(position, convertView, parent);

            Log.d("NotificationsArrayAdapter.getView()","Updating view ...");

            TextView textViewNotificationText = (TextView) row.findViewById(R.id.frag_notification_text);

            String notificationText = "";

            switch(notifications.get(position).notification_type){
                case "post_successful":
                    notificationText = "Your post was successful!";
                    break;
                case "post_viewed":
                    notificationText = "Your post was viewed by " + notifications.get(position).payload.organization + "!";
                    break;
                case "new_message":
                    notificationText = "You have new message from " + notifications.get(position).payload.organization + "!";
                    break;
                case"message_sent":
                    notificationText = "Your message was successfully sent.";
                    break;
                default:
                    notificationText = "Generic notification";
                    break;
            }

            textViewNotificationText.setText(notificationText);

            /*

            TextView textViewTitle = (TextView) row.findViewById(R.id.frag_home_notification_title);
            TextView textViewPublishDateTime = (TextView) row.findViewById(R.id.frag_home_notification_publish_datetime);
            TextView textViewPublishAuthor = (TextView) row.findViewById(R.id.frag_home_notification_publish_author);

            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");
            textViewPublishAuthor.setTypeface(font);
            textViewPublishDateTime.setTypeface(font);

            textViewPublishAuthor.setText(getString(R.string.fa_user) + " " + notifications.get(position).author_first_name + " " + notifications.get(position).author_last_name);
            textViewTitle.setText(this.notifications.get(position).title);

            Date pubDateStr = YellrUtils.PrettifyDateTime(this.notifications.get(position).publish_datetime);
            String pubAgo = YellrUtils.calcTimeBetween(pubDateStr, new Date());

            Log.v("dateissue", this.notifications.get(position).publish_datetime);
            textViewPublishDateTime.setText(getString(R.string.fa_pencil) + " " + pubAgo + " ago.");

            */

            return row;
        }

    }
    
}
