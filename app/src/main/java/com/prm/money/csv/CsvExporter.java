package com.prm.money.csv;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import com.prm.money.model.MonthlyGoal;
import com.prm.money.model.Transaction;
import com.prm.money.model.Wallet;
import com.prm.money.model.WalletCategory;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvExporter {
    private static final String EXPORT_DIR = "MoneyMind";
    private static final String TAG = "CsvExporter";

    public static void exportAll(Context context,
                                 List<Transaction> transactions,
                                 List<MonthlyGoal> goals,
                                 List<Wallet> wallets,
                                 List<WalletCategory> categories) {

        File exportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), EXPORT_DIR);
        if (!exportDir.exists() && !exportDir.mkdirs()) {
            Log.e(TAG, "Failed to create export directory");
            return;
        }

        exportTransactions(new File(exportDir, "transactions.csv"), transactions, context);
        exportGoals(new File(exportDir, "goals.csv"), goals, context);
        exportWallets(new File(exportDir, "wallets.csv"), wallets, context);
        exportCategories(new File(exportDir, "categories.csv"), categories, context);
    }

    private static void exportTransactions(File file, List<Transaction> list, Context context) {
        Log.d("CsvExporter", "Writing transactions to: " + file.getAbsolutePath());
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeNext(new String[]{"id", "amount", "date", "note", "walletId", "categoryId", "type", "activity", "monthlyGoalId", "createdAt", "updatedAt", "isDelete"});
            for (Transaction t : list) {
                writer.writeNext(new String[]{
                        String.valueOf(t.getId()),
                        String.valueOf(t.getAmount()),
                        t.getDate(),
                        t.getNote(),
                        String.valueOf(t.getWalletId()),
                        String.valueOf(t.getCategoryId()),
                        t.getType(),
                        t.getActivity(),
                        t.getMonthlyGoalId() == null ? "" : String.valueOf(t.getMonthlyGoalId()),
                        t.getCreatedAt(),
                        t.getUpdatedAt(),
                        String.valueOf(t.isDelete())
                });
            }
            Log.d("CsvExporter", "Transactions export complete. File exists: " + file.exists());
        } catch (IOException e) {
            Log.e(TAG, "Failed to export transactions", e);
        }
        MediaScannerConnection.scanFile(
                context,
                new String[]{file.getAbsolutePath()},
                null,
                null
        );
    }

    private static void exportGoals(File file, List<MonthlyGoal> list, Context context) {
        Log.d("CsvExporter", "Writing goals to: " + file.getAbsolutePath());
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeNext(new String[]{"id", "month", "year", "goalAmount", "categoryId", "walletId", "note", "createdAt", "updatedAt", "isDelete"});
            for (MonthlyGoal g : list) {
                writer.writeNext(new String[]{
                        String.valueOf(g.getId()),
                        String.valueOf(g.getMonth()),
                        String.valueOf(g.getYear()),
                        String.valueOf(g.getGoalAmount()),
                        String.valueOf(g.getCategoryId()),
                        g.getWalletId() == null ? "" : String.valueOf(g.getWalletId()),
                        g.getNote(),
                        g.getCreatedAt(),
                        g.getUpdatedAt(),
                        String.valueOf(g.isDelete())
                });
            }
            Log.d("CsvExporter", "Goals export complete. File exists: " + file.exists());
        } catch (IOException e) {
            Log.e(TAG, "Failed to export goals", e);
        }
        MediaScannerConnection.scanFile(
                context,
                new String[]{file.getAbsolutePath()},
                null,
                null
        );
    }

    private static void exportWallets(File file, List<Wallet> list, Context context) {
        Log.d("CsvExporter", "Writing wallets to: " + file.getAbsolutePath());
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeNext(new String[]{"id", "name", "balance", "description", "categoryId", "icon", "isDelete"});
            for (Wallet w : list) {
                writer.writeNext(new String[]{
                        String.valueOf(w.getId()),
                        w.getName(),
                        String.valueOf(w.getBalance()),
                        w.getDescription(),
                        w.getCategoryId() == null ? "" : String.valueOf(w.getCategoryId()),
                        w.getIcon(),
                        String.valueOf(w.isDelete())
                });
            }
            Log.d("CsvExporter", "Wallets export complete. File exists: " + file.exists());
        } catch (IOException e) {
            Log.e(TAG, "Failed to export wallets", e);
        }
        MediaScannerConnection.scanFile(
                context,
                new String[]{file.getAbsolutePath()},
                null,
                null
        );
    }

    private static void exportCategories(File file, List<WalletCategory> list, Context context) {
        Log.d("CsvExporter", "Writing categories to: " + file.getAbsolutePath());
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeNext(new String[]{"id", "name", "description", "color", "icon", "isDelete"});
            for (WalletCategory c : list) {
                writer.writeNext(new String[]{
                        String.valueOf(c.getId()),
                        c.getName(),
                        c.getDescription(),
                        c.getColor(),
                        c.getIcon(),
                        String.valueOf(c.isDelete())
                });
            }
            Log.d("CsvExporter", "Categories export complete. File exists: " + file.exists());
        } catch (IOException e) {
            Log.e(TAG, "Failed to export categories", e);
        }
        MediaScannerConnection.scanFile(
                context,
                new String[]{file.getAbsolutePath()},
                null,
                null
        );
    }
}
