package com.example.rma20celosmanovicselma04.budget;

import android.content.Context;

import com.example.rma20celosmanovicselma04.data.ITransactionsInteractor;
import com.example.rma20celosmanovicselma04.data.TransactionsIntreactor;

public class BudgetPresenter implements IBudgetPresenter {
    private IBudgetView view;
    private static ITransactionsInteractor interactor;
    private Context context;

    public BudgetPresenter(IBudgetView view, Context context) {
        this.view = view;
        this.interactor = new TransactionsIntreactor();
        this.context = context;
    }

    public void start () {
        view.setBudgetText(interactor.getCurrentBudget(true));
        view.setTotalLimitFld(interactor.getAccount().getTotalLimit());
        view.setMonthLimitFld(interactor.getAccount().getMonthLimit());
    }

    @Override
    public void saveNewChanges(Double totalLimit, Double monthLimit) {
        interactor.getAccount().setTotalLimit(totalLimit);
        interactor.getAccount().setMonthLimit(monthLimit);
    }

    @Override
    public void refreshFields() {
        view.setTotalLimitFld(interactor.getAccount().getTotalLimit());
        view.setMonthLimitFld(interactor.getAccount().getMonthLimit());
    }
}
