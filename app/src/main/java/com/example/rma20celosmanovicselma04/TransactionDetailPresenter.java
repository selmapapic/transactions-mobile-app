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

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
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
        interactor.changeTransaction(oldTrn, newTrn);
    }

    public void addTransaction(Transaction trn) {
        interactor.addTransaction(trn);
    }

    public boolean limitExceeded (Double currentTrn, boolean isAdd) {
        System.out.println(isAdd);
        System.out.println(currentTrn);
        Double monthSum = interactor.getAmountForLimit(false);
        System.out.println(monthSum);
        monthSum += currentTrn;
        if(!isAdd) monthSum -= getTransaction().getAmount();
        System.out.println(monthSum);
        Double allSum = interactor.getAmountForLimit(true);
        allSum += currentTrn;
        if(!isAdd) allSum -= getTransaction().getAmount();
        System.out.println(allSum);
        System.out.println(interactor.getAccount().getMonthLimit());
        System.out.println(interactor.getAccount().getTotalLimit());
        return interactor.getAccount().getMonthLimit() < monthSum || interactor.getAccount().getTotalLimit() < allSum;
    }
}
