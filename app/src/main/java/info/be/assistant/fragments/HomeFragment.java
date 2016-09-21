package info.be.assistant.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import info.be.assistant.MainActivity;
import info.be.assistant.R;
import info.be.assistant.connection.ConnectionAsyncTask;

/**
 * Created by be on 17/09/16.
 */
public class HomeFragment extends Fragment{

    public int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private String recognizedText;
    private String text;
    // widgets
    private Button listenButton; //Micro Button
    private TextView agentParole;
    private ProgressBar progressBar;//proress bar
    private EditText editText;

    private ConnectionAsyncTask connectionAsyncTask = null;
    private MainActivity mainActivity;
    private boolean isFirstStart;
    private View view;
    public HomeFragment(){
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment,container, false);
        this.view = view;
        progressBar = (ProgressBar) view.findViewById(R.id.circular_progress_bar);
        progressBar.setVisibility(View.GONE);
        agentParole = (TextView) view.findViewById(R.id.agent_say_text_view);
        //text input
        editText = (EditText) view.findViewById(R.id.edit_text);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEND){
                    Snackbar.make(view, "Thinking.. ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    sendText();
                }
                return false;
            }
        });
        //voice input
        listenButton = (Button) view.findViewById(R.id.button_listen);
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startListning();
            }
        });
        mainActivity = (MainActivity) getActivity();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void startListning() {
        agentParole.setText("");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something .. ");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        getActivity().startActivityForResult(intent,VOICE_RECOGNITION_REQUEST_CODE);
        mainActivity.vibrate();
    }

    //send inut text to server
    private void sendText(){
        agentParole.setText("");
        editText.setText("");
        connectionAsyncTask = new ConnectionAsyncTask(agentParole, editText.getText().toString(), mainActivity, progressBar);
        connectionAsyncTask.execute();
        Snackbar.make(view, "Thinking.. ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

    }

    //getters & setters
    public boolean isFirstStart() {
        return isFirstStart;
    }

    public void setFirstStart(boolean firstStart) {
        isFirstStart = firstStart;
    }

    public String getRecognizedText() {
        return recognizedText;
    }

    public void setRecognizedText(String recognizedText) {
        this.recognizedText = recognizedText;
    }

    public TextView getAgentParole() {
        return agentParole;
    }

    public void setAgentParole(TextView agentParole) {
        this.agentParole = agentParole;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public ConnectionAsyncTask getConnectionAsyncTask() {
        return connectionAsyncTask;
    }

    public void setConnectionAsyncTask(ConnectionAsyncTask connectionAsyncTask) {
        this.connectionAsyncTask = connectionAsyncTask;
    }
}
