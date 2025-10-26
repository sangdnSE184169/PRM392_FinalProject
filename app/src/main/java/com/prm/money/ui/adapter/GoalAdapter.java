package com.prm.money.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.prm.money.R;
import com.prm.money.data.repository.TransactionService;
import com.prm.money.data.repository.WalletCategoryDao;
import com.prm.money.model.MonthlyGoal;
import com.prm.money.model.WalletCategory;
import com.prm.money.ui.activity.AddEditGoalActivity;
import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {
    private List<MonthlyGoal> goalList;
    private WalletCategoryDao categoryDao;
    private TransactionService transactionService;

    public GoalAdapter(List<MonthlyGoal> goalList, WalletCategoryDao categoryDao, TransactionService transactionService) {
        this.goalList = goalList;
        this.categoryDao = categoryDao;
        this.transactionService = transactionService;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        MonthlyGoal goal = goalList.get(position);
        
        // Lấy tên category
        WalletCategory category = categoryDao.getWalletCategoryById(goal.getCategoryId());
        String categoryName = category != null ? category.getName() : "Unknown Category";
        holder.tvCategoryName.setText(categoryName);
        
        // Lấy spent amount từ TransactionService
        double spentAmount = transactionService.getGoalSpentAmount(goal.getId());
        double goalAmount = goal.getGoalAmount();
        
        holder.tvGoalAmount.setText("Target: " + String.format("%,.0fđ", goalAmount));
        holder.tvSpentAmount.setText("Spent: " + String.format("%,.0fđ", spentAmount));
        holder.tvGoalMonthYear.setText("Month " + goal.getMonth() + "/" + goal.getYear());
        holder.tvGoalNote.setText(goal.getNote() != null && !goal.getNote().isEmpty() ? "Note: " + goal.getNote() : "");
        
        // Calculate and set progress
        int progress = goalAmount > 0 ? (int) ((spentAmount / goalAmount) * 100) : 0;
        holder.progressBar.setProgress(Math.min(progress, 100)); // Cap at 100%
        holder.tvProgress.setText(progress + "%");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AddEditGoalActivity.class);
            intent.putExtra(AddEditGoalActivity.EXTRA_GOAL_ID, goal.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }

    public static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvGoalAmount, tvSpentAmount, tvGoalMonthYear, tvGoalNote, tvProgress;
        ProgressBar progressBar;
        
        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvGoalAmount = itemView.findViewById(R.id.tvGoalAmount);
            tvSpentAmount = itemView.findViewById(R.id.tvSpentAmount);
            tvGoalMonthYear = itemView.findViewById(R.id.tvGoalMonthYear);
            tvGoalNote = itemView.findViewById(R.id.tvGoalNote);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
