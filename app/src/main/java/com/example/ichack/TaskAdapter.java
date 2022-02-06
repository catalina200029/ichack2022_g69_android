package com.example.ichack;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>  {

    // Context object
    Context context;

    // Layout Inflater
    LayoutInflater mLayoutInflater;

    Task task;

    // Viewpager Constructor
    public TaskAdapter(Context context, Task task) {
        this.context = context;
        this.task = task;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View taskView = inflater.inflate(R.layout.task_attribute, parent, false);

//        taskView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(taskView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.taskQuestion.setText(task.taskAttributes.get(position).question);
        holder.taskAnswer.setText(task.taskAttributes.get(position).answer);
    }

    @Override
    public int getItemCount() {
        return task.taskAttributes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView taskQuestion;
        public TextView taskAnswer;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            taskQuestion = (TextView) itemView.findViewById(R.id.taskQuestion);
            taskAnswer = (TextView) itemView.findViewById(R.id.taskAnswer);
        }
    }
}
