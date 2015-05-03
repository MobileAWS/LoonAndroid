package com.maws.loonandroid.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.maws.loonandroid.R;

public class CustomProgressSpinner extends Dialog{

    String spinnerMessage;

    public CustomProgressSpinner(Context context, String message) {
        super(context);
        spinnerMessage = message;
    }

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.setContentView(R.layout.custom_progress_spinner_layout);
        this.setCanceledOnTouchOutside(false);
        //this.setCancelable(false);
        ImageView rotatorImage = (ImageView) findViewById(R.id.rotatorImage);
        TextView loadingTextView = (TextView) findViewById(R.id.loadingTextView);

        loadingTextView.setText(spinnerMessage);

        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.setFillAfter(true);
        animSet.setFillEnabled(true);

        final RotateAnimation animRotate = new RotateAnimation(0.0f, 360.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotate.setDuration(1500);
        animRotate.setRepeatMode(Animation.RESTART);
        animRotate.setRepeatCount(Animation.INFINITE);
        animRotate.setFillAfter(true);
        animSet.addAnimation(animRotate);
        rotatorImage.startAnimation(animSet);
    }

}
