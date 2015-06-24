package com.maws.loonandroid.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.maws.loonandroid.R;
import com.maws.loonandroid.models.Property;

/**
 * Created by Andrexxjc on 24/06/2015.
 */
public class PropertyAdapter extends BaseAdapter {

    private static final String TAG = "PropertyAdapter";
    private final Context context;
    private final Property[] items;

    static class ViewHolder {
        TextView nameTV;
        ToggleButton enabledTB;
        EditText delayET;
    }

    public PropertyAdapter(Context context, Property[] values) {
        this.context = context;
        this.items = values;
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

        ViewHolder viewHolder;
        if(convertView==null){
            // inflate the layout
            convertView = LinearLayout.inflate(context, R.layout.property_item, null);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.nameTV = (TextView) convertView.findViewById(R.id.nameTV);
            viewHolder.enabledTB = (ToggleButton) convertView.findViewById(R.id.enabledTB);
            viewHolder.delayET = (EditText) convertView.findViewById(R.id.delayET);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        }else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Property thisProperty = items[position];
        viewHolder.nameTV.setText( context.getString( thisProperty.getDisplayId() ) );
        return convertView;

    }

}