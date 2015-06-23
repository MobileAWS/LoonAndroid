package com.maws.loonandroid.adapters;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.maws.loonandroid.LoonAndroid;
import com.maws.loonandroid.R;
import com.maws.loonandroid.dao.DevicePropertyDao;
import com.maws.loonandroid.gatt.GattManager;
import com.maws.loonandroid.gatt.operations.GattConnectOperation;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.models.DeviceProperty;
import com.maws.loonandroid.models.Property;
import com.maws.loonandroid.util.Util;
import java.util.List;

/**
 * Created by Andrexxjc on 12/04/2015.
 */
public class DeviceListAdapter extends BaseAdapter {

    private static final String TAG = "DeviceListAdapter";
    private final Context context;
    private final List<Device> items;

    static class ViewHolder {
        Button connectBtn;
        TextView nameTV, addressTV;
        ImageView checkIV, signalIV, batteryIV;
        LinearLayout alarmsLL;
    }

    public DeviceListAdapter(Context context, List<Device> values) {
        this.context = context;
        this.items = values;
    }

    @Override
    public int getCount() {
        return items.size();
    }
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }
    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView==null){
            // inflate the layout
            convertView = LinearLayout.inflate(context, R.layout.sensor_item, null);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.nameTV = (TextView) convertView.findViewById(R.id.nameTV);
            viewHolder.addressTV = (TextView) convertView.findViewById(R.id.addressTV);
            viewHolder.checkIV = (ImageView) convertView.findViewById(R.id.checkIV);
            viewHolder.signalIV = (ImageView) convertView.findViewById(R.id.signalIV);
            viewHolder.batteryIV = (ImageView) convertView.findViewById(R.id.batteryIV);
            viewHolder.alarmsLL = (LinearLayout) convertView.findViewById(R.id.alarmsLL);
            viewHolder.connectBtn = (Button) convertView.findViewById(R.id.connectBtn);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        }else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Device thisDevice = items.get(position);

        if(!thisDevice.isConnected() && !LoonAndroid.demoMode){
            viewHolder.nameTV.setText( TextUtils.isEmpty(thisDevice.getDescription())? thisDevice.getName(): thisDevice.getDescription() );
            viewHolder.addressTV.setText(thisDevice.getName());
            viewHolder.connectBtn.setVisibility(View.VISIBLE);
            viewHolder.checkIV.setVisibility(View.GONE);
            viewHolder.signalIV.setVisibility(View.GONE);
            viewHolder.batteryIV.setVisibility(View.GONE);
            viewHolder.connectBtn.setTag(thisDevice.getMacAddress());
            viewHolder.connectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*BLEService instance = BLEService.getInstance();
                    if (instance != null) {
                        instance.connect(v.getTag().toString());
                    }*/
                    String address = v.getTag().toString();
                    BluetoothAdapter mBluetoothAdapter = null;
                    BluetoothManager mBluetoothManager = null;
                    //let's first try to initialize the adapter
                    // For API level 18 and above, get a reference to BluetoothAdapter through
                    // BluetoothManager.
                    if (mBluetoothManager == null) {
                        mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
                        if (mBluetoothManager == null) {
                            Log.e(TAG, "Unable to initialize BluetoothManager.");
                            return;
                        }
                    }

                    mBluetoothAdapter = mBluetoothManager.getAdapter();
                    if (mBluetoothAdapter == null) {
                        Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
                        return;
                    }

                    if (mBluetoothAdapter == null || address == null) {
                        Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
                        return;
                    }

                    BluetoothDevice device = null;
                    try {
                        device = mBluetoothAdapter.getRemoteDevice(address);
                    } catch (Exception ex) {
                        //just don't connect to the device if the mac address is weird
                    }

                    if (device == null) {
                        Log.w(TAG, "Device not found.  Unable to connect.");
                        return;
                    }

                    GattManager manager = new GattManager();
                    GattConnectOperation operation = new GattConnectOperation(device);
                    manager.queue(operation);
                }
            });

        }else if( thisDevice.isActive() && thisDevice.isConnected() ){
            viewHolder.nameTV.setText( TextUtils.isEmpty(thisDevice.getDescription())? thisDevice.getName(): thisDevice.getDescription() );
            viewHolder.addressTV.setText(thisDevice.getName());
            viewHolder.connectBtn.setVisibility(View.GONE);
            viewHolder.checkIV.setVisibility(View.VISIBLE);
            viewHolder.signalIV.setVisibility(View.VISIBLE);
            viewHolder.batteryIV.setVisibility(View.VISIBLE);
        }else{
            viewHolder.nameTV.setText( thisDevice.getName() );
            viewHolder.addressTV.setText(thisDevice.getMacAddress());
            viewHolder.connectBtn.setVisibility(View.GONE);
            viewHolder.checkIV.setVisibility(View.GONE);
            viewHolder.signalIV.setVisibility(View.GONE);
            viewHolder.batteryIV.setVisibility(View.GONE);
        }

        //i need to look for this item's active alarms and list them
        DevicePropertyDao aDao = new DevicePropertyDao(context);
        //viewHolder.alarmsLL.removeAllViews();
        DeviceProperty dProperty = aDao.getLastAlertForDevice(thisDevice.getId());
        viewHolder.alarmsLL.removeAllViews();
        if (dProperty != null && dProperty.getDismissedAt() == null) {

            Property alertProperty = Property.getDefaultProperty(dProperty.getPropertyId());

            View alertView = LinearLayout.inflate(context, R.layout.alert_item, null);
            ((TextView) alertView.findViewById(R.id.alertDateTV)).setText(Util.sdf.format(dProperty.getCreatedAt()));
            ((TextView) alertView.findViewById(R.id.serviceTV)).setText(context.getString(alertProperty.getDisplayId()));
            alertView.setTag(dProperty.getId());

            alertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long id = Long.valueOf(v.getTag().toString());

                    DevicePropertyDao aDao = new DevicePropertyDao(context);
                    DeviceProperty deviceProperty = aDao.get(id);

                    if (deviceProperty != null && deviceProperty.getCreatedAt() != null) {
                        aDao.dismiss(deviceProperty);
                    }
                    v.setVisibility(View.GONE);
                }
            });
            viewHolder.alarmsLL.addView(alertView);
        }
        return convertView;

    }

}
