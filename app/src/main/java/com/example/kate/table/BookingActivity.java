package com.example.kate.table;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLOutput;

/**
 * Created by kate on 28/02/2017.
 */

public class BookingActivity extends Activity{

    private TextView mSent;
    private Button mSubmit;
    private EditText mName;
    private EditText mEmail;
    public static final String EXTRA_TIME_SLOT = "time slot";
    private String mTimeSlot;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        setContentView(R.layout.activity_booking);

        mSent = (TextView) findViewById(R.id.sent);
        mSubmit = (Button) findViewById(R.id.submit);
        mName   = (EditText)findViewById(R.id.editTextName);
        mEmail   = (EditText)findViewById(R.id.editTextEmail);
        mTimeSlot = getIntent().getStringExtra(EXTRA_TIME_SLOT);


    }

    public void submitRecord(View view){
        new PostDataTask().execute("http://192.168.0.17:3000/api/booking");
    }

    class PostDataTask extends AsyncTask<String, Void, String>{


        ProgressDialog mProgressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(BookingActivity.this);
            mProgressDialog.setMessage("Loading data...");
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return postData(params[0]);
            } catch (IOException e){
                return "Network error!";
            } catch (JSONException js){
                return "Data invalid";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mSent.setText(result);

            //remove progress dialog
            if(mProgressDialog != null){
                mProgressDialog.dismiss();
            }
            Toast.makeText(BookingActivity.this, R.string.confirm_booking, Toast.LENGTH_SHORT).show();
        }

        private String postData(String urlPath) throws IOException, JSONException{

            StringBuilder result = new StringBuilder();
            BufferedWriter bufferedWriter = null;
            BufferedReader bufferedReader = null;

            try {
            //create data
                JSONObject dataToSend = new JSONObject();
                dataToSend.put("name", BookingActivity.this.mName.getText().toString());
                dataToSend.put("email", BookingActivity.this.mEmail.getText().toString());
                dataToSend.put("tag", BookingActivity.this.mTimeSlot);
                System.out.println(dataToSend);

                //initialise request + connect to server
                URL url = new URL(urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true); //enable body data
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                //write data into server
                OutputStream outputStream = urlConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                bufferedWriter.write(dataToSend.toString());
                bufferedWriter.flush();

                //read the response from server
                InputStream inputStream = urlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line).append("\n");
                }
            } finally {
                if (bufferedReader != null){
                    bufferedReader.close();
                }
                if (bufferedWriter != null){
                    bufferedWriter.close();
                }
            }

            return result.toString();
        }
    }

}
