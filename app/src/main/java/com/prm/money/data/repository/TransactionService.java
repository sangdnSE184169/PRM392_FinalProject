package com.prm.money.data.repository;

import android.content.Context;

import com.prm.money.model.MonthlyGoal;
import com.prm.money.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Service class to handle transaction operations and update related wallet/goal balances
 */
public class TransactionService {
    private TransactionDao transactionDao;
    private WalletDao walletDao;
    private MonthlyGoalDao goalDao;

    public TransactionService(Context context) {
        this.transactionDao = new TransactionDao(context);
        this.walletDao = new WalletDao(context);
        this.goalDao = new MonthlyGoalDao(context);
    }

    /**
     * Add a new transaction and update wallet balance
     * @param transaction The transaction to add
     * @return The ID of the newly added transaction, or -1 if failed
     */
    public long addTransaction(Transaction transaction) {
        long transactionId = transactionDao.addTransaction(transaction);

        if (transactionId != -1) {
            // Update wallet balance
            updateWalletBalanceForTransaction(transaction, true);

            // Update goal progress if transaction has monthlyGoalId
            if (transaction.getMonthlyGoalId() != null) {
                updateGoalProgressForTransaction(transaction, true);
            }
        }

        return transactionId;
    }

    /**
     * Update an existing transaction and adjust wallet balance
     * @param oldTransaction The original transaction
     * @param newTransaction The updated transaction
     * @return Number of rows affected
     */
    public int updateTransaction(Transaction oldTransaction, Transaction newTransaction) {
        // Revert old transaction effects
        updateWalletBalanceForTransaction(oldTransaction, false);
        if (oldTransaction.getMonthlyGoalId() != null) {
            updateGoalProgressForTransaction(oldTransaction, false);
        }

        // Apply new transaction
        int result = transactionDao.updateTransaction(newTransaction);

        if (result > 0) {
            // Apply new transaction effects
            updateWalletBalanceForTransaction(newTransaction, true);
            if (newTransaction.getMonthlyGoalId() != null) {
                updateGoalProgressForTransaction(newTransaction, true);
            }
        }

        return result;
    }

    /**
     * Delete a transaction and update wallet balance
     * @param transactionId The transaction ID to delete
     * @return Number of rows affected
     */
    public int deleteTransaction(int transactionId) {
        // Get the transaction before deleting
        Transaction transaction = transactionDao.getTransaction(transactionId);

        if (transaction != null) {
            // Delete the transaction
            int result = transactionDao.deleteTransaction(transactionId);

            if (result > 0) {
                // Revert transaction effects
                updateWalletBalanceForTransaction(transaction, false);
                if (transaction.getMonthlyGoalId() != null) {
                    updateGoalProgressForTransaction(transaction, false);
                }
            }

            return result;
        }

        return 0;
    }

    /**
     * Update wallet balance based on a transaction
     * @param transaction The transaction
     * @param isAdding True if adding transaction effect, false if removing
     */
    private void updateWalletBalanceForTransaction(Transaction transaction, boolean isAdding) {
        double amount = transaction.getAmount();

        // Determine the effect on wallet balance
        if ("expense".equals(transaction.getType())) {
            amount = -amount; // Expenses decrease balance
        }
        // Income increases balance (amount stays positive)

        if (!isAdding) {
            amount = -amount; // Reverse the effect
        }

        // Update wallet balance
        walletDao.updateWalletBalance(transaction.getWalletId(), amount);
    }

    /**
     * Update goal progress based on a transaction (only for expenses)
     * @param transaction The transaction
     * @param isAdding True if adding transaction effect, false if removing
     */
    private void updateGoalProgressForTransaction(Transaction transaction, boolean isAdding) {
        // Only process expense transactions for goal tracking
        if (!"expense".equals(transaction.getType())) {
            return;
        }

        // For goals, we track spending, so expenses increase spent amount
        // This is just tracking - actual goal spent calculation should be done via queries
        // as goals don't have spent fields in the current model
    }

    /**
     * Find and associate transaction with appropriate monthly goal based on category and date
     * @param transaction The transaction to check
     * @return The goal ID if found, null otherwise
     */
    public Integer findMatchingGoalId(Transaction transaction) {
        if (!"expense".equals(transaction.getType())) {
            return null; // Only expenses can be tracked against goals
        }

        try {
            // Parse transaction date to get month and year
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = sdf.parse(transaction.getDate());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            int month = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based
            int year = cal.get(Calendar.YEAR);

            // Find goal for this category and time period
            MonthlyGoal goal = goalDao.getMonthlyGoalByCategoryAndTime(
                    transaction.getCategoryId(), month, year);

            return goal != null ? goal.getId() : null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Recalculate wallet balance based on all its transactions
     * @param walletId The wallet ID
     */
    public void recalculateWalletBalance(int walletId) {
        walletDao.recalculateWalletBalance(walletId);
    }

    /**
     * Get spent amount for a specific goal
     * @param goalId The goal ID
     * @return Total spent amount
     */
    public double getGoalSpentAmount(int goalId) {
        return transactionDao.getTotalExpenseByMonthlyGoal(goalId);
    }
}