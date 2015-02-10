package yellr.net.yellr_android.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * Created by TDuffy on 2/7/2015.
 */
public class ViewStoryFragment extends Fragment {

    public static final String ARG_STORY_TITLE = "storyTitle";
    public static final String ARG_STORY_AUTHOR = "storyAuthor";
    public static final String ARG_STORY_PUBLISHED_DATETIME = "storyPublishedDateTime";
    public static final String ARG_STORY_CONTENTS = "storyContents";

    private TextView storyTitle;
    private TextView storyAuthor;
    private TextView storyPublishedDatetTime;
    private TextView storyContents;

    public ViewStoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_story, container, false);

        storyTitle = (TextView)view.findViewById(R.id.frag_view_story_title);
        storyAuthor = (TextView)view.findViewById(R.id.frag_view_story_author);
        storyPublishedDatetTime = (TextView)view.findViewById(R.id.frag_view_story_published_datetime);
        storyContents = (TextView)view.findViewById(R.id.frag_view_story_contents);

        Intent intent = getActivity().getIntent();

        String title = intent.getStringExtra(ViewStoryFragment.ARG_STORY_TITLE);
        String author = intent.getStringExtra(ViewStoryFragment.ARG_STORY_AUTHOR);
        String publishedDateTime = intent.getStringExtra(ViewStoryFragment.ARG_STORY_PUBLISHED_DATETIME);
        String contents = intent.getStringExtra(ViewStoryFragment.ARG_STORY_CONTENTS);

        //TODO Use new datetime prettifier
        Date pubDT = YellrUtils.PrettifyDateTime(publishedDateTime);
        String pubAgo = YellrUtils.calcTimeBetween(pubDT, new Date());

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");
        storyAuthor.setTypeface(font);
        storyPublishedDatetTime.setTypeface(font);

        storyTitle.setText(title);
        storyAuthor.setText(getString(R.string.fa_user) + " " + author);
        storyPublishedDatetTime.setText(getString(R.string.fa_pencil) + " " + pubAgo + " ago.");
        storyContents.setText(contents);

        return view;
    }
}
