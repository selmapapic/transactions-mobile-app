package com.example.rma20celosmanovicselma04.transactionsList;

import java.time.LocalDate;

public interface ITransactionsPresenter {
    void changeMonthForward ();
    void changeMonthBackward ();
    String dateToString (LocalDate date);
    void start();
    void setCurrentBudget ();
    void searchTransactions(String query);
    void searchAccount(String query);
    void refreshAllTransactions(String filter, String sort);
}
