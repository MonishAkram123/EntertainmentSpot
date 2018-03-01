package com.example.monishakram.entertainmentspot;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class DialogItemsAdapter extends BaseAdapter {
    private final Context context;
    ArrayList<DialogItems> list = new ArrayList<>();

    DialogItemsAdapter(Context context){
        this.context = context;
    }

    void addItem(DialogItems member){
        list.add(member);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MyViewHolder myViewHolder;
        convertView = LayoutInflater.from(context).inflate(R.layout.members_of_new_group, parent, false);
        myViewHolder = new MyViewHolder(convertView);
        myViewHolder.textView.setText(list.get(position).memberName);
        myViewHolder.checkBox.setChecked(list.get(position).isSelected);
        myViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.get(position).isSelected = ((CheckBox) v).isChecked();
            }
        });
        return convertView;
    }

    private class MyViewHolder {
        TextView textView;
        CheckBox checkBox;
        MyViewHolder(View v){
            textView = v.findViewById(R.id.memberName);
            checkBox = v.findViewById(R.id.isSelected);
        }
    }
}
