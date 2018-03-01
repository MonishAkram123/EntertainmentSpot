package com.example.monishakram.entertainmentspot;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    IntentFilter intentFilter;
    RecyclerViewChatAdapter chatAdapter = new RecyclerViewChatAdapter();
    ImageButton btnSend;
    PersonDetails p;
    String fileName, s;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // IntentFilter is required for Connecting Broadcast receiver to the Service.
        intentFilter = new IntentFilter();
        intentFilter.addAction(MySocket.newChatMessage);


        // Setting up for the top label to display user
        p = (PersonDetails) getIntent().getSerializableExtra("FriendDetails");
        ((TextView)findViewById(R.id.chatActivityFriendName)).setText(p.name);
        ((TextView)findViewById(R.id.chatActivityStatus)).setText(p.status);
        MySocket.isAGroup = MySocket.isMyGroup(p.name);
        Log.i("ChatActivity", "isMyGroup =  " +MySocket.isMyGroup(p.name));

        // Does'nt matter coz user is always online :P
        if(Objects.equals(p.status, "Offline"))
            ((ImageView)findViewById(R.id.chatActivityStatusIcon)).setImageResource(R.drawable.offline);
        else
            ((ImageView)findViewById(R.id.chatActivityStatusIcon)).setImageResource(R.drawable.online);
        ((ImageView)findViewById(R.id.chatActivityFriendPhoto)).setImageResource(p.profilePic);

        //Conversation part is handled here
        recyclerView = findViewById(R.id.recyclerViewChatMessage);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);    //makes the RecyclerView to add items from bottom.
        recyclerView.setLayoutManager(layoutManager);   //Required for a RecyclerView
        recyclerView.setAdapter(chatAdapter);       //Setting up my chatAdapter


        //Loading previous conversations from file.
        if(MySocket.isAGroup)
            fileName = p.name;
        else
            fileName = MySocket.loginName +"To" +p.name;
        loadConversation(fileName);
        //Send Button
        btnSend = findViewById(R.id.buttonSendMessage);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MySocket.isAGroup)
                    s = ((EditText) findViewById(R.id.editTextMessage)).getText().toString() +';' +"Group-" +p.name;
                else
                    s = ((EditText) findViewById(R.id.editTextMessage)).getText().toString() +';' +p.name;
                if(s.equals(""))
                    Toast.makeText(ChatActivity.this, "Empty message cannot be send", Toast.LENGTH_SHORT).show();
                else {
                    ((EditText) findViewById(R.id.editTextMessage)).setText("");
                    MySocket.sendMessage(s);
                }
            }
        });
    }

    private void loadConversation(String name) {

        Log.i("FileNameInLoading", name);
        StringBuilder temp = new StringBuilder();
        chatAdapter.clearList();
        String s;
        try {
            FileInputStream inputStream = openFileInput(name);
            InputStreamReader fileReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(fileReader);
            while((s = br.readLine()) != null) {
                temp.append(s);
                Log.i("File Input = " , s);
                if (temp.toString().contains("@@@")) {
                    String[] t = temp.toString().split("@@@");
                    for (int i = 0; i < t.length -1; i++)
                        chatAdapter.addItem(t[i]);
                    temp = new StringBuilder(t[t.length - 1]);
                }
            }
            chatAdapter.addItem(temp.toString());
            fileReader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        chatAdapter.notifyDataSetChanged();
        if(chatAdapter.getItemCount() > 0)
            recyclerView.scrollToPosition(chatAdapter.getItemCount() -1);
    }

    BroadcastReceiver messageFromServerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            if(action.equals(MySocket.newChatMessage) || action.equals(MySocket.groupCreateRequest))
                loadConversation(fileName);
        }
    };

    @Override
    protected void onResume() {
        registerReceiver(messageFromServerReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(messageFromServerReceiver);
        super.onPause();
    }
}
