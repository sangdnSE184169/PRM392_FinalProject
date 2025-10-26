package com.prm.money.ui.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This activity is no longer in use and will be converted to a Fragment.
        // The UI and logic have been moved to MainActivity.
        finish(); // Immediately close this activity if it's ever started by mistake.
    }
}
