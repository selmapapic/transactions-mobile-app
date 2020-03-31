package com.example.rma20celosmanovicselma04;

import java.time.LocalDate;
import java.util.ArrayList;

public interface ITransactionsPresenter {
    void changeMonthForward ();
    void changeMonthBackward ();
    String dateToString (LocalDate date);
    void start();
    void refreshTransactionsByMonthAndYear();
    void refreshFilterAndSort (String filter, String sort);
    ArrayList<Transaction> sortTransactions (ArrayList<Transaction> trns, String type);
    ArrayList<Transaction> filterAndSort (String filter, String sort);
    void setCurrentBudget ();
}
