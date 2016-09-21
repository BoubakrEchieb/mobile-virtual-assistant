package info.be.assistant.connection;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by be on 10/08/16.
 */
public class Connection {
    private InputStream inputStream;
    private JSONObject jsonObject;
    private String json="";

    //constructor
    public Connection(){

    }
    //make httpGet or httpPost request
    public JSONObject makeHTTPRequest(String url, String methode, List<NameValuePair> params){

        try {
            //make post request
            if(methode.equalsIgnoreCase("POST")){

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);
                post.setEntity(new UrlEncodedFormEntity(params));
                //recevoire la réponse et la mettre dans un inputStream
                HttpResponse response = httpClient.execute(post);
                HttpEntity entity = response.getEntity();
                inputStream=entity.getContent();

            }
            //make get Request
            if(methode.equalsIgnoreCase("GET")){

                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramsString = URLEncodedUtils.format(params,"UTF-8");
                url+="?"+paramsString;
                HttpGet get = new HttpGet(url);
                HttpResponse response = httpClient.execute(get);
                HttpEntity entity = response.getEntity();
                inputStream=entity.getContent();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line=reader.readLine())!=null){
                sb.append(line + "\n");
            }
            inputStream.close();
            reader.close();
            json = sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //convert the string obtained to a jsonObject
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
