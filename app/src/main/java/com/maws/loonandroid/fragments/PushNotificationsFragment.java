package com.maws.loonandroid.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.maws.loonandroid.R;
import com.maws.loonandroid.activities.MainActivity;
import com.maws.loonandroid.adapters.UploadSensorListAdapter;
import com.maws.loonandroid.adapters.UserListAdapter;
import com.maws.loonandroid.models.Sensor;
import com.maws.loonandroid.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrexxjc on 12/04/2015.
 */
public class PushNotificationsFragment extends Fragment {

    private ListView usersLV;
    private UserListAdapter adapter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PushNotificationsFragment newInstance() {
        PushNotificationsFragment fragment = new PushNotificationsFragment();
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    public PushNotificationsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_push_notifications, container, false);
        usersLV = (ListView) rootView.findViewById(R.id.usersLV);
        List<User> users = null;
        if(getActivity() instanceof MainActivity){
            users = ((MainActivity)getActivity()).getUsers();
        }else{
            users = new ArrayList<User>();
        }
        adapter = new UserListAdapter(this.getActivity(), users);
        usersLV.setAdapter(adapter);
        usersLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.toogleItem(position);
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.push_notifications, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }


}
