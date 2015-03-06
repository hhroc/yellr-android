package yellr.net.yellr_android.activities;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.fragments.PostFragment;

public class PostActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setTheme();

        // Setup the action bar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.hide();
        //actionBar.setDisplayShowHomeEnabled(false);
        //actionBar.setDisplayShowTitleEnabled(false);

        setContentView(R.layout.activity_post);
        if (savedInstanceState == null) {
            int assignmentId = getIntent().getExtras().getInt(PostFragment.ARG_ASSIGNMENT_ID);
            String questionText = getIntent().getExtras().getString(PostFragment.ARG_ASSIGNMENT_QUESTION);
            String questionDescription = getIntent().getExtras().getString(PostFragment.ARG_ASSIGNMENT_DESCRIPTION);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PostFragment().newInstance(assignmentId, questionText, questionDescription))
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
