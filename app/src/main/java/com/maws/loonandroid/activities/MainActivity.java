package com.maws.loonandroid.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.TextView;

import com.maws.loonandroid.BuildConfig;
import com.maws.loonandroid.LoonAndroid;
import com.maws.loonandroid.R;
import com.maws.loonandroid.enums.FragmentType;
import com.maws.loonandroid.fragments.NavigationDrawerFragment;
import com.maws.loonandroid.fragments.PushNotificationsFragment;
import com.maws.loonandroid.fragments.DeviceFragment;
import com.maws.loonandroid.fragments.SensorsFragment;
import com.maws.loonandroid.fragments.SupportFragment;
import com.maws.loonandroid.fragments.UploadToCloudFragment;
import com.maws.loonandroid.models.User;
import com.maws.loonandroid.services.BLEService;
import com.maws.loonandroid.util.Util;
import com.maws.loonandroid.views.CustomProgressSpinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public final static int REQUEST_SCAN = 1002;
    public static final String TAG_MONITOR ="ss";
    public static final String TAG_SENSOR = "ss1";
    public static final String TAG_PUSH_NOTIFICATION = "pn";
    public static final String TAG_SUPPORT = "sp";
    public static final String TAG_UPLOAD = "up";


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!Util.isMyServiceRunning(this, BLEService.class) && !LoonAndroid.demoMode){
            Intent intent = new Intent(this,BLEService.class);
            this.startService(intent);
        }else{
            BLEService service = BLEService.getInstance();
            if(service != null){
                service.initializeMonitors();
            }
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        TextView versionTV = (TextView) findViewById(R.id.versionTV);
        versionTV.setText("Version "+versionName+" "+ String.valueOf(versionCode));
    }

    @Override
    public void onNavigationDrawerItemSelected(FragmentType type) {

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment toReplace = null;
        String tag = "";
        switch (type){
            case MONITOR:
                toReplace = DeviceFragment.newInstance();
                tag = TAG_SENSOR;
                break;
            case SENSOR:
                toReplace= SensorsFragment.newInstance();
                tag = TAG_SENSOR;
                break;
            case PUSH_NOTIFICATION:
                toReplace = PushNotificationsFragment.newInstance();
                tag = TAG_PUSH_NOTIFICATION;
                break;
            case UPLOAD:
                toReplace = UploadToCloudFragment.newInstance();
                tag = TAG_UPLOAD;
                break;
            case SUPPORT:
                toReplace = SupportFragment.newInstance();
                tag = TAG_SUPPORT;
                break;
            case LOGOUT:
                logout();
                break;
        }

        if(toReplace != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, toReplace, tag)
                    .commit();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        /*switch (item.getItemId()) {
            case R.id.action_start_scan:
                scanForMonitors();
                return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        Intent logOutIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(logOutIntent);
        this.finish();
    }

    private static List<User> users = null;
    public List<User> getUsers(){
        if(users == null) {
            users = new ArrayList<User>();
            users.add(new User("Andres J.", "andres@mobileaws.com", "password"));
            users.add(new User("Joel G.", "joel@mobileaws.com", "password"));
            users.add(new User("Mike A.", "mike@mobileaws.com", "password"));
            users.add(new User("Edison G.", "edison@mobileaws.com", "password"));
        }
        return users;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        //for now, let's just refresh the monitors fragment if it's visible
        Fragment f = getSupportFragmentManager().findFragmentByTag(TAG_SENSOR);
        if(f != null && f instanceof DeviceFragment && f.isVisible()){
            ((DeviceFragment)f).loadSensors();
        }

    }
    public void onResourcesContact(View v){
        final CustomProgressSpinner spinner = new CustomProgressSpinner(this, this.getString(R.string.support_spinner));
        spinner.show();
        if(v.getId() == R.id.resourceSupportTV ) {
            String url = "http://www.shopcaresentinel.com/product-category/build-your-own-sensor-system";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            spinner.dismiss();
        }
        if(v.getId() == R.id.urlHomeSupportTV){
            String url = "http://www.shopcaresentinel.com/resources/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            spinner.dismiss();
        }
        if(v.getId() == R.id.emailSupportTV){
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","Info@shopcaresentinel.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
            spinner.dismiss();
        }
        if(v.getId() == R.id.phoneSupportTv){
            String posted_by = "1-855-282-0004";
            String uri = "tel:" + posted_by.trim() ;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
            spinner.dismiss();
        }
    }
}
