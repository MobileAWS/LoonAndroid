package com.maws.loonandroid.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.maws.loonandroid.R;
import com.maws.loonandroid.dao.DevicePropertyDao;
import com.maws.loonandroid.models.Device;
import com.maws.loonandroid.models.DeviceProperty;
import com.maws.loonandroid.models.Property;
import com.maws.loonandroid.util.Util;
import java.util.List;

/**
 * Created by Andres Prada on 6/15/2015.
 */
public class DevicePropertyHistoryListAdapter extends BaseAdapter{

    private final Context context;
    private final List<DeviceProperty> items;
    private final Device device;

    static class ViewHolder {
        TextView descAlertHv, alertDismiss, alertDateHv, dismissDateHv, timeAlertHv, alertDateTV;
    }

    public DevicePropertyHistoryListAdapter(Device device, List<DeviceProperty> items, Context context) {
        this.items = items;
        this.context = context;
        this.device = device;
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
            viewHolder.alertDismiss = (TextView) convertView.findViewById(R.id.alertDismiss);
            viewHolder.alertDateTV = (TextView) convertView.findViewById(R.id.alertDateTV);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        DeviceProperty thisAlert = items.get(position);
        Property property = Property.getDefaultProperty( thisAlert.getPropertyId(), device.getType());

        viewHolder.alertDateTV.setText(Util.longDateFormat.format(thisAlert.getCreatedAt()));
        viewHolder.descAlertHv.setText( context.getString(property.getDisplayId()) );
        viewHolder.alertDateHv.setText( Util.timeOnlyFormat.format(thisAlert.getCreatedAt()));
        viewHolder.dismissDateHv.setText(thisAlert.getDismissedAt() == null ? "-" : Util.timeOnlyFormat.format(thisAlert.getDismissedAt()));
        viewHolder.timeAlertHv.setText(thisAlert.getDismissedAt() == null ? "-" : String.format( context.getString(R.string.elapsed_time), Util.totalTimeDismissed(thisAlert.getTotalTimeAlarm())) ) ;

        if(thisAlert.getDismissedAt() == null) {
            viewHolder.alertDismiss.setVisibility(View.GONE);
            viewHolder.dismissDateHv.setVisibility(View.GONE);
            viewHolder.timeAlertHv.setVisibility(View.GONE);
        }else{
            viewHolder.alertDismiss.setVisibility(View.VISIBLE);
            viewHolder.dismissDateHv.setVisibility(View.VISIBLE);
            viewHolder.timeAlertHv.setVisibility(View.VISIBLE);

        }
        return convertView;
    }

}
