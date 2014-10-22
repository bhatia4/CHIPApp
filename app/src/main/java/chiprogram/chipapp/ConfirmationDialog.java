package chiprogram.chipapp;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;

public class ConfirmationDialog extends DialogFragment {

    public interface ConfirmationDialogListener {
        void onFinishConfirmationDialog(String tag);
    }

    public static final String ARG_MESSAGE_TEXT = "chiprogram.chipapp.ARG_MESSAGE_TEXT";
    public static final String ARG_TAG = "chiprogram.chipapp.ARG_TAG";

    public ConfirmationDialog() {
    }

    private String m_messageText;
    private String m_tag;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        m_messageText = getArguments().getString(ARG_MESSAGE_TEXT);
        m_tag = getArguments().getString(ARG_TAG);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(m_messageText)
                .setPositiveButton(R.string.common_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ConfirmationDialogListener activity =
                                (ConfirmationDialogListener) getActivity();

                        activity.onFinishConfirmationDialog(m_tag);
                    }
                })
                .setNegativeButton(R.string.common_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        return builder.create();
    }
}
