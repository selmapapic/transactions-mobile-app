package com.example.rma20celosmanovicselma04;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TransactionsIntreactor implements ITransactionsInteractor {

    public LocalDate getCurrentDate() {
        return TransactionsModel.getCurrentDate();
    }
    public void setCurrentDate (LocalDate date) { TransactionsModel.setCurrentDate(date); }

    @Override
    public ArrayList<Transaction> getTransactions() {
        return TransactionsModel.transactions;
    }

    @Override
    public ArrayList<Transaction> getTransactionsByMonthAndYear() {
        ArrayList<Transaction> allTransactions = TransactionsModel.transactions;
        return (ArrayList<Transaction>) allTransactions.stream().
                filter(tr -> tr.getDate().getYear() == TransactionsModel.currentDate.getYear() && tr.getDate().getMonth() == TransactionsModel.currentDate.getMonth()).
                collect(Collectors.toList());
    }

}
