package com.example.rma20celosmanovicselma04;

import android.content.Context;

import java.time.LocalDate;


public class TransactionsPresenter implements ITransactionsPresenter {
    private ITransactionsView view;
    private ITransactionsInteractor interactor;
    private Context context;

    public TransactionsPresenter(ITransactionsView view, Context context) {
        this.view = view;
        this.interactor = new TransactionsIntreactor();
        this.context = context;
    }

    public void refreshTransactions() {
        view.setTransactions(interactor.getTransactions());
        view.notifyTransactionsListDataSetChanged();
    }

    public void refreshTransactionsByMonthAndYear () {
        view.setTransactions(interactor.getTransactionsByMonthAndYear());
        view.notifyTransactionsListDataSetChanged();
    }

    public void changeMonthForward () {
        interactor.setCurrentDate(interactor.getCurrentDate().plusMonths(1));
        view.refreshDate(dateToString(interactor.getCurrentDate()));
        System.out.println(interactor.getCurrentDate());
    }

    public void changeMonthBackward () {
        interactor.setCurrentDate(interactor.getCurrentDate().minusMonths(1));
        view.refreshDate(dateToString(interactor.getCurrentDate()));
    }

    public String dateToString (LocalDate date) {
        return date.getMonth().name() + ", " + date.getYear();
    }

    public void start () {
        view.refreshDate(dateToString(interactor.getCurrentDate()));
        view.setFilterSpinner(interactor.getTypes());
    }
}
