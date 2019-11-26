package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.object.Homework;
import com.example.myapplication.utils.HomeworkDbManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    RecyclerView homework;
    FloatingActionButton actionButton;
    HomeworkDbManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new HomeworkDbManager(this);
        fetchHomework();

        homework = findViewById(R.id.listViewHomework);
        actionButton = findViewById(R.id.fab);

        homework.setHasFixedSize(true);
        homework.setLayoutManager(new LinearLayoutManager(this));
        homework.setAdapter(new HomeworkAdapter(fetchHomework()));

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
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                ((HomeworkAdapter)homework.getAdapter()).updateList(fetchHomework());
            }
        }

    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    private List<Homework> fetchHomework() {
        List<Homework> result = null;
        try {
            result = db.getAllHomework();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

}
