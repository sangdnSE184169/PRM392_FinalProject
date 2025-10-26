package com.prm.money.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.prm.money.data.db.MoneyMindDbHelper;
import com.prm.money.model.Wallet;

import java.util.ArrayList;
import java.util.List;

public class WalletDao {
    private MoneyMindDbHelper dbHelper;
    public static final String TABLE_NAME = "Wallet";

    public WalletDao(Context context) {
        dbHelper = new MoneyMindDbHelper(context);
    }

    public long addWallet(Wallet wallet) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", wallet.getName());
        values.put("balance", wallet.getBalance());
        values.put("description", wallet.getDescription());
        values.put("categoryId", wallet.getCategoryId());
        values.put("icon", wallet.getIcon());
        values.put("isDelete", wallet.isDelete() ? 1 : 0);
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public int updateWallet(Wallet wallet) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", wallet.getName());
        values.put("balance", wallet.getBalance());
        values.put("description", wallet.getDescription());
        values.put("categoryId", wallet.getCategoryId());
        values.put("icon", wallet.getIcon());
        values.put("isDelete", wallet.isDelete() ? 1 : 0);
        int rows = db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(wallet.getId())});
        db.close();
        return rows;
    }

    public int softDeleteWallet(int walletId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isDelete", 1);
        int rows = db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(walletId)});
        db.close();
        return rows;
    }

    public List<Wallet> getAllWallets() {
        List<Wallet> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "isDelete = 0", null, null, null, "name ASC");
        if (cursor.moveToFirst()) {
            do {
                Wallet wallet = new Wallet(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("balance")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.isNull(cursor.getColumnIndexOrThrow("categoryId")) ? null : cursor.getInt(cursor.getColumnIndexOrThrow("categoryId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("icon")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("isDelete")) == 1
                );
                list.add(wallet);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public Wallet getWallet(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressWarnings("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
            @SuppressWarnings("Range") double balance = cursor.getDouble(cursor.getColumnIndex("balance"));
            @SuppressWarnings("Range") String description = cursor.getString(cursor.getColumnIndex("description"));
            @SuppressWarnings("Range") int categoryId = cursor.getInt(cursor.getColumnIndex("categoryId"));
            @SuppressWarnings("Range") String icon = cursor.getString(cursor.getColumnIndex("icon"));
            @SuppressWarnings("Range") boolean isDelete = cursor.getInt(cursor.getColumnIndex("isDelete")) == 1;

            Wallet wallet = new Wallet(id, name, balance, description, categoryId, icon, isDelete);
            cursor.close();
            return wallet;
        }
        return null;
    }

    public long insertWallet(Wallet wallet) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", wallet.getName());
        values.put("balance", wallet.getBalance());
        values.put("description", wallet.getDescription());
        values.put("categoryId", wallet.getCategoryId());
        values.put("icon", wallet.getIcon());
        values.put("isDelete", wallet.isDelete() ? 1 : 0);
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public void deleteWallet(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isDelete", 1);
        db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    /**
     * Update wallet balance by adding/subtracting an amount
     * @param walletId The wallet ID to update
     * @param amount The amount to add (positive) or subtract (negative)
     * @return Number of rows affected
     */
    public int updateWalletBalance(int walletId, double amount) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET balance = balance + ? WHERE id = ?", 
                new Object[]{amount, walletId});
        db.close();
        return 1; // Return 1 to indicate success
    }

    /**
     * Update wallet balance directly to a specific value
     * @param walletId The wallet ID to update
     * @param newBalance The new balance value
     * @return Number of rows affected
     */
    public int setWalletBalance(int walletId, double newBalance) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("balance", newBalance);
        int rows = db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(walletId)});
        db.close();
        return rows;
    }

    /**
     * Calculate and update wallet balance based on all transactions
     * @param walletId The wallet ID to recalculate
     */
    public void recalculateWalletBalance(int walletId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // Calculate total income
        Cursor incomeResult = db.rawQuery(
            "SELECT COALESCE(SUM(amount), 0) FROM `Transaction` WHERE walletId = ? AND type = 'income' AND isDelete = 0", 
            new String[]{String.valueOf(walletId)}
        );
        double totalIncome = 0;
        if (incomeResult.moveToFirst()) {
            totalIncome = incomeResult.getDouble(0);
        }
        incomeResult.close();
        
        // Calculate total expense
        Cursor expenseResult = db.rawQuery(
            "SELECT COALESCE(SUM(amount), 0) FROM `Transaction` WHERE walletId = ? AND type = 'expense' AND isDelete = 0", 
            new String[]{String.valueOf(walletId)}
        );
        double totalExpense = 0;
        if (expenseResult.moveToFirst()) {
            totalExpense = expenseResult.getDouble(0);
        }
        expenseResult.close();
        
        // Update wallet balance
        double newBalance = totalIncome - totalExpense;
        ContentValues values = new ContentValues();
        values.put("balance", newBalance);
        db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(walletId)});
        
        db.close();
    }
}
