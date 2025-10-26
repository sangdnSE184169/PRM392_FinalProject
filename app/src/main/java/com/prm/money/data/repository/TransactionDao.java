package com.prm.money.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.prm.money.data.db.MoneyMindDbHelper;
import com.prm.money.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionDao {
    private MoneyMindDbHelper dbHelper;
    public static final String TABLE_NAME = "`Transaction`";

    public TransactionDao(Context context) {
        dbHelper = new MoneyMindDbHelper(context);
    }

    public long addTransaction(Transaction transaction) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", transaction.getAmount());
        values.put("date", transaction.getDate());
        values.put("note", transaction.getNote());
        values.put("walletId", transaction.getWalletId());
        values.put("categoryId", transaction.getCategoryId());
        values.put("type", transaction.getType());
        values.put("activity", transaction.getActivity());
        values.put("monthlyGoalId", transaction.getMonthlyGoalId());
        values.put("createdAt", transaction.getCreatedAt());
        values.put("updatedAt", transaction.getUpdatedAt());
        values.put("isDelete", transaction.isDelete() ? 1 : 0);
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public int updateTransaction(Transaction transaction) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", transaction.getAmount());
        values.put("date", transaction.getDate());
        values.put("note", transaction.getNote());
        values.put("walletId", transaction.getWalletId());
        values.put("categoryId", transaction.getCategoryId());
        values.put("type", transaction.getType());
        values.put("activity", transaction.getActivity());
        values.put("monthlyGoalId", transaction.getMonthlyGoalId());
        values.put("createdAt", transaction.getCreatedAt());
        values.put("updatedAt", transaction.getUpdatedAt());
        values.put("isDelete", transaction.isDelete() ? 1 : 0);
        int rows = db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(transaction.getId())});
        db.close();
        return rows;
    }

    public Transaction getTransaction(int transactionId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Transaction transaction = null;
        Cursor cursor = db.query(TABLE_NAME, null, "id = ? AND isDelete = 0", new String[]{String.valueOf(transactionId)}, null, null, null);
        if (cursor.moveToFirst()) {
            transaction = new Transaction(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                cursor.getString(cursor.getColumnIndexOrThrow("date")),
                cursor.getString(cursor.getColumnIndexOrThrow("note")),
                cursor.getInt(cursor.getColumnIndexOrThrow("walletId")),
                cursor.getInt(cursor.getColumnIndexOrThrow("categoryId")),
                cursor.getString(cursor.getColumnIndexOrThrow("type")),
                cursor.getString(cursor.getColumnIndexOrThrow("activity")),
                cursor.isNull(cursor.getColumnIndexOrThrow("monthlyGoalId")) ? null : cursor.getInt(cursor.getColumnIndexOrThrow("monthlyGoalId")),
                cursor.getString(cursor.getColumnIndexOrThrow("createdAt")),
                cursor.getString(cursor.getColumnIndexOrThrow("updatedAt")),
                cursor.getInt(cursor.getColumnIndexOrThrow("isDelete")) == 1
            );
        }
        cursor.close();
        db.close();
        return transaction;
    }

    public int deleteTransaction(int transactionId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(transactionId)});
        db.close();
        return rows;
    }

    public int softDeleteTransaction(int transactionId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isDelete", 1);
        int rows = db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(transactionId)});
        db.close();
        return rows;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "isDelete = 0", null, null, null, "date DESC");
        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("note")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("walletId")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("categoryId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("type")),
                        cursor.getString(cursor.getColumnIndexOrThrow("activity")),
                        cursor.isNull(cursor.getColumnIndexOrThrow("monthlyGoalId")) ? null : cursor.getInt(cursor.getColumnIndexOrThrow("monthlyGoalId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("createdAt")),
                        cursor.getString(cursor.getColumnIndexOrThrow("updatedAt")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("isDelete")) == 1
                );
                list.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
    public List<Transaction> getTransactionsBetween(String startDate, String endDate) {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = "isDelete = 0 AND date BETWEEN ? AND ?";
        String[] selectionArgs = new String[]{startDate, endDate};

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, "date DESC");

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("note")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("walletId")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("categoryId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("type")),
                        cursor.getString(cursor.getColumnIndexOrThrow("activity")),
                        cursor.isNull(cursor.getColumnIndexOrThrow("monthlyGoalId")) ? null : cursor.getInt(cursor.getColumnIndexOrThrow("monthlyGoalId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("createdAt")),
                        cursor.getString(cursor.getColumnIndexOrThrow("updatedAt")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("isDelete")) == 1
                );
                list.add(transaction);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public double getTotalExpenseByCategory(int categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM " + TABLE_NAME + " WHERE categoryId = ? AND type = ? AND isDelete = 0", new String[]{String.valueOf(categoryId), "expense"});
        if (cursor.moveToFirst()) {
            total = cursor.isNull(0) ? 0 : cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public double getTotalExpenseByWallet(int walletId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM " + TABLE_NAME + " WHERE walletId = ? AND type = ? AND isDelete = 0", new String[]{String.valueOf(walletId), "expense"});
        if (cursor.moveToFirst()) {
            total = cursor.isNull(0) ? 0 : cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public double getTotalExpenseByMonthlyGoal(int monthlyGoalId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM " + TABLE_NAME + " WHERE monthlyGoalId = ? AND type = ? AND isDelete = 0", new String[]{String.valueOf(monthlyGoalId), "expense"});
        if (cursor.moveToFirst()) {
            total = cursor.isNull(0) ? 0 : cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public double getTotalExpenseByMonthYear(int month, int year) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM " + TABLE_NAME + " WHERE strftime('%m', date) = ? AND strftime('%Y', date) = ? AND type = ? AND isDelete = 0", new String[]{String.format("%02d", month), String.valueOf(year), "expense"});
        if (cursor.moveToFirst()) {
            total = cursor.isNull(0) ? 0 : cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public double getTotalExpenseByDate(String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM " + TABLE_NAME + " WHERE date = ? AND type = ? AND isDelete = 0", new String[]{date, "expense"});
        if (cursor.moveToFirst()) {
            total = cursor.isNull(0) ? 0 : cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public double getTotalExpenseByActivity(String activity) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM " + TABLE_NAME + " WHERE activity = ? AND type = ? AND isDelete = 0", new String[]{activity, "expense"});
        if (cursor.moveToFirst()) {
            total = cursor.isNull(0) ? 0 : cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public double getTotalIncomeByCategory(int categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM " + TABLE_NAME + " WHERE categoryId = ? AND type = ? AND isDelete = 0", new String[]{String.valueOf(categoryId), "income"});
        if (cursor.moveToFirst()) {
            total = cursor.isNull(0) ? 0 : cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public double getTotalIncomeByWallet(int walletId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM " + TABLE_NAME + " WHERE walletId = ? AND type = ? AND isDelete = 0", new String[]{String.valueOf(walletId), "income"});
        if (cursor.moveToFirst()) {
            total = cursor.isNull(0) ? 0 : cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public double getTotalIncomeByMonthlyGoal(int monthlyGoalId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM " + TABLE_NAME + " WHERE monthlyGoalId = ? AND type = ? AND isDelete = 0", new String[]{String.valueOf(monthlyGoalId), "income"});
        if (cursor.moveToFirst()) {
            total = cursor.isNull(0) ? 0 : cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public double getTotalIncomeByMonthYear(int month, int year) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM " + TABLE_NAME + " WHERE strftime('%m', date) = ? AND strftime('%Y', date) = ? AND type = ? AND isDelete = 0", new String[]{String.format("%02d", month), String.valueOf(year), "income"});
        if (cursor.moveToFirst()) {
            total = cursor.isNull(0) ? 0 : cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public double getTotalIncomeByDate(String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM " + TABLE_NAME + " WHERE date = ? AND type = ? AND isDelete = 0", new String[]{date, "income"});
        if (cursor.moveToFirst()) {
            total = cursor.isNull(0) ? 0 : cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public double getTotalIncomeByActivity(String activity) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM " + TABLE_NAME + " WHERE activity = ? AND type = ? AND isDelete = 0", new String[]{activity, "income"});
        if (cursor.moveToFirst()) {
            total = cursor.isNull(0) ? 0 : cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }
} 