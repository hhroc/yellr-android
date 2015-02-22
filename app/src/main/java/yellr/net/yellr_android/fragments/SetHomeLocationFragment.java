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

import yellr.net.yellr_android.R;
import yellr.net.yellr_android.utils.YellrUtils;

public class SetHomeLocationFragment extends DialogFragment {

    public static final String DIALOG_SET_LOCATION = "yellr.net.yellr_android.dialog.SET_LOCATION";

    private String city;
    private String stateCode;

    public SetHomeLocationFragment(){

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final String zipcode = getArguments().getString("zipcode");
        final String city = getArguments().getString("city");
        final String stateCode =  getArguments().getString("stateCode");
        final Float lat = getArguments().getFloat("lat");
        final Float lng = getArguments().getFloat("lng");

        builder.setTitle("Set Home Location?")
                .setMessage("Set " + city + ", " + stateCode + " to your home location?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //YellrUtils.resetCUID(getActivity());

                        YellrUtils.setHomeLocation(getActivity().getApplicationContext(), zipcode, city, stateCode, lat, lng);

                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }
}