package com.example.rma20celosmanovicselma04;

import android.content.Context;

public class TransactionsPresenter implements ITransactionsPresenter {
    private ITransactionsView view;
    private ITransactionsInteractor interactor;
    private Context context;

    public TransactionsPresenter(ITransactionsView view, Context context) {
        this.view = view;
        this.interactor = new TransactionsIntreactor();
        this.context = context;
    }

    @Override
    public void refreshTransactions() {

    }
}
