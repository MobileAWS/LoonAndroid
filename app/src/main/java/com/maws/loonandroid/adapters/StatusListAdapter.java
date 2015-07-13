package com.maws.loonandroid.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maws.loonandroid.R;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.models.Property;
import com.maws.loonandroid.services.BLEService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aprada on 7/9/2015.
 */
public class StatusListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<Device> items;
    private final HashMap<String,List<Property>> itemsProperties;
    private List<Integer> selectedItems;
    private ChildViewHolder childViewHolder;
    private GroupViewHolder groupViewHolder;


    public final class GroupViewHolder {
        TextView deviceNameTV;
    }

    public final class ChildViewHolder {
        TextView sensorTv;
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public Property getChild(int groupPosition, int childPosititon) {
        return this.itemsProperties.get("0").get(childPosititon);
    }
    @Override
    public int getChildrenCount(int i) {
        return this.itemsProperties.get("0").size();
    }

    @Override
    public Device getGroup(int i) {
        return items.get(i);
    }



    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }



    public StatusListAdapter(Context context, List<Device> values, HashMap<String,List<Property>> propertyList) {
        this.context = context;
        this.items = values;
        this.selectedItems = new ArrayList<Integer>();
        this.itemsProperties = propertyList;
    }

    @Override
    public View getGroupView(int position, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if(convertView == null) {
            groupViewHolder = new GroupViewHolder();
            convertView = LinearLayout.inflate(context, R.layout.status_group, null);
            groupViewHolder.deviceNameTV = (TextView) convertView.findViewById(R.id.deviceNameTV);
        }else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        Device thisDevice = items.get(position);
        if(thisDevice.isActive()) {
            if(!thisDevice.getName().isEmpty()) {
                groupViewHolder.deviceNameTV.setText(thisDevice.getName());
            }else {
                groupViewHolder.deviceNameTV.setText(thisDevice.getDescription());
            }
        }else {
            groupViewHolder.deviceNameTV.setTextColor(context.getResources().getColor(R.color.light_gray));
        }
        convertView.setTag(groupViewHolder);

        //ExpandableListView eLV = (ExpandableListView) parent;
        //eLV.expandGroup(position);
        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder viewHolder;
        if(convertView == null){
            convertView = LinearLayout.inflate(context, R.layout.status_item, null);
            viewHolder = new ChildViewHolder();
            viewHolder.sensorTv = (TextView) convertView.findViewById(R.id.sensorTv);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ChildViewHolder) convertView.getTag();
        }
        Device thisDevice = items.get(groupPosition);
        Property property = itemsProperties.get("0").get(childPosition);

        boolean isOn = false;

        if(thisDevice.isActive()) {
            if (BLEService.switchValues.containsKey(thisDevice.getMacAddress())) {
                String currentValue = BLEService.switchValues.get(thisDevice.getMacAddress());
                if (currentValue.charAt(Integer.valueOf(String.valueOf(property.getId()))) == '1') {
                    isOn = true;
                }
            }
            if (isOn) {
                viewHolder.sensorTv.setTextColor(context.getResources().getColor(R.color.green));
                viewHolder.sensorTv.setText(property.getName());
            } else {
                viewHolder.sensorTv.setTextColor(context.getResources().getColor(R.color.dark_orange));
                viewHolder.sensorTv.setText(property.getName());
            }
        }else {
            viewHolder.sensorTv.setTextColor(context.getResources().getColor(R.color.light_gray));
            viewHolder.sensorTv.setText(property.getName());
        }
        return convertView;
    }


}
