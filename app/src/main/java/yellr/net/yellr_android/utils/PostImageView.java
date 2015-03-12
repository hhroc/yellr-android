package yellr.net.yellr_android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by TDuffy on 3/12/2015.
 */
public class PostImageView extends ImageView {

    public PostImageView(Context context, AttributeSet attrs) { //}, int defaultStyle) {
        super(context, attrs); //, defaultStyle);
    }

    public void setImage(String url, int position) {
        BitmapDownloaderTask task = new BitmapDownloaderTask();
        task.execute(url);
    }

    //
    // This code modified from:
    //    http://android-developers.blogspot.com/2010/07/multithreading-for-performance.html
    //
    class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private String url;

        public BitmapDownloaderTask() {
        }

        @Override
        // Actual download method, run in the task thread
        protected Bitmap doInBackground(String... params) {
            this.url = params[0];
            // params comes from the execute() call: params[0] is the url.
            return YellrUtils.downloadBitmap(this.url);
        }

        @Override
        // Once the image is downloaded, associates it to the imageView
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            } else {
                //this.imageView.setImageBitmap(bitmap);
            }

            Log.d("BitmapDownloaderTask.onPostExecute()","Setting Image URL: " + url);

            setImageBitmap(bitmap);
        }
    }
}
