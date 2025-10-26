package com.prm.money.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.prm.money.R;
import com.prm.money.data.repository.MonthlyGoalDao;
import com.prm.money.data.repository.TransactionDao;
import com.prm.money.model.MonthlyGoal;
import com.prm.money.model.Transaction;
import com.prm.money.ui.adapter.TransactionAdapter;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private TextView tvTotalSpending, tvSpendingProgress;
    private ProgressBar progressBarSpending;
    private RecyclerView rvRecentTransactions;
    private TransactionAdapter transactionAdapter;
    private TransactionDao transactionDao;
    private MonthlyGoalDao goalDao;

    private boolean isAscending = true;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initDaos();
        initViews(view);
        loadDashboardData();
        loadRecentTransactions();

        // L?y tham chi?u ?úng ki?u c?a MaterialButton
        MaterialButton btnSortAmount = view.findViewById(R.id.btnSortAmount1);
        btnSortAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortTransactionsByAmount();

                // C?p nh?t icon theo tr?ng thái
                if (isAscending) {
                    btnSortAmount.setIconResource(R.drawable.arrow_upward_24px);
                } else {
                    btnSortAmount.setIconResource(R.drawable.arrow_downward_24px);
                }

                isAscending = !isAscending; // ??o tr?ng thái
            }
        });

        return view;
    }


    private void initDaos() {
        transactionDao = new TransactionDao(getContext());
        goalDao = new MonthlyGoalDao(getContext());
    }
    private void sortTransactionsByAmount() {
        if (transactionAdapter == null) return; // Prevent NPE
        List<Transaction> transactionList = transactionAdapter.getTransactions(); // Get the current list from adapter
        if (isAscending) {
            Collections.sort(transactionList, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction t1, Transaction t2) {
                    return Double.compare(t1.getAmount(), t2.getAmount());
                }
            });
        } else {
            Collections.sort(transactionList, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction t1, Transaction t2) {
                    return Double.compare(t2.getAmount(), t1.getAmount());
                }
            });
        }
        transactionAdapter.updateTransactions(transactionList); // Notify adapter to update the list and UI
    }

    private void initViews(View view) {
        tvTotalSpending = view.findViewById(R.id.tvTotalSpending);
        tvSpendingProgress = view.findViewById(R.id.tvSpendingProgress);
        progressBarSpending = view.findViewById(R.id.progressBarSpending);
        rvRecentTransactions = view.findViewById(R.id.rvRecentTransactions);

        rvRecentTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecentTransactions.setNestedScrollingEnabled(false);
    }

    private void loadDashboardData() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        double totalExpense = transactionDao.getTotalExpenseByMonthYear(month, year);
        tvTotalSpending.setText(formatCurrency(totalExpense));

        List<MonthlyGoal> goals = goalDao.getMonthlyGoalsByTime(month, year);
        double goalAmount = 0;
        if (!goals.isEmpty()) {
            for(MonthlyGoal goal : goals) {
                goalAmount += goal.getGoalAmount();
            }
        }

        tvSpendingProgress.setText(String.format("%s / %s", formatCurrency(totalExpense), formatCurrency(goalAmount)));

        int percent = (goalAmount > 0) ? (int) Math.min(100, (totalExpense * 100 / goalAmount)) : 0;
        progressBarSpending.setProgress(percent);
    }

    private void loadRecentTransactions() {
        List<Transaction> recentTransactions = transactionDao.getAllTransactions();
        if (recentTransactions.size() > 5) {
            recentTransactions = recentTransactions.subList(0, 5);
        }
        transactionAdapter = new TransactionAdapter(recentTransactions);
        rvRecentTransactions.setAdapter(transactionAdapter);
    }

    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }
} 