package com.maws.loonandroid.adapters;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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
public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder> {

    private static final String TAG = "DeviceListAdapter";
    private final Context context;
    private final List<Device> items;
    private DeviceViewHolderClickListener listener;

    public static interface DeviceViewHolderClickListener {
        void onClick(Device device);
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        Button connectBtn;
        TextView nameTV, addressTV, headerTV;
        ImageView signalIV, batteryIV;
        LinearLayout alarmsLL;
        View mainView;

        public DeviceViewHolder(View v) {
            super(v);
            this.mainView = v;
        }

    }

    public DeviceListAdapter(Context context, List<Device> values, DeviceViewHolderClickListener listener) {
        this.context = context;
        this.items = values;
        this.listener = listener;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sensor_item, viewGroup, false);
        DeviceViewHolder viewHolder = new DeviceViewHolder(convertView);
        viewHolder.headerTV = (TextView) convertView.findViewById(R.id.headerTV);
        viewHolder.nameTV = (TextView) convertView.findViewById(R.id.nameTV);
        viewHolder.addressTV = (TextView) convertView.findViewById(R.id.addressTV);
        viewHolder.signalIV = (ImageView) convertView.findViewById(R.id.signalIV);
        viewHolder.batteryIV = (ImageView) convertView.findViewById(R.id.batteryIV);
        viewHolder.alarmsLL = (LinearLayout) convertView.findViewById(R.id.alarmsLL);
        viewHolder.connectBtn = (Button) convertView.findViewById(R.id.connectBtn);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    int position = (int) v.getTag();
                    if(items.get(position).isActive()) {
                        listener.onClick(items.get(position));
                    }
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder viewHolder, int position) {

        boolean showHeader = position == 0 || items.get(position).isActive() != items.get(position - 1).isActive();

        Device thisDevice = items.get(position);
        viewHolder.mainView.setTag(position);
        if(showHeader){
            viewHolder.headerTV.setVisibility(View.VISIBLE);
        }else{
            viewHolder.headerTV.setVisibility(View.GONE);
        }
        if(thisDevice.isActive()){
            viewHolder.headerTV.setText(context.getString(R.string.active_sensors));
        }else{
            viewHolder.headerTV.setText(context.getString(R.string.inactive_sensors));
        }

        if(!thisDevice.isConnected() && !LoonAndroid.demoMode){
            viewHolder.nameTV.setText( TextUtils.isEmpty(thisDevice.getDescription())? thisDevice.getName(): thisDevice.getDescription() );
            viewHolder.addressTV.setText(thisDevice.getName());
            viewHolder.connectBtn.setVisibility(View.VISIBLE);
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

                    GattManager manager = GattManager.getInstance();
                    GattConnectOperation operation = new GattConnectOperation(device);
                    manager.queue(operation);
                }
            });

        }else if( (thisDevice.isActive() && thisDevice.isConnected()) || LoonAndroid.demoMode ){
            viewHolder.nameTV.setText( TextUtils.isEmpty(thisDevice.getDescription())? thisDevice.getName(): thisDevice.getDescription() );
            viewHolder.addressTV.setText(thisDevice.getName());
            viewHolder.connectBtn.setVisibility(View.GONE);
            viewHolder.signalIV.setVisibility(View.VISIBLE);
            viewHolder.batteryIV.setVisibility(View.VISIBLE);
            Util.setUpSignalView(context, viewHolder.signalIV, thisDevice);
            Util.setUpBatteryView(context, viewHolder.batteryIV, thisDevice);
        }else{
            viewHolder.nameTV.setText( thisDevice.getName() );
            viewHolder.addressTV.setText(thisDevice.getMacAddress());
            viewHolder.connectBtn.setVisibility(View.GONE);
            viewHolder.signalIV.setVisibility(View.GONE);
            viewHolder.batteryIV.setVisibility(View.GONE);
        }

        //i need to look for this item's active alarms and list them
        DevicePropertyDao aDao = new DevicePropertyDao(context);
        //viewHolder.alarmsLL.removeAllViews();
        DeviceProperty dProperty = aDao.getLastAlertForDevice(thisDevice.getId(),Util.getCustomerId(context),Util.getSiteId(context),Util.getUserId(context));
        viewHolder.alarmsLL.removeAllViews();
        if (dProperty != null && dProperty.getDismissedAt() == null) {

            Property alertProperty = Property.getDefaultProperty(dProperty.getPropertyId());
            String propertyMessage = dProperty.getValue().equalsIgnoreCase("on")?
                    context.getString(alertProperty.getOnTextId()):
                    context.getString(alertProperty.getOffTextId());

            View alertView = LinearLayout.inflate(context, R.layout.alert_item, null);
            ((TextView) alertView.findViewById(R.id.alertDateTV)).setText(Util.sdf.format(dProperty.getCreatedAt()));
            ((TextView) alertView.findViewById(R.id.serviceTV)).setText(propertyMessage);
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
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Device getItem(int position) {
        return items.get(position);
    }

    public void remove(int position){
        items.remove(position);
        this.notifyDataSetChanged();
    }
}
