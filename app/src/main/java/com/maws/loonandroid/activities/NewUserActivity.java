package com.maws.loonandroid.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.maws.loonandroid.R;
import com.maws.loonandroid.listener.StandardRequestListener;
import com.maws.loonandroid.requests.UserRequestHandler;
import com.maws.loonandroid.views.CustomToast;

import org.json.JSONObject;

/**
 * Created by Andrexxjc on 10/04/2015.
 */
public class NewUserActivity extends Activity implements View.OnClickListener {

    // UI references.
    private static final String TAG = "NEW USER";
    private EditText emailET, confirmEmailET, passwordET, confirmPasswordET, siteET;
    private Button createUserBtn;
    private long customerId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        customerId = getIntent().getLongExtra("customerId", 0);
        if(customerId == 0){
            finish();
        }

        TextView tv = (TextView) findViewById(R.id.customerIdTV);
        tv.setText( String.format( getString(R.string.customer_id), customerId ) );
        emailET = (EditText) findViewById(R.id.emailET);
        confirmEmailET = (EditText) findViewById(R.id.confirmEmailET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        confirmPasswordET = (EditText) findViewById(R.id.confirmPasswordET);
        createUserBtn = (Button) findViewById(R.id.createUserBtn);
        siteET = (EditText) findViewById(R.id.siteET);
        createUserBtn.setOnClickListener(this);
    }

    private void attemptLogin(){

        //first, i need to check if the confirmation fields match.
        String email = emailET.getText().toString();
        String emailConfirmation = confirmEmailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordConfirmation = confirmPasswordET.getText().toString();
        String site = siteET.getText().toString();

        StringBuilder errors = new StringBuilder();
        if(TextUtils.isEmpty(email)){
           errors.append( getString(R.string.validation_email_required) );
            errors.append( " " );
        }
        if(TextUtils.isEmpty(password)){
            errors.append( getString(R.string.validation_password_required) );
            errors.append( " " );
        }
        if(TextUtils.isEmpty(site)){
            errors.append( getString(R.string.validation_site_required) );
            errors.append( " " );
        }

        if( !email.equals(emailConfirmation)) {
            errors.append( getString(R.string.email_confirmation_must_match) );
            errors.append( " " );
        }
        if( !password.equals(passwordConfirmation)) {
            errors.append( getString(R.string.password_confirmation_must_match) );
            errors.append( " " );
        }
        if( password.length() < 8) {
            errors.append( getString(R.string.validation_password_char_length) );
            errors.append( " " );
        }

        if(errors.length() > 0){
            CustomToast.showAlert(this, errors.toString(), CustomToast._TYPE_ERROR);
        }else{
            UserRequestHandler.signUp(this, new StandardRequestListener() {
                @Override
                public void onSuccess(JSONObject jsonObject) {

                    try {
                        if (jsonObject.getString("response").equalsIgnoreCase("done")) {
                            CustomToast.showAlert(NewUserActivity.this, NewUserActivity.this.getString(R.string.message_account_creation_success), CustomToast._TYPE_SUCCESS);
                            finish();
                        }
                    }catch(Exception ex){
                        onFailure(ex.getMessage());
                    }
                }

                @Override
                public void onFailure(String error) {
                    try{
                        JSONObject object = new JSONObject(error);
                        CustomToast.showAlert(NewUserActivity.this, object.getString("message"), CustomToast._TYPE_ERROR);
                    }catch(Exception ex) {
                        CustomToast.showAlert(NewUserActivity.this, NewUserActivity.this.getString(R.string.default_request_error_message), CustomToast._TYPE_ERROR);
                    }
                }
            }, email, password, customerId, site);
        }
    };

    @Override
    public void onClick(View v) {
        if(v == createUserBtn){
            attemptLogin();
        }
    }
}
