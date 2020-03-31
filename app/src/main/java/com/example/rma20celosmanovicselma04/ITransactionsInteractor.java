package com.example.rma20celosmanovicselma04;

import java.time.LocalDate;
import java.util.ArrayList;

public interface ITransactionsInteractor {
    LocalDate getCurrentDate();
    ArrayList<Transaction> getTransactions();
    void setCurrentDate(LocalDate date);
    ArrayList<String> getTypes ();
    ArrayList<String> getSortTypes ();
    void removeTransaction(Transaction trn);
    void changeTransaction(Transaction oldTrn, Transaction newTrn);
    void addTransaction (Transaction trn);
    Account getAccount ();
    void setBudget (double budget);
    ArrayList<Transaction> getTransactionsByDate ();
    double getCurrentBudget (boolean isAllNoDate);
    double getAmountForLimit (boolean isAllNoDate);
}
