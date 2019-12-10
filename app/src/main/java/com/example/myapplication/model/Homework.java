package com.example.myapplication.model;

import com.example.myapplication.util.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Homework implements Serializable {
    private long id = -1;
    private String subject;
    private Date date;
    private List<Category> categoryList;

    public Homework() {
        this.categoryList = new ArrayList<>();
    }

    public Homework(String subject, Date date, List<Category> groupList, long id) {
        this.subject = subject;
        this.date = date;
        this.categoryList = groupList;
        this.id = id;
    }

    public Homework(Homework homework) {
        this.subject = homework.subject;
        this.date = homework.date;
        this.id = homework.id;
        this.categoryList = new ArrayList<>();
    }

    public long getId() {
        return this.id;
    }

    public String getSubject() {
        return subject;
    }

    public Date getDate() {
        return this.date;
    }

    public String getDateAsString() {
        return Utils.dateToString(this.date);
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDate(Date date) {
        this.date = date;
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

}
