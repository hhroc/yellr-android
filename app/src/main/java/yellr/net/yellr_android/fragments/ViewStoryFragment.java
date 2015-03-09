package yellr.net.yellr_android.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.util.Date;

import yellr.net.yellr_android.BuildConfig;
import yellr.net.yellr_android.R;
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * Created by TDuffy on 2/7/2015.
 */
public class ViewStoryFragment extends Fragment {

    public static final String ARG_STORY_TITLE = "storyTitle";
    public static final String ARG_STORY_AUTHOR = "storyAuthor";
    public static final String ARG_STORY_PUBLISHED_DATETIME = "storyPublishedDateTime";
    public static final String ARG_STORY_BANNER_MEDIA_FILE_NAME = "storyBannerMediaFilename";
    public static final String ARG_STORY_TOP_TEXT = "storyTopText";
    public static final String ARG_STORY_CONTENTS = "storyContents";
    public static final String ARG_STORY_CONTENTS_RENDERED = "storyContentsRendered";

    private TextView storyTitle;
    private TextView storyAuthor;
    private TextView storyPublishedDatetTime;
    private TextView storyLoading;
    //private TextView storyContentsRendered;
    private WebView storyContentsRendered;

    public ViewStoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_story, container, false);

        storyTitle = (TextView) view.findViewById(R.id.frag_view_story_title);
        storyAuthor = (TextView) view.findViewById(R.id.frag_view_story_author);
        storyPublishedDatetTime = (TextView) view.findViewById(R.id.frag_view_story_published_datetime);
        storyLoading = (TextView) view.findViewById(R.id.frag_view_story_loading);
        //storyContentsRendered = (TextView)view.findViewById(R.id.frag_view_story_contents_rendered);
        storyContentsRendered = (WebView) view.findViewById(R.id.frag_view_story_contents_rendered);

        Intent intent = getActivity().getIntent();

        String title = intent.getStringExtra(ViewStoryFragment.ARG_STORY_TITLE);
        String author = intent.getStringExtra(ViewStoryFragment.ARG_STORY_AUTHOR);
        String publishedDateTime = intent.getStringExtra(ViewStoryFragment.ARG_STORY_PUBLISHED_DATETIME);

        String bannerMediaFilename = intent.getStringExtra(ViewStoryFragment.ARG_STORY_BANNER_MEDIA_FILE_NAME);
        String topText = intent.getStringExtra(ViewStoryFragment.ARG_STORY_TOP_TEXT);
        String contentsRendered = intent.getStringExtra(ViewStoryFragment.ARG_STORY_CONTENTS_RENDERED);

        //TODO Use new datetime prettifier
        Date pubDT = YellrUtils.prettifyDateTime(publishedDateTime);
        String pubAgo = YellrUtils.calcTimeBetween(pubDT, new Date());

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");
        storyAuthor.setTypeface(font);
        storyPublishedDatetTime.setTypeface(font);

        storyTitle.setText(title);
        storyAuthor.setText(getString(R.string.fa_user) + " " + author);
        storyPublishedDatetTime.setText(getString(R.string.fa_pencil) + " " + pubAgo + " ago.");

        // remove loading text
        //storyLoading.setText("");

        //storyContentsRendered.setText(Html.fromHtml(contentsRendered));
        //storyContentsRendered.evaluateJavascript();
        storyContentsRendered.setVisibility(View.GONE);
        storyContentsRendered.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                storyLoading.setVisibility(View.GONE);
                storyContentsRendered.setVisibility(View.VISIBLE);
            }
        });
        storyContentsRendered.loadData(generateStoryHtml(contentsRendered), "text/html", null);

        return view;
    }

    private String generateStoryHtml(String renderedMarkdown) {

        String htmlTemplate = ""
                + "<html>"
                + "<head>"
                + "<style>"
                + "body { background-color: #EEEEEE; } "
                + "div.banner-wrapper { padding: 10px 10px 10px 10px;} "
                + "img { max-width: 75%;} "
                //+ "div.top-text-wrapper { padding: 10px 10px 10px 10px; font-style: italic; text-align: center; font-size: 12px; }"
                //+ "div.loading { padding 10px 10px 10px 10px; font-size: 12px; }"
                + "div.story-wrapper { padding: 25px 25px 25px 25px; font-size: 16dp; } " // display: none; }"
                + "</style>"
                + "</head>"
                + "<body>"
                //+ "<div class=\"top-text-wrapper\"><center><img id=\"banner-image\" src=\"<banner />\"></img></center></div>"
                //+ "<div class=\"top-text-wrapper\"><topText /></div>"
                //+ "<div id=\"loading\" class=\"loading\">Loading Story ...</div>"
                + "<div id=\"story\" class=\"story-wrapper\"><renderedMarkdown /></div>"
                //+ "<script>"
                //+ "  //(function() {"
                //+ "    document.getElementById('loading').style.display = 'none';"
                //+ "    document.getElementById('story').style.display = 'block';"
                //+ "  //})();"
                //+ "</script>"
                + "</body>"
                + "</html>";

        Log.d("generateStoryHtml()", "HTML: \n" + htmlTemplate);

        //String banner = BuildConfig.BASE_URL + "/media/" + bannerMediaFilename;

        String renderedHtml = htmlTemplate.replace("<renderedMarkdown />",renderedMarkdown);

        return renderedHtml;

    }

}
