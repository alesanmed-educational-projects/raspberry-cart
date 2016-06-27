package com.acmezon.acmezon_dash.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.acmezon.acmezon_dash.R;

/*
 * Created by alesanmed on 6/06/16.
 */
public class ConnectDialog extends DialogFragment {
    String dName;
    String dMac;

    public static ConnectDialog newInstance(DeviceItem device) {
        ConnectDialog d = new ConnectDialog();

        Bundle args = new Bundle();
        args.putString("name", device.getDeviceName());
        args.putString("mac", device.getAddress());

        d.setArguments(args);

        return d;
    }

    public interface ConnectDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    ConnectDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ConnectDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.connect_dialog, null);
        dName = getArguments().getString("name");
        dMac = getArguments().getString("mac");

        // Get field from view
        TextView nameView = (TextView) view.findViewById(R.id.bluetoothName);
        nameView.setText(dName);

        TextView macView = (TextView) view.findViewById(R.id.bluetoothMac);
        macView.setText(dMac);

        // Use the Builder class for convenient dialog construction
        builder.setView(view)
                .setPositiveButton(R.string.bluetooth_connect, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                        mListener.onDialogPositiveClick(ConnectDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(ConnectDialog.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public String getdName() {
        return dName;
    }

    public String getdMac() {
        return dMac;
    }
}
