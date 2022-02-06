package com.example.ichack;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.Objects;

class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder>  {

    // Context object
    Context context;

    // Layout Inflater
    LayoutInflater mLayoutInflater;

    Task[] tasks;
    String nfc;

    // Viewpager Constructor
    public TasksAdapter(Context context, Task[] tasks, String nfc) {
        this.context = context;
        this.tasks = tasks;
        this.nfc = nfc;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View taskView = inflater.inflate(R.layout.task, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(taskView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = tasks[position];
        holder.taskName.setText("Task " + position);

        if (task.done) {
            holder.taskName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0);
        } else {
            holder.taskName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_close_24, 0, 0, 0);
        }

        holder.taskName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TaskActivity.class);
                intent.putExtra("done", task.done);
                intent.putExtra("nfc", nfc);

                ArrayList<String> answers = new ArrayList<>();
                ArrayList<String> questions = new ArrayList<>();
                int wrong_guesses = task.wrong_guesses;

                for (TaskAttribute taskAttribute : task.taskAttributes) {
                    answers.add(taskAttribute.answer);
                    questions.add(taskAttribute.question);
                }
                intent.putStringArrayListExtra("answers", answers);
                intent.putStringArrayListExtra("questions", questions);
                intent.putExtra("wrong_guesses", wrong_guesses);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView taskName;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            taskName = (TextView) itemView.findViewById(R.id.taskName);
        }
    }
}
