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
            add(new Transaction(LocalDate.of(2020, 2, 15), 200.0, "Dzeparac", TransactionType.INDIVIDUALINCOME, null, 30, null));
            add(new Transaction(LocalDate.of(2020, 4, 26), 3700.0, "Rata za stan", TransactionType.REGULARPAYMENT, "Rata za kupovinu stana", 29, LocalDate.of(2021, 4, 26)));
            add(new Transaction(LocalDate.of(2020, 3, 1), 110.0, "Bajramluk", TransactionType.INDIVIDUALINCOME, null, null, null));
            add(new Transaction(LocalDate.of(2020, 2, 8), 50.0, "Rodjendan", TransactionType.INDIVIDUALINCOME, null, null, null));
            add(new Transaction(LocalDate.of(2020, 2, 28), 0.8, "Voda", TransactionType.REGULARPAYMENT, "Voda Lejla", 1, LocalDate.of(2021, 2, 28)));
            add(new Transaction(LocalDate.of(2020, 3, 26), 9.45, "Grickalice", TransactionType.PURCHASE, "Kokice, cips, cokolada", null, null));
            add(new Transaction(LocalDate.of(2020, 4, 3), 120.0, "Stipendija", TransactionType.REGULARINCOME, null, 30, LocalDate.of(2020, 7, 15)));
            add(new Transaction(LocalDate.of(2020, 2, 12), 2300.0, "Laptop", TransactionType.PURCHASE, "HP Envy x360", null, null));
            add(new Transaction(LocalDate.of(2020, 1, 1), 750.0, "Nagradna igra", TransactionType.INDIVIDUALINCOME, "Nagradna igra - CM", null, null));
            add(new Transaction(LocalDate.of(2020, 5, 19), 20.0, "Clanarina", TransactionType.INDIVIDUALPAYMENT, "Clanarina za steleks", null,null));
            add(new Transaction(LocalDate.of(2019, 6, 6), 35.6, "Netflix", TransactionType.REGULARPAYMENT, "Netflix pretplata", 25, LocalDate.of(2020, 8, 6)));
            add(new Transaction(LocalDate.of(2018, 8, 17), 580.0, "Penzija", TransactionType.REGULARINCOME, null, 30, LocalDate.of(2023, 8, 9)));
            add(new Transaction(LocalDate.of(2020, 4, 16), 20.0, "Tal za poklon", TransactionType.INDIVIDUALPAYMENT, "Poklon za neciji rodjendan", null, null));
            add(new Transaction(LocalDate.of(2021, 10, 10), 550.0, "Freelancerska plata", TransactionType.REGULARINCOME, null, 15, LocalDate.of(2022, 10, 10)));
            add(new Transaction(LocalDate.of(2020, 3, 23), 70.0, "Nabavka", TransactionType.PURCHASE, "Kupovina za kucu", null, null));

//            add(new Transaction(LocalDate.of(2020, 1, 27), 150.0, "Ogrlica", TransactionType.PURCHASE, "Zlatni lanac sa smaragdom", null, null));
//            add(new Transaction(LocalDate.of(2020, 2, 18), 3200.0, "Plata", TransactionType.REGULARINCOME, null, 30, LocalDate.of(2020, 12, 18)));
//            add(new Transaction(LocalDate.of(2020, 3, 5), 16.95, "KONZUM - kasa", TransactionType.INDIVIDUALPAYMENT, "Kupovina namire u KONZUM-u", null, null));
//            add(new Transaction(LocalDate.of(2020, 4, 14), 235D, "Elektrotehnički fakultet - isplata", TransactionType.INDIVIDUALINCOME, null, null, null));
//            add(new Transaction(LocalDate.of(2020, 3, 7), 30D, "Netflix - članarina", TransactionType.REGULARPAYMENT, "Mjesečna članarina", 30, LocalDate.of(2020, 9, 7)));
//            add(new Transaction(LocalDate.of(2020, 1, 14), 800D, "Apple Watch Series 3", TransactionType.PURCHASE, "Pametni sat kompanije Apple", null, null));
//            add(new Transaction(LocalDate.of(2020, 5, 1), 150D, "Stipendija - uplata", TransactionType.REGULARINCOME, null, 30, LocalDate.of(2020, 11, 30)));
//            add(new Transaction(LocalDate.of(2020, 3, 19), 16.95, "NIKE - kasa", TransactionType.INDIVIDUALPAYMENT, "Nike AirForce One tene", null, null));
//            add(new Transaction(LocalDate.of(2019,12,15), 17.6, "Porez", TransactionType.REGULARPAYMENT, "Porez za nekretninu", 28, LocalDate.of(2020, 7, 9)));
//            add(new Transaction(LocalDate.of(2020, 2, 8), 50.0, "Rodjendan", TransactionType.INDIVIDUALINCOME, "Poklon", null, null));
//            add(new Transaction(LocalDate.of(2020,3,3), 45.0, "Kupovina sveske", TransactionType.INDIVIDUALPAYMENT, "Sveska za skolu", null, null));
//            add(new Transaction(LocalDate.of(2019, 12, 12), 180.0, "Pavlaka", TransactionType.PURCHASE, "Pavlaka Milkos", null, null));
//            add(new Transaction(LocalDate.of(2020, 1, 1), 750.0, "Nagradna igra", TransactionType.INDIVIDUALINCOME, "Nagradna igra - CM", null, null));
//            add(new Transaction(LocalDate.of(2020, 2, 28), 0.8, "Kola", TransactionType.REGULARPAYMENT, "Voda Lejla", 1, LocalDate.of(2021, 2, 28)));
//            add(new Transaction(LocalDate.of(2020, 3, 23), 70.0, "Nabavka", TransactionType.PURCHASE, "Kupovina za kucu", null, null));
//            add(new Transaction(LocalDate.of(2019, 6, 6), 35.6, "Netflix", TransactionType.REGULARPAYMENT, "Netflix pretplata", 25, LocalDate.of(2020, 8, 6)));
        }
    };
    public static ArrayList<String> transactionTypes = new ArrayList<String>() {
        {
            add("Filter by");
            add(TransactionType.INDIVIDUALINCOME.getTransactionName());
            add(TransactionType.REGULARINCOME.getTransactionName());
            add(TransactionType.PURCHASE.getTransactionName());
            add(TransactionType.INDIVIDUALPAYMENT.getTransactionName());
            add(TransactionType.REGULARPAYMENT.getTransactionName());
        }
    };

    public static ArrayList<String> sortTypes = new ArrayList<String>() {
        {
            add("Sort by");
            add("Price - Ascending");
            add("Price - Descending");
            add("Title - Ascending");
            add("Title - Descending");
            add("Date - Ascending");
            add("Date - Descending");
        }
    };

    public static LocalDate getCurrentDate() {
        return currentDate;
    }

    public static void setCurrentDate(LocalDate currentDate) {
        TransactionsModel.currentDate = currentDate;
    }
}
