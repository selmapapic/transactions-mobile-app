package com.example.rma20celosmanovicselma04;

import java.time.LocalDate;

public interface ITransactionDetailPresenter {
    void create (LocalDate date, Double amount, String title, TransactionType type, String itemDescription, Integer transactionInterval, LocalDate endDate);
    Transaction getTransaction ();
    void start ();

}
