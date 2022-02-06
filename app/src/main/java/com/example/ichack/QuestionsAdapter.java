package com.example.ichack;

import android.content.Context;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import java.util.Objects;

class QuestionsAdapter extends PagerAdapter {

    // Context object
    Context context;

    // Array of images
    String[] questions;

    // Layout Inflater
    LayoutInflater mLayoutInflater;
    EditText[] editTexts;
    TextWatcher watcher;


    // Viewpager Constructor
    public QuestionsAdapter(Context context, String[] questions, TextWatcher watcher) {
        this.context = context;
        this.questions = questions;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        editTexts = new EditText[getCount()];
        this.watcher = watcher;
    }

    public EditText[] getEditTexts() {
        return editTexts;
    }

    @Override
    public int getCount() {
        // return the number of images
        return questions.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        // inflating the item.xml
        View itemView = mLayoutInflater.inflate(R.layout.question, container, false);

        // referencing the image view from the item.xml file
        TextView questionNumberTextView = (TextView) itemView.findViewById(R.id.questionNumberTextView);
        TextView questionTextView = (TextView) itemView.findViewById(R.id.questionTextView);
        EditText editText = (EditText) itemView.findViewById(R.id.editText);
        editTexts[position] = editText;
        editText.addTextChangedListener(watcher);

        // setting the text in the textView
        questionNumberTextView.setText("Question " + (position + 1) + "/" + getCount());
        questionTextView.setText(questions[position]);

        // Adding the View
        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
