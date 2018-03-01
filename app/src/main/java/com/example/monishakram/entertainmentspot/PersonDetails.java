package com.example.monishakram.entertainmentspot;


import android.support.annotation.Nullable;

import java.io.Serializable;

class PersonDetails implements Serializable{
    String name;
    int profilePic;             //Must be replaced after profile picture will be given by server
    String status;
    PersonDetails(@Nullable String name, @Nullable String status){
        if(name == null || name.equals(""))
            name = "No Name";
        if(status == null || status.equals(""))
            status = "Offline";

        this.name = name;
        this.status = status;
        profilePic = R.drawable.ic_profile_pic;

    }
}
