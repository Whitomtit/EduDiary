package com.example.myapplication.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.myapplication.model.Category;
import com.example.myapplication.model.Homework;
import com.example.myapplication.model.Item;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeworkDbManager {
    private HomeworkDbHelper dbHelper;

    public HomeworkDbManager(Context context) {
        this.dbHelper = new HomeworkDbHelper(context);
    }

    public List<Homework> getAllHomework() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<Homework> homeworkList = new ArrayList<>();

        //Sort by date
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
            homeworkList.add(new Homework(subject, date, getCategoriesByHomeworkId(homeworkId), homeworkId));
        }
        cursor.close();
        return homeworkList;
    }

    private List<Category> getCategoriesByHomeworkId(long homeworkId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<Category> categoryList = new ArrayList<>();

        String[] projection = {
                CategoryEntry._ID,
                CategoryEntry.COLUMN_NAME_NAME
        };

        String selection = CategoryEntry.COLUMN_NAME_HOMEWORK_ID + " = ?";
        String[] selectionArgs = { String.valueOf(homeworkId) };

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
            long categoryId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(CategoryEntry._ID));
            categoryList.add(new Category(categoryId, name, getItemsByCategoryId(categoryId)));
        }

        cursor.close();

        return categoryList;
    }

    private List<Item> getItemsByCategoryId(long categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<Item> itemList = new ArrayList<>();

        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_NAME_CONTENT,
                ItemEntry.COLUMN_NAME_IS_DONE
        };

        String selection = ItemEntry.COLUMN_NAME_CATEGORY_ID + " = ?";
        String[] selectionArgs = { String.valueOf(categoryId) };

        Cursor cursor = db.query(
                ItemEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            String content = cursor.getString(
                    cursor.getColumnIndexOrThrow(ItemEntry.COLUMN_NAME_CONTENT));
            boolean isDone = cursor.getInt(
                    cursor.getColumnIndexOrThrow(ItemEntry.COLUMN_NAME_IS_DONE)) > 0;
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(ItemEntry._ID));
            itemList.add(new Item(itemId, content, isDone));
        }

        cursor.close();

        return itemList;
    }

    private void addHomework(Homework homework) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HomeworkEntry.COLUMN_NAME_SUBJECT, homework.getSubject());
        values.put(HomeworkEntry.COLUMN_NAME_DATE, homework.getDate().getTime());

        long homeworkId = db.insert(HomeworkEntry.TABLE_NAME, null, values);

        for (Category category : homework.getCategoryList())
            addCategory(category, homeworkId);
    }

    private void addCategory(Category category, long homeworkId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CategoryEntry.COLUMN_NAME_NAME, category.getName());
        values.put(CategoryEntry.COLUMN_NAME_HOMEWORK_ID, homeworkId);

        long categoryId = db.insert(CategoryEntry.TABLE_NAME, null, values);

        for (Item item : category.getItemList())
            addItem(item, categoryId);
    }

    private void addItem(Item item, long categoryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_NAME_CONTENT, item.getContent());
        values.put(ItemEntry.COLUMN_NAME_IS_DONE, item.isDone());
        values.put(ItemEntry.COLUMN_NAME_CATEGORY_ID, categoryId);

        db.insert(ItemEntry.TABLE_NAME, null, values);
    }

    public void deleteHomework(Homework homework) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = HomeworkEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(homework.getId()) };

        db.delete(HomeworkEntry.TABLE_NAME, selection, selectionArgs);

        deleteCategories(homework.getId());
    }

    private void deleteCategories(long homeworkId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] projection = { CategoryEntry._ID };

        String selection = CategoryEntry.COLUMN_NAME_HOMEWORK_ID + " = ?";
        String[] selectionArgs = { String.valueOf(homeworkId) };

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
            deleteItems(cursor.getLong(
                    cursor.getColumnIndexOrThrow(CategoryEntry._ID)));
        }
        cursor.close();

        selection = CategoryEntry.COLUMN_NAME_HOMEWORK_ID + " LIKE ?";
        selectionArgs[0] = String.valueOf(homeworkId);

        db.delete(CategoryEntry.TABLE_NAME, selection, selectionArgs);

    }

    private void deleteItems(long categoryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = ItemEntry.COLUMN_NAME_CATEGORY_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(categoryId) };

        db.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void deleteItem(Item item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = ItemEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(item.getId()) };

        db.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void deleteCategory(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        deleteItems(category.getId());

        String selection = CategoryEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(category.getId()) };

        db.delete(CategoryEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void updateHomework(Homework homework) {
        //if id isn't set, there isn't that homework in database, need to add
        if (homework.getId() == -1) {
            addHomework(homework);
        } else {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(HomeworkEntry.COLUMN_NAME_SUBJECT, homework.getSubject());
            values.put(HomeworkEntry.COLUMN_NAME_DATE, homework.getDate().getTime());

            String selection = HomeworkEntry._ID + " LIKE ?";
            String[] selectionArgs = { String.valueOf(homework.getId()) };

            db.update(HomeworkEntry.TABLE_NAME, values, selection, selectionArgs);

            for (Category category : homework.getCategoryList()) {
                if (category.getId() != -1) {
                    for (Item item : category.getItemList())
                        if (item.getId() == -1)
                            addItem(item, category.getId());
                } else {
                    addCategory(category, homework.getId());
                }
            }

        }
    }

    public void updateItem(Item item) {
        if (item.getId() == -1)
            return;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_NAME_IS_DONE, item.isDone());

        String selection = ItemEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(item.getId()) };

        db.update(ItemEntry.TABLE_NAME, values, selection, selectionArgs);
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
        private static final String COLUMN_NAME_HOMEWORK_ID = "homework_id";

        private static final String SQL_CREATE_ENTRY =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_NAME + " TEXT," +
                        COLUMN_NAME_HOMEWORK_ID + " INTEGER)";
    }

    private static class ItemEntry implements BaseColumns {
        private static final String TABLE_NAME = "item_entry";
        private static final String COLUMN_NAME_CONTENT = "content";
        private static final String COLUMN_NAME_IS_DONE = "is_done";
        private static final String COLUMN_NAME_CATEGORY_ID = "category_id";

        private static final String SQL_CREATE_ENTRY =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_CONTENT + " TEXT," +
                        COLUMN_NAME_IS_DONE + " BOOLEAN," +
                        COLUMN_NAME_CATEGORY_ID + " INTEGER)";
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
            sqLiteDatabase.execSQL(ItemEntry.SQL_CREATE_ENTRY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        }
    }

}
