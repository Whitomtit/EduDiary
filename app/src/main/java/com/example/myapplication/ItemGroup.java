package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class ItemGroup {
    private String name;
    private List<Item> itemList;

    public ItemGroup(String name, List<Item> itemList) {
        this.name = name;
        this.itemList = itemList;
    }

    public ItemGroup(String name) {
        this.name = name;
        this.itemList = new ArrayList<>();
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
}
