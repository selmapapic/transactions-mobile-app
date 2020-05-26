package com.example.rma20celosmanovicselma04.details;

import com.example.rma20celosmanovicselma04.data.Account;
import com.example.rma20celosmanovicselma04.data.ITransactionsInteractor;
import com.example.rma20celosmanovicselma04.data.Transaction;

import java.util.ArrayList;

public interface ITransactionDetailPresenter {
    Transaction getTransaction ();
    void removeTransaction (Transaction trn);
    void changeTransaction (Transaction oldTrn, Transaction newTrn);
    void addTransaction(Transaction trn);
    boolean limitExceeded (Transaction currentTrn, boolean isAdd);
    void setTransaction(Transaction transaction);
    ITransactionsInteractor getInteractor();
    ArrayList<String> getTypes();
    void POSTTransaction (Transaction newTrn, Transaction oldTrn, boolean isDelete);
    void searchAccount(String query, Account edit);
    void start();
    void searchTransactions(String query);
}
