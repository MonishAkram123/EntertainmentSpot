package com.example.monishakram.entertainmentspot;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewChatAdapter extends RecyclerView.Adapter<RecyclerViewChatAdapter.ChatViewHolder>{
    private ArrayList<String> currentChat = new ArrayList<>();

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(viewType == 1)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_box_left, parent, false);
        else
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_box_right, parent, false);
        return new ChatViewHolder(v);
    }

    void addItem(String s){
        currentChat.add(s);
    }
    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        String message = currentChat.get(position);
        String temp [] = message.split(":");
        temp = temp[1].split(";");
        message = temp[0];
        holder.textView.setText(message);
        Log.i("CHAT", "MESSAGE SET = " +holder.textView.getText() +"ID= " +holder.textView.getId());
        Log.i("TAG", "IS a group = " +MySocket.isAGroup);
        holder.textViewSenderName.setText(currentChat.get(position).split(":")[0]);
        if(getItemViewType(position) == 0 && MySocket.isAGroup)
            holder.textViewSenderName.setVisibility(View.VISIBLE);
        else
            holder.textViewSenderName.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return currentChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        String message = currentChat.get(position);
        return message.startsWith(MySocket.loginName)?1:0;
    }


    void clearList() {
        if(currentChat != null)
            currentChat.clear();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        TextView textViewSenderName;
        ChatViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.message);
            textViewSenderName = itemView.findViewById(R.id.textViewMemberName);
        }

    }
}
