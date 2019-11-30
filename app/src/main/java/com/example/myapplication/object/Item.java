package com.example.myapplication.object;

public class Item {
    private String content;
    private boolean isDone;

    public Item(String content) {
        this.content = content;
        this.isDone = false;
    }

    public Item(String content, boolean isDone) {
        this.content = content;
        this.isDone = isDone;
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
