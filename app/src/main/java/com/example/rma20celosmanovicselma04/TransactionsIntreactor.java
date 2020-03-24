package com.example.rma20celosmanovicselma04;

import java.time.LocalDate;
import java.util.ArrayList;

public class TransactionsIntreactor implements ITransactionsInteractor {

    public LocalDate getCurrentDate() {
        return TransactionsModel.getCurrentDate();
    }
    public void setCurrentDate (LocalDate date) { TransactionsModel.setCurrentDate(date); }

    @Override
    public ArrayList<Transaction> getTransactions() {
        return TransactionsModel.transactions;
    }
}
