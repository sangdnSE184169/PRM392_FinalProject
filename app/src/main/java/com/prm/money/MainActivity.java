package com.prm.money;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import java.text.ParseException;
import android.net.Uri;
import android.os.Build;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.opencsv.CSVReader;
import com.prm.money.csv.CsvExporter;
import com.prm.money.data.repository.MonthlyGoalDao;
import com.prm.money.data.repository.TransactionDao;
import com.prm.money.data.repository.WalletCategoryDao;
import com.prm.money.data.repository.WalletDao;
import com.prm.money.model.MonthlyGoal;
import com.prm.money.model.Transaction;
import com.prm.money.model.Wallet;
import com.prm.money.model.WalletCategory;
import com.prm.money.ui.activity.AddEditTransactionActivity;
import com.prm.money.ui.activity.ThemeSettingsActivity;
import com.prm.money.ui.fragment.HomeFragment;
import com.prm.money.ui.fragment.TransactionListFragment;
import com.prm.money.ui.fragment.WalletListFragment;
import com.prm.money.ui.fragment.GoalListFragment;
import com.prm.money.utils.ThemeManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private ThemeManager themeManager;
    private FloatingActionButton toggleThemeButton;
    
    // DAO objects
    private TransactionDao transactionDao;
    private MonthlyGoalDao goalDao;
    private WalletDao walletDao;
    private WalletCategoryDao categoryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Khởi tạo ThemeManager và áp dụng theme TRƯỚC khi gọi super.onCreate()
        themeManager = new ThemeManager(this);
        themeManager.applyTheme();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DAO objects
        transactionDao = new TransactionDao(this);
        goalDao = new MonthlyGoalDao(this);
        walletDao = new WalletDao(this);
        categoryDao = new WalletCategoryDao(this);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        toggleThemeButton = findViewById(R.id.toggle_theme_button);

        setupBottomNavigation();
        setupThemeToggle();

        // Kiểm tra Intent từ GoalListActivity
        String targetFragment = getIntent().getStringExtra("fragment");

        // Load the default fragment
        if (savedInstanceState == null) {
            if (targetFragment != null) {
                handleFragmentNavigation(targetFragment);
            } else {
                loadFragment(new HomeFragment());
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reset lại selected item dựa trên fragment hiện tại
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (currentFragment instanceof HomeFragment) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else if (currentFragment instanceof TransactionListFragment) {
            bottomNavigationView.setSelectedItemId(R.id.nav_transactions);
        } else if (currentFragment instanceof WalletListFragment) {
            bottomNavigationView.setSelectedItemId(R.id.nav_wallets);
        } else if (currentFragment instanceof GoalListFragment) {
            bottomNavigationView.setSelectedItemId(R.id.nav_goals);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_transactions) {
               selectedFragment = new TransactionListFragment();
            } else if (itemId == R.id.nav_wallets) {
                selectedFragment = new WalletListFragment();
            } else if (itemId == R.id.nav_goals) {
                selectedFragment = new GoalListFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });

        fab.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, view);
            popup.getMenuInflater().inflate(R.menu.fab_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.menu_add_transaction) {
                    startActivity(new Intent(MainActivity.this, AddEditTransactionActivity.class));
                    return true;
                } else if (id == R.id.menu_export_csv) {
                    checkAndExportCsv();
                    return true;
                } else if (id == R.id.menu_import_csv) {
                    openCsvFilePicker();
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    private void setupThemeToggle() {
        // Cập nhật icon theo trạng thái hiện tại
        updateThemeIcon();

        toggleThemeButton.setOnClickListener(v -> {
            // Thêm animation tùy chỉnh
            Animation themeAnimation = AnimationUtils.loadAnimation(this, R.anim.theme_switch_animation);
            toggleThemeButton.startAnimation(themeAnimation);

            // Chuyển đổi theme
            toggleTheme();
        });
    }

    private void updateThemeIcon() {
        boolean isDarkMode = themeManager.isDarkMode();
        int iconRes = isDarkMode ? R.drawable.ic_light_mode : R.drawable.ic_dark_mode;
        toggleThemeButton.setImageDrawable(ContextCompat.getDrawable(this, iconRes));
    }

    private void toggleTheme() {
        boolean isDarkMode = themeManager.isDarkMode();
        
        if (isDarkMode) {
            // Chuyển sang light mode
            themeManager.setDarkMode(false);
            Toast.makeText(this, "Đã chuyển sang chế độ sáng", Toast.LENGTH_SHORT).show();
        } else {
            // Chuyển sang dark mode
            themeManager.setDarkMode(true);
            Toast.makeText(this, "Đã chuyển sang chế độ tối", Toast.LENGTH_SHORT).show();
        }

        // Cập nhật icon
        updateThemeIcon();

        // Tạo lại activity với animation
        recreate();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
        fragmentTransaction.commit();
    }

    private void handleFragmentNavigation(String fragmentName) {
        Fragment selectedFragment = null;
        int selectedItemId = R.id.nav_home;

        switch (fragmentName) {
            case "transactions":
                selectedFragment = new TransactionListFragment();
                selectedItemId = R.id.nav_transactions;
                break;
            case "wallets":
                selectedFragment = new WalletListFragment();
                selectedItemId = R.id.nav_wallets;
                break;
            case "goals":
                selectedFragment = new GoalListFragment();
                selectedItemId = R.id.nav_goals;
                break;
            case "home":
            default:
                selectedFragment = new HomeFragment();
                selectedItemId = R.id.nav_home;
                break;
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment);
            bottomNavigationView.setSelectedItemId(selectedItemId);
        }
    }

    private static final int IMPORT_CSV_REQUEST_CODE = 2001;

    private void openCsvFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select CSV file"), IMPORT_CSV_REQUEST_CODE);
    }
    private void checkAndExportCsv() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            exportCsv();
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        } else {
            exportCsv();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMPORT_CSV_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                String mimeType = getContentResolver().getType(fileUri);
                String fileName = getFileNameFromUri(fileUri); // Helper to get file name
                if ("text/csv".equals(mimeType) || (fileName != null && fileName.toLowerCase().endsWith(".csv"))) {
                    importCsvFromUri(fileUri);
                } else {
                    Toast.makeText(this, "Please select a .csv file", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex != -1) {
                fileName = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        return fileName;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            exportCsv();
        } else {
            Toast.makeText(this, "Permission denied. Can't export CSV.", Toast.LENGTH_SHORT).show();
        }
    }
    private void importCsvFromUri(Uri fileUri) {

        try (InputStream inputStream = getContentResolver().openInputStream(fileUri);
             InputStreamReader reader = new InputStreamReader(inputStream);
             CSVReader csvReader = new CSVReader(reader)) {

            String[] header = csvReader.readNext();
            String[] line;

            while ((line = csvReader.readNext()) != null) {

                if (line.length == 12) {
                    // Transaction
                    Transaction t = new Transaction(
                            Integer.parseInt(line[0]),
                            Double.parseDouble(line[1]),
                            line[2],
                            line[3],
                            Integer.parseInt(line[4]),
                            Integer.parseInt(line[5]),
                            line[6],
                            line[7],
                            line[8].isEmpty() ? null : Integer.parseInt(line[8]),
                            line[9],
                            line[10],
                            line[11].equals("1")
                    );
                    transactionDao.addTransaction(t);
                } else if (line.length == 10) {
                    // MonthlyGoal
                    MonthlyGoal g = new MonthlyGoal(
                            Integer.parseInt(line[0]),
                            Integer.parseInt(line[1]),
                            Integer.parseInt(line[2]),
                            Double.parseDouble(line[3]),
                            Integer.parseInt(line[4]),
                            line[5].isEmpty() ? null : Integer.parseInt(line[5]),
                            line[6],
                            line[7],
                            line[8],
                            line[9].equals("1")
                    );
                    goalDao.addMonthlyGoal(g);
                } else if (line.length == 7) {
                    // Wallet
                    Wallet w = new Wallet(
                            Integer.parseInt(line[0]),
                            line[1],
                            Double.parseDouble(line[2]),
                            line[3],
                            line[4].isEmpty() ? null : Integer.parseInt(line[4]),
                            line[5],
                            line[6].equals("1")
                    );
                    walletDao.addWallet(w);
                }
            }

            Toast.makeText(this, "Import completed!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Import failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void exportCsv() {
        List<Transaction> transactions = transactionDao.getAllTransactions();
        List<MonthlyGoal> goals = goalDao.getAllMonthlyGoals();
        List<Wallet> wallets = walletDao.getAllWallets();
        List<WalletCategory> categories = categoryDao.getAllWalletCategories();

        CsvExporter.exportAll(this, transactions, goals, wallets, categories);

        runOnUiThread(() ->
                Toast.makeText(this, "Exported to Downloads/MoneyMind/", Toast.LENGTH_LONG).show()
        );
    }
}
