package yellr.net.yellr_android.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.fragments.MessagesFragment;

/**
 * Created by TDuffy on 3/27/2015.
 */
public class MessagesActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MessagesFragment())
                    .commit();
        }
    }
}