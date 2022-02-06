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
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    public static final String ERROR_DETECTED = "No NFC tag detected!";
    public static final String WRITE_SUCCESS = "Text written to the NFC tag successfully!";
    public static final String WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?";
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

    String[] questions = {"how s your mum", "good", ":("};
    String NFC_ID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        tvNFCContent = (TextView) findViewById(R.id.nfc_contents);
        messageTextview = (TextView) findViewById(R.id.message_textview);
        questionsViewPager = (ViewPager) findViewById(R.id.questionViewPager);
        questionsFrameLayout = (LinearLayout) findViewById(R.id.questionsFrameLayout);

        leftNav = (ImageButton) findViewById(R.id.left_nav);
        rightNav = (ImageButton) findViewById(R.id.right_nav);

        submitButton = (Button) findViewById(R.id.submitButton);

        questionsAdapter = new QuestionsAdapter(this, questions, watcher);
        questionsViewPager.setOffscreenPageLimit(questions.length);

        questionsViewPager.setAdapter(questionsAdapter);

        questionsFrameLayout.setVisibility(View.GONE);

        leftNav.setVisibility(View.INVISIBLE);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
        }

        leftNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = questionsViewPager.getCurrentItem();

                if (tab > 0) {
                    --tab;
                }

                if (tab == 0) {
                    leftNav.setVisibility(View.INVISIBLE);
                }

                rightNav.setVisibility(View.VISIBLE);

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

                if (tab == questionsAdapter.getCount() - 1) {
                    rightNav.setVisibility(View.INVISIBLE);
                }

                leftNav.setVisibility(View.VISIBLE);

                questionsViewPager.setCurrentItem(tab);
            }
        });

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
    }

    private boolean inGame(String NFC_ID) {
        return false;
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

        tvNFCContent.setText(text);

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
        tvNFCContent.setVisibility(View.VISIBLE);

        if (inGame(NFC_ID)) {
            // TODO
        } else {
            questionsFrameLayout.setVisibility(View.VISIBLE);
        }
        messageTextview.setText("Your ID is");
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