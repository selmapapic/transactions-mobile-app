package com.example.rma20celosmanovicselma04;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class TransactionsPresenter implements ITransactionsPresenter {
    private ITransactionsView view;
    private ITransactionsInteractor interactor;
    private Context context;

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    public String changeMonthForward () {
        interactor.nextMonth();
        return interactor.turnToString();
    }

    public String changeMonthBackward () {
        interactor.previousMonth();
        return interactor.turnToString();
    }

    public String dateToString () {
        return interactor.turnToString();
    }
}
