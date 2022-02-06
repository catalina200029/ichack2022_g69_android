package com.example.ichack;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {


    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;
    Context context;
    QuestionsAdapter questionsAdapter;

    TextView tvNFCContent;
    TextView messageTextview;
    ViewPager questionsViewPager;
    LinearLayout questionsFrameLayout;
    ImageButton leftNav;
    ImageButton rightNav;
    Button submitButton;
    Button backButton;

    ArrayList<String> questions;
    String NFC_ID;

    String url = "https://ichack22-backend.herokuapp.com";
    RequestQueue reqQueue;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        reqQueue = Volley.newRequestQueue(context);

        startActivity(new Intent(context, TasksActivity.class));

        initNFC();
        initQuestions();
    }

    private void initNFC(){
        messageTextview = (TextView) findViewById(R.id.message_textview);
        tvNFCContent = (TextView) findViewById(R.id.nfc_contents);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
        }

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };

    }

    private void showNFC(){
        tvNFCContent.setText(NFC_ID);
        tvNFCContent.setVisibility(View.VISIBLE);
        messageTextview.setText("Your ID is");
    }

    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {}
        @Override
        public void afterTextChanged(Editable s) {
            EditText[] editTexts = questionsAdapter.getEditTexts();
            boolean allQuestionsCompleted = true;

            for (int i = 0; i < questionsAdapter.getCount(); ++i) {
                if (editTexts[i].getText() == null || editTexts[i].getText().toString().trim().length() == 0) {
                    allQuestionsCompleted = false;
                    break;
                }
            }

            submitButton.setEnabled(allQuestionsCompleted);
        }
    };

    private void initQuestions(){
        questionsViewPager = (ViewPager) findViewById(R.id.questionViewPager);
        questionsFrameLayout = (LinearLayout) findViewById(R.id.questionsFrameLayout);
        questionsFrameLayout.setVisibility(View.GONE);

        leftNav = (ImageButton) findViewById(R.id.left_nav);
        leftNav.setVisibility(View.INVISIBLE);
        rightNav = (ImageButton) findViewById(R.id.right_nav);

        submitButton = (Button) findViewById(R.id.submitButton);
        backButton = (Button) findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText[] editTexts = questionsAdapter.getEditTexts();
                JSONArray answers = new JSONArray();
                for (int i = 0; i < questionsAdapter.getCount(); ++i){
                    answers.put(editTexts[i].getText().toString());
                }
                JSONObject message = new JSONObject();
                try {
                    message.put("uid", NFC_ID);
                    message.put("answers", answers);
                    System.out.println(message.toString());
                } catch (JSONException e) {
                    Toast.makeText(context, "Failed to process JSON", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                JsonObjectRequest answersRequest = new JsonObjectRequest(Request.Method.POST, url + "/game/register_player/", message,
                        response -> {
                            try{
                                System.out.println("HELLO " + response.toString() + " ## " + response.getString("uid"));
                            } catch(JSONException e){
                                e.printStackTrace();
                            }
                            Toast.makeText(context, "Yaaaay", Toast.LENGTH_SHORT).show();
                            tryLogin();
                            },

                        error -> {error.printStackTrace();Toast.makeText(context, "Error connecting to server", Toast.LENGTH_SHORT).show();});
                answersRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 5, 1.0f));
                reqQueue.add(answersRequest);
            }
        });

        leftNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = questionsViewPager.getCurrentItem();

                if (tab > 0) {
                    --tab;
                }

                questionsViewPager.setCurrentItem(tab);
            }
        });

        // Images right navigating
        rightNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = questionsViewPager.getCurrentItem();

                if (tab < questionsAdapter.getCount() - 1) {
                    ++tab;
                }

                questionsViewPager.setCurrentItem(tab);
            }
        });
    }

    private void processQuestions(JSONObject response){
        try {
            JSONArray questionArray = response.getJSONArray("questions");

            for (int i = 0; i < questionArray.length(); i++)
                questions.add(questionArray.getString(i));
        } catch(JSONException e){
            Toast.makeText(context, "Failed to process JSON", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void loadQuestions(){
        System.out.println("Loading questions");
        questions = new ArrayList<>();

        JsonObjectRequest questionRequest = new JsonObjectRequest(Request.Method.GET, url + "/game/get_questions/", null,
                response -> {processQuestions(response); showQuestions();},
                error -> {Toast.makeText(context, "Error connecting to server", Toast.LENGTH_SHORT).show();});
        questionRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 5, 1.0f));
        reqQueue.add(questionRequest);
    }

    private void showQuestions(){
        questionsAdapter = new QuestionsAdapter(this, questions, watcher);
        questionsViewPager.setOffscreenPageLimit(questions.size());
        questionsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int tab = questionsViewPager.getCurrentItem();

                if (tab == 0) {
                    leftNav.setVisibility(View.INVISIBLE);
                } else {
                    leftNav.setVisibility(View.VISIBLE);
                }

                if (tab == questionsAdapter.getCount() - 1) {
                    rightNav.setVisibility(View.INVISIBLE);
                } else {
                    rightNav.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        questionsViewPager.setAdapter(questionsAdapter);

        questionsFrameLayout.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);
    }

    private void gotoTasks(String login_json){
        Intent taskIntent = new Intent(context, TasksActivity.class);
        taskIntent.putExtra("nfc", NFC_ID);
        taskIntent.putExtra("json_data", login_json);
        startActivity(taskIntent);
    }


    private void goWait(){
        Intent waitIntent = new Intent(context, TasksActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("uid", NFC_ID);
        waitIntent.putExtras(bundle);
        startActivity(waitIntent);
    }

    private void tryLogin(){
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
                            goWait();
                        }
                    } catch(JSONException e){
                        e.printStackTrace();
                    }

                },
                error -> {
                    if (error.networkResponse.statusCode == 500){
                        loadQuestions();
                    }
                    else{
                        Toast.makeText(context, "Error connecting to server", Toast.LENGTH_SHORT).show();
                    }});
        reqQueue.add(loginRequest);
    }

    /******************************************************************************
     **********************************Read From NFC Tag***************************
     ******************************************************************************/
    private String readFromIntent(Intent intent) {
        //startActivity(new Intent(context, TasksActivity.class));
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            return buildTagViews(msgs);
        }
        return null;
    }

    private String buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return null;

        String text = "";
//        String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
        int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

        try {
            // Get the Text
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", e.toString());
        }

        return text;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        NFC_ID = readFromIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
        showNFC();
        tryLogin();
    }


    @Override
    public void onPause(){
        super.onPause();
        WriteModeOff();
    }

    @Override
    public void onResume(){
        super.onResume();
        WriteModeOn();
    }

    /******************************************************************************
     **********************************Enable Write********************************
     ******************************************************************************/
    private void WriteModeOn(){
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }
    /******************************************************************************
     **********************************Disable Write*******************************
     ******************************************************************************/
    private void WriteModeOff(){
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }
}