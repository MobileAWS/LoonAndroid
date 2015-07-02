package com.maws.loonandroid.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maws.loonandroid.R;
import com.maws.loonandroid.ifaces.MultipleSelectionAdapter;
import com.maws.loonandroid.models.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrexxjc on 12/04/2015.
 */
public class UploadSensorListAdapter extends BaseAdapter implements MultipleSelectionAdapter<Device> {

    private final Context context;
    private final List<Device> items;
    private List<Integer> selectedItems;

    static class ViewHolder {
        TextView nameTV, serialTV;
        ImageView checkIV;
        ProgressBar progressBarUploadIV;
        TextView successIV;
    }

    public UploadSensorListAdapter(Context context, List<Device> values) {
        this.context = context;
        this.items = values;
        this.selectedItems = new ArrayList<Integer>();
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
        if(convertView==null){
            // inflate the layout
            convertView = LinearLayout.inflate(context, R.layout.upload_sensor_item, null);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.nameTV = (TextView) convertView.findViewById(R.id.nameTV);
            viewHolder.serialTV = (TextView) convertView.findViewById(R.id.serialTV);
            viewHolder.checkIV = (ImageView) convertView.findViewById(R.id.checkIV);
            viewHolder.progressBarUploadIV = (ProgressBar) convertView.findViewById(R.id.progressBarUploadIV);
            viewHolder.successIV = (TextView) convertView.findViewById(R.id.successIV);
            // store the holder with the view.
            convertView.setTag(viewHolder);

        }else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Device thisDevice = items.get(position);
        viewHolder.nameTV.setText( TextUtils.isEmpty(thisDevice.getDescription())?thisDevice.getName():thisDevice.getDescription());
        viewHolder.serialTV.setText( TextUtils.isEmpty(thisDevice.getDescription())? thisDevice.getHardwareId(): thisDevice.getName());

        //select or unselect items
        if(selectedItems.contains( new Integer(position) )){
            viewHolder.checkIV.setVisibility(View.VISIBLE);
        }else{
            viewHolder.checkIV.setVisibility(View.GONE);
        }
        return convertView;
    }



    @Override
    public List<Device> getSelectedItems(){
        ArrayList<Device> selectedItemsToReturn = new ArrayList<Device>();
        for(Integer position: selectedItems){
            selectedItemsToReturn.add(items.get(position));
        }
        return selectedItemsToReturn;
    }

    @Override
    public void toogleItem(int position){

        if(selectedItems.contains( new Integer(position) )){
            unselectItem(position);
        }else{
            selectItem(position);
        }
        notifyDataSetInvalidated();
    }


    @Override
    public void selectItem(int position) {
        selectedItems.add( new Integer(position) );
    }

    @Override
    public void unselectItem(int position) {
        selectedItems.remove( new Integer(position) );
    }

    @Override
    public void selectAll() {
        for(int i = 0; i < items.size(); i++){
            if(!items.contains( new Integer(i) )){
                selectItem(i);
            }
        }
        notifyDataSetInvalidated();
    }


}
