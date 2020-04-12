package com.example.rma20celosmanovicselma04;

import android.content.Context;

public class BudgetPresenter implements IBudgetPresenter {
    private IBudgetView view;
    private static ITransactionsInteractor interactor;
    private Context context;

    public BudgetPresenter(IBudgetView view, Context context) {
        this.view = view;
        this.interactor = new TransactionsIntreactor();
        this.context = context;
    }


}
