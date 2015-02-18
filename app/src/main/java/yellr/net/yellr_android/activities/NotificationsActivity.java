package yellr.net.yellr_android.activities;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.fragments.NotificationsFragment;

public class NotificationsActivity extends ActionBarActivity implements NotificationsFragment.OnFragmentInteractionListener {

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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    /*
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
            return rootView;
        }
    }
    */
}
