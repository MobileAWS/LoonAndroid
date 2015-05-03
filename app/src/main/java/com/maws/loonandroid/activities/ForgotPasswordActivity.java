package com.maws.loonandroid.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.maws.loonandroid.R;
import com.maws.loonandroid.listener.StandardRequestListener;
import com.maws.loonandroid.requests.UserRequestHandler;
import com.maws.loonandroid.views.CustomToast;

import org.json.JSONObject;

/**
 * Created by Andrexxjc on 10/04/2015.
 */
public class ForgotPasswordActivity extends Activity implements View.OnClickListener {

    // UI references.
    private EditText emailET;
    private Button forgotPasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Set up the login form.
        emailET = (EditText) findViewById(R.id.emailET);
        forgotPasswordBtn = (Button) findViewById(R.id.forgotPasswordBtn);
        forgotPasswordBtn.setOnClickListener(this);
    }

    private void attemptPasswordReset(){
        String email = emailET.getText().toString();
        if(TextUtils.isEmpty(email)){
            CustomToast.showAlert( this, getString(R.string.validation_email_required), CustomToast._TYPE_ERROR );
            return;
        }

        UserRequestHandler.resetPassword(this, new StandardRequestListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CustomToast.showAlert(ForgotPasswordActivity.this, getString(R.string.success_password_reset), CustomToast._TYPE_SUCCESS);
                ForgotPasswordActivity.this.finish();
            }

            @Override
            public void onFailure(String error) {
                CustomToast.showAlert(ForgotPasswordActivity.this, getString(R.string.error_password_reset), CustomToast._TYPE_ERROR);
            }
        }, email);
    };

    @Override
    public void onClick(View v) {
        if(v == forgotPasswordBtn){
            attemptPasswordReset();
        }
    }

}
