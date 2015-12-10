package com.maws.loonandroid.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maws.loonandroid.R;
import com.maws.loonandroid.models.Contact;
import com.maws.loonandroid.models.Device;

import java.util.List;

/**
 * Created by Aprada on 12/8/15.
 */
public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder> {

    private static final String TAG = "ContactListAdapter";
    private final Context context;
    private final List<Contact> items;
    private ContactViewHolderClickListener listener;

    public static interface ContactViewHolderClickListener {
        void onClick(Device device);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        View mainView, emptyMessageLV;
        RecyclerView contactRV;
        TextView numberTV,nameTV;


        public ContactViewHolder(View v) {
            super(v);
            this.mainView = v;
        }

    }

    public ContactListAdapter(Context context, List<Contact> items, ContactViewHolderClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public ContactListAdapter.ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sensor_item, viewGroup, false);
        ContactViewHolder viewHolder = new ContactViewHolder(convertView);
        viewHolder.emptyMessageLV = convertView.findViewById(R.id.emptyLayout);
        viewHolder.contactRV = (RecyclerView) convertView.findViewById(R.id.contactRV);
        viewHolder.nameTV = (TextView) convertView.findViewById(R.id.nameTV);
        viewHolder.numberTV = (TextView) convertView.findViewById(R.id.numberTV);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactListAdapter.ContactViewHolder viewHolder, int position) {
        Contact contact = items.get(position);
        viewHolder.nameTV.setText(contact.getName());
        viewHolder.numberTV.setText(contact.getNumber());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    public Contact getItem(int position) {
        return items.get(position);
    }
}
