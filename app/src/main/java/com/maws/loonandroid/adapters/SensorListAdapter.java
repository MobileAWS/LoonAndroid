package com.maws.loonandroid.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maws.loonandroid.R;
import com.maws.loonandroid.ifaces.MultipleSelectionAdapter;
import com.maws.loonandroid.models.Sensor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrexxjc on 12/04/2015.
 */
public class SensorListAdapter extends BaseAdapter {

    private final Context context;
    private final List<Sensor> items;

    static class ViewHolder {
        TextView nameTV, serialTV;
        ImageView checkIV;
    }

    public SensorListAdapter(Context context, List<Sensor> values) {
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
            viewHolder.serialTV = (TextView) convertView.findViewById(R.id.serialTV);
            viewHolder.checkIV = (ImageView) convertView.findViewById(R.id.checkIV);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        }else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Sensor thisSensor = items.get(position);
        viewHolder.nameTV.setText(thisSensor.getName());
        viewHolder.serialTV.setText(thisSensor.getSerial());

        return convertView;
    }

}
