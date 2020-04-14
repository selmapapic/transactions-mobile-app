package com.example.rma20celosmanovicselma04.transactionsList;

import android.content.Context;

import com.example.rma20celosmanovicselma04.data.ITransactionsInteractor;
import com.example.rma20celosmanovicselma04.data.Transaction;
import com.example.rma20celosmanovicselma04.data.TransactionType;
import com.example.rma20celosmanovicselma04.data.TransactionsIntreactor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;


public class TransactionsPresenter implements ITransactionsPresenter {
    private ITransactionsView view;
    private static ITransactionsInteractor interactor;
    private Context context;

    public TransactionsPresenter(ITransactionsView view, Context context) {
        this.view = view;
        this.interactor = new TransactionsIntreactor();
        this.context = context;
    }

    public static ITransactionsInteractor getInteractor() {
        return interactor;
    }

    public void start () {
        view.refreshDate(dateToString(interactor.getCurrentDate()));
        view.setFilterSpinner(interactor.getTypes());
        view.setSortSpinner(interactor.getSortTypes());
        view.setBudgetLimit(interactor.getAccount().getBudget(), interactor.getAccount().getTotalLimit());
    }

    public void refreshTransactionsByMonthAndYear () {
        view.setTransactions(interactor.getTransactionsByDate(null));
        view.notifyTransactionsListDataSetChanged();
    }

    public ArrayList<Transaction> getTransactionsByType (ArrayList<Transaction> trns, String type) {
        return (ArrayList<Transaction>) trns.stream().filter(tr -> tr.getType().equals(TransactionType.getType(type))).collect(Collectors.toList());
    }

    public void refreshFilterAndSort (String filter, String sort) {
        ArrayList<Transaction> trns = filterAndSort(filter, sort);
        view.setTransactions(trns);
        view.notifyTransactionsListDataSetChanged();

    }

    public ArrayList<Transaction> filterAndSort (String filter, String sort) {
        ArrayList<Transaction> trns = interactor.getTransactionsByDate(null);
        if((filter == null || filter.equals("Filter by")) && (sort == null || sort.equals("Sort by"))) return trns;
        else if((filter == null || filter.equals("Filter by") && !(sort == null || sort.equals("Sort by")))) {
            return sortTransactions(trns, sort);
        }
        else if((sort == null || sort.equals("Sort by") && !(filter == null || filter.equals("Filter by")))) {
            return getTransactionsByType(trns, filter);
        }
        else {
            trns = getTransactionsByType(trns, filter);
            return sortTransactions(trns, sort);
        }
    }

    public ArrayList<Transaction> sortTransactions (ArrayList<Transaction> trns, String type) {
        if(type.equals("Price - Ascending")) trns = sortByPrice(trns, true);
        else if(type.equals("Price - Descending")) trns = sortByPrice(trns, false);
        else if (type.equals("Title - Ascending")) trns = sortByTitle(trns, true);
        else if (type.equals("Title - Descending")) trns = sortByTitle(trns, false);
        else if (type.equals("Date - Ascending")) trns = sortByDate(trns, true);
        else if(type.equals("Date - Descending")) trns = sortByDate(trns, false);

        return trns;
    }

    public ArrayList<Transaction> sortByPrice (ArrayList<Transaction> trns, boolean way) {
        if(way) return (ArrayList<Transaction>) trns.stream().sorted(Comparator.comparingDouble(Transaction::getAmount)).collect(Collectors.toList());
        return (ArrayList<Transaction>) trns.stream().sorted(Comparator.comparingDouble(Transaction::getAmount).reversed()).collect(Collectors.toList());
    }

    public ArrayList<Transaction> sortByTitle (ArrayList<Transaction> trns, boolean way) {
        if(way) return (ArrayList<Transaction>) trns.stream().sorted(Comparator.comparing(Transaction::getTitle, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList());
        return (ArrayList<Transaction>) trns.stream().sorted(Comparator.comparing(Transaction::getTitle, String.CASE_INSENSITIVE_ORDER).reversed()).collect(Collectors.toList());
    }

    public ArrayList<Transaction> sortByDate (ArrayList<Transaction> trns, boolean way) {
        if(way) return (ArrayList<Transaction>) trns.stream().sorted(Comparator.comparing(Transaction::getDate)).collect(Collectors.toList());
        return (ArrayList<Transaction>) trns.stream().sorted(Comparator.comparing(Transaction::getDate).reversed()).collect(Collectors.toList());
    }

    public void changeMonthForward () {
        interactor.setCurrentDate(interactor.getCurrentDate().plusMonths(1));
        view.refreshDate(dateToString(interactor.getCurrentDate()));
    }

    public void changeMonthBackward () {
        interactor.setCurrentDate(interactor.getCurrentDate().minusMonths(1));
        view.refreshDate(dateToString(interactor.getCurrentDate()));
    }

    public String dateToString (LocalDate date) {
        return date.getMonth().name() + ", " + date.getYear();
    }

    public void setCurrentBudget () {
        interactor.setBudget(interactor.getCurrentBudget(true));
        view.setBudget(interactor.getCurrentBudget(true));
    }
}
