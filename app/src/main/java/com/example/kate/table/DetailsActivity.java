package com.example.kate.table;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by kate on 28/02/2017.
 */

public class DetailsActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
    }

    public void gotoTable(View view){
        Intent i = new Intent(DetailsActivity.this, TableActivity.class);
        startActivity(i);
    }
    public void makeBooking(View view){
        Intent i = new Intent(DetailsActivity.this, BookingActivity.class);
        startActivity(i);
    }
}
