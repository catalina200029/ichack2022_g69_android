package com.example.ichack;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {

    public static final String ERROR_DETECTED = "No NFC tag detected!";
    public static final String WRITE_SUCCESS = "Text written to the NFC tag successfully!";
    public static final String WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?";
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;

    Context context;

    Button backButton;
    TaskAdapter taskAdapter;
    RecyclerView taskRecyclerView;
    TextView wrong_guesses;
    TextView instructions_textview;

    Task task;

    String NFC_ID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        context = this;

//        TODO: uncomment next line
//        NFC_ID = getIntent().getStringExtra("nfc");

        boolean done = getIntent().getBooleanExtra("done", false);
        ArrayList<String> answers = getIntent().getStringArrayListExtra("answers");
        ArrayList<String> questions = getIntent().getStringArrayListExtra("questions");
        int wrong_guesses_val = getIntent().getIntExtra("wrong_guesses", -1);

        List<TaskAttribute> taskAttributes = new ArrayList<>();
        for (int i = 0; i < answers.size(); ++i) {
            taskAttributes.add(new TaskAttribute(questions.get(i), answers.get(i)));
        }

        task = new Task(taskAttributes, done, wrong_guesses_val);

        backButton = (Button) findViewById(R.id.backButton);
        taskRecyclerView = (RecyclerView) findViewById(R.id.taskRecyclerView);
        instructions_textview = (TextView) findViewById(R.id.instructions_textview);
        wrong_guesses = (TextView) findViewById(R.id.wrong_guesses);

        if (task.done) {
            instructions_textview.setText("You already finished this task!");
        }

        wrong_guesses.setText(task.wrong_guesses + " wrong guesses");

        taskAdapter = new TaskAdapter(this, task);

        taskRecyclerView.setAdapter(taskAdapter);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TasksActivity.class);
                intent.putExtra("nfc", NFC_ID);
                startActivity(intent);

            }
        });

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

    /******************************************************************************
     **********************************Read From NFC Tag***************************
     ******************************************************************************/
    private String readFromIntent(Intent intent) {
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

    public boolean isAGoodNFC(String guesses_nfc_id) {
        return false;
    }

    public void markTaskAsFinished() {
        System.out.println("DEBUG markTaskAsFinished");
    }

    public void increaseWrongGuesses() {
        System.out.println("DEBUG increaseWrongGuesses");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (task.done) return;

        String guesses_nfc_id = readFromIntent(intent);

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }

        if (isAGoodNFC(guesses_nfc_id)) {
            markTaskAsFinished();
            openDialog("Congratulations", "You finished the task!");
        } else {
            openDialog("Ops... wrong guess!!", "Try again!");
        }
    }

    public void openDialog(String title, String content) {
        Dialog dialog = new Dialog(TaskActivity.this);

        dialog.setContentView(R.layout.custom_dialog);

        TextView titleTextView = (TextView) dialog.findViewById(R.id.title);
        TextView contentTextView = (TextView) dialog.findViewById(R.id.content);

        titleTextView.setText(title);
        contentTextView.setText(content);

        dialog.show();

        return;
    }

    @Override
    public void onPause() {
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