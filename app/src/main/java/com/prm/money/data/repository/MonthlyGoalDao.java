package com.prm.money.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.prm.money.data.db.MoneyMindDbHelper;
import com.prm.money.model.MonthlyGoal;

import java.util.ArrayList;
import java.util.List;

public class MonthlyGoalDao {
    private MoneyMindDbHelper dbHelper;

    public MonthlyGoalDao(Context context) {
        dbHelper = new MoneyMindDbHelper(context);
    }

    public long addMonthlyGoal(MonthlyGoal goal) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("month", goal.getMonth());
        values.put("year", goal.getYear());
        values.put("goalAmount", goal.getGoalAmount());
        values.put("categoryId", goal.getCategoryId());
        values.put("walletId", goal.getWalletId());
        values.put("note", goal.getNote());
        values.put("createdAt", goal.getCreatedAt());
        values.put("updatedAt", goal.getUpdatedAt());
        values.put("isDelete", goal.isDelete() ? 1 : 0);
        long id = db.insert("MonthlyGoal", null, values);
        db.close();
        return id;
    }

    public int updateMonthlyGoal(MonthlyGoal goal) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("month", goal.getMonth());
        values.put("year", goal.getYear());
        values.put("goalAmount", goal.getGoalAmount());
        values.put("categoryId", goal.getCategoryId());
        values.put("walletId", goal.getWalletId());
        values.put("note", goal.getNote());
        values.put("createdAt", goal.getCreatedAt());
        values.put("updatedAt", goal.getUpdatedAt());
        int rows = db.update("MonthlyGoal", values, "id = ?", new String[]{String.valueOf(goal.getId())});
        db.close();
        return rows;
    }

    public int deleteMonthlyGoal(int goalId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("MonthlyGoal", "id = ?", new String[]{String.valueOf(goalId)});
        db.close();
        return rows;
    }

    public List<MonthlyGoal> getAllMonthlyGoals() {
        List<MonthlyGoal> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("MonthlyGoal", null, null, null, null, null, "year DESC, month DESC");
        if (cursor.moveToFirst()) {
            do {
                MonthlyGoal goal = cursorToGoal(cursor);
                list.add(goal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
    public List<MonthlyGoal> getMonthlyGoalsBetween(int startYear, int startMonth, int endYear, int endMonth) {
        List<MonthlyGoal> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = "isDelete = 0 AND ((year > ?) OR (year = ? AND month >= ?)) AND ((year < ?) OR (year = ? AND month <= ?))";
        String[] selectionArgs = new String[]{
                String.valueOf(startYear), String.valueOf(startYear), String.valueOf(startMonth),
                String.valueOf(endYear), String.valueOf(endYear), String.valueOf(endMonth)
        };

        Cursor cursor = db.query("MonthlyGoal", null, selection, selectionArgs, null, null, "year DESC, month DESC");

        if (cursor.moveToFirst()) {
            do {
                MonthlyGoal goal = new MonthlyGoal(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("month")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("year")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("goalAmount")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("categoryId")),
                        cursor.isNull(cursor.getColumnIndexOrThrow("walletId")) ? null : cursor.getInt(cursor.getColumnIndexOrThrow("walletId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("note")),
                        cursor.getString(cursor.getColumnIndexOrThrow("createdAt")),
                        cursor.getString(cursor.getColumnIndexOrThrow("updatedAt")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("isDelete")) == 1
                );
                list.add(goal);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }
    public MonthlyGoal getMonthlyGoalById(int goalId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("MonthlyGoal", null, "id = ?", new String[]{String.valueOf(goalId)}, null, null, null);
        MonthlyGoal goal = null;
        if (cursor.moveToFirst()) {
            goal = cursorToGoal(cursor);
        }
        cursor.close();
        db.close();
        return goal;
    }

    public List<MonthlyGoal> getMonthlyGoalsByCategory(int categoryId) {
        List<MonthlyGoal> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("MonthlyGoal", null, "categoryId = ?", new String[]{String.valueOf(categoryId)}, null, null, "year DESC, month DESC");
        if (cursor.moveToFirst()) {
            do {
                MonthlyGoal goal = cursorToGoal(cursor);
                list.add(goal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<MonthlyGoal> getMonthlyGoalsByWallet(int walletId) {
        List<MonthlyGoal> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("MonthlyGoal", null, "walletId = ?", new String[]{String.valueOf(walletId)}, null, null, "year DESC, month DESC");
        if (cursor.moveToFirst()) {
            do {
                MonthlyGoal goal = cursorToGoal(cursor);
                list.add(goal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public int softDeleteMonthlyGoal(int goalId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isDelete", 1);
        int rows = db.update("MonthlyGoal", values, "id = ?", new String[]{String.valueOf(goalId)});
        db.close();
        return rows;
    }

    public List<MonthlyGoal> getAllActiveMonthlyGoals() {
        List<MonthlyGoal> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("MonthlyGoal", null, "isDelete = 0", null, null, null, "year DESC, month DESC");
        if (cursor.moveToFirst()) {
            do {
                MonthlyGoal goal = cursorToGoal(cursor);
                list.add(goal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<MonthlyGoal> getMonthlyGoalsByTime(int month, int year) {
        List<MonthlyGoal> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("MonthlyGoal", null, "month = ? AND year = ? AND isDelete = 0", new String[]{String.valueOf(month), String.valueOf(year)}, null, null, "year DESC, month DESC");
        if (cursor.moveToFirst()) {
            do {
                MonthlyGoal goal = cursorToGoal(cursor);
                list.add(goal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<MonthlyGoal> getMonthlyGoalsByCategoryAndTime(int categoryId, int month, int year) {
        List<MonthlyGoal> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("MonthlyGoal", null, "categoryId = ? AND month = ? AND year = ? AND isDelete = 0", new String[]{String.valueOf(categoryId), String.valueOf(month), String.valueOf(year)}, null, null, "year DESC, month DESC");
        if (cursor.moveToFirst()) {
            do {
                MonthlyGoal goal = cursorToGoal(cursor);
                list.add(goal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public MonthlyGoal getMonthlyGoalByCategoryAndTime(int categoryId, int month, int year) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("MonthlyGoal", null, "categoryId = ? AND month = ? AND year = ? AND isDelete = 0",
                new String[]{String.valueOf(categoryId), String.valueOf(month), String.valueOf(year)}, null, null, null);
        MonthlyGoal goal = null;
        if (cursor.moveToFirst()) {
            goal = cursorToGoal(cursor);
        }
        cursor.close();
        db.close();
        return goal;
    }

    public long insertOrUpdateMonthlyGoal(MonthlyGoal goal) {
        MonthlyGoal existing = getMonthlyGoalByCategoryAndTime(goal.getCategoryId(), goal.getMonth(), goal.getYear());
        if (existing != null) {
            goal.setId(existing.getId());
            updateMonthlyGoal(goal);
            return existing.getId();
        } else {
            return addMonthlyGoal(goal);
        }
    }

    private MonthlyGoal cursorToGoal(Cursor cursor) {
        return new MonthlyGoal(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getInt(cursor.getColumnIndexOrThrow("month")),
                cursor.getInt(cursor.getColumnIndexOrThrow("year")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("goalAmount")),
                cursor.getInt(cursor.getColumnIndexOrThrow("categoryId")),
                cursor.isNull(cursor.getColumnIndexOrThrow("walletId")) ? null : cursor.getInt(cursor.getColumnIndexOrThrow("walletId")),
                cursor.getString(cursor.getColumnIndexOrThrow("note")),
                cursor.getString(cursor.getColumnIndexOrThrow("createdAt")),
                cursor.getString(cursor.getColumnIndexOrThrow("updatedAt")),
                cursor.getInt(cursor.getColumnIndexOrThrow("isDelete")) == 1
        );
    }
}
