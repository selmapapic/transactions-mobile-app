package com.example.rma20celosmanovicselma04;

import android.content.Context;

import java.time.LocalDate;

public class TransactionDetailPresenter implements ITransactionDetailPresenter{
    private Context context;

    private Transaction transaction;

    public TransactionDetailPresenter (Context context) {
        this.context = context;
    }

    @Override
    public void create (LocalDate date, Double amount, String title, TransactionType type, String itemDescription, Integer transactionInterval, LocalDate endDate) {
        this.transaction = new Transaction(date, amount, title, type, itemDescription, transactionInterval, endDate);
    }

    @Override
    public Transaction getTransaction() {
        return transaction;
    }


}
