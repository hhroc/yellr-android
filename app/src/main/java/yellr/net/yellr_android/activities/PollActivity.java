package yellr.net.yellr_android.activities;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.fragments.PollFragment;

public class PollActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // remove title and action bars
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        // Setup the action bar
        final ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.hide();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        setContentView(R.layout.activity_poll);
        if (savedInstanceState == null) {
            int assignmentId = getIntent().getExtras().getInt(PollFragment.ARG_ASSIGNMENT_ID);
            String pollText = getIntent().getExtras().getString(PollFragment.ARG_POLL_TEXT);
            String pollOptions = getIntent().getExtras().getString(PollFragment.ARG_POLL_OPTIONS);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PollFragment().newInstance(assignmentId, pollText, pollOptions))
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
