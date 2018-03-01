package com.example.monishakram.entertainmentspot;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class LoginActivity extends AppCompatActivity {
    EditText editTextUserName, editTextPassword;
    Button buttonLogin;
    ProgressBar progressBar;
    String userName, password;
    TextView textView;
    private Intent serviceIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUserName = findViewById(R.id.editTextUserName);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.textViewWrongCredential);
        serviceIntent = new Intent(getApplicationContext(), ServerListenerService.class);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = editTextUserName.getText().toString();
                password = editTextPassword.getText().toString();
                if(userName.equals("") || password.equals(""))
                    Toast.makeText(LoginActivity.this, "UserName and Password cannot be blank", Toast.LENGTH_SHORT).show();
                else{
                    new LoginTask().execute();
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    class LoginTask extends AsyncTask<Object, String, Object>{
        String response, userList;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            boolean running = true;
            while(running) {
                try {
                    Thread.sleep(1000);
                    if (MySocket.socket != null) {
                        if (MySocket.objectOutputStream == null) {
                            MySocket.objectOutputStream = new ObjectOutputStream(MySocket.socket.getOutputStream());
                            MySocket.objectOutputStream.flush();
                        }
                        if (MySocket.objectInputStream == null)
                            MySocket.objectInputStream = new ObjectInputStream(MySocket.socket.getInputStream());
                        String request = "LOGIN: " + userName + "," + password;
                        MySocket.objectOutputStream.writeObject(request);
                        Log.i("LoginActivity", "Request: " + request);
                        MySocket.objectOutputStream.flush();
                        response = (String) MySocket.objectInputStream.readObject();
                        Log.i("LoginActivity", "Response: " + response);
                        if (response.equals(userName)) {
                            userList = (String) MySocket.objectInputStream.readObject();
                            Log.i("TAG", "Initial user List: " +userList);
                            MySocket.userList = userList;
                            running = false;
                        }
                        else
                            publishProgress();
                        Log.i("LoginActivity", "Response: " + response);
                    }
                } catch (IOException | InterruptedException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.GONE);
            if(response == null || !response.equals(userName)){
                textView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.GONE);
            if(response == null || !response.equals(userName)){
                textView.setVisibility(View.VISIBLE);
            }
            else {
                MySocket.loginName = userName;
                Log.i("LoginActivity", "Starting new Service");
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                startService(serviceIntent);
                this.cancel(true);
            }
        }

    }
    @Override
    public void finish() {
        Log.i("TAG", "Finish Called");
        try {
            if(MySocket.objectOutputStream != null) {
                MySocket.objectOutputStream.writeObject("exit@rr");
                MySocket.objectOutputStream.close();
                if(MySocket.objectOutputStream != null)
                    MySocket.objectInputStream.close();
                if(MySocket.socket != null)
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
