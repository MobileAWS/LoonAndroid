package com.maws.loonandroid.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
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
    private FloatingActionsMenu fabMenu;
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
        EventBus.registerReceiver(this, Util.EVENT_CONTACT_ADDRESS_BOOK);
        EventBus.registerReceiver(this, Util.EVENT_CONTACT_NEW);
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
        fabMenu = (FloatingActionsMenu) rootView.findViewById(R.id.fabMenu);
       FloatingActionButton fabScanButton1 = (FloatingActionButton) rootView.findViewById(R.id.smsOption1);
        fabScanButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addAddressBook(context);

            }
        });
        FloatingActionButton fabScanButton2 = (FloatingActionButton) rootView.findViewById(R.id.smsOption2);
        fabScanButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newContact(context);
            }
        });
        return rootView;
    }
    private void addAddressBook(final Context context){
        fabMenu.collapse();
        if(Util.isLoginOnline(context)) {
            List<Contact> contactlistFb = contactDao.getAll();
            if (contactlistFb.size() < 5) {
                EventBus.postEvent(Util.CONTACT_INTEND);
            } else {
                CustomToast.showAlert(context, "Only 5 contact are possible to add. ", CustomToast._TYPE_WARNING);
            }
        }else {
            showLoginBook();
        }
    }

    private void newContact(final Context context){
        fabMenu.collapse();
        if(Util.isLoginOnline(context)) {
            List<Contact> contactlistFb = contactDao.getAll();
            if (contactlistFb.size() < 5) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.add_contact_dialogbox, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                alertDialogBuilder.setView(promptsView);

                final EditText nameContact = (EditText) promptsView.findViewById(R.id.nameET);
                final EditText numberContact = (EditText) promptsView
                        .findViewById(R.id.numberET);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Add",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        List<Contact> contactlistFb = contactDao.getAll();
                                        if (nameContact.getText().toString() != null && !nameContact.getText().toString().isEmpty() &&
                                                numberContact.getText().toString() != null && !numberContact.getText().toString().isEmpty()) {
                                            Contact contact = new Contact();
                                            contact.setName(nameContact.getText().toString());
                                            contact.setNumber(numberContact.getText().toString());
                                            if (!Util.searchNameAndName(contactlistFb, contact)) {
                                                ContactDao contactDao = new ContactDao(context);
                                                contactDao.create(contact);
                                                refreshAdapter();
                                                dialog.cancel();
                                            } else {
                                                CustomToast.showAlert(context, "This user name or number already exist", CustomToast._TYPE_ERROR);
                                            }
                                        } else {
                                            CustomToast.showAlert(context, "You need to fillout all the fields.", CustomToast._TYPE_WARNING);
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            } else {
                CustomToast.showAlert(context, "Only 5 contact are possible to add. ", CustomToast._TYPE_WARNING);
            }
        }else {
            showLoginNew();
        }
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
            case Util.EVENT_CONTACT_ADDRESS_BOOK:
                    //addAddressBook(this.getActivity());
                break;
            case Util.EVENT_CONTACT_NEW:
                    //newContact(this.getActivity());
                break;
            default:
                break;
        }
    }
    private void showLoginNew(){
        EventBus.postEvent(Util.EVENT_MAIN_NEW_CONTACT);

    }
    private void showLoginBook(){
        EventBus.postEvent(Util.EVENT_MAIN_ADDRESS_BOOK);

    }

}
