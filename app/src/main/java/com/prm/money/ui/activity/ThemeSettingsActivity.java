package com.prm.money.ui.activity;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.prm.money.R;
import com.prm.money.utils.ThemeManager;

public class ThemeSettingsActivity extends AppCompatActivity {
    
    private ThemeManager themeManager;
    private RadioGroup themeRadioGroup;
    private RadioButton lightModeRadio;
    private RadioButton darkModeRadio;
    private RadioButton systemModeRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Áp dụng theme trước khi tạo view
        themeManager = new ThemeManager(this);
        themeManager.applyTheme();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_settings);

        setupToolbar();
        setupViews();
        loadCurrentTheme();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Cài đặt giao diện");
        }
    }

    private void setupViews() {
        themeRadioGroup = findViewById(R.id.theme_radio_group);
        lightModeRadio = findViewById(R.id.light_mode_radio);
        darkModeRadio = findViewById(R.id.dark_mode_radio);
        systemModeRadio = findViewById(R.id.system_mode_radio);

        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.light_mode_radio) {
                themeManager.setFollowSystem(false);
                themeManager.setDarkMode(false);
                Toast.makeText(this, "Đã chuyển sang chế độ sáng", Toast.LENGTH_SHORT).show();
                recreate();
            } else if (checkedId == R.id.dark_mode_radio) {
                themeManager.setFollowSystem(false);
                themeManager.setDarkMode(true);
                Toast.makeText(this, "Đã chuyển sang chế độ tối", Toast.LENGTH_SHORT).show();
                recreate();
            } else if (checkedId == R.id.system_mode_radio) {
                themeManager.setFollowSystem(true);
                Toast.makeText(this, "Đã chuyển sang theo hệ thống", Toast.LENGTH_SHORT).show();
                recreate();
            }
        });
    }

    private void loadCurrentTheme() {
        if (themeManager.isFollowSystem()) {
            systemModeRadio.setChecked(true);
        } else if (themeManager.isDarkMode()) {
            darkModeRadio.setChecked(true);
        } else {
            lightModeRadio.setChecked(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
