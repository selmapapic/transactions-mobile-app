package com.example.rma20celosmanovicselma04;

import java.time.LocalDate;

public interface ITransactionsPresenter {
    void refreshTransactions();
    void changeMonthForward ();
    void changeMonthBackward ();
    String dateToString (LocalDate date);
    void start();
    void refreshTransactionsByMonthAndYear();
}
