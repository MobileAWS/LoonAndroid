package com.maws.loonandroid.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maws.loonandroid.R;
import com.maws.loonandroid.activities.MainActivity;
import com.maws.loonandroid.adapters.ContactListAdapter;


public class SmsFragment extends Fragment {

    private ContactListAdapter adapter;

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
        RecyclerView contactRv = (RecyclerView) rootView.findViewById(R.id.contactRV);

        contactRv.setAdapter(adapter);
        FloatingActionButton fabScanButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fabScanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                startActivityForResult(pickContactIntent, MainActivity.PICK_CONTACT);
            }
        });
        return rootView;
    }

    public void refreshAdapter(){
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }
}
