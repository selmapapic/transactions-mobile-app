package com.example.rma20celosmanovicselma04.graphs;

import android.content.Context;

import com.example.rma20celosmanovicselma04.data.ITransactionsInteractor;
import com.example.rma20celosmanovicselma04.data.Transaction;
import com.example.rma20celosmanovicselma04.transactionsList.TransactionsPresenter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

public class GraphsPresenter implements IGraphsPresenter {
    private ITransactionsInteractor interactor;
    private IGraphsView view;
    private Context context;


    public GraphsPresenter (Context context, IGraphsView view) {
        this.context = context;
        this.view = view;
        interactor = TransactionsPresenter.getInteractor();
    }

    private double calculateAmountOfRegular(Transaction t, int month) {
        double budget = 0;
        LocalDate d = t.getDate();
        while(d.getMonthValue() < month) {
            d = d.plusDays(t.getTransactionInterval());
        }
        int i = 0;
        while(d.getMonthValue() == month) {
            i++;
            d = d.plusDays(t.getTransactionInterval());
        }
        budget += (t.getAmount() * i);
        return budget;
    }

    public double expenseGraphMonthValues (int month) {
        ArrayList<Double> values = new ArrayList<>();
        ArrayList<Transaction> transactions = interactor.getTransactions();
        transactions = (ArrayList<Transaction>) transactions.stream().filter(transaction -> transaction.getType().toString().contains("PAYMENT") || transaction.getType().toString().contains("PURCHASE")).collect(Collectors.toList());
        for (Transaction t : transactions) {
            if(t.getDate().getMonthValue() == month && (t.getType().toString().contains("PURCHASE") || t.getType().toString().contains("INDIVIDUAL"))){
                values.add(t.getAmount());
            }
        }
        for(Transaction t : transactions) {
            if(t.getType().toString().contains("REGULAR")) {
                Transaction tr = t;
                if(t.getDate().isBefore(LocalDate.now().with(firstDayOfYear())) || t.getEndDate().isAfter(LocalDate.now().with(lastDayOfYear()))) { //popravka pocetnih datuma
                    if(t.getDate().isBefore(LocalDate.now().with(firstDayOfYear()))) {
                        tr.setDate(LocalDate.now().with(firstDayOfYear()));
                    }
                    if(t.getEndDate().isAfter(LocalDate.now().with(lastDayOfYear()))) {
                        tr.setEndDate(LocalDate.now().with(lastDayOfYear()));
                    }
                    values.add(calculateAmountOfRegular(tr, month));
                }
                else if(t.getDate().getMonthValue() <= month && !t.getDate().isBefore(LocalDate.now().with(firstDayOfYear())) && t.getEndDate().getMonthValue() >= month){   //ako je u trenutnoj godini zapocelo prije trenutnog mjeseca
                    values.add(calculateAmountOfRegular(tr, month));
                }
            }
        }
        return values.stream().mapToDouble(Double::doubleValue).sum();
    }

    public double incomeGraphMonthValues (int month) {
        ArrayList<Double> values = new ArrayList<>();
        ArrayList<Transaction> transactions = interactor.getTransactions();
        transactions = (ArrayList<Transaction>) transactions.stream().filter(transaction -> transaction.getType().toString().contains("INCOME")).collect(Collectors.toList());
        for (Transaction t : transactions) {
            if(t.getDate().getMonthValue() == month && t.getType().toString().contains("INDIVIDUAL")){
                values.add(t.getAmount());
            }
        }
        for(Transaction t : transactions) {
            if(t.getType().toString().contains("REGULAR")) {
                Transaction tr = t;
                if(t.getDate().isBefore(LocalDate.now().with(firstDayOfYear())) || t.getEndDate().isAfter(LocalDate.now().with(lastDayOfYear()))) { //popravka pocetnih datuma
                    if(t.getDate().isBefore(LocalDate.now().with(firstDayOfYear()))) {
                        tr.setDate(LocalDate.now().with(firstDayOfYear()));
                    }
                    if(t.getEndDate().isAfter(LocalDate.now().with(lastDayOfYear()))) {
                        tr.setEndDate(LocalDate.now().with(lastDayOfYear()));
                    }
                    values.add(calculateAmountOfRegular(tr, month));
                }
                else if(t.getDate().getMonthValue() <= month && !t.getDate().isBefore(LocalDate.now().with(firstDayOfYear())) && t.getEndDate().getMonthValue() >= month){   //ako je u trenutnoj godini zapocelo prije trenutnog mjeseca
                    values.add(calculateAmountOfRegular(tr, month));
                }
            }
        }
        return values.stream().mapToDouble(Double::doubleValue).sum();
    }

    public double combinedGraphMonthValues(int month) {
        double valueTillNow = 0;
        for(int i = 1; i <= month; i++) {
            valueTillNow += incomeGraphMonthValues(month) - expenseGraphMonthValues(month);
        }
        return valueTillNow;
    }


    @Override
    public void refreshIncomeGraphByMonth() {
        view.setIncomeGraph();
    }

    public void refreshExpenseGraphByMonth() {
        view.setExpenseGraph();
    }

    public void refreshCombinedGraphByMonth() {
        view.setCombinedGraph();
    }

}
