package yellr.net.yellr_android.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import yellr.net.yellr_android.BuildConfig;
import yellr.net.yellr_android.R;
import yellr.net.yellr_android.utils.YellrUtils;

/**
 * Created by TDuffy on 3/18/2015.
 */
public class AboutMainFragment extends DialogFragment {

    public static final String DIALOG_SHOW_ABOUT_MAIN = "yellr.net.yellr_android.dialog.SHOW_ABOUT_MAIN";

    private String city;
    private String stateCode;

    public AboutMainFragment(){

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //final String zipcode = getArguments().getString("zipcode");
        //final String city = getArguments().getString("city");
        //final String stateCode =  getArguments().getString("stateCode");
        //final Float lat = getArguments().getFloat("lat");
        //final Float lng = getArguments().getFloat("lng");

        String aboutMessage = ""
                + getString(R.string.about_yellr_second) + "\n\n"
                + getString(R.string.about_yellr_third) + "\n\n"
                + getString(R.string.about_yellr_fourth) + "\n\n";

        builder.setTitle(getString(R.string.about_yellr_first) + " " + BuildConfig.VERSION_NAME + "!")
                .setMessage(aboutMessage)
                .setPositiveButton("Get Started!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing to do.
                    }
                });
        return builder.create();
    }
}

