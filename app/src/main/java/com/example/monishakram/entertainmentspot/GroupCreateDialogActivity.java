package com.example.monishakram.entertainmentspot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class GroupCreateDialogActivity extends AppCompatActivity {

    ListView listView;
    DialogItemsAdapter adapter;
    Button create;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create_dialog);
        String userList = getIntent().getStringExtra("UserList");
        adapter = new DialogItemsAdapter(this);
        for(String member: userList.split(","))
            if(!member.equals(MySocket.loginName))
                adapter.addItem(new DialogItems(member, false));
        listView = findViewById(R.id.selectMembers);
        listView.setAdapter(adapter);
        create = findViewById(R.id.create_action);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = ((EditText)findViewById(R.id.editTextGroupName)).getText().toString() ;
                String groupCreateRequest = MySocket.groupCreateRequest +groupName;
                if(groupName.equals("")) {
                    Toast.makeText(GroupCreateDialogActivity.this, "Specify Name first", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuilder selectedMembers = new StringBuilder(" ," +MySocket.loginName);
                for(DialogItems items: adapter.list)
                    if(items.isSelected)
                        selectedMembers.append(",").append(items.memberName);
                if(selectedMembers.equals(new StringBuilder(" ," +MySocket.loginName))) {
                    Toast.makeText(GroupCreateDialogActivity.this, "No members selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                groupCreateRequest += selectedMembers;
                Log.i("GroupCreateActivity", "Final Message to create group:" +groupCreateRequest);
                MySocket.sendMessage(groupCreateRequest);
                Toast.makeText(GroupCreateDialogActivity.this, "Group created successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
