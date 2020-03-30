package com.example.rma20celosmanovicselma04;

import android.content.Context;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
        interactor.changeTransaction(oldTrn, newTrn);
    }

    public void addTransaction(Transaction trn) {
        interactor.addTransaction(trn);
    }

    public boolean limitExceeded (Double currentTrn, boolean isAdd) {
        Double monthSum = getTransactionsByDate().stream().mapToDouble(Transaction::getAmount).sum();
        for (Transaction t : getTransactionsByDate()) System.out.println(t);
        monthSum += currentTrn;
        if(!isAdd) monthSum -= getTransaction().getAmount();
        System.out.println(monthSum + " month suma");

        Double allSum = interactor.getTransactions().stream().mapToDouble(Transaction::getAmount).sum();
        allSum += currentTrn;
        if(!isAdd) allSum -= getTransaction().getAmount();
        System.out.println(allSum);

        return interactor.getAccount().getMonthLimit() < monthSum || interactor.getAccount().getTotalLimit() < allSum;
    }

    public ArrayList<Transaction> getTransactionsByDate () {
        LocalDate curr = interactor.getCurrentDate();
        ArrayList<Transaction> allTransactions = interactor.getTransactions();

        return (ArrayList<Transaction>) allTransactions.stream().
                filter(tr -> (tr.getDate().getYear() == curr.getYear() && tr.getDate().getMonth() == curr.getMonth()) ||
                        (tr.getType().toString().contains("REGULAR") && (tr.getEndDate().getMonth().getValue() == curr.getMonth().getValue() && tr.getEndDate().getYear() == curr.getYear() ||
                                (tr.getDate().isBefore(curr) && tr.getEndDate().isAfter(curr))))).
                collect(Collectors.toList());
    }
}
