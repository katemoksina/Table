package com.example.kate.table;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kate on 28/02/2017.
 */

public class BookingActivity extends Activity{
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
    }

    public void submitRecord(View view){
        JSONObject newRecord = new JSONObject();
        try {
            newRecord.put("key", "tu1");
            newRecord.put("name", "john doe");
            newRecord.put("email", "jd111");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String jsonStr = newRecord.toString();
        System.out.println("jsonString: "+jsonStr);
    }
}
