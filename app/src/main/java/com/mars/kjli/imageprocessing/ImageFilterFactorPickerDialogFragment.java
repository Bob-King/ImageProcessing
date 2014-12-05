package com.mars.kjli.imageprocessing;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFilterFactorPickerDialogFragment extends DialogFragment {

    private final String TAG = ImageFilterFactorPickerDialogFragment.class.getCanonicalName();
    private ImageFilterFactorPickerDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (ImageFilterFactorPickerDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement interface ImageFilterFactorPickerDialogListener!");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setView(getActivity().getLayoutInflater().inflate(R.layout.dialog_image_filter_factor_picker, null))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text = ((EditText) getDialog().findViewById(R.id.image_filter_factor)).getText().toString();
                        try {
                            float factor = Float.parseFloat(text);
                            mListener.OnFactorSelected(factor);
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "The number format is wrong!");
                        }
                    }
                })
                .create();
    }

    public interface ImageFilterFactorPickerDialogListener {
        void OnFactorSelected(float factor);
    }
}
