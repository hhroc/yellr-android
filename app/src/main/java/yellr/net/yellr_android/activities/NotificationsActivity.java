package yellr.net.yellr_android.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.fragments.NotificationsFragment;

public class NotificationsActivity extends ActionBarActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new NotificationsFragment())
                    .commit();
        }
    }
}
