package com.maws.loonandroid.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.maws.loonandroid.R;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.views.CustomToast;

/**
 * Created by Aprada on 12/14/15.
 */
public class ActivationDialogFragment extends DialogFragment {

    private TextView serialTV, nameTV ,titleTV;
    private EditText descriptionET;

    private Device device;
    private ActivationDialogFragmentListener listener;
    private Context context;

    public interface ActivationDialogFragmentListener{
        public void onSensorAdded(Device device);
    }

    public static ActivationDialogFragment newInstance(Device device,Context context, ActivationDialogFragmentListener  listener) {

        ActivationDialogFragment fm = new ActivationDialogFragment();
        fm.setContext(context);
        Bundle args = new Bundle();
        args.putParcelable("device", device);
        fm.setArguments(args);
        fm.setListener(listener);
        fm.setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Dialog_NoActionBar);
        return  fm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        if(this.getArguments() == null){
            this.dismiss();
        }
        device = (Device) this.getArguments().getParcelable("device");

        View v = inflater.inflate(R.layout.fragment_add_sensor, container, false);
        ((TextView) v.findViewById(R.id.sensorNameTV)).setText(device.getName());
        ((TextView) v.findViewById(R.id.sensorAddressTV)).setText(device.getMacAddress());
        titleTV = (TextView) v.findViewById(R.id.titleTV);
        descriptionET = (EditText) v.findViewById(R.id.sensorDescriptionET);
        titleTV.setText(context.getString(R.string.change_ignore_activate));
        Button okBtn = (Button)v.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceName = descriptionET.getText().toString();
                if(deviceName != null && !deviceName.isEmpty()) {
                    device.setDescription(descriptionET.getText().toString());
                    listener.onSensorAdded(device);
                    dismiss();
                }else {
                    CustomToast.showAlert(context, context.getString(R.string.message_device_name_obligatory), CustomToast._TYPE_NORMAL);
                }
            }
        });

        Button cancelBtn = (Button) v.findViewById(R.id.cancelBtn);
        cancelBtn.setText(context.getString(R.string.cancel));
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ActivationDialogFragmentListener getListener() {
        return listener;
    }

    public void setListener(ActivationDialogFragmentListener listener) {
        this.listener = listener;
    }
}
