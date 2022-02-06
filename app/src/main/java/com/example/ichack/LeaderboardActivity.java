package com.example.ichack;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ichack.databinding.ActivityLeaderboardBinding;

public class LeaderboardActivity extends AppCompatActivity {
    Context context;

    RecyclerView recyclerView;
    LeaderboardAdapter leaderboardAdapter;

    String[] players;
    int[] points;
    String NFC_ID = "350UDE8RTQ";

    TextView positionTextView;
    TextView personTextView;
    TextView pointsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        context = this;

        players = new String[6];
        players[0] = "ABC0";
        players[1] = "ABC1";
        players[2] = "ABC2";
        players[3] = "350UDE8RTQ";
        players[4] = "ABC4";
        players[5] = "ABC5";

        points = new int[6];
        points[0] = 60;
        points[1] = 50;
        points[2] = 40;
        points[3] = 30;
        points[4] = 20;
        points[5] = 10;

        recyclerView = (RecyclerView) findViewById(R.id.leaderboardRecycleView);

        positionTextView = (TextView) findViewById(R.id.position);
        personTextView = (TextView) findViewById(R.id.person);
        pointsTextView = (TextView) findViewById(R.id.points);

        int position = getPlayerPosition(NFC_ID);

        positionTextView.setText(Integer.toString(position + 1));
        personTextView.setText(players[position] + " - YOU");
        pointsTextView.setText(Integer.toString(points[position]));


        leaderboardAdapter = new LeaderboardAdapter(this, NFC_ID, players, points);

        recyclerView.setAdapter(leaderboardAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public int getPlayerPosition(String nfc_id) {
        for (int i = 0; i < players.length; ++i) {
            if (players[i] == nfc_id) {
                return i;
            }
        }
        return -1;
    }
}