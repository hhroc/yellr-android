package yellr.net.yellr_android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.intent_services.local_posts.MediaObject;
import yellr.net.yellr_android.intent_services.local_posts.Post;
import yellr.net.yellr_android.utils.LocalPostViewHolder;

/**
 * Created by TDuffy on 4/16/2015.
 */
public class ViewPostFragment extends Fragment {

    public static final String ARG_POST_LIST_POSITION = "postListPosition";

    public static final String ARG_POST_DATETIME = "postDateTime";
    public static final String ARG_POST_QUESTION_TEXT = "postQuestionText";
    public static final String ARG_POST_UP_VOTE_COUNT = "postUpVoteCount";
    public static final String ARG_POST_DOWN_VOTE_COUNT = "postDownVoteCount";
    public static final String ARG_POST_HAS_VOTED = "postHasVoted";
    public static final String ARG_POST_IS_UP_VOTE = "postIsUpVote";

    public static final String ARG_POST_MEDIA_OBJECT_TEXT = "postMediaObjectText";
    public static final String ARG_POST_MEDIA_OBJECT_FILENAME = "postMediaObjectFilename";
    public static final String ARG_POST_MEDIA_OBJECT_MEDIA_TYPE = "postMediaObjectMediaType";

    public ViewPostFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);

        Post post = new Post();

        Intent intent = getActivity().getIntent();

        int position = intent.getIntExtra(ViewPostFragment.ARG_POST_LIST_POSITION, 0);

        post.post_datetime = intent.getStringExtra(ViewPostFragment.ARG_POST_DATETIME);
        post.question_text = intent.getStringExtra(ViewPostFragment.ARG_POST_QUESTION_TEXT);
        post.up_vote_count = intent.getIntExtra(ViewPostFragment.ARG_POST_UP_VOTE_COUNT,0);
        post.down_vote_count = intent.getIntExtra(ViewPostFragment.ARG_POST_DOWN_VOTE_COUNT,0);
        post.has_voted = intent.getIntExtra(ViewPostFragment.ARG_POST_HAS_VOTED,0);
        post.is_up_vote = intent.getIntExtra(ViewPostFragment.ARG_POST_IS_UP_VOTE,0);

        post.media_objects = new MediaObject[1];
        post.media_objects[0] = new MediaObject();

        post.media_objects[0].media_text = intent.getStringExtra(ViewPostFragment.ARG_POST_MEDIA_OBJECT_TEXT);
        post.media_objects[0].caption = intent.getStringExtra(ViewPostFragment.ARG_POST_MEDIA_OBJECT_TEXT);
        post.media_objects[0].file_name = intent.getStringExtra(ViewPostFragment.ARG_POST_MEDIA_OBJECT_FILENAME);
        post.media_objects[0].media_type_name = intent.getStringExtra(ViewPostFragment.ARG_POST_MEDIA_OBJECT_MEDIA_TYPE);

        LocalPostViewHolder localPostViewHolder = new LocalPostViewHolder(getActivity().getApplicationContext(), position, view, true);

        localPostViewHolder.build(getActivity().getApplicationContext(), post);

        return view;
    }

}
