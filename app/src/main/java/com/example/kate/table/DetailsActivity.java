package com.example.kate.table;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kate on 28/02/2017.
 */

public class DetailsActivity  extends AppCompatActivity {

    private TextView mResult;
    public static final String EXTRA_TIME_SLOT = "time slot";
    private String mTimeSlot;
    String timeSlot;
    Map<String, String> result = null;
    public static final String EXTRA_RECORD_ID = "id";
    private String mID;
    public static final String EXTRA_PASS_HASH = "hashed password";
    private String mHash;

    private SoundPool mSoundPool;
    int clickID;


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

        mTimeSlot = getIntent().getStringExtra(EXTRA_TIME_SLOT);

        mSoundPool = new SoundPool.Builder().build();
        clickID = mSoundPool.load(this, R.raw.click,1);

        //make GET request
        new GetDataTask().execute("http://"+TableActivity.currentIP+":3000/api/booking");
    }

    public void gotoTable(View view){
        mSoundPool.play(clickID,1,1,1,0,1);
        Intent i = new Intent(DetailsActivity.this, TableActivity.class);
        startActivity(i);
    }

    public void cancelBooking(View view){
        mSoundPool.play(clickID,1,1,1,0,1);
        Intent i = new Intent(DetailsActivity.this, CancelActivity.class);
        i.putExtra(DetailsActivity.EXTRA_TIME_SLOT, mTimeSlot);
        i.putExtra(DetailsActivity.EXTRA_RECORD_ID, mID);
        i.putExtra(DetailsActivity.EXTRA_PASS_HASH, mHash);
        startActivity(i);
    }

    public void makeBooking(View view){
        mSoundPool.play(clickID,1,1,1,0,1);
        Intent i = new Intent(DetailsActivity.this, BookingActivity.class);
        mTimeSlot = view.getTag().toString();
        i.putExtra(BookingActivity.EXTRA_TIME_SLOT, mTimeSlot);
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
                getData(params[0]);
                return null;
            } catch (IOException e){
                return "network error!";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //set data response to textView
            TextView booking_name = (TextView)findViewById(R.id.booking_name);
            booking_name.setText(DetailsActivity.this.result.get("name"));
            TextView booking_title = (TextView)findViewById(R.id.booking_title);
            booking_title.setText(DetailsActivity.this.result.get("title"));
            TextView booking_email = (TextView)findViewById(R.id.booking_email);
            booking_email.setText(DetailsActivity.this.result.get("email"));
            TextView booking_notes = (TextView)findViewById(R.id.booking_notes);
            booking_notes.setText(DetailsActivity.this.result.get("notes"));

            //remove progrss dialog
            if(mProgressDialog != null){
                mProgressDialog.dismiss();
            }

        }

        private Map getData(String urlPath) throws IOException {
            Map<String, String> result = new HashMap<String, String>();
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
                    try {
                        JSONArray jsonline = new JSONArray(line);

                        for (int i=0; i < jsonline.length(); i++){
                            JSONObject jsonObject = jsonline.getJSONObject(i);
                            if (((String)jsonObject.get("tag")).equals(mTimeSlot)){
                                System.out.println(jsonObject.keys());
                                result.put("name", (String)jsonObject.get("name"));
                                result.put("title", (String)jsonObject.get("title"));
                                result.put("email", (String)jsonObject.get("email"));
                                result.put("notes", (String)jsonObject.get("notes"));
                                mID = (String)jsonObject.get("_id");
                                mHash = (String)jsonObject.get("password");
                            } else {
                                System.out.println("tag"+(String)jsonObject.get("tag"));
                                System.out.println("time slot" + mTimeSlot);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

            } finally {
                if (bufferedReader != null)
                    bufferedReader.close();
            }
            DetailsActivity.this.result = result;
            System.out.println(DetailsActivity.this.result);
            return result;
        }

    }
}
