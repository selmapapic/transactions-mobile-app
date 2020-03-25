package com.example.rma20celosmanovicselma04;

import java.time.LocalDate;
import java.util.ArrayList;

public class TransactionsModel {
    public static LocalDate currentDate = LocalDate.now();
    public static ArrayList<Transaction> transactions = new ArrayList<Transaction>() {
        {
            add(new Transaction(LocalDate.of(2020,3,3), 45.0, "Kupovina sveske", TransactionType.INDIVIDUALPAYMENT, "Sveska za skolu", null, null));
            add(new Transaction(LocalDate.of(2019, 12, 12), 180.0, "Pavlaka", TransactionType.PURCHASE, "Pavlaka Milkos", null, null));
            add(new Transaction(LocalDate.of(2020, 1, 27), 150.0, "Ogrlica", TransactionType.PURCHASE, "Zlatni lanac sa smaragdom", null, null));
            add(new Transaction(LocalDate.of(2020, 2, 18), 3200.0, "Plata", TransactionType.REGULARINCOME, null, 30, LocalDate.of(2020, 12, 18)));
            add(new Transaction(LocalDate.of(2020, 3, 5), 16.95, "KONZUM - kasa", TransactionType.INDIVIDUALPAYMENT, "Kupovina namire u KONZUM-u", null, null));
            add(new Transaction(LocalDate.of(2019,12,15), 17.6, "Porez", TransactionType.REGULARPAYMENT, "Porez za nekretninu", 28, LocalDate.of(2020, 7, 9)));
        }
    };
    public static ArrayList<String> transactionTypes = new ArrayList<String>() {
        {
            add(TransactionType.INDIVIDUALINCOME.getTransactionName());
            add(TransactionType.REGULARINCOME.getTransactionName());
            add(TransactionType.PURCHASE.getTransactionName());
            add(TransactionType.INDIVIDUALPAYMENT.getTransactionName());
            add(TransactionType.REGULARPAYMENT.getTransactionName());
        }
    };

    public static LocalDate getCurrentDate() {
        return currentDate;
    }

    public static void setCurrentDate(LocalDate currentDate) {
        TransactionsModel.currentDate = currentDate;
    }
}
