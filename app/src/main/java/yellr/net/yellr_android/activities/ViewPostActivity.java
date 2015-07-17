package yellr.net.yellr_android.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.fragments.ViewPostFragment;

/**
 * Created by TDuffy on 4/16/2015.
 */
public class ViewPostActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ViewPostFragment())
                    .commit();
        }
    }

}
