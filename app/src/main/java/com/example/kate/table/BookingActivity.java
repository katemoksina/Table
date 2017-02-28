package com.example.kate.table;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Created by kate on 28/02/2017.
 */

public class BookingActivity extends Activity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_booking);
    }
}
