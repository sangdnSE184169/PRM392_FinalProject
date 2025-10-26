package com.prm.money.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageButton;

import com.google.android.material.button.MaterialButton;
import com.prm.money.R;
import com.prm.money.data.repository.TransactionDao;
import com.prm.money.data.repository.TransactionService;
import com.prm.money.model.Transaction;
import com.prm.money.ui.activity.AddEditTransactionActivity;
import com.prm.money.ui.adapter.SwipeHelper;
import com.prm.money.ui.adapter.TransactionAdapter;
import com.prm.money.utils.ThemeManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TransactionListFragment extends Fragment implements
        TransactionAdapter.OnTransactionClickListener,
        SwipeHelper.SwipeHelperCallback {

    private RecyclerView rvTransactions;
    private TransactionAdapter adapter;
    private TransactionDao transactionDao;
    private TransactionService transactionService;
    private ImageButton fabAddTransaction;
    private ThemeManager themeManager;
    private boolean isAscending = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Áp dụng theme
        themeManager = new ThemeManager(requireContext());
        themeManager.applyTheme();

        View view = inflater.inflate(R.layout.fragment_transaction_list, container, false);

        rvTransactions = view.findViewById(R.id.rvTransactions);
        fabAddTransaction = view.findViewById(R.id.fab_add_transaction);

        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionDao = new TransactionDao(getContext());
        transactionService = new TransactionService(getContext());

        // Setup SwipeHelper
        SwipeHelper swipeHelper = new SwipeHelper(getContext(), this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
        itemTouchHelper.attachToRecyclerView(rvTransactions);

        // Setup FAB click listener
        fabAddTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddEditTransactionActivity.class);
            startActivity(intent);
        });
        MaterialButton btnSortAmount = view.findViewById(R.id.btnSortAmount);
        btnSortAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortTransactionsByAmount();

                // Cập nhật icon theo trạng thái
                if (isAscending) {
                    btnSortAmount.setIconResource(R.drawable.arrow_upward_24px);
                } else {
                    btnSortAmount.setIconResource(R.drawable.arrow_downward_24px);
                }

                isAscending = !isAscending; // Đảo trạng thái
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        loadTransactions();
    }
    private void sortTransactionsByAmount() {
        if (adapter == null) return; // Prevent NPE
        List<Transaction> transactionList = adapter.getTransactions(); // Get the current list from adapter
        if (isAscending) {
            Collections.sort(transactionList, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction t1, Transaction t2) {
                    return Double.compare(t1.getAmount(), t2.getAmount());
                }
            });
        } else {
            Collections.sort(transactionList, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction t1, Transaction t2) {
                    return Double.compare(t2.getAmount(), t1.getAmount());
                }
            });
        }
        adapter.updateTransactions(transactionList); // Notify adapter to update the list and UI
    }

    private void loadTransactions() {
        List<Transaction> transactionList = transactionDao.getAllTransactions();
        if (adapter == null) {
            adapter = new TransactionAdapter(transactionList);
            adapter.setOnTransactionClickListener(this);
            rvTransactions.setAdapter(adapter);
        } else {
            adapter.updateTransactions(transactionList);
        }
    }

    // TransactionAdapter.OnTransactionClickListener implementation
    @Override
    public void onTransactionClick(Transaction transaction) {
        // Open transaction details/edit
        Intent intent = new Intent(getContext(), AddEditTransactionActivity.class);
        intent.putExtra(AddEditTransactionActivity.EXTRA_TRANSACTION_ID, transaction.getId());
        startActivity(intent);
    }

    @Override
    public void onTransactionEdit(Transaction transaction) {
        Intent intent = new Intent(getContext(), AddEditTransactionActivity.class);
        intent.putExtra(AddEditTransactionActivity.EXTRA_TRANSACTION_ID, transaction.getId());
        startActivity(intent);
    }

    @Override
    public void onTransactionDelete(Transaction transaction) {
        showDeleteConfirmDialog(transaction);
    }

    // SwipeHelper.SwipeHelperCallback implementation
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (adapter == null) return; // Prevent NPE
        int position = viewHolder.getAdapterPosition();
        Transaction transaction = adapter.getTransactionAt(position);

        if (direction == ItemTouchHelper.LEFT) {
            // Swipe left - Delete
            showDeleteConfirmDialog(transaction, position);
        } else if (direction == ItemTouchHelper.RIGHT) {
            // Swipe right - Edit
            Intent intent = new Intent(getContext(), AddEditTransactionActivity.class);
            intent.putExtra(AddEditTransactionActivity.EXTRA_TRANSACTION_ID, transaction.getId());
            startActivity(intent);
            // Restore the item position
            adapter.notifyItemChanged(position);
        }
    }

    private void showDeleteConfirmDialog(Transaction transaction) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int result = transactionService.deleteTransaction(transaction.getId());
                    if (result > 0) {
                        loadTransactions(); // Refresh the list
                        Toast.makeText(getContext(), "Transaction deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to delete transaction", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmDialog(Transaction transaction, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int result = transactionService.deleteTransaction(transaction.getId());
                    if (result > 0) {
                        adapter.removeTransaction(position);
                        Toast.makeText(getContext(), "Transaction deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to delete transaction", Toast.LENGTH_SHORT).show();
                        // Restore the item position if delete fails
                        adapter.notifyItemChanged(position);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Restore the item position if user cancels
                    adapter.notifyItemChanged(position);
                })
                .show();
    }
}