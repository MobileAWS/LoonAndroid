package com.maws.loonandroid.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.maws.loonandroid.R;
import com.maws.loonandroid.models.Sensor;

/**
 * Created by Andrexxjc on 10/05/2015.
 */
public class AddSensorDialogFragment extends DialogFragment {

    private TextView serialTV, nameTV;
    private EditText descriptionET;
    private Sensor sensor;
    private AddSensorDialogListener listener;

    public interface AddSensorDialogListener{
        public void onSensorAdded(Sensor sensor);
        public void onSensorIgnored(Sensor sensor);
    }

    public static AddSensorDialogFragment newInstance(Sensor sensor, AddSensorDialogListener listener) {
        AddSensorDialogFragment fm = new AddSensorDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("sensor", sensor);
        fm.setArguments(args);
        fm.setListener(listener);
        fm.setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Dialog_NoActionBar);
        return  fm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(this.getArguments() == null){
            this.dismiss();
        }
        sensor = (Sensor) this.getArguments().getParcelable("sensor");

        View v = inflater.inflate(R.layout.fragment_add_sensor, container, false);
        ((TextView) v.findViewById(R.id.sensorNameTV)).setText(sensor.getName());
        ((TextView) v.findViewById(R.id.sensorNameTV)).setText(sensor.getName());
        descriptionET = (EditText) v.findViewById(R.id.sensorDescriptionET);

        Button okBtn = (Button)v.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensor.setDescription(descriptionET.getText().toString() );
                listener.onSensorAdded(sensor);
                dismiss();
            }
        });

        Button cancelBtn = (Button) v.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensor.setDescription(descriptionET.getText().toString());
                listener.onSensorIgnored(sensor);
                dismiss();
            }
        });

        return v;
    }

    public AddSensorDialogListener getListener() {
        return listener;
    }

    public void setListener(AddSensorDialogListener listener) {
        this.listener = listener;
    }
}
