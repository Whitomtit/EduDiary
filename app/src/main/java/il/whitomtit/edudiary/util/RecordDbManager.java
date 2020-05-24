package il.whitomtit.edudiary.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import il.whitomtit.edudiary.model.Category;
import il.whitomtit.edudiary.model.Record;
import il.whitomtit.edudiary.model.Item;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordDbManager {
    private RecordDbHelper dbHelper;

    public RecordDbManager(Context context) {
        this.dbHelper = new RecordDbHelper(context);
    }

    public List<Record> getAllRecords() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<Record> recordList = new ArrayList<>();

        //Sort by date
        String sortOrder = RecordEntry.COLUMN_NAME_DATE + " ASC";
        Cursor cursor = db.query(
                RecordEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                sortOrder
        );

        while (cursor.moveToNext()) {
            String subject = cursor.getString(
                    cursor.getColumnIndexOrThrow(RecordEntry.COLUMN_NAME_SUBJECT));
            Date date = new Date(cursor.getLong(
                    cursor.getColumnIndexOrThrow(RecordEntry.COLUMN_NAME_DATE)));
            long recordId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(RecordEntry._ID));
            recordList.add(new Record(subject, date, getCategoriesByRecordId(recordId), recordId));
        }
        cursor.close();
        return recordList;
    }

    private List<Category> getCategoriesByRecordId(long recordId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<Category> categoryList = new ArrayList<>();

        String[] projection = {
                CategoryEntry._ID,
                CategoryEntry.COLUMN_NAME_NAME
        };

        String selection = CategoryEntry.COLUMN_NAME_RECORD_ID + " = ?";
        String[] selectionArgs = { String.valueOf(recordId) };

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

    private void addRecord(Record record) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RecordEntry.COLUMN_NAME_SUBJECT, record.getSubject());
        values.put(RecordEntry.COLUMN_NAME_DATE, record.getDate().getTime());

        long recordId = db.insert(RecordEntry.TABLE_NAME, null, values);

        for (Category category : record.getCategoryList())
            addCategory(category, recordId);
    }

    private void addCategory(Category category, long recordId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CategoryEntry.COLUMN_NAME_NAME, category.getName());
        values.put(CategoryEntry.COLUMN_NAME_RECORD_ID, recordId);

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

    public void deleteRecord(Record record) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = RecordEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(record.getId()) };

        db.delete(RecordEntry.TABLE_NAME, selection, selectionArgs);

        deleteCategories(record.getId());
    }

    private void deleteCategories(long recordId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] projection = { CategoryEntry._ID };

        String selection = CategoryEntry.COLUMN_NAME_RECORD_ID + " = ?";
        String[] selectionArgs = { String.valueOf(recordId) };

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

        selection = CategoryEntry.COLUMN_NAME_RECORD_ID + " LIKE ?";
        selectionArgs[0] = String.valueOf(recordId);

        db.delete(CategoryEntry.TABLE_NAME, selection, selectionArgs);

    }

    private void deleteItems(long categoryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = ItemEntry.COLUMN_NAME_CATEGORY_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(categoryId) };

        db.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void updateRecord(Record record) {
        //if record was previously in database - delete it
        if (record.getId() != -1) {
            deleteRecord(record);
        }
        addRecord(record);
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


    private static class RecordEntry implements BaseColumns {
        private static final String TABLE_NAME = "record_entry";
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
        private static final String COLUMN_NAME_RECORD_ID = "record_id";

        private static final String SQL_CREATE_ENTRY =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_NAME + " TEXT," +
                        COLUMN_NAME_RECORD_ID + " INTEGER)";
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

    public class RecordDbHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "RecordManager.db";

        private RecordDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(RecordEntry.SQL_CREATE_ENTRY);
            sqLiteDatabase.execSQL(CategoryEntry.SQL_CREATE_ENTRY);
            sqLiteDatabase.execSQL(ItemEntry.SQL_CREATE_ENTRY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        }
    }

}
