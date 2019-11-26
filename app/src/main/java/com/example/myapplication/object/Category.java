package com.example.myapplication.object;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String name;
    private List<Item> itemList;
    transient private View view;
    transient private ViewGroup itemBox;

    public Category(String name, List<Item> itemList) {
        this.name = name;
        this.itemList = itemList;
    }

    public Category(String name, ViewGroup itemBox, View categoryView) {
        this.name = name;
        this.itemList = new ArrayList<>();
        this.itemBox = itemBox;
        this.view = categoryView;
    }

    public void addItem(Item item) {
        itemList.add(item);
    }

    public String getName() {
        return name;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public ViewGroup getItemBox() {
        return itemBox;
    }

    public View getView() {
        return view;
    }

    public void removeItem(Item item) {
        itemList.remove(item);
    }

    public boolean isEmpty() {
        return itemList.isEmpty();
    }
}
