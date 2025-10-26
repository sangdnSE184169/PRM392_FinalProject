package com.prm.money.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class MoneyMindDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "moneymind.db";
    private static final int DATABASE_VERSION = 7;

    public MoneyMindDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS `Transaction`");
        db.execSQL("DROP TABLE IF EXISTS MonthlyGoal");
        db.execSQL("DROP TABLE IF EXISTS Wallet");
        db.execSQL("DROP TABLE IF EXISTS WalletCategory");
        onCreate(db);
    }

    private void createTables(SQLiteDatabase db) {
        // T?o b?ng WalletCategory
        db.execSQL("CREATE TABLE WalletCategory (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "description TEXT," +
                "color TEXT," +
                "icon TEXT," +
                "isDelete INTEGER DEFAULT 0" +
                ")");

        // T?o b?ng Wallet
        db.execSQL("CREATE TABLE Wallet (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "balance REAL NOT NULL," +
                "description TEXT," +
                "categoryId INTEGER," +
                "icon TEXT," +
                "isDelete INTEGER DEFAULT 0," +
                "FOREIGN KEY(categoryId) REFERENCES WalletCategory(id)" +
                ")");

        // T?o b?ng MonthlyGoal
        db.execSQL("CREATE TABLE MonthlyGoal (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "month INTEGER NOT NULL," +
                "year INTEGER NOT NULL," +
                "goalAmount REAL NOT NULL," +
                "categoryId INTEGER NOT NULL," +
                "walletId INTEGER," +
                "note TEXT," +
                "createdAt TEXT," +
                "updatedAt TEXT," +
                "isDelete INTEGER DEFAULT 0," +
                "FOREIGN KEY(categoryId) REFERENCES WalletCategory(id)," +
                "FOREIGN KEY(walletId) REFERENCES Wallet(id)," +
                "UNIQUE(categoryId, month, year)" +
                ")");

        // T?o b?ng Transaction
        db.execSQL("CREATE TABLE `Transaction` (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "amount REAL NOT NULL," +
                "date TEXT NOT NULL," +
                "note TEXT," +
                "walletId INTEGER NOT NULL," +
                "categoryId INTEGER NOT NULL," +
                "type TEXT NOT NULL," +
                "activity TEXT," +
                "monthlyGoalId INTEGER," +
                "createdAt TEXT," +
                "updatedAt TEXT," +
                "isDelete INTEGER DEFAULT 0," +
                "FOREIGN KEY(walletId) REFERENCES Wallet(id)," +
                "FOREIGN KEY(categoryId) REFERENCES WalletCategory(id)," +
                "FOREIGN KEY(monthlyGoalId) REFERENCES MonthlyGoal(id)" +
                ")");
    }

    private void insertInitialData(SQLiteDatabase db) {
        // 6 Jars Categories
        db.execSQL("INSERT INTO WalletCategory (name, description, color, icon) VALUES ('Necessities (NEC)', 'Chi tiêu c?n thi?t (55%)', '#FFC107', 'ic_necessities')");
        db.execSQL("INSERT INTO WalletCategory (name, description, color, icon) VALUES ('Long-term Savings (LTS)', 'Ti?t ki?m dài h?n (10%)', '#03A9F4', 'ic_savings')");
        db.execSQL("INSERT INTO WalletCategory (name, description, color, icon) VALUES ('Education (EDU)', '??u t? cho giáo d?c (10%)', '#4CAF50', 'ic_education')");
        db.execSQL("INSERT INTO WalletCategory (name, description, color, icon) VALUES ('Play (PLY)', 'H??ng th? (10%)', '#E91E63', 'ic_play')");
        db.execSQL("INSERT INTO WalletCategory (name, description, color, icon) VALUES ('Financial Freedom (FFA)', 'T? do tài chính (10%)', '#9C27B0', 'ic_freedom')");
        db.execSQL("INSERT INTO WalletCategory (name, description, color, icon) VALUES ('Give (GIV)', 'Cho ?i (5%)', '#FF5722', 'ic_give')");

        // Default Wallets
        db.execSQL("INSERT INTO Wallet (name, balance, description, categoryId) VALUES ('Cash', 0, 'Ti?n m?t', 1)");
        db.execSQL("INSERT INTO Wallet (name, balance, description, categoryId) VALUES ('Bank Account', 0, 'Tài kho?n ngân hàng', 1)");
    }
} 