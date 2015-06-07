package com.maws.loonandroid.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maws.loonandroid.R;
import com.maws.loonandroid.models.SensorCharacteristic;

import java.util.List;

/**
 * Created by Andrexxjc on 15/05/2015.
 */
public class SensorServiceListAdapter extends BaseAdapter {

    private final Context context;
    private final List<SensorCharacteristic> items;

    static class ViewHolder {
        TextView nameTV;
        ImageView soundIV;
    }

    public SensorServiceListAdapter(Context context, List<SensorCharacteristic> values) {
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
            convertView = LinearLayout.inflate(context, R.layout.sensor_service_item, null);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.nameTV = (TextView) convertView.findViewById(R.id.nameTV);
            viewHolder.soundIV = (ImageView) convertView.findViewById(R.id.soundIV);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        }else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SensorCharacteristic sService = items.get(position);
        viewHolder.nameTV.setText(sService.getName());

        /*if(sService.isOn()){
            viewHolder.soundIV.setColorFilter( R.color.green );
            viewHolder.soundIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_audio_on));
        }else{
            viewHolder.soundIV.setColorFilter( R.color.dark_orange );
            viewHolder.soundIV.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_audio_off));
        }*/


        return convertView;
    }

}