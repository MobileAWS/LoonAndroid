package com.maws.loonandroid.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.maws.loonandroid.R;
import com.maws.loonandroid.models.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrexxjc on 27/05/2015.
 */
public class BluetoothDeviceAdapter extends BaseAdapter {

    private final Context context;
    private final List<Device> items = new ArrayList<Device>();
    private BluetoothDeviceOptionListener listener;

    public interface BluetoothDeviceOptionListener{
        public void onDeviceAdded(Device device);
        public void onDeviceIgnored(Device device);
    }

    static class ViewHolder {
        TextView nameTV, serialTV, alreadyAddedTV;
        Button addBtn, ignoreBtn;
    }

    public BluetoothDeviceAdapter(Context context, BluetoothDeviceOptionListener listener) {
        this.context = context;
        this.listener = listener;
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
    public long getItemId(int position) { return position; }

    public void clear(){
        this.items.clear();
        this.notifyDataSetChanged();
    }

    public void add(Device device){
        if(!items.contains(device)) {
            this.items.add(device);
            this.notifyDataSetChanged();
        }
    };

    public void remove(Device device){
        Device toRemove = null;
        for(Device mDevice : items){
            if(mDevice.getMacAddress() == device.getMacAddress()){
                toRemove = mDevice;
            }
        }
        if(toRemove != null) {
            this.items.remove(device);
            this.notifyDataSetChanged();
        }
    };

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView==null){
            // inflate the layout
            convertView = LinearLayout.inflate(context, R.layout.bluetooth_device_item, null);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.nameTV = (TextView) convertView.findViewById(R.id.nameTV);
            viewHolder.serialTV = (TextView) convertView.findViewById(R.id.serialTV);
            viewHolder.alreadyAddedTV = (TextView) convertView.findViewById(R.id.alreadyAddedTV);
            viewHolder.addBtn = (Button) convertView.findViewById(R.id.addBtn);
            viewHolder.ignoreBtn = (Button) convertView.findViewById(R.id.ignoreBtn);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        }else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Device thisDevice = items.get(position);
        viewHolder.nameTV.setText(thisDevice.getName());
        viewHolder.serialTV.setText(thisDevice.getMacAddress());
        viewHolder.addBtn.setText(" " + context.getString(R.string.use_it));
        viewHolder.ignoreBtn.setText(" " + context.getString(R.string.ignore));

        if(thisDevice.getId() >= 0){
            viewHolder.addBtn.setVisibility(View.GONE);
            viewHolder.ignoreBtn.setVisibility(View.GONE);
            viewHolder.alreadyAddedTV.setVisibility(View.VISIBLE);
        }else{
            viewHolder.addBtn.setVisibility(View.VISIBLE);
            viewHolder.ignoreBtn.setVisibility(View.VISIBLE);
            viewHolder.alreadyAddedTV.setVisibility(View.GONE);

            viewHolder.addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeviceAdded(thisDevice);
                }
            });

            viewHolder.ignoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeviceIgnored(thisDevice);
                }
            });
        }

        return convertView;
    }

}