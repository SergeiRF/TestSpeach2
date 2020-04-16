package com.anas.voicerecognitioncalculator;

import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener , RecognitionListener {

    private final String TAG = "SER-TAG";
    TextView firstNumTextView;
    TextView secondNumTextView;
    TextView operatorTextView;
    TextView resultTextView;
    Button goButton;

    TextToSpeech textToSpeech;

    private int FIRST_NUMBER;
    private int SECOND_NUMBER;
    private char OPERATOR;
    private int RESULT;

    private SpeechRecognizer speech;
    private Intent mSpeechRecognizerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textToSpeech = new TextToSpeech(this, this);

        firstNumTextView = findViewById(R.id.firstNumTextView);
        secondNumTextView = findViewById(R.id.secondNumTextView);
        operatorTextView = findViewById(R.id.operatorTextView);
        resultTextView = findViewById(R.id.resultTextView);
        goButton = findViewById(R.id.goButton);

        firstNumTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "he");
                intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{"he"});
                startActivityForResult(intent, 10);
            }
        });

        secondNumTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
                startActivityForResult(intent, 20);
            }
        });

        operatorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
                startActivityForResult(intent, 30);
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RESULT = performCalculations();
                resultTextView.setText(String.valueOf(RESULT));
                textToSpeech.speak(String.valueOf(RESULT), TextToSpeech.QUEUE_ADD, null);
            }
        });

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "he");
        mSpeechRecognizerIntent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{"he"});
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        speech.startListening(mSpeechRecognizerIntent);
//        speech = SpeechRecognizer.createSpeechRecognizer(this);
//        speech.setRecognitionListener(this);
//        restartSR();

    }
    private void restartSR() {
        Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
//                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "he");
        recognizerIntent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{"he"});
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        speech.startListening(recognizerIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case 10:
                    ArrayList<String> stringArrayListExtra = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    int intFound = getNumberFromResult(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));
//                    if (intFound != -1) {
//                        FIRST_NUMBER = intFound;
//                        firstNumTextView.setText(String.valueOf(intFound));
//                    } else {
                    for(String s : stringArrayListExtra) {
                        Toast.makeText(getApplicationContext(), "" + s, Toast.LENGTH_LONG).show();
                    }
//                    }
                    break;
                case 20:
                    int intFound = getNumberFromResult(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));
                    if (intFound != -1) {
                        SECOND_NUMBER = intFound;
                        secondNumTextView.setText(String.valueOf(intFound));
                    } else {
                        Toast.makeText(getApplicationContext(), "Sorry, I didn't catch that! Please try again", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 30:
                    char operatorFound = getOperatorFromResult(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));
                    if (operatorFound != '0') {
                        OPERATOR = operatorFound;
                        operatorTextView.setText(String.valueOf(operatorFound));
                    } else {
                        Toast.makeText(getApplicationContext(), "Sorry, I didn't catch that! Please try again", Toast.LENGTH_LONG).show();
                    }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to recognize speech!", Toast.LENGTH_LONG).show();
        }
    }

    // method to loop through results trying to find a number
    private int getNumberFromResult(ArrayList<String> results) {
        for (String str : results) {
            if (getIntNumberFromText(str) != -1) {
                return getIntNumberFromText(str);
            }
        }
        return -1;
    }

    // method to loop through results trying to find an operator
    private char getOperatorFromResult(ArrayList<String> results) {
        for (String str : results) {
            if (getCharOperatorFromText(str) != '0') {
                return getCharOperatorFromText(str);
            }
        }
        return '0';
    }

    // method to convert string number to integer
    private int getIntNumberFromText(String strNum) {
        switch (strNum) {
            case "zero":
                return 0;
            case "one":
                return 1;
            case "two":
                return 2;
            case "three":
                return 3;
            case "four":
                return 4;
            case "five":
                return 5;
            case "six":
                return 6;
            case "seven":
                return 7;
            case "eight":
                return 8;
            case "nine":
                return 9;
        }
        return -1;
    }

    // method to convert string operator to char
    private char getCharOperatorFromText(String strOper) {
        switch (strOper) {
            case "plus":
            case "add":
                return '+';
            case "minus":
            case "subtract":
                return '-';
            case "times":
            case "multiply":
                return '*';
            case "divided by":
            case "divide":
                return '/';
            case "power":
            case "raised to":
                return '^';
        }
        return '0';
    }

    private int performCalculations() {
        switch (OPERATOR) {
            case '+':
                return FIRST_NUMBER + SECOND_NUMBER;
            case '-':
                return FIRST_NUMBER - SECOND_NUMBER;
            case '*':
                return FIRST_NUMBER * SECOND_NUMBER;
            case '/':
                return FIRST_NUMBER / SECOND_NUMBER;
            case '^':
                return FIRST_NUMBER ^ SECOND_NUMBER;
        }
        return -999;
    }

    @Override
    public void onInit(int i) {

    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG,"onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG,"onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.d(TAG,"onRmsChanged");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d(TAG,"onBufferReceived");
    }


    @Override
    public void onEndOfSpeech() {
        Log.d(TAG,"onEndOfSpeech");

//        Executors.newSingleThreadScheduledExecutor().schedule(()->{
//            runOnUiThread(()->{
        speech.stopListening();
                speech.startListening(mSpeechRecognizerIntent);
//            });
//        },1, TimeUnit.SECONDS);
    }

    @Override
    public void onError(int error) {
        Log.d(TAG,"onError "+error);
//        if(error == 6)
//        if(error == 8){
//            speech.stopListening();
//            Executors.newSingleThreadScheduledExecutor().schedule(()->{
//                runOnUiThread(()->{
//                    speech.startListening(mSpeechRecognizerIntent);
//                });
//            },1, TimeUnit.SECONDS);
//            return;
//        }
//            speech.startListening(mSpeechRecognizerIntent);
    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG,"onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.d(TAG,"matches size = "+matches.size());

//        ArrayList<String> stringArrayListExtra = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    int intFound = getNumberFromResult(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));
//                    if (intFound != -1) {
//                        FIRST_NUMBER = intFound;
//                        firstNumTextView.setText(String.valueOf(intFound));
//                    } else {
        int i = 0;
        for(String s : matches) {
            i ++;
            Toast.makeText(getApplicationContext(), "" + s, Toast.LENGTH_LONG).show();
            if(i >= 1)
                break;
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d(TAG,"onPartialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d(TAG,"onEvent");
    }
}
