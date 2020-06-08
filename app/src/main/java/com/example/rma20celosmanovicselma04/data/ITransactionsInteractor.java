package com.example.rma20celosmanovicselma04.data;

import android.content.Context;

import java.time.LocalDate;
import java.util.ArrayList;

public interface ITransactionsInteractor {
    LocalDate getCurrentDate();
    void setCurrentDate(LocalDate date);
    ArrayList<String> getTypes ();
    ArrayList<String> getSortTypes ();
    Integer getTypeId (String nameType);
    TransactionType getType (int typeId);

    void addToDb(Transaction trn, Context applicationContext);

    void addToModel(ArrayList<Transaction> results);
    ArrayList<Transaction> getFromModel();
    ArrayList<Transaction> getTransactionsByDate (LocalDate date);
}
