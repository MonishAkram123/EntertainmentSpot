package com.example.monishakram.entertainmentspot;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ServerListenerService extends Service {
    MyServiceBinder myServiceBinder = new MyServiceBinder();
    private boolean running = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myServiceBinder;
    }


    class MyServiceBinder extends Binder{
        ServerListenerService getService(){
            return this.getService();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(running){
                    try {
                        Object response = MySocket.objectInputStream.readObject();
                        handleResponse(response);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        if(MySocket.socket == null || MySocket.socket.isClosed()) {
                            running = false;
                            MySocket.reset();
                            stopSelf();
                        }
                    }
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleResponse(Object response) throws IOException {
        Intent intent = new Intent();
        //If there is any pending file on the server needs to be downloaded.
        if(MySocket.fileName != null){
            byte[] content = BytesUtil.toByteArray(response);
            saveFile("/sdcard/downloads/" +MySocket.fileName, content);
            MySocket.fileName = null;
            intent.setAction(MySocket.attachment);
            sendBroadcast(intent);
        }
        //If chat message contains any attachment information then set pending message
        else if(response.toString().startsWith("Atch:")){
            String[] message = response.toString().split(":");
            String fileName = message[2];
            String receiverName = message[3];
            String senderName = message[1];
            if(senderName.equals(MySocket.loginName))
                writeToFile(senderName +"To" +receiverName, BytesUtil.toByteArray(senderName +":"+fileName +";" +receiverName +"@@@"));
            if(receiverName.equals(MySocket.loginName)){
                writeToFile(receiverName +"To" +senderName, BytesUtil.toByteArray(senderName +":"+fileName +";" +receiverName +"@@@"));
                MySocket.fileName = fileName;
                MySocket.objectOutputStream.writeObject("REQUEST FOR DOWNLOADFILE:" +MySocket.fileName);
            }
            intent.setAction(MySocket.newChatMessage);
            sendBroadcast(intent);
        }
        //If new Group is created in which current user is a member
        else if(response.toString().startsWith(MySocket.groupCreateRequest) && response.toString().contains(MySocket.loginName)){
            MySocket.myGroups.add(response.toString().split(":")[1]);
            intent.setAction(MySocket.groupCreateRequest);
            sendBroadcast(intent);
        }
        //If message is a groupMessage
        else if(response.toString().contains("Group-")){
            String[] message = response.toString().split(":");
            String senderName = message[0];
            String receiverName = message[1].split(";")[1].split("-")[1];
            String content = message[1].split(";")[0];
            writeToFile(receiverName, (senderName +":" +content +";" +receiverName +"@@@").getBytes());
            intent.setAction(MySocket.newChatMessage);
            sendBroadcast(intent);
        }
        //If chat message is a simple text
        else if(response.toString().startsWith(MySocket.loginName) || response.toString().endsWith(MySocket.loginName)){
            String[] message = response.toString().split(":");
            String senderName = message[0];
            message = message[1].split(";");
            String receiverName = message[1];
            if(response.toString().startsWith(MySocket.loginName))
                writeToFile(senderName +"To" +receiverName, (response +"@@@").getBytes());
            else
                writeToFile(receiverName +"To" +senderName, (response +"@@@").getBytes());
            intent.setAction(MySocket.newChatMessage);
            sendBroadcast(intent);
        }
        //Check for UserList
        else if(response.toString().startsWith(MySocket.userListUpdateAction)){
            MySocket.userList = response.toString();
            intent.setAction(MySocket.userListUpdateAction);
            sendBroadcast(intent);
        }
    }
    private void writeToFile(String fileName, byte[] content) throws IOException {
        FileOutputStream stream = openFileOutput(fileName, Context.MODE_APPEND);
        stream.write(content);
        stream.close();
    }
    void saveFile(String fileName, byte[] content) throws IOException {
        File file = new File(fileName);
        FileOutputStream stream = new FileOutputStream(file);
        stream.write(content);
        stream.close();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
