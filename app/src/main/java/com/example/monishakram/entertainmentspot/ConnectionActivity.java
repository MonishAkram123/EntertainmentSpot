package com.example.monishakram.entertainmentspot;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;

public class ConnectionActivity extends AppCompatActivity {

    Button btnConnect;
    EditText editTextIP, editTextPort;
    MyConnectionTask myConnectionTask;
    ProgressBar progressBar;
    int x = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        btnConnect = findViewById(R.id.buttonConnect);
        editTextIP = findViewById(R.id.editTextIP);
        editTextPort = findViewById(R.id.editTextPort);
        progressBar = findViewById(R.id.progressBar);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x = btnConnect.getId();
                MySocket.port = editTextPort.getText().toString();
                if(MySocket.port.equals(""))
                    Toast.makeText(ConnectionActivity.this, "Port Number is Required", Toast.LENGTH_SHORT).show();
                else if(Integer.parseInt(MySocket.port) <= 0)
                    Toast.makeText(ConnectionActivity.this, "Invalid Port number", Toast.LENGTH_SHORT).show();
                MySocket.ip = editTextIP.getText().toString();
                if(MySocket.ip.equals(""))
                    Toast.makeText(ConnectionActivity.this, "IP is required", Toast.LENGTH_SHORT).show();
                else{

                    Log.i("TAG", "Connection Request to " +MySocket.ip +":" +MySocket.port);
                    myConnectionTask = new MyConnectionTask();
                    myConnectionTask.execute();
                }
            }
        });
    }
    class MyConnectionTask extends AsyncTask{

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try{
                Thread.sleep(1000);
                MySocket.socket = new Socket(MySocket.ip, Integer.parseInt(MySocket.port));
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if(MySocket.socket == null)
                Toast.makeText(ConnectionActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
            else if(MySocket.socket.isConnected()){
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        }
    }
    @Override
    public void finish() {
        Log.i("TAG", "Finish Called");
        try {
            if(MySocket.objectOutputStream != null) {
                MySocket.objectOutputStream.writeObject("exit@rr");
                MySocket.socket.close();
                MySocket.socket = null;
                MySocket.objectInputStream = null;
                MySocket.objectOutputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.finish();
    }

}
