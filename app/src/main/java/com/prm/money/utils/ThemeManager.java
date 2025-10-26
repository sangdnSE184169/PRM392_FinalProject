package com.prm.money.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {
    private static final String PREF_DARK_MODE = "dark_mode";
    private static final String PREF_FOLLOW_SYSTEM = "follow_system";

    private SharedPreferences prefs;
    private Context context;

    public ThemeManager(Context context) {
        this.context = context;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Kiểm tra xem có đang ở chế độ dark mode không
     */
    public boolean isDarkMode() {
        return prefs.getBoolean(PREF_DARK_MODE, false);
    }

    /**
     * Kiểm tra xem có theo cài đặt hệ thống không
     */
    public boolean isFollowSystem() {
        return prefs.getBoolean(PREF_FOLLOW_SYSTEM, true);
    }

    /**
     * Chuyển đổi theme
     */
    public void toggleTheme() {
        boolean currentDarkMode = isDarkMode();
        setDarkMode(!currentDarkMode);
    }

    /**
     * Đặt chế độ dark mode
     */
    public void setDarkMode(boolean isDarkMode) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_DARK_MODE, isDarkMode);
        editor.putBoolean(PREF_FOLLOW_SYSTEM, false); // Tắt follow system khi set manual
        editor.apply();

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    /**
     * Đặt theo cài đặt hệ thống
     */
    public void setFollowSystem(boolean followSystem) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_FOLLOW_SYSTEM, followSystem);
        editor.apply();

        if (followSystem) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            // Áp dụng theme đã lưu
            setDarkMode(isDarkMode());
        }
    }

    /**
     * Áp dụng theme hiện tại
     */
    public void applyTheme() {
        if (isFollowSystem()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            setDarkMode(isDarkMode());
        }
    }

    /**
     * Lấy tên theme hiện tại
     */
    public String getCurrentThemeName() {
        if (isFollowSystem()) {
            return "Theo hệ thống";
        } else if (isDarkMode()) {
            return "Chế độ tối";
        } else {
            return "Chế độ sáng";
        }
    }
}