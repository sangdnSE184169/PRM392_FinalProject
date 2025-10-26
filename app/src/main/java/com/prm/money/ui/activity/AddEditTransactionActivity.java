package com.prm.money.ui.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.prm.money.R;
import com.prm.money.data.repository.TransactionDao;
import com.prm.money.data.repository.TransactionService;
import com.prm.money.data.repository.WalletCategoryDao;
import com.prm.money.data.repository.WalletDao;
import com.prm.money.model.Transaction;
import com.prm.money.model.Wallet;
import com.prm.money.model.WalletCategory;
import com.prm.money.utils.ThemeManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class AddEditTransactionActivity extends AppCompatActivity {
    public static final String EXTRA_TRANSACTION_ID = "EXTRA_TRANSACTION_ID";
    private static final int INVALID_ID = -1;

    private EditText etAmount, etDate, etNote, etActivity;
    private RadioButton rbExpense, rbIncome;
    private Spinner spinnerCategory, spinnerWallet;

    private TransactionDao transactionDao;
    private TransactionService transactionService;
    private WalletDao walletDao;
    private WalletCategoryDao categoryDao;
    private ThemeManager themeManager;

    private Transaction currentTransaction;
    private int transactionId = INVALID_ID;
    private Calendar selectedDate = Calendar.getInstance();

    private List<Wallet> walletList;
    private List<WalletCategory> categoryList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Áp d?ng theme tr??c khi t?o view
        themeManager = new ThemeManager(this);
        themeManager.applyTheme();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_transaction);

        initDaos();
        initViews();
        setupDatePicker();

        loadSpinnersData();

        transactionId = getIntent().getIntExtra(EXTRA_TRANSACTION_ID, INVALID_ID);
        if (transactionId == INVALID_ID) {
            setTitle("Add Transaction");
            currentTransaction = null;
        } else {
            setTitle("Edit Transaction");
            loadExistingTransaction();
        }
    }

    private void initDaos() {
        transactionDao = new TransactionDao(this);
        transactionService = new TransactionService(this);
        walletDao = new WalletDao(this);
        categoryDao = new WalletCategoryDao(this);
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etAmount = findViewById(R.id.etAmount);
        etActivity = findViewById(R.id.etActivity);
        etDate = findViewById(R.id.etDate);
        etNote = findViewById(R.id.etNote);
        rbExpense = findViewById(R.id.rbExpense);
        rbIncome = findViewById(R.id.rbIncome);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerWallet = findViewById(R.id.spinnerWallet);

        updateDateInView();
    }

    private void setupDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, month);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateInView();
        };

        etDate.setOnClickListener(v -> new DatePickerDialog(this, dateSetListener,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)).show());
    }


    private void loadSpinnersData() {
        walletList = walletDao.getAllWallets();
        List<String> walletNames = walletList.stream().map(Wallet::getName).collect(Collectors.toList());
        ArrayAdapter<String> walletAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, walletNames);
        walletAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWallet.setAdapter(walletAdapter);

        categoryList = categoryDao.getAllWalletCategories();
        List<String> categoryNames = categoryList.stream().map(WalletCategory::getName).collect(Collectors.toList());
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    private void updateDateInView() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        etDate.setText(sdf.format(selectedDate.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_edit, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        deleteItem.setVisible(transactionId != INVALID_ID);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_save) {
            saveTransaction();
            return true;
        } else if (id == R.id.action_delete) {
            confirmAndDelete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void saveTransaction() {
        // Validation
        if (etAmount.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(etAmount.getText().toString());
        String activity = etActivity.getText().toString().trim();
        String type = rbExpense.isChecked() ? "expense" : "income";
        String date = etDate.getText().toString();
        String note = etNote.getText().toString().trim();
        
        int selectedWalletPos = spinnerWallet.getSelectedItemPosition();
        if (selectedWalletPos < 0 || selectedWalletPos >= walletList.size()){
            Toast.makeText(this, "Please select a wallet", Toast.LENGTH_SHORT).show();
            return;
        }
        int walletId = walletList.get(selectedWalletPos).getId();

        int selectedCategoryPos = spinnerCategory.getSelectedItemPosition();
        if (selectedCategoryPos < 0 || selectedCategoryPos >= categoryList.size()){
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }
        int categoryId = categoryList.get(selectedCategoryPos).getId();
        
        if (transactionId == INVALID_ID) {
            // Add new transaction
            Transaction newTransaction = new Transaction(0, amount, date, note, walletId, categoryId, type, activity, null, "", "", false);
            
            // Try to find matching goal based on category and date
            Integer matchingGoalId = transactionService.findMatchingGoalId(newTransaction);
            newTransaction.setMonthlyGoalId(matchingGoalId);
            
            long result = transactionService.addTransaction(newTransaction);
            
            if(result != -1) {
                Toast.makeText(this, "Transaction saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save transaction", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Update existing transaction
            Transaction oldTransaction = new Transaction(
                currentTransaction.getId(),
                currentTransaction.getAmount(),
                currentTransaction.getDate(),
                currentTransaction.getNote(),
                currentTransaction.getWalletId(),
                currentTransaction.getCategoryId(),
                currentTransaction.getType(),
                currentTransaction.getActivity(),
                currentTransaction.getMonthlyGoalId(),
                currentTransaction.getCreatedAt(),
                currentTransaction.getUpdatedAt(),
                currentTransaction.isDelete()
            );
            
            currentTransaction.setAmount(amount);
            currentTransaction.setActivity(activity);
            currentTransaction.setType(type);
            currentTransaction.setDate(date);
            currentTransaction.setNote(note);
            currentTransaction.setWalletId(walletId);
            currentTransaction.setCategoryId(categoryId);
            
            // Try to find matching goal based on new category and date
            Integer matchingGoalId = transactionService.findMatchingGoalId(currentTransaction);
            currentTransaction.setMonthlyGoalId(matchingGoalId);
            
            int result = transactionService.updateTransaction(oldTransaction, currentTransaction);
            
            if(result > 0) {
                Toast.makeText(this, "Transaction updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update transaction", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadExistingTransaction() {
        currentTransaction = transactionDao.getTransaction(transactionId);
        if (currentTransaction != null) {
            populateFields();
        }
    }

    private void populateFields() {
        etAmount.setText(String.valueOf(currentTransaction.getAmount()));
        etActivity.setText(currentTransaction.getActivity());
        etDate.setText(currentTransaction.getDate());
        etNote.setText(currentTransaction.getNote());
        
        // Set transaction type
        if ("income".equals(currentTransaction.getType())) {
            rbIncome.setChecked(true);
        } else {
            rbExpense.setChecked(true);
        }
        
        // Set wallet selection
        for (int i = 0; i < walletList.size(); i++) {
            if (walletList.get(i).getId() == currentTransaction.getWalletId()) {
                spinnerWallet.setSelection(i);
                break;
            }
        }
        
        // Set category selection
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getId() == currentTransaction.getCategoryId()) {
                spinnerCategory.setSelection(i);
                break;
            }
        }
        
        // Parse and set date
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            selectedDate.setTime(sdf.parse(currentTransaction.getDate()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confirmAndDelete() {
        new AlertDialog.Builder(this)
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction?")
            .setPositiveButton("Delete", (dialog, which) -> {
                int result = transactionService.deleteTransaction(transactionId);
                if (result > 0) {
                    Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to delete transaction", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
} 