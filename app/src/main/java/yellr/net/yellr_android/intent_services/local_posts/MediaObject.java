package yellr.net.yellr_android.intent_services.local_posts;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import yellr.net.yellr_android.BuildConfig;
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * Created by tduffy on 3/11/2015.
 */
public class MediaObject {

    public String media_id;
    public String preview_file_name;
    public String file_name;
    public String media_text;
    public String caption;
    public String media_type_description;
    public String media_type_name;

    public String displayImageUrl;
    public Bitmap displayImage;

    /*
    public void downloadDisplayImage() {
        //try {
            BitmapDownloaderTask task = new BitmapDownloaderTask();
            String url = BuildConfig.BASE_URL + "/media/" + YellrUtils.getPreviewImageName(this.file_name);
            //Log.d("MediaObject.downloadDisplayImage()","url: " + url);
            task.execute(url);
        //} catch (Exception e) {
        //    // TODO: do something with exception
        //}
    }
    */


}
