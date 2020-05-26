package com.example.rma20celosmanovicselma04.data;

import java.time.LocalDate;
import java.util.ArrayList;

public interface ITransactionsInteractor {
    LocalDate getCurrentDate();
    ArrayList<Transaction> getTransactions();
    void setCurrentDate(LocalDate date);
    ArrayList<String> getTypes ();
    ArrayList<String> getSortTypes ();
    //Account getAccount ();
    //void setBudget (double budget);
    //ArrayList<Transaction> getTransactionsByDate (LocalDate date);
    double getCurrentBudget (boolean isAllNoDate);
    double getAmountForLimit (boolean isAllNoDate, LocalDate d);
    Integer getTypeId (String nameType);
    TransactionType getType (int typeId);
}
