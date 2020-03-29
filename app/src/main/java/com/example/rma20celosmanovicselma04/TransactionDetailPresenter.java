package com.example.rma20celosmanovicselma04;

import android.content.Context;

import java.time.LocalDate;
import java.util.ArrayList;

public class TransactionDetailPresenter implements ITransactionDetailPresenter{
    private ITransactionsInteractor interactor;
    private ITransactionDetailView view;
    private Context context;

    private Transaction transaction;

    public TransactionDetailPresenter (Context context, ITransactionDetailView view) {
        this.context = context;
        this.view = view;
        interactor = TransactionsPresenter.getInteractor();
    }

    @Override
    public void create (LocalDate date, Double amount, String title, TransactionType type, String itemDescription, Integer transactionInterval, LocalDate endDate) {
        this.transaction = new Transaction(date, amount, title, type, itemDescription, transactionInterval, endDate);
    }

    @Override
    public Transaction getTransaction() {
        return transaction;
    }

    public void start () {
        ArrayList<String> types = interactor.getTypes();
        types.remove("Filter by");
        view.setTypeSpinner(interactor.getTypes());
    }

    public void removeTransaction (Transaction trn) {
        interactor.removeTransaction(trn);
    }

    public void changeTransaction (Transaction oldTrn, Transaction newTrn) {
//        System.out.println(oldTrn);
//        System.out.println(newTrn);
//        System.out.println(TransactionsModel.transactions.get(5));
        interactor.changeTransaction(oldTrn, newTrn);
    }

    public void addTransaction(Transaction trn) {
        interactor.addTransaction(trn);
    }
}
