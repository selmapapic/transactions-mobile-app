package com.example.rma20celosmanovicselma04;

import java.time.LocalDate;
import java.util.ArrayList;

public interface ITransactionsInteractor {
    LocalDate getCurrentDate();
    void nextMonth ();
    void previousMonth();
    String turnToString ();
    ArrayList<Transaction> getTransactions();
}
