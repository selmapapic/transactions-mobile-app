package com.example.rma20celosmanovicselma04;

import java.time.LocalDate;
import java.util.ArrayList;

public interface ITransactionsInteractor {
    LocalDate getCurrentDate();
    ArrayList<Transaction> getTransactions();
    void setCurrentDate(LocalDate date);
    ArrayList<Transaction> getTransactionsByMonthAndYear();
    ArrayList<String> getTypes ();
    ArrayList<Transaction> getTransactionsByType  (String type);
}
