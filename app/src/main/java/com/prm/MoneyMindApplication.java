    package com.prm.money;

    import android.app.Application;
    import com.prm.money.utils.ThemeManager;

    public class MoneyMindApplication extends Application {
    
        @Override
        public void onCreate() {
            super.onCreate();
        
            // Áp d?ng theme khi ?ng d?ng kh?i ??ng
            ThemeManager themeManager = new ThemeManager(this);
            themeManager.applyTheme();
        }
    } 