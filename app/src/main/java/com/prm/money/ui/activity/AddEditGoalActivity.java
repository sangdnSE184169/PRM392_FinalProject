package com.prm.money.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.prm.money.R;
import com.prm.money.data.repository.MonthlyGoalDao;
import com.prm.money.model.MonthlyGoal;
import com.prm.money.utils.ThemeManager;

public class AddEditGoalActivity extends AppCompatActivity {
    public static final String EXTRA_GOAL_ID = "goal_id";
    private EditText etGoalAmount, etGoalNote;
    private Button btnSaveGoal;
    private MonthlyGoalDao goalDao;
    private MonthlyGoal goal;
    private ThemeManager themeManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Áp dụng theme trước khi tạo view
        themeManager = new ThemeManager(this);
        themeManager.applyTheme();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_goal);

        etGoalAmount = findViewById(R.id.etGoalAmount);
        etGoalNote = findViewById(R.id.etGoalNote);
        btnSaveGoal = findViewById(R.id.btnSaveGoal);
        goalDao = new MonthlyGoalDao(this);

        int goalId = getIntent().getIntExtra(EXTRA_GOAL_ID, -1);
        if (goalId != -1) {
            goal = goalDao.getMonthlyGoalById(goalId);
            if (goal != null) {
                etGoalAmount.setText(String.valueOf((int)goal.getGoalAmount()));
                etGoalNote.setText(goal.getNote());
            }
        }

        btnSaveGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGoal();
            }
        });
    }

    private void saveGoal() {
        String amountStr = etGoalAmount.getText().toString().trim();
        if (TextUtils.isEmpty(amountStr)) {
            etGoalAmount.setError("Nhập số tiền mục tiêu");
            return;
        }
        double amount = Double.parseDouble(amountStr);
        String note = etGoalNote.getText().toString().trim();
        if (goal != null) {
            goal.setGoalAmount(amount);
            goal.setNote(note);
            goal.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            goalDao.insertOrUpdateMonthlyGoal(goal);
            Toast.makeText(this, "Đã lưu mục tiêu!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
