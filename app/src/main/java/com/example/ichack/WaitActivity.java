package com.example.ichack;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class WaitActivity extends AppCompatActivity {

    Context context;
    String NFC_ID;
    Button refreshButton;
    Button nextButton;
    RequestQueue reqQueue;
    String url = "https://ichack22-backend.herokuapp.com";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        context = this;
        reqQueue = Volley.newRequestQueue(context);

        NFC_ID = getIntent().getExtras().getString("uid");

        refreshButton = (Button) findViewById(R.id.refreshButton);
        nextButton = (Button) findViewById(R.id.nextButton);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    System.out.println("Trying log in");
                    JSONObject id = new JSONObject();
                    try {
                        id.put("uid", NFC_ID);
                    } catch(JSONException e){
                        e.printStackTrace();
                    }
                    JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, url + "/game/login_player/", id,
                            response -> {
                                try {
                                    if (response.getBoolean("game_started")){
                                        gotoTasks(response.toString());
                                    }
                                    else{
                                        Toast.makeText(context, "Game has not started yet", Toast.LENGTH_LONG).show();
                                    }
                                } catch(JSONException e){
                                    e.printStackTrace();
                                    Toast.makeText(context, "Game has not started yet", Toast.LENGTH_LONG).show();
                                }

                            },
                            error -> {
                                    Toast.makeText(context, "Game has not started yet", Toast.LENGTH_LONG).show();});
                    reqQueue.add(loginRequest);
                }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("HELLOO WAIT");
                Intent taskIntent = new Intent(context, TasksActivity.class);
                taskIntent.putExtra("nfc", "NFC_ID");
                taskIntent.putExtra("json_data", "login_json");
                startActivity(taskIntent);
            }
        });
    }
    private void gotoTasks(String login_json){
        System.out.println("HELLOO WAIT");
        Intent taskIntent = new Intent(context, TasksActivity.class);
        taskIntent.putExtra("nfc", NFC_ID);
        taskIntent.putExtra("json_data", login_json);
        startActivity(taskIntent);
    }
}