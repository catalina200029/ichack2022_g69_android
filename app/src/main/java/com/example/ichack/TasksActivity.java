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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TasksActivity extends AppCompatActivity {
    Context context;

    TextView tvNFCContent;
    TasksAdapter tasksAdapter;
    RecyclerView tasksRecyclerView;

    List<TaskAttribute> taskAttributeList1 = new ArrayList<>();
    List<TaskAttribute> taskAttributeList2 = new ArrayList<>();
    List<TaskAttribute> taskAttributeList3 = new ArrayList<>();

    Task[] tasks = {
            new Task(taskAttributeList1, true, 3),
            new Task(taskAttributeList2, false, 77),
            new Task(taskAttributeList3, true, 100)
    };

    String NFC_ID;

    Button leaderboardButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        context = this;

//        TODO: uncomment next line
//        NFC_ID = getIntent().getStringExtra("nfc");

        taskAttributeList1.add(new TaskAttribute("q1.1", "a1.1"));
        taskAttributeList1.add(new TaskAttribute("q1.2", "a1.2"));
        taskAttributeList1.add(new TaskAttribute("q1.3", "a1.3"));

        taskAttributeList2.add(new TaskAttribute("q2.1", "a1.1"));
        taskAttributeList2.add(new TaskAttribute("q2.2", "a2.2"));
        taskAttributeList2.add(new TaskAttribute("q2.3", "a2.3"));

        taskAttributeList3.add(new TaskAttribute("q3.1", "a3.1"));
        taskAttributeList3.add(new TaskAttribute("q3.2", "a3.2"));
        taskAttributeList3.add(new TaskAttribute("q3.3", "a3.3"));

        tvNFCContent = (TextView) findViewById(R.id.nfc_contents);
        leaderboardButton = (Button) findViewById(R.id.leaderboardButton);

        tasksRecyclerView = (RecyclerView) findViewById(R.id.tasksRecyclerView);
        tasksAdapter = new TasksAdapter(this, tasks, NFC_ID);

        tasksRecyclerView.setAdapter(tasksAdapter);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        leaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LeaderboardActivity.class);
                intent.putExtra("nfc", NFC_ID);
                startActivity(intent);
            }
        });
    }

}