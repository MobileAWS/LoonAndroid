package com.maws.loonandroid.views;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.maws.loonandroid.R;

/**
 * Created by Andrexxjc on 03/05/2015.
 */
public class CustomToast {

    public static final int _TYPE_NORMAL = 0;
    public static final int _TYPE_WARNING = 1;
    public static final int _TYPE_SUCCESS = 2;
    public static final int _TYPE_ERROR = 3;

    public static void showAlert(Context context,  String alertText) {
        showAlert(context, alertText, _TYPE_NORMAL);
    }

    public static void showAlert(Context context, String alertText, int type ) {

        if(type == _TYPE_NORMAL){
            Toast.makeText(context, alertText, Toast.LENGTH_SHORT).show();
            return;
        }

        int resourceId = R.layout.toast_error;
        switch (type){
            case _TYPE_WARNING:
                resourceId = R.layout.toast_warning;
                break;
            case _TYPE_SUCCESS:
                resourceId = R.layout.toast_success;
                break;
        }

        View layout =  LinearLayout.inflate(context, resourceId, null);
        ((TextView) layout.findViewById(R.id.text)).setText(alertText);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

}
