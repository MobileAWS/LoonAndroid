package com.maws.loonandroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maws.loonandroid.R;
import com.maws.loonandroid.models.IconTextOption;

/**
 * Created by Andrexxjc on 11/04/2015.
 */
public class DrawerListAdapter extends ArrayAdapter<IconTextOption> {

    private IconTextOption[] items;

    public DrawerListAdapter(Context context, int resource, IconTextOption[] objects) {
        super(context, resource, objects);
        this.items = objects;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public IconTextOption getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate( R.layout.drawer_item, parent, false);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.drawer_item_image);
        TextView textView = (TextView) convertView.findViewById(R.id.drawer_item_text);
        imageView.setImageResource(items[position].getIcon());
        textView.setText(items[position].getText());
        return convertView;
    }
}

