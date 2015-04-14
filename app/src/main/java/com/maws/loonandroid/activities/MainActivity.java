package com.maws.loonandroid.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import com.maws.loonandroid.R;
import com.maws.loonandroid.enums.FragmentType;
import com.maws.loonandroid.fragments.NavigationDrawerFragment;
import com.maws.loonandroid.fragments.PushNotificationsFragment;
import com.maws.loonandroid.fragments.SensorFragment;
import com.maws.loonandroid.fragments.UploadToCloudFragment;
import com.maws.loonandroid.models.Sensor;
import com.maws.loonandroid.models.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

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

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(FragmentType type) {

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment toReplace = null;
        switch (type){
            case SENSOR:
                toReplace = SensorFragment.newInstance();
                break;
            case PUSH_NOTIFICATION:
                toReplace = PushNotificationsFragment.newInstance();
                break;
            case UPLOAD:
                toReplace = UploadToCloudFragment.newInstance();
                break;
            case LOGOUT:
                logout();
                break;
        }

        if(toReplace != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, toReplace)
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

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_change_background) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        Intent logOutIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(logOutIntent);
        this.finish();
    }


    private static List<Sensor> sensors = null;
    public List<Sensor> getSensors(){

        if(sensors == null) {
            sensors = new ArrayList<Sensor>();
            Sensor bedSensor = new Sensor();
            bedSensor.setName("Bed Sensor");
            bedSensor.setCode("Sensor LM01");
            sensors.add(bedSensor);

            Sensor chairSensor = new Sensor();
            chairSensor.setName("Chair Sensor");
            chairSensor.setCode("Sensor LM02");
            sensors.add(chairSensor);

            Sensor toiletSensor = new Sensor();
            toiletSensor.setName("Toilet Sensor");
            toiletSensor.setCode("Sensor LM03");
            sensors.add(toiletSensor);

            Sensor callSensor = new Sensor();
            callSensor.setName("Call Button");
            callSensor.setCode("Sensor LM04");
            sensors.add(callSensor);

            Sensor incontinenceSensor = new Sensor();
            incontinenceSensor.setName("Incontinence Sensor");
            incontinenceSensor.setCode("Sensor LM05");
            sensors.add(incontinenceSensor);
        }
        return sensors;
    }

    private static List<User> users = null;
    public List<User> getUsers(){
        if(users == null) {
            users = new ArrayList<User>();
            users.add(new User("Andres J.", "andres@mobileaws.com"));
            users.add(new User("Joel G.", "joel@mobileaws.com"));
            users.add(new User("Mike A.", "mike@mobileaws.com"));
            users.add(new User("Edison G.", "edison@mobileaws.com"));
        }
        return users;
    }

}
