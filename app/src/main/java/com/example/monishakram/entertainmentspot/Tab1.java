package com.example.monishakram.entertainmentspot;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab1 extends Fragment {


    RecyclerView recyclerView;
    RecyclerViewFriendListAdapter recyclerViewFriendListAdapter;
    RecyclerView.LayoutManager layoutManager;
    IntentFilter intentFilter;
    FloatingActionButton fab_createGroup;
    ArrayList<PersonDetails> list = null;

    public Tab1() {
        // Required empty public constructor
        recyclerViewFriendListAdapter = new RecyclerViewFriendListAdapter(new RecyclerViewFriendListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PersonDetails item) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("FriendDetails", item);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutManager = new LinearLayoutManager(getActivity());
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab1, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);
        if(list == null)
            list = new ArrayList<>();
        updateList();

        intentFilter = new IntentFilter();
        intentFilter.addAction(MySocket.userListUpdateAction);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewFriendListAdapter);
        fab_createGroup = v.findViewById(R.id.fabCreateGroup);
        fab_createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateGroupDialog();
            }
        });

        return v;
    }

    private void showCreateGroupDialog() {
        Intent intent = new Intent(getContext(), GroupCreateDialogActivity.class);
        Log.i("TAG", "Fab Clicked");
        if(MySocket.userList == null)
            return;
        String[] temp = MySocket.userList.split(",");
        StringBuilder userList = new StringBuilder();
        for(int i = 1; i < temp.length; i++)
            userList.append(temp[i]).append(",");
        intent.putExtra("UserList", userList.toString());
        startActivity(intent);
    }


    void updateList(){
        recyclerViewFriendListAdapter.updateDataSet(list);
    }

}
