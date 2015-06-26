package com.maws.loonandroid.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.maws.loonandroid.LoonAndroid;
import com.maws.loonandroid.R;
import com.maws.loonandroid.dao.CustomerDao;
import com.maws.loonandroid.dao.DevicePropertyDao;
import com.maws.loonandroid.dao.LoonMedicalDao;
import com.maws.loonandroid.dao.PropertyDao;
import com.maws.loonandroid.dao.SiteDao;
import com.maws.loonandroid.dao.UserDao;
import com.maws.loonandroid.listener.StandardRequestListener;
import com.maws.loonandroid.models.Customer;
import com.maws.loonandroid.models.DeviceProperty;
import com.maws.loonandroid.models.Property;
import com.maws.loonandroid.models.Site;
import com.maws.loonandroid.models.User;
import com.maws.loonandroid.requests.UserRequestHandler;
import com.maws.loonandroid.services.BLEService;
import com.maws.loonandroid.util.Util;
import com.maws.loonandroid.views.CustomToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements OnClickListener {

    // UI references.
    private static final String TAG = "LOGIN";
    private EditText emailET, passwordET, siteIdET, customerIdET;
    private TextView forgotPasswordTV, newUserTV;
    private Button loginBtn, loginNoCloudBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        emailET = (EditText) findViewById(R.id.emailET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        siteIdET = (EditText) findViewById(R.id.siteIdET);
        customerIdET = (EditText) findViewById(R.id.customerIdET);
        forgotPasswordTV = (TextView) findViewById(R.id.forgotPasswordTV);
        newUserTV = (TextView) findViewById(R.id.newUserTV);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginNoCloudBtn = (Button) findViewById(R.id.loginNoCloudBtn);
        forgotPasswordTV.setOnClickListener(this);
        newUserTV.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        loginNoCloudBtn.setOnClickListener(this);
        createDefaultProperties();
    }

    private StringBuilder validateFields(){
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String siteId = siteIdET.getText().toString();
        String customerId = customerIdET.getText().toString();
        StringBuilder errors = new StringBuilder();
        if(TextUtils.isEmpty(email)){
            errors.append( getString(R.string.validation_email_required) );
            errors.append( " " );
        }
        if(TextUtils.isEmpty(password)){
            errors.append( getString(R.string.validation_password_required) );
            errors.append( " " );
        }
        if(TextUtils.isEmpty(siteId)){
            errors.append( getString(R.string.validation_site_required) );
            errors.append( " " );
        }
        if(TextUtils.isEmpty(customerId)){
            errors.append( getString(R.string.validation_customer_required) );
            errors.append( " " );
        }
        return errors;
    }

    private void createDefaultProperties(){
        PropertyDao pDao = new PropertyDao(this);
        if(pDao.get(0) == null) {
            //after creating the table, add the default properties to the table
            for (int i = 0; i < Property.defaultProperties.length; i++) {
                pDao.create(Property.defaultProperties[i]);
            }
        }
    }

    private void attemptLogin(){

        StringBuilder errors = validateFields();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String siteId = siteIdET.getText().toString();
        String customerId = customerIdET.getText().toString();

        final User user = new User();
        user.setName("");
        user.setEmail(email);
        user.setPassword(password);

        if(errors.length() > 0){
            CustomToast.showAlert(this, errors.toString(), CustomToast._TYPE_ERROR);
        }else{
            UserRequestHandler.login(this, new UserRequestHandler.LoginListener() {
                @Override
                public void onSuccess(JSONObject jsonObject, User user, String siteId, String customerId,Context context) {
                    try {
                        JSONObject response = jsonObject.getJSONObject("response");
                        if (jsonObject.getString("response").equalsIgnoreCase("done")) {
                            finish();
                        }
                        if (response.getString("token") != null && response.getString("role") != null ) {
                            user.setToken(response.getString("token"));
                            user.setRole(response.getString("role"));
                            User.setCurrent(user, context);
                            goTonextPage();
                            finish();
                        }
                    } catch (Exception ex) {
                        onFailure(ex.getMessage());
                    }
                }

                @Override
                public void onFailure(String error) {
                    try {
                        JSONObject object = new JSONObject(error);
                        CustomToast.showAlert(LoginActivity.this, object.getString("message"), CustomToast._TYPE_ERROR);
                    } catch (Exception ex) {
                        CustomToast.showAlert(LoginActivity.this, LoginActivity.this.getString(R.string.default_request_error_message), CustomToast._TYPE_ERROR);
                    }
                }
            }, user, siteId, customerId);
        }
    };

    private void attemptOfflineLogin(){

        StringBuilder errors = validateFields();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String siteId = siteIdET.getText().toString();
        String customerId = customerIdET.getText().toString();

        //for offline, we also need to validate if the user exists in our local database
        LoonMedicalDao lDao = new LoonMedicalDao(this);
        UserDao uDao = new UserDao(this);

        User user = uDao.get(email, lDao.getReadableDatabase());
        if (user == null || !Util.MD5(password).equals(user.getPassword())) {
            errors.append( getString(R.string.validation_incorrect_user_password) );
            errors.append( " " );
        }

        if(errors.length() > 0){
            CustomToast.showAlert(this, errors.toString(), CustomToast._TYPE_ERROR);
        }else{

            //if everything is good, i set the current user, site and customerId for the app
            User.setCurrent(user, this);

            goTonextPage();
            this.finish();
        }
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
            attemptLogin();

        }else if(v == loginNoCloudBtn){
            attemptOfflineLogin();
        }
    }

    private  void goTonextPage(){
        String siteId = siteIdET.getText().toString();
        String customerId = customerIdET.getText().toString();
        CustomerDao cDao = new CustomerDao(this);
        Customer customer = cDao.get(customerId);
        if(customer == null){
            customer = new Customer();
            customer.setCode(customerId);
            cDao.create(customer);
        }
        Customer.setCurrent(customer, this);

        SiteDao sDao = new SiteDao(this);
        Site site = sDao.get(siteId);
        if(site == null){
            site = new Site();
            site.setCode(siteId);
            sDao.create(site);
        }
        Site.setCurrent(site, this);

        Intent newUserIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(newUserIntent);
    }

}



