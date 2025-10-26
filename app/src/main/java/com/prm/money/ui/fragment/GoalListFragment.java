package com.prm.money.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import android.widget.ImageButton;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.prm.money.R;
import com.prm.money.data.repository.MonthlyGoalDao;
import com.prm.money.data.repository.TransactionDao;
import com.prm.money.data.repository.TransactionService;
import com.prm.money.data.repository.WalletCategoryDao;
import com.prm.money.model.MonthlyGoal;
import com.prm.money.model.Transaction;
import com.prm.money.model.WalletCategory;
import com.prm.money.ui.activity.AddEditGoalActivity;
import com.prm.money.ui.adapter.GoalAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GoalListFragment extends Fragment {
    private RecyclerView rvGoals;
    private GoalAdapter adapter;
    private MonthlyGoalDao goalDao;
    private TransactionDao transactionDao;
    private TransactionService transactionService;
    private BarChart barChart;
    private Button btnPrevMonth, btnNextMonth;
    private TextView tvCurrentMonth;
    private ImageButton fabAddGoal;
    
    private int currentMonth, currentYear;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal_list, container, false);

        initViews(view);
        initCurrentDate();
        setupChart();
        setupMonthNavigation();
        loadData();

        return view;
    }

    private void initViews(View view) {
        rvGoals = view.findViewById(R.id.rvGoals);
        rvGoals.setLayoutManager(new LinearLayoutManager(getContext()));
        
        barChart = view.findViewById(R.id.barChart);
        btnPrevMonth = view.findViewById(R.id.btnPrevMonth);
        btnNextMonth = view.findViewById(R.id.btnNextMonth);
        tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth);
        fabAddGoal = view.findViewById(R.id.fab_add_goal);
        
        goalDao = new MonthlyGoalDao(getContext());
        transactionDao = new TransactionDao(getContext());
        transactionService = new TransactionService(getContext());
        
        // Setup FAB click listener
        fabAddGoal.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddEditGoalActivity.class);
            startActivity(intent);
        });
    }

    private void initCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH) + 1;
        currentYear = calendar.get(Calendar.YEAR);
        updateCurrentMonthText();
    }

    private void setupChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        // Apply dark/light theme colors dynamically
        int textColor = ContextCompat.getColor(requireContext(), R.color.colorText);
        int bgColor = ContextCompat.getColor(requireContext(), R.color.colorBackground);

        barChart.setBackgroundColor(bgColor);
        barChart.getXAxis().setTextColor(textColor);
        barChart.getAxisLeft().setTextColor(textColor);
        barChart.getLegend().setTextColor(textColor);
    }

    private void setupMonthNavigation() {
        btnPrevMonth.setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth < 1) {
                currentMonth = 12;
                currentYear--;
            }
            updateCurrentMonthText();
            loadData();
        });
        
        btnNextMonth.setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth > 12) {
                currentMonth = 1;
                currentYear++;
            }
            updateCurrentMonthText();
            loadData();
        });
    }

    private void updateCurrentMonthText() {
        tvCurrentMonth.setText("Tháng " + currentMonth + "/" + currentYear);
    }

    private void loadData() {
        createMonthlyGoalsIfNeeded();
        loadGoals();
        loadChartData();
    }

    private void createMonthlyGoalsIfNeeded() {
        WalletCategoryDao categoryDao = new WalletCategoryDao(getContext());
        List<WalletCategory> categories = categoryDao.getAllActiveWalletCategories();
        String now = String.valueOf(System.currentTimeMillis());

        for (WalletCategory cat : categories) {
            if (goalDao.getMonthlyGoalByCategoryAndTime(cat.getId(), currentMonth, currentYear) == null) {
                MonthlyGoal newGoal = new MonthlyGoal(0, currentMonth, currentYear, 0, cat.getId(), null, "", now, now, false);
                goalDao.insertOrUpdateMonthlyGoal(newGoal);
            }
        }
    }

    private void loadGoals() {
        List<MonthlyGoal> goalList = goalDao.getMonthlyGoalsByTime(currentMonth, currentYear);
        WalletCategoryDao categoryDao = new WalletCategoryDao(getContext());
        adapter = new GoalAdapter(goalList, categoryDao, transactionService);
        rvGoals.setAdapter(adapter);
    }

    private void loadChartData() {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        
        // Lấy dữ liệu 6 tháng gần nhất
        for (int i = 5; i >= 0; i--) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MONTH, currentMonth - 1);
            cal.set(Calendar.YEAR, currentYear);
            cal.add(Calendar.MONTH, -i);
            
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            
            double totalExpense = getTotalExpenseForMonth(month, year);
            entries.add(new BarEntry(5 - i, (float) totalExpense));
            labels.add(month + "/" + year);
        }
        
        BarDataSet dataSet = new BarDataSet(entries, "Chi tiêu");
        dataSet.setColor(Color.parseColor("#FF5722"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);
        
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f);
        
        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setLabelCount(labels.size());
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private double getTotalExpenseForMonth(int month, int year) {
        List<Transaction> transactions = transactionDao.getAllTransactions();
        double total = 0;
        
        for (Transaction transaction : transactions) {
            if (transaction.getType().equals("expense") && !transaction.isDelete()) {
                // Parse date string "yyyy-MM-dd" và kiểm tra tháng/năm
                String[] dateParts = transaction.getDate().split("-");
                if (dateParts.length == 3) {
                    int transYear = Integer.parseInt(dateParts[0]);
                    int transMonth = Integer.parseInt(dateParts[1]);
                    
                    if (transMonth == month && transYear == year) {
                        total += transaction.getAmount();
                    }
                }
            }
        }
        
        return total;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}
