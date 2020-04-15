package com.example.rma20celosmanovicselma04.details;

import android.content.Context;

import com.example.rma20celosmanovicselma04.data.ITransactionsInteractor;
import com.example.rma20celosmanovicselma04.data.Transaction;
import com.example.rma20celosmanovicselma04.data.TransactionType;
import com.example.rma20celosmanovicselma04.transactionsList.TransactionsPresenter;

import java.util.ArrayList;

public class TransactionDetailPresenter implements ITransactionDetailPresenter {
    private ITransactionsInteractor interactor;
    private ITransactionDetailView view;
    private Context context;

    private Transaction transaction;

    public TransactionDetailPresenter (Context context, ITransactionDetailView view) {
        this.context = context;
        this.view = view;
        interactor = TransactionsPresenter.getInteractor();
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public Transaction getTransaction() {
        return transaction;
    }

    public void removeTransaction (Transaction trn) {
        interactor.removeTransaction(trn);
    }

    public void changeTransaction (Transaction oldTrn, Transaction newTrn) {
        interactor.changeTransaction(oldTrn, newTrn);
    }

    public void addTransaction(Transaction trn) {
        interactor.addTransaction(trn);
    }

    public boolean limitExceeded (Transaction currentTrn, boolean isAdd) {
        Double monthSum = interactor.getAmountForLimit(false, currentTrn.getDate());
        monthSum += currentTrn.getAmount();
        if(!isAdd) monthSum -= getTransaction().getAmount();
        Double allSum = interactor.getAmountForLimit(true, currentTrn.getDate());
        allSum += currentTrn.getAmount();
        if(!isAdd) allSum -= getTransaction().getAmount();
        return interactor.getAccount().getMonthLimit() < monthSum || interactor.getAccount().getTotalLimit() < allSum;
    }

    public ITransactionsInteractor getInteractor() {
        return interactor;
    }

    public ArrayList<String> getTypes () {
        ArrayList<String> types = new ArrayList<>();
        types.add(TransactionType.INDIVIDUALINCOME.getTransactionName());
        types.add(TransactionType.REGULARINCOME.getTransactionName());
        types.add(TransactionType.PURCHASE.getTransactionName());
        types.add(TransactionType.INDIVIDUALPAYMENT.getTransactionName());
        types.add(TransactionType.REGULARPAYMENT.getTransactionName());
        return types;
    }
}
