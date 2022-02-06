package com.example.ichack;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Task {
    transient List<TaskAttribute> taskAttributes;
    transient boolean done;
    int wrong_guesses;

    public Task(List<TaskAttribute> taskAttributes, boolean done, int wrong_guesses) {
        this.taskAttributes = taskAttributes;
        this.done = done;
        this.wrong_guesses = wrong_guesses;
    }

    public List<TaskAttribute> getTaskAttributes() {
        return taskAttributes;
    }

    public void setTaskAttributes(List<TaskAttribute> taskAttributes) {
        this.taskAttributes = taskAttributes;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
