package com.maws.loonandroid.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maws.loonandroid.LoonAndroid;
import com.maws.loonandroid.R;
import com.maws.loonandroid.adapters.ViewPagerAdapter;
import com.maws.loonandroid.dao.ContactDao;
import com.maws.loonandroid.fragments.DeviceFragment;
import com.maws.loonandroid.fragments.SensorsFragment;
import com.maws.loonandroid.fragments.SmsFragment;
import com.maws.loonandroid.fragments.SupportFragment;
import com.maws.loonandroid.fragments.UploadToCloudFragment;
import com.maws.loonandroid.models.Contact;
import com.maws.loonandroid.models.User;
import com.maws.loonandroid.services.BLEService;
import com.maws.loonandroid.util.Util;
import com.maws.loonandroid.views.CustomProgressSpinner;

import org.droidparts.bus.EventBus;
import org.droidparts.bus.EventReceiver;

public class MainActivity extends AppCompatActivity implements EventReceiver
        {

    public static final int REQUEST_ENABLE_BT = 30921;
    public final static int REQUEST_MONITOR_ACTIVITY = 1034;
    public final static int REQUEST_SCAN_ACTIVITY = 132074;
    public final static int RESQUET_LOGIN_ACTIVITY=2040;
    public final static int REQUEST_CONTACT_ACTIVITY=197611;
    public final static int REQUEST_SCAN = 1002;
    public final static int PICK_CONTACT_REQUEST = 013;
    public static final String TAG_MONITOR ="ss";
    public static final String TAG_SENSOR = "ss1";
    public static final String TAG_PUSH_NOTIFICATION = "pn";
    public static final String TAG_SUPPORT = "sp";
    public static final String TAG_UPLOAD = "up";
    public static final String TAG_CONTACT = "contact";
    private boolean initMonitors = false;
    private ViewPager viewPager;
    private ViewPagerAdapter adapterViewPager;
    private Menu mOptionsMenu;
    private Toolbar toolbar;
    private  TabLayout tabLayout;
    private DeviceFragment deviceFragment;
    private SensorsFragment sensorsFragment;
    private UploadToCloudFragment uploadToCloudFragment;
    private SmsFragment smsFragment;


    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User.instance =null;
        initMonitors = getIntent().getBooleanExtra("initMonitors", false);

        if(!Util.isMyServiceRunning(this, BLEService.class) && !LoonAndroid.demoMode){
            Intent intent = new Intent(this,BLEService.class);
            this.startService(intent);
        }else{
            BLEService service = BLEService.getInstance();
            if(service != null && initMonitors){
                //let's only initialize monitors if we come from the login
                service.initializeMonitors();
                initMonitors = false;
            }
        }
        setUpToolBar();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        validateLogout(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        validateLogout(menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_Log_out:
                Util.logout(this);
                setUpToolBar();
                item.setVisible(false);

                return true;
            case R.id.action_Login:
                item.setVisible(false);
                Intent LoginIntent= null;
                LoginIntent = new Intent(this.getApplicationContext(),LoginActivity.class);
                startActivityForResult(LoginIntent, MainActivity.RESQUET_LOGIN_ACTIVITY);

                return true;
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_generate_random_alert:
                deviceFragment.generateRandomAlert();
                return true;
            case R.id.action_remove_sensors:
                deviceFragment.removeSensors();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        //for now, let's just refresh the monitors fragment if it's visible
        Log.e("request code: ", String.valueOf(requestCode));
        if(requestCode == this.REQUEST_SCAN_ACTIVITY){
            Fragment f = getSupportFragmentManager().findFragmentByTag(TAG_SENSOR);
            if(f != null && f instanceof DeviceFragment && f.isVisible()){
                ((DeviceFragment)f).loadSensors();
            }
        }
        if(requestCode == this.RESQUET_LOGIN_ACTIVITY) {
            Fragment f = getSupportFragmentManager().findFragmentByTag(TAG_UPLOAD);
            if(f != null && f instanceof UploadToCloudFragment && f.isVisible()){
                ((UploadToCloudFragment)f).uploadInfoToServer(((UploadToCloudFragment) f).getAdapter());
            }
            if(mOptionsMenu != null)
            validateLogout(mOptionsMenu);
            setUpToolBar();
        }
        if(requestCode == PICK_CONTACT_REQUEST ) {

            if(data != null) {
                Uri contactUri = data.getData();
                String[] projection = new String[]{ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                };

                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int column2 = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                String number = cursor.getString(column);
                String name = cursor.getString(column2);
                Contact contactTest = new Contact(name,number);
                ContactDao contactDao = new ContactDao(this);
                contactDao.create(contactTest);
                EventBus.postEvent(Util.EVENT_CONTACT_CREATED);
            }
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
    private void setupTabLayout(TabLayout tabLayout) {
        if(Util.isLoginOnline(this)) {
            LinearLayout tab0 = getTab(R.drawable.ic_action_heart_monitor_white, R.string.navigation_sensors);
            tabLayout.getTabAt(0).setCustomView(tab0);
            LinearLayout tab1 = getTab(R.drawable.ic_action_monitors_white, R.string.navigation_status);
            tabLayout.getTabAt(1).setCustomView(tab1);
            LinearLayout tab2 = getTab(R.drawable.ic_action_upload_to_cloud_white, R.string.navigation_upload_to_cloud);
            tabLayout.getTabAt(2).setCustomView(tab2);
            LinearLayout tab3 = getTab(R.drawable.ic_support, R.string.support_option);
            tabLayout.getTabAt(3).setCustomView(tab3);
            LinearLayout tab4 = getTab(R.drawable.ic_action_contact, R.string.sms_option);
            tabLayout.getTabAt(4).setCustomView(tab4);
        }
        else {
            LinearLayout tab0 = getTab(R.drawable.ic_action_heart_monitor_white, R.string.navigation_sensors);
            tabLayout.getTabAt(0).setCustomView(tab0);
            LinearLayout tab1 = getTab(R.drawable.ic_action_monitors_white, R.string.navigation_status);
            tabLayout.getTabAt(1).setCustomView(tab1);
            LinearLayout tab2 = getTab(R.drawable.ic_support, R.string.support_option);
            tabLayout.getTabAt(2).setCustomView(tab2);

        }
    }
    private void setupViewPager(ViewPager viewPager) {
        adapterViewPager = new ViewPagerAdapter(getSupportFragmentManager());
        if(Util.isLoginOnline(this)) {
            adapterViewPager.addFrag(DeviceFragment.newInstance(), getString(R.string.navigation_sensors), R.drawable.ic_action_heart_monitor);
            adapterViewPager.addFrag(SensorsFragment.newInstance(), "Status", R.drawable.ic_action_status);
            adapterViewPager.addFrag(UploadToCloudFragment.newInstance(), getString(R.string.navigation_upload_to_cloud), R.drawable.ic_action_upload_to_cloud);
            adapterViewPager.addFrag(SupportFragment.newInstance(), getString(R.string.support_option), R.drawable.ic_support);
            adapterViewPager.addFrag(SmsFragment.newInstance(), getString(R.string.support_option), R.drawable.ic_action_contact);

        }else {
            adapterViewPager.addFrag(DeviceFragment.newInstance(), getString(R.string.navigation_sensors), R.drawable.ic_action_heart_monitor);
            adapterViewPager.addFrag(SensorsFragment.newInstance(), "Status", R.drawable.ic_action_status);
            adapterViewPager.addFrag(SupportFragment.newInstance(), getString(R.string.support_option), R.drawable.ic_support);
        }
            viewPager.setAdapter(adapterViewPager);
            viewPager.setCurrentItem(0);
    }
    private LinearLayout  getNewlayout(Context context) {
        LayoutInflater inflater;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return (LinearLayout) inflater.from(this).inflate(R.layout.custom_tab_layout, null);
    }

    private LinearLayout getTab(int idIcon, int idText){
        LinearLayout tab0 = getNewlayout(this);
        ImageView icon0 = (ImageView)tab0.findViewById(R.id.icon);
        icon0.setImageDrawable(ContextCompat.getDrawable(this, idIcon));
        TextView titlel0 = (TextView) tab0.findViewById(R.id.text1);
        titlel0.setText(this.getString(idText));
        return tab0;
    }
    private void validateLogout(Menu menu){
        if(Util.isLoginOnline(this)){
            menu.findItem(R.id.action_Log_out).setVisible(true);
            menu.findItem(R.id.action_Login).setVisible(false);
        }
        else {
            menu.findItem(R.id.action_Log_out).setVisible(false);
            menu.findItem(R.id.action_Login).setVisible(true);
        }
    }
    private void setUpToolBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabanim_tabs);
        viewPager = (ViewPager) findViewById(R.id.tabanim_viewpager);
        setupViewPager(viewPager);
        getAllFragments();
        tabLayout.setupWithViewPager(viewPager);
        setupTabLayout(tabLayout);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


    }

    @Override
    public void onResume(){
        super.onResume();
        EventBus.registerReceiver(this, Util.CONTACT_INTEND);
    }

    @Override
    public void onPause(){
        super.onPause();
        EventBus.unregisterReceiver(this);

    }
    private  void getAllFragments(){
        if(Util.isLoginOnline(this)) {
            deviceFragment = (DeviceFragment) adapterViewPager.getItem(0);
            sensorsFragment = (SensorsFragment) adapterViewPager.getItem(1);
            uploadToCloudFragment = (UploadToCloudFragment) adapterViewPager.getItem(2);
            smsFragment = (SmsFragment) adapterViewPager.getItem(4);
        }else {
            deviceFragment = (DeviceFragment) adapterViewPager.getItem(0);
            sensorsFragment = (SensorsFragment) adapterViewPager.getItem(1);
        }
    }


    @Override
    public void onEvent(String name, Object data) {
        switch (name){
            case Util.CONTACT_INTEND:
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                startActivityForResult(pickContactIntent, this.PICK_CONTACT_REQUEST);
                break;
            default:
                break;
        }
    }
}
