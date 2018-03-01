package com.example.monishakram.entertainmentspot;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

class MySocket {
    static Socket socket = null;
    static String ip = null;
    static String port = null;
    static ObjectInputStream objectInputStream = null;
    static ObjectOutputStream objectOutputStream = null;
    static String userListUpdateAction = "USERLIST:";
    static String newChatMessage = "RECEIVER NAME:";
    static String groupCreateRequest = "GroupCreateRequest:";
    static String attachment = "Atch:";
    static String loginName = null;
    static String fileName = null;
    static String userList = null;
    static ArrayList<String> myGroups = new ArrayList<>();
    public static boolean isAGroup = false;

    static void reset() {
        socket = null;
        ip = port = null;
        objectOutputStream = null;
        objectInputStream = null;
    }
    static void sendMessage(final String message){
        if(message == null || objectOutputStream == null)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    objectOutputStream.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    static boolean isMyGroup(String s) {
        for(String groupName: myGroups) {
            if (groupName.startsWith(s) && groupName.contains(MySocket.loginName))
                return true;
        }
        return false;
    }
}
