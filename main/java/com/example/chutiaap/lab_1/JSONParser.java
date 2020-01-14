package com.example.chutiaap.lab_1;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Rehan Rajput on 23/04/2018.
 */

public class JSONParser extends AsyncTask<String,Void,String> {
    private TextView[] textViews;
    public JSONParser(TextView[] textViews){
        this.textViews = new TextView[5];
        this.textViews[0] = textViews[0];
        this.textViews[1] = textViews[1];
        this.textViews[2] = textViews[2];
        this.textViews[3] = textViews[3];
        this.textViews[4] = textViews[4];
    }
    @Override
    protected String doInBackground(String... strings) {
        String data = "";
        String isbn = strings[0];

        try {
            URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream input_stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input_stream));
            String line = "";
            while(line != null){
                line = reader.readLine();
                data = data + line;
            }
            reader.close();
            input_stream.close();
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        JSONObject obj = null;
        try {
            obj = new JSONObject(s);
            obj = obj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo");
            this.textViews[1].setText(obj.getJSONArray("authors").getString(0));
            this.textViews[0].setText(obj.getString("title"));
            this.textViews[2].setText(obj.getString("publisher"));
            this.textViews[3].setText(obj.getString("publishedDate"));
            this.textViews[4].setText(obj.getString("description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }
}
