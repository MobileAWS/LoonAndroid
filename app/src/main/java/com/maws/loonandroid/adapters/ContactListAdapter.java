package com.maws.loonandroid.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maws.loonandroid.R;
import com.maws.loonandroid.dao.ContactDao;
import com.maws.loonandroid.models.Contact;
import com.maws.loonandroid.views.CustomToast;

import java.util.List;

/**
 * Created by Aprada on 12/8/15.
 */
public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder> {

    private static final String TAG = "ContactListAdapter";
    private final Context context;
    private List<Contact> items;
    private ContactViewHolderClickListener listener;

    public static interface ContactViewHolderClickListener {
        void onClick(Contact contact);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        View mainView;
        TextView numberTV,nameTV;
        ImageView deleteButtonIV;


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
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_item, viewGroup, false);
        ContactViewHolder viewHolder = new ContactViewHolder(convertView);
        viewHolder.nameTV = (TextView) convertView.findViewById(R.id.nameTV);
        viewHolder.numberTV = (TextView) convertView.findViewById(R.id.numberTV);
        viewHolder.deleteButtonIV = (ImageView) convertView.findViewById(R.id.deleteButtonIV);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder viewHolder, final int position) {
        final Contact contact = items.get(position);
        viewHolder.nameTV.setText(contact.getName());
        viewHolder.numberTV.setText(contact.getNumber());
        viewHolder.deleteButtonIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                ContactDao contactDao = new ContactDao(context);
                                Contact contactToDelete = items.get(position);
                                contactDao.delete(contactToDelete);
                                items = contactDao.getAll();
                                refreshAdapter();
                                CustomToast.showAlert(context,context.getString(R.string.contact_deleted,CustomToast._TYPE_SUCCESS));
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getString(R.string.confirmation_delete_contact)).setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });
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

    private void  refreshAdapter (){
        this.notifyDataSetChanged();
    }
}
