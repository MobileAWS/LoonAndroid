package com.maws.loonandroid.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.maws.loonandroid.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements OnClickListener {

    // UI references.
    private EditText emailET, passwordET, clientNumberET, siteIdET;
    private TextView forgotPasswordTV, newUserTV;
    private Button loginBtn, loginNoCloudBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        emailET = (EditText) findViewById(R.id.emailET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        clientNumberET = (EditText) findViewById(R.id.clientNumberET);
        siteIdET = (EditText) findViewById(R.id.siteIdET);
        forgotPasswordTV = (TextView) findViewById(R.id.forgotPasswordTV);
        newUserTV = (TextView) findViewById(R.id.newUserTV);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginNoCloudBtn = (Button) findViewById(R.id.loginNoCloudBtn);

        forgotPasswordTV.setOnClickListener(this);
        newUserTV.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        loginNoCloudBtn.setOnClickListener(this);
    }

    private void attemptLogin(){

    };

    @Override
    public void onClick(View v) {
        if(v == forgotPasswordTV){
            Intent forgotPwdIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(forgotPwdIntent);
        }else if(v == newUserTV){
            Intent newUserIntent = new Intent(LoginActivity.this, NewUserActivity.class);
            startActivity(newUserIntent);
        }else if(v == loginBtn){
            Intent newUserIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(newUserIntent);
            this.finish();
        }else if(v == loginNoCloudBtn){
            Intent newUserIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(newUserIntent);
            this.finish();
        }
    }

}



