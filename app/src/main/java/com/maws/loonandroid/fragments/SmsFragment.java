package com.maws.loonandroid.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.maws.loonandroid.R;
import com.maws.loonandroid.activities.MainActivity;
import com.maws.loonandroid.adapters.ContactListAdapter;
import com.maws.loonandroid.dao.ContactDao;
import com.maws.loonandroid.models.Contact;
import com.maws.loonandroid.views.CustomToast;

import java.util.List;


public class SmsFragment extends Fragment {

    private ContactListAdapter adapter;
    private  RecyclerView contactRv;
    private LinearLayout emptyMessageLV;
    private ContactDao contactDao = new ContactDao(this.getActivity());

    public static SmsFragment newInstance() {
        SmsFragment fragment = new SmsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SmsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Context context = this.getActivity();
        View rootView = inflater.inflate(R.layout.fragment_sms, container, false);

        emptyMessageLV = (LinearLayout) rootView.findViewById(R.id.emptyMessageLV);
        contactRv= (RecyclerView) rootView.findViewById(R.id.contactRV);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        contactRv.setLayoutManager(layoutManager);
        contactDao = new ContactDao(this.getActivity());
        final List<Contact> contactList = contactDao.getAll();


        if(contactList.size() > 0){
            adapter = new ContactListAdapter(this.getActivity(), contactList, new ContactListAdapter.ContactViewHolderClickListener() {
                @Override
                public void onClick(Contact contact) {
                    Log.e("info", "click list");
                }
            });
            contactRv.setAdapter(adapter);
            emptyMessageLV.setVisibility(View.GONE);
            contactRv.setVisibility(View.VISIBLE);
        }
        else{
            emptyMessageLV.setVisibility(View.VISIBLE);
            contactRv.setVisibility(View.GONE);
        }
        FloatingActionButton fabScanButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fabScanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                 // contactList = contactDao.getAll();
                if(contactList.size() < 5) {
                    Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                    pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                    startActivityForResult(pickContactIntent, MainActivity.PICK_CONTACT);
                }else {
                    CustomToast.showAlert(context,"Only 5 contact are possible to add. ",CustomToast._TYPE_WARNING);
                }
            }
        });
        return rootView;
    }
    public void addContact(String number,String name){
        contactDao.create(new Contact(name,number));
        refreshAdapter(contactDao.getAll());
    }
    private void refreshAdapter(List<Contact> contactList){
        adapter  = new ContactListAdapter(this.getActivity(), contactList, new ContactListAdapter.ContactViewHolderClickListener() {
            @Override
            public void onClick(Contact contact) {

            }
        });
        contactRv.setAdapter(adapter);
        emptyMessageLV.setVisibility(View.GONE);
        contactRv.setVisibility(View.VISIBLE);
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }
}
