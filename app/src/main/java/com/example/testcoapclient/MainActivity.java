package com.example.testcoapclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.coapplugin.GetPostClient;


public class MainActivity extends AppCompatActivity {

    private TextView data;
    private Button sendRequestButton;
    private EditText IPText;
    private String response;
    private String ip;
    private static final String resource="coffee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data=findViewById(R.id.data);
        sendRequestButton=findViewById(R.id.sendRequestButton);
        IPText=findViewById(R.id.ip);
        data.setText(response);

        sendRequestButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ip=IPText.getText().toString();
                response= callServer();
                data.setText(response);
            }
        });
    }

    public String callServer(){
        GetPostClient client = new GetPostClient(ip, resource);
        client.post();
        return client.get();
    }



}