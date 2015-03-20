package yellr.net.yellr_android.intent_services.assignments;

/**
 * Created by TDuffy on 2/3/2015.
 */

public class Assignment {

    public int assignment_id;

    public String organization;

    public int question_type_id; // 1 - free_text, 2 - multi choice

    public String question_text;
    public String description;

    public String answer0;
    public String answer1;
    public String answer2;
    public String answer3;
    public String answer4;
    public String answer5;
    public String answer6;
    public String answer7;
    public String answer8;
    public String answer9;

    public double top_left_lat;
    public double top_left_lng;
    public double bottom_right_lat;
    public double bottom_right_lng;

    public int post_count;
    public boolean has_responded;

    public String publish_datetime;

    @Override
    public String toString() {
        return question_text;
    }

}
