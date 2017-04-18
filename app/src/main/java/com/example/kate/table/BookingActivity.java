package com.example.kate.table;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kate on 28/02/2017.
 */

public class BookingActivity extends Activity{

//    public static final String eduroam = "10.173.19.96";
    private Button mSubmit;
    private EditText mName;
    private EditText mNotes;
    private EditText mTitle;
    private EditText mEmail;
    private EditText mPassword;
    public static final String EXTRA_TIME_SLOT = "time slot";
    private String mTimeSlot;
    private static int workload = 12;

    private SoundPool mSoundPool;
    int badID;
    int successID;
    int clickID;


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

        mSubmit = (Button) findViewById(R.id.submit);
        mName   = (EditText)findViewById(R.id.editTextName);
        mTitle   = (EditText)findViewById(R.id.editTextTitle);
        mEmail   = (EditText)findViewById(R.id.editTextEmail);
        mNotes   = (EditText)findViewById(R.id.editTextNotes);
        mPassword   = (EditText)findViewById(R.id.editTextPassword);
        mTimeSlot = getIntent().getStringExtra(EXTRA_TIME_SLOT);

        mSoundPool = new SoundPool.Builder().build();
        badID = mSoundPool.load(this, R.raw.bad,1);
        successID = mSoundPool.load(this, R.raw.good,1);
        clickID = mSoundPool.load(this, R.raw.click,1);

    }

    public void submitRecord(View view){
        mSoundPool.play(clickID,1,1,1,0,1);
        if(BookingActivity.this.mName.getText().toString().matches("")||BookingActivity.this.mTitle.getText().toString().matches("")||BookingActivity.this.mPassword.getText().toString().matches("")) {
            Toast.makeText(BookingActivity.this, "Please complete Name, Event Title and Booking code", Toast.LENGTH_SHORT).show();
        }else if(BookingActivity.this.mPassword.getText().toString().length()<4||!BookingActivity.this.mPassword.getText().toString().matches("\\d+?")){
            Toast.makeText(BookingActivity.this, "Booking code must have at least 4 digits", Toast.LENGTH_SHORT).show();
        } else {
            new PostDataTask().execute("http://" + TableActivity.currentIP + ":3000/api/booking");
        }
    }

    public void neverMind(View view){
        mSoundPool.play(clickID,1,1,1,0,1);
        Intent i = new Intent(BookingActivity.this, TableActivity.class);
        startActivity(i);
    }

    public static String hashPassword(String password_plaintext) {
        String salt = BCrypt.gensalt(workload);
        String hashed_password = BCrypt.hashpw(password_plaintext, salt);

        return(hashed_password);
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
            Intent i = new Intent(BookingActivity.this, TableActivity.class);
            startActivity(i);

            //remove progress dialog
            if(mProgressDialog != null){
                mProgressDialog.dismiss();
            }
            mSoundPool.play(successID,1,1,1,0,1);
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
                    dataToSend.put("Act_title", BookingActivity.this.mTitle.getText().toString());
                    dataToSend.put("email", BookingActivity.this.mEmail.getText().toString());
                    dataToSend.put("Activity", BookingActivity.this.mNotes.getText().toString());
                    dataToSend.put("password", hashPassword(BookingActivity.this.mPassword.getText().toString()));
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
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    if (bufferedWriter != null) {
                        bufferedWriter.close();
                    }
                }


            return result.toString();
        }
    }

}
