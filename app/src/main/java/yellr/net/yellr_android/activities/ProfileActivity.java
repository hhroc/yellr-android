package yellr.net.yellr_android.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.fragments.ProfileFragment;

public class ProfileActivity extends ActionBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ProfileFragment().newInstance())
                    .commit();
        }
    }
}
