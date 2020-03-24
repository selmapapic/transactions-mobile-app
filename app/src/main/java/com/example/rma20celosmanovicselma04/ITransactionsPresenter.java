package com.example.rma20celosmanovicselma04;

public interface ITransactionsPresenter {
    void refreshTransactions();
    String changeMonthForward ();
    String changeMonthBackward ();
    String dateToString ();
}
