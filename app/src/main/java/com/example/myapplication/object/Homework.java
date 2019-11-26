package com.example.myapplication.object;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Homework {
    private String subject;
    private Date date;
    private List<Category> categoryList;

    public Homework(String subject, Date date, List<Category> groupList) {
        this.subject = subject;
        this.date = date;
        this.categoryList = groupList;
    }

    public Homework() {
        this.subject = null;
        this.date = null;
        this.categoryList = new ArrayList<>();
    }

    public String getDateAsString() {
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM", new Locale("en", "UK"));
        return format.format(this.date);
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void addCategory(Category category) {
        this.categoryList.add(category);
    }

    public void removeCategory(Category category) {
        this.categoryList.remove(category);
    }

    public boolean isEmpty() {
        return categoryList.isEmpty();
    }

    public Date getDate() {
        return this.date;
    }
}
