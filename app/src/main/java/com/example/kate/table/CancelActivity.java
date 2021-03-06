package com.example.kate.table;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kate on 22/03/2017.
 */

public class CancelActivity extends Activity {

    public static final String EXTRA_TIME_SLOT = "time slot";
    public static final String EXTRA_RECORD_ID = "id";
    public static final String EXTRA_PASS_HASH = "hashed password";
    private String mHash;
    private String mTimeSlot;
    private String mID;
    private EditText mPassword;
    private String mPass;
    private EditText mUsername;
    private static int workload = 12;

    private SoundPool mSoundPool;
    int badID;
    int cancelID;
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
        setContentView(R.layout.activity_cancel);

        mTimeSlot = getIntent().getStringExtra(EXTRA_TIME_SLOT);
        mID = getIntent().getStringExtra(EXTRA_RECORD_ID);
        mHash = getIntent().getStringExtra(EXTRA_PASS_HASH);
//        mUsername = (EditText) findViewById(R.id.username);
        mPassword   = (EditText)findViewById(R.id.password);

        mSoundPool = new SoundPool.Builder().build();
        badID = mSoundPool.load(this, R.raw.bad,1);
        cancelID = mSoundPool.load(this, R.raw.cancel,1);
        clickID = mSoundPool.load(this, R.raw.click,1);
    }

    public void cancelData(View view){
        mSoundPool.play(clickID,1,1,1,0,1);
        mPass = CancelActivity.this.mPassword.getText().toString();
        String s = "http://"+TableActivity.currentIP+":3000/api/booking/" + mID;
        System.out.println(s);
        if (checkPassword(mPass,mHash)) {
            mSoundPool.play(cancelID,1,1,1,0,1);
            new DeleteDataTask().execute(s);
        } else {
            mSoundPool.play(badID,1,1,1,0,1);
            Toast.makeText(CancelActivity.this, "Incorrect Booking Code!", Toast.LENGTH_SHORT).show();
    }
    }

    public void neverMind(View view){
        mSoundPool.play(clickID,1,1,1,0,1);
        Intent i = new Intent(CancelActivity.this, TableActivity.class);
        startActivity(i);
    }

    public static boolean checkPassword(String password_plaintext, String stored_hash) {
        boolean password_verified = false;

        if(null == stored_hash || !stored_hash.startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

        password_verified = BCrypt.checkpw(password_plaintext, stored_hash);

        return(password_verified);
    }

    class DeleteDataTask extends AsyncTask<String, Void, String>{

        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(CancelActivity.this);
            mProgressDialog.setMessage("Cancelling the booking...");
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
                try {
                    return deleteData(strings[0]);
                } catch (IOException ex) {
                    return "Network error";
                }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Intent i = new Intent(CancelActivity.this, TableActivity.class);
            startActivity(i);

            //remove progress dialog
            if(mProgressDialog != null){
                mProgressDialog.dismiss();
            }
            Toast.makeText(CancelActivity.this, result, Toast.LENGTH_SHORT).show();

        }

        private String deleteData(String urlPath) throws IOException {
            String result = null;

            URL url = new URL(urlPath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 204){
                result = "Successfully cancelled!";
            } else {
                result = "Cancellation failed";
            }

            return result;
        }
    }


}
