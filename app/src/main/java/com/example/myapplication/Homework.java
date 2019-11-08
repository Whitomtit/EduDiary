package com.example.myapplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Homework {
    private String subject;
    private Date date;
    private List<ItemGroup> groupList;

    public Homework(String subject, Date date, List<ItemGroup> groupList) {
        this.subject = subject;
        this.date = date;
        this.groupList = groupList;
    }

    public String getDateAsString() {
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM", new Locale("en", "UK"));
        return format.format(this.date);
    }

    public String getSubject() {
        return subject;
    }

    public List<ItemGroup> getGroupList() {
        return groupList;
    }
}
