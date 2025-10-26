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
import com.prm.money.R;
import com.prm.money.data.repository.WalletDao;
import com.prm.money.model.Wallet;
import com.prm.money.ui.activity.AddEditWalletActivity;
import com.prm.money.ui.adapter.SwipeHelper;
import com.prm.money.ui.adapter.WalletAdapter;
import com.prm.money.utils.ThemeManager;

import java.util.List;

public class WalletListFragment extends Fragment implements
        WalletAdapter.OnWalletClickListener,
        SwipeHelper.SwipeHelperCallback {

    private RecyclerView rvWallets;
    private WalletAdapter adapter;
    private WalletDao walletDao;
    private ImageButton fabAddWallet;
    private ThemeManager themeManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Áp dụng theme
        themeManager = new ThemeManager(requireContext());
        themeManager.applyTheme();

        View view = inflater.inflate(R.layout.fragment_wallet_list, container, false);

        rvWallets = view.findViewById(R.id.rvWallets);
        fabAddWallet = view.findViewById(R.id.fab_add_wallet);

        rvWallets.setLayoutManager(new LinearLayoutManager(getContext()));
        walletDao = new WalletDao(getContext());

        // Setup SwipeHelper
        SwipeHelper swipeHelper = new SwipeHelper(getContext(), this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
        itemTouchHelper.attachToRecyclerView(rvWallets);

        // Setup FAB click listener
        fabAddWallet.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddEditWalletActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWallets();
    }

    private void loadWallets() {
        List<Wallet> walletList = walletDao.getAllWallets();
        if (adapter == null) {
            adapter = new WalletAdapter(walletList);
            adapter.setOnWalletClickListener(this);
            rvWallets.setAdapter(adapter);
        } else {
            adapter.updateWallets(walletList);
        }
    }

    // WalletAdapter.OnWalletClickListener implementation
    @Override
    public void onWalletClick(Wallet wallet) {
        // Open wallet details/edit
        Intent intent = new Intent(getActivity(), AddEditWalletActivity.class);
        intent.putExtra(AddEditWalletActivity.EXTRA_WALLET_ID, wallet.getId());
        startActivity(intent);
    }

    @Override
    public void onWalletEdit(Wallet wallet) {
        Intent intent = new Intent(getActivity(), AddEditWalletActivity.class);
        intent.putExtra(AddEditWalletActivity.EXTRA_WALLET_ID, wallet.getId());
        startActivity(intent);
    }

    @Override
    public void onWalletDelete(Wallet wallet) {
        showDeleteConfirmDialog(wallet);
    }

    // SwipeHelper.SwipeHelperCallback implementation
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        Wallet wallet = adapter.getWalletAt(position);

        if (direction == ItemTouchHelper.LEFT) {
            // Swipe left - Delete
            showDeleteConfirmDialog(wallet, position);
        } else if (direction == ItemTouchHelper.RIGHT) {
            // Swipe right - Edit
            Intent intent = new Intent(getActivity(), AddEditWalletActivity.class);
            intent.putExtra(AddEditWalletActivity.EXTRA_WALLET_ID, wallet.getId());
            startActivity(intent);
            // Restore the item position
            adapter.notifyItemChanged(position);
        }
    }

    private void showDeleteConfirmDialog(Wallet wallet) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Wallet")
                .setMessage("Are you sure you want to delete this wallet?\n\nWallet: " + wallet.getName())
                .setPositiveButton("Delete", (dialog, which) -> {
                    walletDao.deleteWallet(wallet.getId());
                    loadWallets(); // Refresh the list
                    Toast.makeText(getContext(), "Wallet deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmDialog(Wallet wallet, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Wallet")
                .setMessage("Are you sure you want to delete this wallet?\n\nWallet: " + wallet.getName())
                .setPositiveButton("Delete", (dialog, which) -> {
                    walletDao.deleteWallet(wallet.getId());
                    adapter.removeWallet(position);
                    Toast.makeText(getContext(), "Wallet deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Restore the item position if user cancels
                    adapter.notifyItemChanged(position);
                })
                .show();
    }
}