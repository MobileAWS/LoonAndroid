package com.maws.loonandroid.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maws.loonandroid.R;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.models.Property;
import com.maws.loonandroid.services.BLEService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andres on 7/9/2015.
 */
public class SensorListAdapter extends BaseAdapter {

    private final Context context;
    private final List<Device> items;
    private final List<Property> itemsProperties;
    private List<Integer> selectedItems;

    static class ViewHolder {
        TextView sensor1TV, sensor2TV,sensor3TV,sensor4TV,sensor5TV,sensor6TV;
        TextView deviceNameTV;
    }

    public SensorListAdapter(Context context,List<Device> values, List<Property> propertyList) {
        this.context = context;
        this.items = values;
        this.selectedItems = new ArrayList<Integer>();
        this.itemsProperties = propertyList;
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        List<TextView> listSensor = new ArrayList<>();
        int sensorCount =0;
        if(convertView == null){
            convertView = LinearLayout.inflate(context, R.layout.device_item, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceNameTV = (TextView) convertView.findViewById(R.id.deviceNameTV);
            viewHolder.sensor1TV = (TextView) convertView.findViewById(R.id.sensor1Tv);
            viewHolder.sensor2TV = (TextView) convertView.findViewById(R.id.sensor2Tv);
            viewHolder.sensor3TV = (TextView) convertView.findViewById(R.id.sensor3Tv);
            viewHolder.sensor4TV = (TextView) convertView.findViewById(R.id.sensor4Tv);
            viewHolder.sensor5TV = (TextView) convertView.findViewById(R.id.sensor5Tv);
            viewHolder.sensor6TV = (TextView) convertView.findViewById(R.id.sensor6Tv);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        listSensor = loadSensorList(listSensor,viewHolder);
        Device thisDevice = items.get(position);
        if(!thisDevice.getName().isEmpty()) {
            viewHolder.deviceNameTV.setText(thisDevice.getName());
        }else {
            viewHolder.deviceNameTV.setText(thisDevice.getDescription());
        }

        for (Property property : this.itemsProperties) {
            boolean isOn = false;

            if(thisDevice.isActive()) {
                if (BLEService.switchValues.containsKey(thisDevice.getMacAddress())) {
                    String currentValue = BLEService.switchValues.get(thisDevice.getMacAddress());
                    if (currentValue.charAt(Integer.valueOf(String.valueOf(property.getId()))) == '1') {
                        isOn = true;
                    }
                }
                if (isOn) {
                    listSensor.get(sensorCount).setTextColor(context.getResources().getColor(R.color.green));
                    listSensor.get(sensorCount).setText(property.getName());
                } else {
                    listSensor.get(sensorCount).setTextColor(context.getResources().getColor(R.color.dark_orange));
                    listSensor.get(sensorCount).setText(property.getName());
                }
            }else {
                viewHolder.deviceNameTV.setTextColor(context.getResources().getColor(R.color.light_gray));
                listSensor.get(sensorCount).setTextColor(context.getResources().getColor(R.color.light_gray));
                listSensor.get(sensorCount).setText(property.getName());
            }
            sensorCount++;
        }


        return convertView;
    }

    private List<TextView> loadSensorList(List<TextView> listSensor,ViewHolder viewHolder) {
        listSensor.add(viewHolder.sensor1TV);
        listSensor.add(viewHolder.sensor2TV);
        listSensor.add(viewHolder.sensor3TV);
        listSensor.add(viewHolder.sensor4TV);
        listSensor.add(viewHolder.sensor5TV);
        listSensor.add(viewHolder.sensor6TV);
        return  listSensor;
    }
}
