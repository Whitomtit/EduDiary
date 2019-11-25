package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeworkManagerContract {
    private static HomeworkDbHelper dbHelper;

    public HomeworkManagerContract(Context context) {
        this.dbHelper = new HomeworkDbHelper(context);
    }

    public List<Homework> getAllHomework() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<Homework> homeworkList = new ArrayList<>();

        String sortOrder = HomeworkEntry.COLUMN_NAME_DATE + " ASC";
        Cursor cursor = db.query(
                HomeworkEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                sortOrder
        );
        while (cursor.moveToNext()) {
            String subject = cursor.getString(
                    cursor.getColumnIndexOrThrow(HomeworkEntry.COLUMN_NAME_SUBJECT));
            Date date = new Date(cursor.getLong(
                    cursor.getColumnIndexOrThrow(HomeworkEntry.COLUMN_NAME_DATE)) * 1000);
            long homeworkId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(HomeworkEntry._ID));
            homeworkList.add(new Homework(subject, date, getCategoriesById(homeworkId)));
        }

        return homeworkList;
    }

    public List<Category> getCategoriesById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<Category> categoryList = new ArrayList<>();

        String[] projection = {
                CategoryEntry.COLUMN_NAME_NAME,
                CategoryEntry.COLUMN_NAME_ITEMS
        };

        String selection = CategoryEntry.COLUMN_NAME_HOMEWORK_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        Cursor cursor = db.query(
                CategoryEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(CategoryEntry.COLUMN_NAME_NAME));
            String items = cursor.getString(
                    cursor.getColumnIndexOrThrow(CategoryEntry.COLUMN_NAME_ITEMS));
            try {
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(items.getBytes()));
                categoryList.add(new Category(name, (List<Item>) in.readObject()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return categoryList;
    }


    public static class HomeworkEntry implements BaseColumns {
        private static final String TABLE_NAME = "homework_entry";
        private static final String COLUMN_NAME_SUBJECT = "subject";
        private static final String COLUMN_NAME_DATE = "date";

        private static final String SQL_CREATE_ENTRY =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_SUBJECT + " TEXT," +
                        COLUMN_NAME_DATE + " DATETIME)";
    }

    private static class CategoryEntry implements BaseColumns {
        private static final String TABLE_NAME = "category_entry";
        private static final String COLUMN_NAME_NAME = "name";
        private static final String COLUMN_NAME_ITEMS = "items";
        private static final String COLUMN_NAME_HOMEWORK_ID = "homework_id";

        private static final String SQL_CREATE_ENTRY =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_NAME + " TEXT," +
                        COLUMN_NAME_ITEMS + " TEXT," +
                        COLUMN_NAME_HOMEWORK_ID + " INTEGER)";
    }

    public static class HomeworkDbHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "HomeworkManager.db";

        public HomeworkDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(HomeworkEntry.SQL_CREATE_ENTRY);
            sqLiteDatabase.execSQL(CategoryEntry.SQL_CREATE_ENTRY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        }
    }

}
