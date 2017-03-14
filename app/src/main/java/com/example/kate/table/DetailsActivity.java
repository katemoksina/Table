package com.example.kate.table;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;

/**
 * Created by kate on 28/02/2017.
 */

public class DetailsActivity  extends AppCompatActivity {

    private TextView mResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        setContentView(R.layout.activity_details);

        mResult = (TextView) findViewById(R.id.result);

        //make GET request
        new GetDataTask().execute("http://192.168.0.17:3000/api/booking");
    }

    public void gotoTable(View view){
        Intent i = new Intent(DetailsActivity.this, TableActivity.class);
        startActivity(i);
    }
    public void makeBooking(View view){
        Intent i = new Intent(DetailsActivity.this, BookingActivity.class);
        startActivity(i);
    }

    class GetDataTask extends AsyncTask<String, Void, String>{
        ProgressDialog mProgressDialog;


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            mProgressDialog = new ProgressDialog(DetailsActivity.this);
            mProgressDialog.setMessage("Loading data...");
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return getData(params[0]);
            } catch (IOException e){
                return "network error!";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //set data response to textView
            mResult.setText(result);

            //remove progrss dialog
            if(mProgressDialog != null){
                mProgressDialog.dismiss();
            }

        }

        private String getData(String urlPath) throws IOException {
            StringBuilder result = new StringBuilder();
            BufferedReader bufferedReader = null;

            try {
                //connecting to server
                URL url = new URL(urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                //reading data
                InputStream inputStream = urlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = bufferedReader.readLine()) != null) {
                    result.append(line).append("\n");
                }

            } finally {
                if (bufferedReader != null)
                    bufferedReader.close();
            }
            return result.toString();
        }

    }
}
