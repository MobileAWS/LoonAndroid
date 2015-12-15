package com.maws.loonandroid.fragments;


import android.content.Context;
import android.os.Bundle;
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
import com.maws.loonandroid.adapters.ContactListAdapter;
import com.maws.loonandroid.dao.ContactDao;
import com.maws.loonandroid.models.Contact;
import com.maws.loonandroid.util.Util;
import com.maws.loonandroid.views.CustomToast;

import org.droidparts.bus.EventBus;
import org.droidparts.bus.EventReceiver;

import java.util.List;


public class SmsFragment extends Fragment implements EventReceiver<Object> {

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
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.registerReceiver(this, Util.EVENT_CONTACT_CREATED);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.unregisterReceiver(this);
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
                 List<Contact> contactlistFb = contactDao.getAll();
                if(contactlistFb.size() < 5) {
                    EventBus.postEvent(Util.CONTACT_INTEND);
                }else {
                    CustomToast.showAlert(context,"Only 5 contact are possible to add. ",CustomToast._TYPE_WARNING);
                }
            }
        });
        return rootView;
    }

    private void refreshAdapter(){
        List<Contact> contactList = contactDao.getAll();
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

    @Override
    public void onEvent(String name, Object data) {
        switch (name){
            case Util.EVENT_CONTACT_CREATED:
                refreshAdapter();
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }
}
