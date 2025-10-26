package com.prm.money.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.prm.money.R;
import com.prm.money.data.repository.WalletCategoryDao;
import com.prm.money.data.repository.WalletDao;
import com.prm.money.model.Wallet;
import com.prm.money.model.WalletCategory;
import com.prm.money.utils.ThemeManager;

import java.util.ArrayList;
import java.util.List;

public class AddEditWalletActivity extends AppCompatActivity {

    public static final String EXTRA_WALLET_ID = "com.prm.money.EXTRA_WALLET_ID";

    private TextInputEditText etName, etBalance, etDescription;
    private Spinner spCategory;

    private WalletDao walletDao;
    private WalletCategoryDao walletCategoryDao;
    private ThemeManager themeManager;

    private List<WalletCategory> categoryList;
    private Wallet currentWallet;
    private int walletId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Áp dụng theme trước khi tạo view
        themeManager = new ThemeManager(this);
        themeManager.applyTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_wallet);

        // Khởi tạo DAO
        walletDao = new WalletDao(this);
        walletCategoryDao = new WalletCategoryDao(this);

        // Khởi tạo Views
        etName = findViewById(R.id.et_wallet_name);
        etBalance = findViewById(R.id.et_wallet_balance);
        etDescription = findViewById(R.id.et_wallet_description);
        spCategory = findViewById(R.id.spinner_wallet_category);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_add_edit_wallet);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Load Categories into Spinner
        loadCategories();

        // Kiểm tra xem là edit hay add new
        walletId = getIntent().getIntExtra(EXTRA_WALLET_ID, -1);
        if (walletId != -1) {
            setTitle("Edit Wallet");
            currentWallet = walletDao.getWallet(walletId);
            populateFields();
        } else {
            setTitle("Add New Wallet");
        }
    }

    private void loadCategories() {
        categoryList = walletCategoryDao.getAllWalletCategories();
        List<String> categoryNames = new ArrayList<>();
        for (WalletCategory category : categoryList) {
            categoryNames.add(category.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);
    }

    private void populateFields() {
        if (currentWallet != null) {
            etName.setText(currentWallet.getName());
            etBalance.setText(String.valueOf(currentWallet.getBalance()));
            etDescription.setText(currentWallet.getDescription());

            // Set spinner selection
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getId() == currentWallet.getCategoryId()) {
                    spCategory.setSelection(i);
                    break;
                }
            }
        }
    }

    private void saveWallet() {
        String name = etName.getText().toString().trim();
        String balanceStr = etBalance.getText().toString().trim();

        if (name.isEmpty() || balanceStr.isEmpty()) {
            Toast.makeText(this, "Please insert a name and balance", Toast.LENGTH_SHORT).show();
            return;
        }

        double balance = Double.parseDouble(balanceStr);
        String description = etDescription.getText().toString().trim();
        int selectedCategoryPosition = spCategory.getSelectedItemPosition();
        int categoryId = categoryList.get(selectedCategoryPosition).getId();

        Wallet wallet = new Wallet();
        wallet.setName(name);
        wallet.setBalance(balance);
        wallet.setDescription(description);
        wallet.setCategoryId(categoryId);

        if (walletId != -1) {
            wallet.setId(walletId);
            walletDao.updateWallet(wallet);
            Toast.makeText(this, "Wallet updated", Toast.LENGTH_SHORT).show();
        } else {
            walletDao.insertWallet(wallet);
            Toast.makeText(this, "Wallet saved", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void deleteWallet() {
        if (walletId != -1) {
            confirmAndDelete();
        }
    }

    private void confirmAndDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Wallet")
                .setMessage("Are you sure you want to delete this wallet?\n\nWallet: " + currentWallet.getName() + "\nBalance: " + String.format("%,.0f đ", currentWallet.getBalance()))
                .setPositiveButton("Delete", (dialog, which) -> {
                    walletDao.deleteWallet(walletId);
                    Toast.makeText(this, "Wallet deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_edit, menu);
        // Hide delete menu if it's a new wallet
        if (walletId == -1) {
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveWallet();
            return true;
        } else if (id == R.id.action_delete) {
            deleteWallet();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}