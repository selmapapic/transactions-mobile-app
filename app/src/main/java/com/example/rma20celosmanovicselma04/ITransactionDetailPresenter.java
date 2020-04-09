package com.example.rma20celosmanovicselma04;

import java.time.LocalDate;
import java.util.ArrayList;

public interface ITransactionDetailPresenter {
    void create (LocalDate date, Double amount, String title, TransactionType type, String itemDescription, Integer transactionInterval, LocalDate endDate);
    Transaction getTransaction ();
    void start ();
    void removeTransaction (Transaction trn);
    void changeTransaction (Transaction oldTrn, Transaction newTrn);
    void addTransaction(Transaction trn);
    boolean limitExceeded (Transaction currentTrn, boolean isAdd);
    void setTransaction(Transaction transaction);
    ITransactionsInteractor getInteractor();
    ArrayList<String> getTypes();
}
