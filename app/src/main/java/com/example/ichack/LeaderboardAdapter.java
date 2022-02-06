package com.example.ichack;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>  {

    // Context object
    Context context;

    // Layout Inflater
    LayoutInflater mLayoutInflater;

    String[] players;
    int[] points;

    String nfc;

    // Viewpager Constructor
    public LeaderboardAdapter(Context context, String nfc, String[] players, int[] points) {
        this.context = context;
        this.players = players;
        this.points = points;
        this.nfc = nfc;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View taskView = inflater.inflate(R.layout.leaderboard_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(taskView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.positionTextView.setText(Integer.toString(position + 1));
        holder.personTextView.setText(players[position]);
        holder.pointsTextView.setText(Integer.toString(points[position]));
    }

    @Override
    public int getItemCount() {
        return players.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView positionTextView;
        public TextView personTextView;
        public TextView pointsTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            positionTextView = (TextView) itemView.findViewById(R.id.position);
            personTextView = (TextView) itemView.findViewById(R.id.person);
            pointsTextView = (TextView) itemView.findViewById(R.id.points);
        }
    }
}
