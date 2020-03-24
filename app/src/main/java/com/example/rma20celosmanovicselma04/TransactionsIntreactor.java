package com.example.rma20celosmanovicselma04;

import java.time.LocalDate;
import java.util.ArrayList;

public class TransactionsIntreactor implements ITransactionsInteractor {
    private static LocalDate currentDate = LocalDate.now();

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void nextMonth () {
        currentDate = currentDate.plusMonths(1);
    }

    public void previousMonth () {
        currentDate = currentDate.minusMonths(1);
    }

    public String turnToString () {
        return currentDate.getMonth().name() + ", " + currentDate.getYear();
    }

    @Override
    public ArrayList<Transaction> getTransactions() {
        return TransactionsModel.transactions;
    }
}
