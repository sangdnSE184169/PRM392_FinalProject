package com.prm.money.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.prm.money.R;
import com.prm.money.model.Wallet;
import java.util.List;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.WalletViewHolder> {
    private List<Wallet> wallets;
    private OnWalletClickListener listener;

    public interface OnWalletClickListener {
        void onWalletClick(Wallet wallet);
        void onWalletEdit(Wallet wallet);
        void onWalletDelete(Wallet wallet);
    }

    public WalletAdapter(List<Wallet> wallets) {
        this.wallets = wallets;
    }

    public void setOnWalletClickListener(OnWalletClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public WalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet, parent, false);
        return new WalletViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletViewHolder holder, int position) {
        Wallet wallet = wallets.get(position);
        holder.bind(wallet);
        
        // Set click listener for the whole item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onWalletClick(wallet);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wallets.size();
    }

    public void updateWallets(List<Wallet> newWallets) {
        this.wallets = newWallets;
        notifyDataSetChanged();
    }

    public void removeWallet(int position) {
        wallets.remove(position);
        notifyItemRemoved(position);
    }

    public Wallet getWalletAt(int position) {
        return wallets.get(position);
    }

    public class WalletViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvWalletName;
        private final TextView tvWalletBalance;
        private final TextView tvWalletDescription;
        private final ImageView ivWalletIcon;

        public WalletViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWalletName = itemView.findViewById(R.id.tvWalletName);
            tvWalletBalance = itemView.findViewById(R.id.tvWalletBalance);
            tvWalletDescription = itemView.findViewById(R.id.tvWalletDescription);
            ivWalletIcon = itemView.findViewById(R.id.ivWalletIcon);
        }

        public void bind(Wallet wallet) {
            tvWalletName.setText(wallet.getName());
            tvWalletBalance.setText(String.format("%,.0f đ", wallet.getBalance()));
            
            // Set description text
            if (wallet.getDescription() != null && !wallet.getDescription().isEmpty()) {
                tvWalletDescription.setText(wallet.getDescription());
                tvWalletDescription.setVisibility(View.VISIBLE);
            } else {
                tvWalletDescription.setText("No description");
                tvWalletDescription.setVisibility(View.VISIBLE);
            }
        }
    }

    // Legacy interface for backward compatibility
    public interface OnItemClickListener {
        void onItemClick(Wallet wallet);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.setOnWalletClickListener(new OnWalletClickListener() {
            @Override
            public void onWalletClick(Wallet wallet) {
                listener.onItemClick(wallet);
            }

            @Override
            public void onWalletEdit(Wallet wallet) {
                listener.onItemClick(wallet);
            }

            @Override
            public void onWalletDelete(Wallet wallet) {
                // Do nothing for legacy interface
            }
        });
    }
}
