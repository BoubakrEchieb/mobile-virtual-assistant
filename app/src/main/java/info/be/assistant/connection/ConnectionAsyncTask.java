package info.be.assistant.connection;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import info.be.assistant.MainActivity;

/**
 * Created by be on 10/08/16.
 */
public class ConnectionAsyncTask extends AsyncTask<String,String,String> {
    private Connection connection;
    private TextView textAG;
    private String source;

    private JSONObject jsonObj; //contain json response
    private String state;
    private String response="";
    private MainActivity  mainActivity;
    private ProgressBar progressBar;

    public static String URL_CONNECTION="http://172.16.7.94:8080/Serv/Login"; //server url

    public ConnectionAsyncTask(TextView textAG,String source, MainActivity mainActivity, ProgressBar progressBar){
        this.textAG = textAG;
        this.source = source;
        this.progressBar=progressBar;
        connection = new Connection();
        this.mainActivity = mainActivity;
    }
    @Override
    protected String doInBackground(String... strings) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("speech",source));
        jsonObj = connection.makeHTTPRequest(URL_CONNECTION,"POST",params);
        try {
            state = jsonObj.getString("info");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onPostExecute(String s) {
        //if there is response
        if(state.equalsIgnoreCase("success")){
            //get the response
            try {
                response = jsonObj.getString("response");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //update userInterface
            textAG.setText(response);
            progressBar.setVisibility(View.INVISIBLE);

            mainActivity.speak(response);

        }
        //if there is no response
        if(state.equalsIgnoreCase("fail")){

            //update userInterface
            textAG.setText("Yes !");
            progressBar.setVisibility(View.INVISIBLE);
            textAG.setVisibility(View.VISIBLE);

            //
            mainActivity.speak("yes ! ");
            //
        }

    }
}
