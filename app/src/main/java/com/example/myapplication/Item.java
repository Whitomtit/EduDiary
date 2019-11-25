package com.example.myapplication;

import java.io.Serializable;

public class Item implements Serializable {
    private String content;
    private boolean isDone;

    public Item(String content) {
        this.content = content;
        this.isDone = false;
    }

    public boolean isDone() {
        return isDone;
    }

    public String getContent() {
        return content;
    }

    public void toggle() {
        this.isDone = !isDone;
    }

}
