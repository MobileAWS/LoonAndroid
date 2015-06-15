package com.maws.loonandroid.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maws.loonandroid.R;
import com.maws.loonandroid.ifaces.MultipleSelectionAdapter;
import com.maws.loonandroid.models.Alert;
import com.maws.loonandroid.util.Util;

import org.droidparts.adapter.holder.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andres Prada on 6/15/2015.
 */
public class AlertHistoryListAdapter extends BaseAdapter implements MultipleSelectionAdapter<Alert> {

    private final Context context;
    private final List<Alert> items;
    private List<Integer> selectedItems;

    static class ViewHolder {
        TextView descAlertHv;
        TextView alertDateHv;
        TextView dismissDateHv;
        TextView timeAlertHv;
    }

    public AlertHistoryListAdapter(List<Alert> items, Context context) {
        this.items = items;
        this.context = context;
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
            convertView = LinearLayout.inflate(context, R.layout.alert_history_item, null);
            viewHolder = new ViewHolder();
            viewHolder.descAlertHv = (TextView) convertView.findViewById(R.id.descAlertHv);
            viewHolder.alertDateHv = (TextView) convertView.findViewById(R.id.alertDateHv);
            viewHolder.dismissDateHv = (TextView) convertView.findViewById(R.id.dismissdateHv);
            viewHolder.timeAlertHv = (TextView) convertView.findViewById(R.id.timeAlertHv);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Alert thisAlert = items.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        viewHolder.descAlertHv.setText(String.valueOf(thisAlert.getSensorId()));
        viewHolder.alertDateHv.setText(sdf.format(thisAlert.getAlertDate()));
        viewHolder.dismissDateHv.setText(sdf.format(thisAlert.getDismissedDate()));
        viewHolder.timeAlertHv.setText( Util.totalTimeDismissed(thisAlert.getTotalTimeAlarm()));
        return convertView;
    }

    @Override
    public List<Alert> getSelectedItems(){
        ArrayList<Alert> selectedItemsToReturn = new ArrayList<Alert>();
        for(Integer position: selectedItems){
            selectedItemsToReturn.add(items.get(position));
        }
        return selectedItemsToReturn;
    }

    @Override
    public void selectItem(int position) {

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

    @Override
    public void toogleItem(int position){
        if(selectedItems.contains( new Integer(position) )){
            unselectItem(position);
        }else{
            selectItem(position);
        }
        notifyDataSetInvalidated();
    }

}
