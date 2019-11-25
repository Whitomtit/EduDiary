package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.HomeworkManagerContract.HomeworkDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private RecyclerView homework;
    FloatingActionButton actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HomeworkDbHelper dbHelper = new HomeworkDbHelper(this);

        homework = findViewById(R.id.listViewHomework);
        actionButton = findViewById(R.id.fab);

        homework.setHasFixedSize(true);
        homework.setLayoutManager(new LinearLayoutManager(this));
        homework.setAdapter(new HomeworkAdapter(getTestData()));

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });
    }

    private static List<Homework> getTestData() {
        List<Homework> list = new ArrayList<>();
        List<Category> groupList = new ArrayList<>();
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("ex. 16"));
        items.add(new Item("Написать очень длинное сообщение, которое займёт всю строчку"));
        items.add(new Item("ex. 19"));
        items.add(new Item("ex. 16"));
        items.add(new Item("ex. 18"));
        items.add(new Item("ex. 19"));

        groupList.add(new Category("test1", items));
        groupList.add(new Category("test2", items));
        groupList.add(new Category("test3", items));
        groupList.add(new Category("test4", items));
        list.add(new Homework("Math", new Date(), groupList));
        list.add(new Homework("Programming", new Date(), groupList));
        list.add(new Homework("Literature", new Date(), groupList));
        list.add(new Homework("English", new Date(), groupList));
        list.add(new Homework("Physics", new Date(), groupList));
        return list;
    }
}
