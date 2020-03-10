package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.util.RecordDbManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {

    RecyclerView recordRecycler;
    FloatingActionButton actionButton;
    RecordDbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbManager = new RecordDbManager(this);

        recordRecycler = findViewById(R.id.recyclerview_records);
        actionButton = findViewById(R.id.fab);

        recordRecycler.setHasFixedSize(true);
        recordRecycler.setLayoutManager(new LinearLayoutManager(this));
        recordRecycler.setAdapter(new RecordAdapter(this, dbManager.getAllRecords(), dbManager));

        //Start activity to create a new record
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            recordRecycler.setAdapter(new RecordAdapter(this, dbManager.getAllRecords(), dbManager));
        }

    }

    @Override
    protected void onDestroy() {
        dbManager.close();
        super.onDestroy();
    }


}
