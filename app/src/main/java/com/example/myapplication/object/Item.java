package com.example.myapplication.object;

import java.io.Serializable;

public class Item implements Serializable {
    private String content;
    private boolean isDone;
    private long id = -1;

    public Item(String content) {
        this.content = content;
        this.isDone = false;
    }

    public Item(long id, String content, boolean isDone) {
        this.id = id;
        this.content = content;
        this.isDone = isDone;
    }

    public boolean isDone() {
        return isDone;
    }

    public String getContent() {
        return content;
    }

    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    public long getId() {
        return this.id;
    }

}
