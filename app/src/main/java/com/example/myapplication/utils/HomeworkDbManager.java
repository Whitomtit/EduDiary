package com.example.myapplication.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.myapplication.object.Category;
import com.example.myapplication.object.Homework;
import com.example.myapplication.object.Item;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeworkDbManager {
    private HomeworkDbHelper dbHelper;

    public HomeworkDbManager(Context context) {
        this.dbHelper = new HomeworkDbHelper(context);
    }

    public List<Homework> getAllHomework() throws IOException, ClassNotFoundException {
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
                    cursor.getColumnIndexOrThrow(HomeworkEntry.COLUMN_NAME_DATE)));
            long homeworkId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(HomeworkEntry._ID));
            homeworkList.add(new Homework(subject, date, getCategoriesById(homeworkId)));
        }
        cursor.close();
        return homeworkList;
    }

    public void addHomework(Homework homework) throws IOException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HomeworkEntry.COLUMN_NAME_SUBJECT, homework.getSubject());
        values.put(HomeworkEntry.COLUMN_NAME_DATE, homework.getDate().getTime());

        long id = db.insert(HomeworkEntry.TABLE_NAME, null, values);

        for (Category category : homework.getCategoryList())
            addCategory(category, id);
    }

    public void addCategory(Category category, long id) throws IOException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ByteArrayOutputStream serializedItems = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(serializedItems);
        objectOutputStream.writeObject(category.getItemList());

        ContentValues values = new ContentValues();
        values.put(CategoryEntry.COLUMN_NAME_NAME, category.getName());
        values.put(CategoryEntry.COLUMN_NAME_HOMEWORK_ID, id);
        values.put(CategoryEntry.COLUMN_NAME_ITEMS, serializedItems.toByteArray());

        db.insert(CategoryEntry.TABLE_NAME, null, values);
    }

    public List<Category> getCategoriesById(long id) throws IOException, ClassNotFoundException {
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
            byte[] items = cursor.getBlob(
                    cursor.getColumnIndexOrThrow(CategoryEntry.COLUMN_NAME_ITEMS));
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(items));
            categoryList.add(new Category(name, (List<Item>) in.readObject()));
        }

        cursor.close();

        return categoryList;
    }

    public void close() {
        dbHelper.close();
    }


    private static class HomeworkEntry implements BaseColumns {
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
                        COLUMN_NAME_ITEMS + " BLOB," +
                        COLUMN_NAME_HOMEWORK_ID + " INTEGER)";
    }

    public class HomeworkDbHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "HomeworkManager.db";

        private HomeworkDbHelper(Context context) {
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
