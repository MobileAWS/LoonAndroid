package com.maws.loonandroid.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import com.maws.loonandroid.R;

/**
 * Created by Andrexxjc on 10/04/2015.
 */
public class ForgotPasswordActivity extends Activity {

    // UI references.
    private EditText emailET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Set up the login form.
        emailET = (EditText) findViewById(R.id.emailET);
    }

    private void attemptLogin(){

    };

}
