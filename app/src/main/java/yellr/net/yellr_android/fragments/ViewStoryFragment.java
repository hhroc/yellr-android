package yellr.net.yellr_android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import yellr.net.yellr_android.R;

/**
 * Created by TDuffy on 2/7/2015.
 */
public class ViewStoryFragment extends Fragment {

    public static final String ARG_STORY_TITLE = "storyTitle";
    public static final String ARG_STORY_CONTENTS = "storyContents";

    private TextView storyTitle;
    private TextView storyContents;

    public ViewStoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_story, container, false);

        storyTitle = (TextView)view.findViewById(R.id.frag_view_story_title);
        storyContents = (TextView)view.findViewById(R.id.frag_view_story_contents);

        Intent intent = getActivity().getIntent();

        String title = intent.getStringExtra(ViewStoryFragment.ARG_STORY_TITLE);
        String contents = intent.getStringExtra(ViewStoryFragment.ARG_STORY_CONTENTS);

        storyTitle.setText(title);
        storyContents.setText(contents);

        return view;
    }
}
