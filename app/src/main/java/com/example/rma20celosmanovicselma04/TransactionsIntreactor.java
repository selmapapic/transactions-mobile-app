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

    public ArrayList<String> getTypes () {
        return TransactionsModel.transactionTypes;
    }

    @Override
    public ArrayList<String> getSortTypes () {
        return TransactionsModel.sortTypes;
    }

    public void removeTransaction(Transaction trn) {
        TransactionsModel.transactions.remove(trn);
    }

    public void changeTransaction(Transaction oldTrn, Transaction newTrn) {
        System.out.println(oldTrn);
        System.out.println("old");
        System.out.println(TransactionsModel.transactions.get(5));
        System.out.println("model");
        System.out.println(newTrn);
        System.out.println("new");
        int indexOld = TransactionsModel.transactions.indexOf(oldTrn);
        TransactionsModel.transactions.set(indexOld, newTrn);
    }

    public void addTransaction (Transaction trn) {
        TransactionsModel.transactions.add(trn);
    }
}
