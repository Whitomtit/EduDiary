package com.example.myapplication.model;

import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Category implements Serializable {
    private long id = -1;
    private String name;
    private List<Item> itemList;
    transient private View view;
    transient private ViewGroup itemBox;

    public Category(long id, String name, List<Item> itemList) {
        this.id = id;
        this.name = name;
        this.itemList = itemList;
    }

    public Category(String name, ViewGroup itemBox, View categoryView) {
        this.name = name;
        this.itemBox = itemBox;
        this.view = categoryView;
        this.itemList = new ArrayList<>();
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public View getView() {
        return view;
    }

    public ViewGroup getItemBox() {
        return itemBox;
    }

    public void addItem(Item item) {
        itemList.add(item);
    }

    public void removeItem(Item item) {
        itemList.remove(item);
    }

    public boolean isEmpty() {
        return itemList.isEmpty();
    }
}
