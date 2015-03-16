package yellr.net.yellr_android.intent_services.local_posts;

/**
 * Created by tduffy on 3/11/2015.
 */
public class LocalPost {

    public int post_id;

    public String post_datetime;

    public boolean verified_user;
    public String first_name;
    public String last_name;

    public String language_name;
    public String language_code;

    public String question_text;

    public int up_vote_count;
    public int down_vote_count;
    public int has_voted;
    public int is_up_vote;

    public MediaObject[] media_objects;

}
