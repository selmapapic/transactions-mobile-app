package com.example.rma20celosmanovicselma04;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TransactionsAdapter extends ArrayAdapter<Transaction> {
    private int resource;

    public TextView title;
    public ImageView icon;
    public TextView amount;

    public TransactionsAdapter(@NonNull Context context, int _resource, @NonNull List<Transaction> objects) {
        super(context, _resource, objects);
        resource = _resource; //resource je id od layouta na kojem se nalazi list item
    }

    public Transaction getTransaction(int position) {
        return getItem(position);
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.addAll(transactions);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LinearLayout newView;
        if (convertView == null) {
            newView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater)getContext().getSystemService(inflater);
            li.inflate(resource, newView, true);
        } else {
            newView = (LinearLayout)convertView;
        }

        Transaction transaction = getItem(position);

        icon = newView.findViewById(R.id.icon);
        title = newView.findViewById(R.id.title);
        amount = newView.findViewById(R.id.amount);

        title.setText(transaction.getTitle());
        amount.setText(transaction.getAmount().toString());

        if(transaction.getType().equals(TransactionType.REGULARINCOME)) {
            icon.setImageResource(R.drawable.regular_income);
        }
        else if(transaction.getType().equals(TransactionType.INDIVIDUALINCOME)) {
            icon.setImageResource(R.drawable.individual_income);
        }
        else if(transaction.getType().equals(TransactionType.PURCHASE)) {
            icon.setImageResource(R.drawable.purchase);
        }
        else if(transaction.getType().equals(TransactionType.REGULARPAYMENT)) {
            icon.setImageResource(R.drawable.regular_payment);
        }
        else if(transaction.getType().equals(TransactionType.INDIVIDUALPAYMENT)) {
            icon.setImageResource(R.drawable.individual_payment);
        }

        return newView;
    }

}
