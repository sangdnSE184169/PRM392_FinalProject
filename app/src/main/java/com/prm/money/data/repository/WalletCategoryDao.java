package com.prm.money.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.prm.money.data.db.MoneyMindDbHelper;
import com.prm.money.model.WalletCategory;

import java.util.ArrayList;
import java.util.List;

public class WalletCategoryDao {
    private MoneyMindDbHelper dbHelper;
    public static final String TABLE_NAME = "WalletCategory";

    public WalletCategoryDao(Context context) {
        dbHelper = new MoneyMindDbHelper(context);
    }

    public long addWalletCategory(WalletCategory category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", category.getName());
        values.put("description", category.getDescription());
        values.put("color", category.getColor());
        values.put("icon", category.getIcon());
        values.put("isDelete", category.isDelete() ? 1 : 0);
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public int updateWalletCategory(WalletCategory category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", category.getName());
        values.put("description", category.getDescription());
        values.put("color", category.getColor());
        values.put("icon", category.getIcon());
        values.put("isDelete", category.isDelete() ? 1 : 0);
        int rows = db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(category.getId())});
        db.close();
        return rows;
    }

    public int deleteWalletCategory(int categoryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(categoryId)});
        db.close();
        return rows;
    }

    public int softDeleteWalletCategory(int categoryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isDelete", 1);
        int rows = db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(categoryId)});
        db.close();
        return rows;
    }

    public List<WalletCategory> getAllWalletCategories() {
        List<WalletCategory> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "name ASC");
        if (cursor.moveToFirst()) {
            do {
                WalletCategory category = cursorToCategory(cursor);
                list.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public WalletCategory getWalletCategoryById(int categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "id = ?", new String[]{String.valueOf(categoryId)}, null, null, null);
        WalletCategory category = null;
        if (cursor.moveToFirst()) {
            category = cursorToCategory(cursor);
        }
        cursor.close();
        db.close();
        return category;
    }

    public List<WalletCategory> getAllActiveWalletCategories() {
        List<WalletCategory> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "isDelete = 0", null, null, null, "name ASC");
        if (cursor.moveToFirst()) {
            do {
                WalletCategory category = cursorToCategory(cursor);
                list.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    private WalletCategory cursorToCategory(Cursor cursor) {
        return new WalletCategory(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                cursor.getString(cursor.getColumnIndexOrThrow("color")),
                cursor.getString(cursor.getColumnIndexOrThrow("icon")),
                cursor.getInt(cursor.getColumnIndexOrThrow("isDelete")) == 1
        );
    }
}