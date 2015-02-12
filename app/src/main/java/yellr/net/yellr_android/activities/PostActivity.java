package yellr.net.yellr_android.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.fragments.PostFragment;

public class PostActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

}
