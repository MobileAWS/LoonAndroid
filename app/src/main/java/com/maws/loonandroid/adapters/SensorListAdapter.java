package com.maws.loonandroid.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maws.loonandroid.R;
import com.maws.loonandroid.dao.AlertDao;
import com.maws.loonandroid.dao.LoonMedicalDao;
import com.maws.loonandroid.dao.SensorServiceDao;
import com.maws.loonandroid.ifaces.MultipleSelectionAdapter;
import com.maws.loonandroid.models.Alert;
import com.maws.loonandroid.models.Sensor;
import com.maws.loonandroid.views.CustomToast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Andrexxjc on 12/04/2015.
 */
public class SensorListAdapter extends BaseAdapter {

    private final Context context;
    private final List<Sensor> items;

    static class ViewHolder {
        TextView nameTV, addressTV;
        ImageView checkIV, signalIV, batteryIV;
        LinearLayout alarmsLL;
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
            viewHolder.addressTV = (TextView) convertView.findViewById(R.id.addressTV);
            viewHolder.checkIV = (ImageView) convertView.findViewById(R.id.checkIV);
            viewHolder.signalIV = (ImageView) convertView.findViewById(R.id.signalIV);
            viewHolder.batteryIV = (ImageView) convertView.findViewById(R.id.batteryIV);
            viewHolder.alarmsLL = (LinearLayout) convertView.findViewById(R.id.alarmsLL);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        }else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Sensor thisSensor = items.get(position);

        if(thisSensor.isActive()){
            viewHolder.nameTV.setText( TextUtils.isEmpty(thisSensor.getDescription())? thisSensor.getName():thisSensor.getDescription() );
            viewHolder.addressTV.setText(thisSensor.getName());
            viewHolder.checkIV.setVisibility(View.VISIBLE);
            viewHolder.signalIV.setVisibility(View.VISIBLE);
            viewHolder.batteryIV.setVisibility(View.VISIBLE);
        }else{
            viewHolder.nameTV.setText( thisSensor.getName() );
            viewHolder.addressTV.setText(thisSensor.getMacAddress());
            viewHolder.checkIV.setVisibility(View.GONE);
            viewHolder.signalIV.setVisibility(View.GONE);
            viewHolder.batteryIV.setVisibility(View.GONE);
        }

        //i need to look for this item's active alarms and list them
        AlertDao aDao = new AlertDao(context);
        LoonMedicalDao lDao = new LoonMedicalDao(context);

        Cursor alertCursor = aDao.getUndismissedAlertInfo(lDao.getReadableDatabase(), thisSensor.getId());
        viewHolder.alarmsLL.removeAllViews();
        if (alertCursor.moveToFirst()) {
            do {
                Date alertDate = new Date( alertCursor.getLong( alertCursor.getColumnIndex(AlertDao.KEY_ALERT_DATE) ) );
                long alertId = alertCursor.getLong(alertCursor.getColumnIndex(AlertDao.KEY_ID));
                String serviceName = alertCursor.getString( alertCursor.getColumnIndex("Service") );
                View alertView = LinearLayout.inflate(context, R.layout.alert_item, null);
                ((TextView)alertView.findViewById(R.id.alertDateTV)).setText(alertDate.toString());
                ((TextView)alertView.findViewById(R.id.serviceTV)).setText(serviceName);
                alertView.setTag(alertId);

                alertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long alertId = Long.valueOf( v.getTag().toString());
                        Alert alert = new Alert();
                        alert.setId(alertId);
                        AlertDao aDao = new AlertDao(context);
                        LoonMedicalDao lDao = new LoonMedicalDao(context);
                        aDao.dismiss(alert, lDao.getWritableDatabase());
                        v.setVisibility(View.GONE);
                    }
                });

                viewHolder.alarmsLL.addView(alertView);
            } while (alertCursor.moveToNext());
        }
        alertCursor.close();

        return convertView;
    }

}
