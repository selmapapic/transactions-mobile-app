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

    @Override
    public void refreshTransactions() {
        view.setTransactions(interactor.getTransactions());
        view.notifyMovieListDataSetChanged();
    }

    public void changeMonthForward () {
        interactor.setCurrentDate(interactor.getCurrentDate().plusMonths(1));
        view.refreshDate(dateToString(interactor.getCurrentDate()));
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
    }
}
