package com.example.myapplication.model;

import java.io.Serializable;

public class Item implements Serializable {
    private long id = -1;
    private String content;
    private boolean isDone;

    public Item(String content) {
        this.content = content;
        this.isDone = false;
    }

    public Item(long id, String content, boolean isDone) {
        this.id = id;
        this.content = content;
        this.isDone = isDone;
    }

    public long getId() {
        return this.id;
    }

    public String getContent() {
        return content;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

}
