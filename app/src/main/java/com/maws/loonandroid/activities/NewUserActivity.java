package com.maws.loonandroid.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.maws.loonandroid.R;
import com.maws.loonandroid.dao.LoonMedicalDao;
import com.maws.loonandroid.dao.UserDao;
import com.maws.loonandroid.listener.StandardRequestListener;
import com.maws.loonandroid.models.User;
import com.maws.loonandroid.requests.UserRequestHandler;
import com.maws.loonandroid.util.Util;
import com.maws.loonandroid.views.CustomToast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andrexxjc on 10/04/2015.
 */
public class NewUserActivity extends Activity implements View.OnClickListener {

    // UI references.
    private static final String TAG = "NEW USER";
    private EditText emailET, confirmEmailET, passwordET, confirmPasswordET;
    private Button createUserBtn;
    private User userToSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        emailET = (EditText) findViewById(R.id.emailET);
        confirmEmailET = (EditText) findViewById(R.id.confirmEmailET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        confirmPasswordET = (EditText) findViewById(R.id.confirmPasswordET);
        createUserBtn = (Button) findViewById(R.id.createUserBtn);
        createUserBtn.setOnClickListener(this);
    }

    private void attemptUserCreation(){

        //first, i need to check if the confirmation fields match.
        String email = emailET.getText().toString();
        String emailConfirmation = confirmEmailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordConfirmation = confirmPasswordET.getText().toString();

        StringBuilder errors = new StringBuilder();
        if(TextUtils.isEmpty(email)){
           errors.append( getString(R.string.validation_email_required) );
            errors.append( " " );
        }
        if(TextUtils.isEmpty(password)){
            errors.append( getString(R.string.validation_password_required) );
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

        //let's see if this user already exists in the local database
        if(errors.length() > 0){
            CustomToast.showAlert(this, errors.toString(), CustomToast._TYPE_ERROR);
        }else{

            //TODO: put code here to try to detect network and save the user online first.
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            userToSave = user;
            UserRequestHandler.signUp(this, user, new StandardRequestListener() {
                @Override
                public void onSuccess(JSONObject jsonObject) {

                    try {
                        if (jsonObject.getString("response").equalsIgnoreCase("done")) {
                            saveUserLocalDb();

                        }
                    }catch(Exception ex){
                        onFailure(ex.getMessage());
                    }
                }
                @Override
                public void onFailure(String error) {
                    try {
                        JSONObject errorJsonObject = new JSONObject(error);
                        CustomToast.showAlert(NewUserActivity.this, errorJsonObject.getString("message"), CustomToast._TYPE_ERROR);
                    } catch (JSONException e) {
                        CustomToast.showAlert(NewUserActivity.this, NewUserActivity.this.getString(R.string.default_request_error_message), CustomToast._TYPE_ERROR);
                        e.printStackTrace();
                    }
                }
            });
        }
    };

     private void onBackView(String emailBack) {
        Intent returnIntent = new Intent();
        if(!emailBack.isEmpty()) {
            Bundle extras = new Bundle();
            extras.putString("email", emailBack);
            returnIntent.putExtras(extras);
        }
        this.setResult(RESULT_OK, returnIntent);
        return;
    }
    @Override
    public void onClick(View v) {
        if(v == createUserBtn){
            attemptUserCreation();
        }
    }
    private void saveUserLocalDb(){

        LoonMedicalDao lDao = new LoonMedicalDao(this);
        UserDao uDao = new UserDao(this);
        User inDbUser = uDao.get(userToSave.getEmail(), lDao.getReadableDatabase());
        if(inDbUser == null) {
            uDao.create(userToSave, lDao.getWritableDatabase());
            inDbUser = uDao.get(userToSave.getEmail(), lDao.getReadableDatabase());
            CustomToast.showAlert(this, getString(R.string.user_created_successfully), CustomToast._TYPE_SUCCESS);
            onBackView(inDbUser.getEmail());
            this.finish();
        } else {
            uDao.update(userToSave,lDao.getReadableDatabase());
        }

    }

}
