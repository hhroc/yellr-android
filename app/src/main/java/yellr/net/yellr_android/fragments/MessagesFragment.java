package yellr.net.yellr_android.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.intent_services.messages.Message;
import yellr.net.yellr_android.intent_services.messages.MessagesIntentService;
import yellr.net.yellr_android.intent_services.messages.MessagesResponse;
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * Created by TDuffy on 3/27/2015.
 */
public class MessagesFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private String cuid;
    private MessagesArrayAdapter messagesArrayAdapter;

    private Message[] messages;
    //private MessagesArrayAdapter messagesArrayAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessagesFragment.
     */
    public static MessagesFragment newInstance() {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        Log.d("MessagesFragment.onCreate()", "Creating fragment ...");

        // get the cuid
        this.cuid = YellrUtils.getCUID(getActivity().getApplicationContext());

        // init new messages receiver
        Context context = getActivity().getApplicationContext();
        IntentFilter messagesFilter = new IntentFilter(MessagesReceiver.ACTION_NEW_MESSAGES);
        messagesFilter.addCategory(Intent.CATEGORY_DEFAULT);
        MessagesReceiver messagesReceiver = new MessagesReceiver();
        context.registerReceiver(messagesReceiver, messagesFilter);

        messagesArrayAdapter = new MessagesArrayAdapter(getActivity(), new ArrayList<Message>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_messages, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.frag_messages_swipe_refresh_layout);
        listView = (ListView)view.findViewById(R.id.frag_messages_messages_list);

        listView.setAdapter(messagesArrayAdapter);
        listView.setOnItemClickListener(new MessageListOnClickListener());

        // This appear to be a linting error the Android Docs call for a Color Resource to be used here...
        // https://developer.android.com/reference/android/support/v4/widget/SwipeRefreshLayout.html#setProgressBackgroundColor(int)
        swipeRefreshLayout.setProgressBackgroundColor(R.color.yellow);
        swipeRefreshLayout.setColorSchemeResources(R.color.black);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMessageData();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        Log.d("MessagesFragment.onResume()", "Starting messages intent service ...");
        refreshMessageData();
        super.onResume();
    }

    private void refreshMessageData() {
        // init service
        Context context = getActivity().getApplicationContext();
        Intent messagesWebIntent = new Intent(context, MessagesIntentService.class);
        //messagesWebIntent.putExtra(MessagesIntentService.PARAM_CUID, cuid);
        messagesWebIntent.setAction(MessagesIntentService.ACTION_GET_MESSAGES);
        context.startService(messagesWebIntent);
    }

    public class MessagesReceiver extends BroadcastReceiver {
        public static final String ACTION_NEW_MESSAGES =
                "yellr.net.yellr_android.action.NEW_MESSAGES";

        public MessagesReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("MessagesReceiver.onReceive()", "New messages payload ...");

            try {

                String messagesJson = intent.getStringExtra(MessagesIntentService.PARAM_MESSAGES_JSON);

                Gson gson = new Gson();
                MessagesResponse response = new MessagesResponse();
                try{
                    response = gson.fromJson(messagesJson, MessagesResponse.class);
                } catch (Exception e){
                    Log.d("MessagesFragment.onReceive", "GSON puked");
                }

                Log.d("MessagesReceiver.onReceive()","Success: " + response.success);

                if (response.success) {
                    messagesArrayAdapter.clear();
                    messages = new Message[response.messages.length];
                    for (int i = 0; i < response.messages.length; i++) {
                        Message message = response.messages[i];
                        messagesArrayAdapter.add(message);
                        messages[i] = message;
                    }
                    swipeRefreshLayout.setRefreshing(false);

                    Log.d("MessagesReceiver.onReceive()","messages.length = " + messages.length);

                }

            } catch ( Exception ex ){
                Log.d("MessagesReceiver.onReceive()","ERROR: " + ex.toString());
            }

            /*


            */
        }
    }

    class MessageListOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            /*
            Intent intent;
            intent = new Intent(getActivity().getApplicationContext(), ViewMessageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra(ViewMessageFragment.ARG_MESSAGE_TITLE,messages[position].title);
            intent.putExtra(ViewMessageFragment.ARG_MESSAGE_AUTHOR,messages[position].author_first_name + " " + messages[position].author_last_name);
            intent.putExtra(ViewMessageFragment.ARG_MESSAGE_PUBLISHED_DATETIME,messages[position].publish_datetime);
            intent.putExtra(ViewMessageFragment.ARG_MESSAGE_BANNER_MEDIA_FILE_NAME,messages[position].banner_media_file_name);
            intent.putExtra(ViewMessageFragment.ARG_MESSAGE_TOP_TEXT,messages[position].top_text);
            //intent.putExtra(ViewMessageFragment.ARG_MESSAGE_CONTENTS,messages[position].contents);
            intent.putExtra(ViewMessageFragment.ARG_MESSAGE_CONTENTS_RENDERED,messages[position].contents_rendered);

            startActivity(intent);
            */
        }

    }

    class MessagesArrayAdapter extends ArrayAdapter<Message> {

        private ArrayList<Message> messages;

        public MessagesArrayAdapter(Context context, ArrayList<Message> messages) {
            super(context, R.layout.fragment_message_row, R.id.frag_messages_message_subject, messages);
            this.messages = messages;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = super.getView(position, convertView, parent);

            Log.d("MessagesArrayAdapter.getView()","Updating view ...");

            TextView textViewMessageSubject = (TextView) row.findViewById(R.id.frag_messages_message_subject);

            textViewMessageSubject.setText(messages.get(position).message_subject);

            String messageText = "";

            /*
            switch(messages.get(position).){
                case "post_successful":
                    messageText = "Your post was successful!";
                    break;
                case "post_viewed":
                    messageText = "Your post was viewed by " + messages.get(position).payload.organization + "!";
                    break;
                case "new_message":
                    messageText = "You have new message from " + messages.get(position).payload.organization + "!";
                    break;
                case"message_sent":
                    messageText = "Your message was successfully sent.";
                    break;
                default:
                    messageText = "Generic message";
                    break;
            }
            */

            //textViewMessageText.setText(messageText);

            /*

            TextView textViewTitle = (TextView) row.findViewById(R.id.frag_home_message_title);
            TextView textViewPublishDateTime = (TextView) row.findViewById(R.id.frag_home_message_publish_datetime);
            TextView textViewPublishAuthor = (TextView) row.findViewById(R.id.frag_home_message_publish_author);

            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");
            textViewPublishAuthor.setTypeface(font);
            textViewPublishDateTime.setTypeface(font);

            textViewPublishAuthor.setText(getString(R.string.fa_user) + " " + messages.get(position).author_first_name + " " + messages.get(position).author_last_name);
            textViewTitle.setText(this.messages.get(position).title);

            Date pubDateStr = YellrUtils.PrettifyDateTime(this.messages.get(position).publish_datetime);
            String pubAgo = YellrUtils.calcTimeBetween(pubDateStr, new Date());

            Log.v("dateissue", this.messages.get(position).publish_datetime);
            textViewPublishDateTime.setText(getString(R.string.fa_pencil) + " " + pubAgo + " ago.");

            */

            return row;
        }

    }

    /*
    public class MessagesReceiver extends BroadcastReceiver {
        //public static final String ACTION_NEW_MESSAGES =
        //        "yellr.net.yellr_android.action.NEW_MESSAGES";

        public MessagesReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("MessagesFragment.onReceive", "New Local Posts for display.");

            String messagesJson = intent.getStringExtra(MessagesIntentService.PARAM_MESSAGES_JSON);

            Gson gson = new Gson();
            MessagesResponse response = new MessagesResponse();
            try{
                response = gson.fromJson(messagesJson, MessagesResponse.class);
            } catch(Exception e){
                Log.d("MessagesFragment.onReceive", "GSON puked");
            }

            if (response.success && response.messages != null) {
                messagesArrayAdapter.clear();
                messages = new Message[response.messages.length];
                for (int i = 0; i < response.messages.length; i++) {
                    Message message = response.messages[i];
                    messagesArrayAdapter.add(message);
                    messages[i] = message;
                }
            }

            swipeRefreshLayout.setRefreshing(false);
        }
    }
    */
}
