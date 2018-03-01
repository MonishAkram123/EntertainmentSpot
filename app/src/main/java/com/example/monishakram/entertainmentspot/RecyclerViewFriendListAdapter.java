package com.example.monishakram.entertainmentspot;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class RecyclerViewFriendListAdapter extends RecyclerView.Adapter<RecyclerViewFriendListAdapter.FriendViewHolder> {
    private ArrayList<PersonDetails> friendList;
    interface OnItemClickListener{
        void onItemClick(PersonDetails item);
    }
    private OnItemClickListener listener;
    RecyclerViewFriendListAdapter(OnItemClickListener listener){
        friendList = new ArrayList<>();
        this.listener = listener;
    }
    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout, parent, false);
        return new FriendViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        holder.imageView.setImageResource(friendList.get(position).profilePic);
        holder.friendName.setText(friendList.get(position).name);
        holder.status.setText(friendList.get(position).status);
        if(friendList.get(position).status.equals("Online"))
            holder.statusIcon.setImageResource(R.drawable.online);
        else
            holder.statusIcon.setImageResource(R.drawable.offline);
        holder.bind(friendList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    void updateDataSet(ArrayList<PersonDetails> list) {
        friendList.clear();
        friendList.addAll(list);
        Log.i("TAG", "New List size in RecyclerView = " +list.size());
        notifyDataSetChanged();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView, statusIcon;
        TextView friendName, status;
        ImageButton buttonChat;
        FriendViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.friendPhoto);
            friendName = itemView.findViewById(R.id.friendName);
            status = itemView.findViewById(R.id.status);
            buttonChat = itemView.findViewById(R.id.buttonChat);
            statusIcon = itemView.findViewById(R.id.statusIcon);
        }

        void bind(final PersonDetails personDetails, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(personDetails);
                }
            });
        }
    }
}
