package com.example.rma20celosmanovicselma04;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
            add(new Transaction(LocalDate.of(2020, 2, 15), 200.0, "Dzeparac", TransactionType.INDIVIDUALINCOME, "Mjesecni dzeparac", 30, null));
        }
    };
    public static ArrayList<String> transactionTypes = new ArrayList<String>() {
        {
            add("Filter by: ");
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

    public static ArrayList<Transaction> getTransactionsByMonthAndYear() {
        LocalDate curr = TransactionsModel.currentDate;
        ArrayList<Transaction> allTransactions = TransactionsModel.transactions;

        return (ArrayList<Transaction>) allTransactions.stream().
                filter(tr -> (tr.getDate().getYear() == curr.getYear() && tr.getDate().getMonth() == curr.getMonth()) ||
                        (tr.getType().toString().contains("REGULAR") && (tr.getEndDate().getMonth().getValue() == curr.getMonth().getValue() && tr.getEndDate().getYear() == curr.getYear() ||
                                (tr.getDate().isBefore(curr) && tr.getEndDate().isAfter(curr))))).
                collect(Collectors.toList());
    }

    public static TransactionType getType (String type) {
        if(type == null || type.equals("Filter by: ")) return null;
        if(type.equals("Regular income")) return TransactionType.REGULARINCOME;
        else if(type.equals("Regular payment")) return TransactionType.REGULARPAYMENT;
        else if (type.equals("Purchase")) return TransactionType.PURCHASE;
        else if(type.equals("Individual payment")) return TransactionType.INDIVIDUALPAYMENT;
        else if (type.equals("Individual income")) return TransactionType.INDIVIDUALINCOME;
        return null;
    }

    public static ArrayList<Transaction> getTransactionsByType (String type) {
        if(type == null || type.equals("Filter by: ")) return getTransactionsByMonthAndYear();
        return (ArrayList<Transaction>) getTransactionsByMonthAndYear().stream().filter(tr -> tr.getType().equals(getType(type))).collect(Collectors.toList());
    }
}
