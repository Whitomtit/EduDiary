package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.util.HomeworkDbManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {

    RecyclerView homeworkRecycler;
    FloatingActionButton actionButton;
    HomeworkDbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbManager = new HomeworkDbManager(this);

        homeworkRecycler = findViewById(R.id.listViewHomework);
        actionButton = findViewById(R.id.fab);

        homeworkRecycler.setHasFixedSize(true);
        homeworkRecycler.setLayoutManager(new LinearLayoutManager(this));
        homeworkRecycler.setAdapter(new HomeworkAdapter(this, dbManager.getAllHomework(), dbManager));

        //Start activity to create a new homework
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
            homeworkRecycler.setAdapter(new HomeworkAdapter(this, dbManager.getAllHomework(), dbManager));
        }

    }

    @Override
    protected void onDestroy() {
        dbManager.close();
        super.onDestroy();
    }


}
