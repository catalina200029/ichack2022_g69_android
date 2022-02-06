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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TasksActivity extends AppCompatActivity {
    Context context;

    TextView tvNFCContent;
    TasksAdapter tasksAdapter;
    RecyclerView tasksRecyclerView;

    String url = "https://ichack22-backend.herokuapp.com";
    RequestQueue reqQueue;

    List<TaskAttribute> taskAttributeList1 = new ArrayList<>();
    List<TaskAttribute> taskAttributeList2 = new ArrayList<>();
    List<TaskAttribute> taskAttributeList3 = new ArrayList<>();

    Task[] tasks = {
            new Task(taskAttributeList1, true, 3),
            new Task(taskAttributeList2, false, 77),
            new Task(taskAttributeList3, true, 100)
    };

    // TODO: remove init
    String NFC_ID = "350UDE8RTQ";
    String json_data;
    JSONObject json;

    Button leaderboardButton;
    TextView pointsTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        context = this;

        reqQueue = Volley.newRequestQueue(context);

        NFC_ID = getIntent().getStringExtra("nfc");
        NFC_ID = "qwert12345";
//        json_data = getIntent().getStringExtra("json_data");

//        try {
//            json = new JSONObject(json_data);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            System.out.println(e.getMessage());
//        }

        taskAttributeList1.add(new TaskAttribute(NFC_ID, "a1.1"));
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
        pointsTextView = (TextView) findViewById(R.id.total_points);

        tasksRecyclerView = (RecyclerView) findViewById(R.id.tasksRecyclerView);

//        loadPoints();

//        loadTasks();
//        showTasks();

        leaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LeaderboardActivity.class);
                intent.putExtra("nfc", NFC_ID);
//                intent.putExtra("json_data", json_data);
                startActivity(intent);
            }
        });
    }

    private void loadTasks() {
        System.out.println("Loading tasks");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", NFC_ID);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        JsonObjectRequest taskRequest = new JsonObjectRequest(Request.Method.GET, url + "/game/get_tasks/", jsonObject,
                response -> {processTasks(response); showTasks();},
                error -> {Toast.makeText(context, "Error connecting to server", Toast.LENGTH_SHORT).show();});
        taskRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 5, 1.0f));
        reqQueue.add(taskRequest);
    }

    public void showTasks() {
        tasksAdapter = new TasksAdapter(this, tasks, NFC_ID);

        tasksRecyclerView.setAdapter(tasksAdapter);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void processTasks(JSONObject response) {
        try {
            JSONArray questionArray = response.getJSONArray("target");

            tasks = new Task[questionArray.length()];

            for (int i = 0; i < questionArray.length(); i++) {
                // TODO
//                tasks[i] = questionArray.get(i);
            }
        } catch(JSONException e){
            Toast.makeText(context, "Failed to process JSON", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }



    private void loadPoints() {
        System.out.println("Loading points");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", NFC_ID);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        JsonObjectRequest taskRequest = new JsonObjectRequest(Request.Method.GET, url + "/game/user_score/", jsonObject,
                response -> {processTasks(response); showPoints();},
                error -> {Toast.makeText(context, "Error connecting to server", Toast.LENGTH_SHORT).show();});
        taskRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 5, 1.0f));
        reqQueue.add(taskRequest);
    }

    public void showPoints() {

    }

    private void processPoints(JSONObject response) {
        try {
            int score = response.getInt("score");

            pointsTextView.setText(score);
        } catch (JSONException e) {
            Toast.makeText(context, "Failed to process JSON", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}