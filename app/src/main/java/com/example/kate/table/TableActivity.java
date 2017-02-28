package com.example.kate.table;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TableActivity extends AppCompatActivity {
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

//        mButton = (Button)findViewById(R.id.mo1);
//        mButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                //
//            }
//        });
    }

    public void seeDetails(View view){
        //create DetailsActivity with onClick=seeDetails in activity_table.xml
        Intent i = new Intent(TableActivity.this, DetailsActivity.class);
        startActivity(i);
    }
}
