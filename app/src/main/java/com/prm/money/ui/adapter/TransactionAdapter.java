package com.prm.money.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.prm.money.R;
import com.prm.money.model.Transaction;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<Transaction> transactionList;
    private OnTransactionClickListener listener;

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
        void onTransactionEdit(Transaction transaction);
        void onTransactionDelete(Transaction transaction);
    }

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public List<Transaction> getTransactions() {
        return transactionList;
    }
    public void setOnTransactionClickListener(OnTransactionClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        String amountStr = ("income".equals(transaction.getType()) ? "+" : "-") + String.format("%,.0f", transaction.getAmount()) + "đ";
        holder.tvAmount.setText(amountStr);

        // Set color based on transaction type
        if ("income".equals(transaction.getType())) {
            holder.tvAmount.setTextColor(holder.tvAmount.getContext().getResources().getColor(R.color.colorIncome));
        } else {
            holder.tvAmount.setTextColor(holder.tvAmount.getContext().getResources().getColor(R.color.colorExpense));
        }

        holder.tvActivity.setText(transaction.getActivity() != null && !transaction.getActivity().isEmpty()
                ? transaction.getActivity() : "No activity");
        holder.tvDate.setText(transaction.getDate());
        holder.tvNote.setText(transaction.getNote() != null && !transaction.getNote().isEmpty()
                ? transaction.getNote() : "No notes");

        // Set click listener for the whole item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTransactionClick(transaction);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public void updateTransactions(List<Transaction> newTransactions) {
        this.transactionList = newTransactions;
        notifyDataSetChanged();
    }

    public void removeTransaction(int position) {
        transactionList.remove(position);
        notifyItemRemoved(position);
    }

    public Transaction getTransactionAt(int position) {
        return transactionList.get(position);
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvActivity, tvDate, tvNote;
        ImageView ivTransactionIcon;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvActivity = itemView.findViewById(R.id.tvActivity);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNote = itemView.findViewById(R.id.tvNote);
            ivTransactionIcon = itemView.findViewById(R.id.ivTransactionIcon);
        }
    }
}