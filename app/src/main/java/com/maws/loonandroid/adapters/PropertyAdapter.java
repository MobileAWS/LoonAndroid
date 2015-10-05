package com.maws.loonandroid.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.maws.loonandroid.R;
import com.maws.loonandroid.dao.DeviceEnabledPropertyDao;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.models.DeviceEnabledProperty;
import com.maws.loonandroid.models.Property;
import com.maws.loonandroid.models.User;
import com.maws.loonandroid.services.BLEService;

/**
 * Created by Andrexxjc on 24/06/2015.
 */
public class PropertyAdapter extends BaseAdapter {

    private static final String TAG = "PropertyAdapter";
    private final Context context;
    private final Property[] items;
    private final Device device;

    public PropertyAdapter(Context context, Property[] values, Device device) {
        this.context = context;
        this.items = values;
        this.device = device;
    }

    @Override
    public int getCount() {
        return items.length;
    }
    @Override
    public Object getItem(int position) {
        return items[position];
    }
    @Override
    public long getItemId(int position) {
        return items[position].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // inflate the layout
        convertView = LinearLayout.inflate(context, R.layout.property_item, null);
        final TextView nameTV = (TextView) convertView.findViewById(R.id.nameTV);
        final EditText delayET = (EditText) convertView.findViewById(R.id.delayET);
        final ToggleButton enabledTB = (ToggleButton) convertView.findViewById(R.id.enabledTB);

        final Property thisProperty = items[position];
        nameTV.setText( context.getString( thisProperty.getDisplayId() ) );

        //i need to get the status of each property
        final User user = User.getCurrent(context);
        final DeviceEnabledPropertyDao depDao = new DeviceEnabledPropertyDao(context);
        final DeviceEnabledProperty deviceEProp = depDao.findByDevicePropertyUser( device.getId(), thisProperty.getId(), user.getId() );

        //is this property ON?
        boolean isOn = false;
        if(BLEService.switchValues.containsKey(device.getMacAddress())){
            String currentValue = BLEService.switchValues.get(device.getMacAddress());
            if( currentValue.charAt( Integer.valueOf( String.valueOf(thisProperty.getId())) ) == '1'){
                isOn = true;

            }
        }
        if(thisProperty.getName().equalsIgnoreCase("Call")){
            isOn = !isOn;
        }
        if(device.isConnected() && isOn){
            nameTV.setTextColor( context.getResources().getColor( R.color.green ) );
        }else{
            nameTV.setTextColor( context.getResources().getColor( R.color.dark_orange ) );
        }

        if(deviceEProp == null){
            enabledTB.setChecked(true);
            delayET.setText("0");
        }else{
            enabledTB.setChecked(deviceEProp.isEnabled());
            delayET.setText( String.valueOf(deviceEProp.getDelay()) );
        }

        enabledTB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateDeviceEnabledProperty(device.getId() ,user.getId(), thisProperty.getId(), delayET,enabledTB );
            }
        });
        /*delayET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                updateDeviceEnabledProperty(deviceId,user.getId(),thisProperty.getId(), delayET,enabledTB );
                return true;
            }
        });*/
        return convertView;
    }

    private void updateDeviceEnabledProperty( long deviceId,long userId,long propertyId, EditText delayET, ToggleButton enabledTB ){
        int delay = TextUtils.isEmpty( delayET.getText().toString() )?0:Integer.valueOf(delayET.getText().toString());
        DeviceEnabledPropertyDao depDao = new DeviceEnabledPropertyDao(context);
        DeviceEnabledProperty thisDEP = depDao.findByDevicePropertyUser( deviceId, propertyId, userId );
        boolean shouldCreate = false;
        if(thisDEP == null){
            thisDEP = new DeviceEnabledProperty();
            thisDEP.setDeviceId(deviceId);
            thisDEP.setPropertyId(propertyId);
            thisDEP.setUserId(userId);
            shouldCreate = true;
        }
        thisDEP.setDelay(delay);
        thisDEP.setIsEnabled(enabledTB.isChecked());
        if(shouldCreate){
            depDao.create(thisDEP);
        }else{
            depDao.update(thisDEP);
        }
    }

}