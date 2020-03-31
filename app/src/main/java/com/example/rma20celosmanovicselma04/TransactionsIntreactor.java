package com.example.rma20celosmanovicselma04;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TransactionsIntreactor implements ITransactionsInteractor {

    public LocalDate getCurrentDate() {
        return TransactionsModel.getCurrentDate();
    }
    public void setCurrentDate (LocalDate date) { TransactionsModel.setCurrentDate(date); }

    @Override
    public ArrayList<Transaction> getTransactions() {
        return TransactionsModel.transactions;
    }

    public ArrayList<Transaction> getTransactionsByDate () {
        LocalDate curr = TransactionsModel.getCurrentDate();
        ArrayList<Transaction> allTransactions = TransactionsModel.transactions;

        return (ArrayList<Transaction>) allTransactions.stream().
                filter(tr -> (tr.getDate().getYear() == curr.getYear() && tr.getDate().getMonth() == curr.getMonth()) ||
                        (tr.getType().toString().contains("REGULAR") && (tr.getEndDate().getMonth().getValue() == curr.getMonth().getValue() && tr.getEndDate().getYear() == curr.getYear() ||
                                (tr.getDate().isBefore(curr) && tr.getEndDate().isAfter(curr))))).
                collect(Collectors.toList());
    }

    public ArrayList<String> getTypes () {
        return TransactionsModel.transactionTypes;
    }

    @Override
    public ArrayList<String> getSortTypes () {
        return TransactionsModel.sortTypes;
    }

    public void removeTransaction(Transaction trn) {
        TransactionsModel.transactions.remove(trn);
    }

    public void changeTransaction(Transaction oldTrn, Transaction newTrn) {
        int indexOld = TransactionsModel.transactions.indexOf(oldTrn);
        TransactionsModel.transactions.set(indexOld, newTrn);
    }

    public void addTransaction (Transaction trn) {
        TransactionsModel.transactions.add(trn);
    }

    public Account getAccount () {
        return AccountModel.account;
    }

    public void setBudget (double budget) {
        getAccount().setBudget(budget);
    }

    public double getCurrentBudget (boolean isAllNoDate) { //is all no date - da li zelim da uzmem stanje svih transakcija, tj da nisu po odredjenom datumu
        ArrayList<Transaction> trns;
        if(isAllNoDate) trns = getTransactions();
        else trns = getTransactionsByDate();

        for(Transaction t : trns) {
            if (t.getAmount() < 0) t.setAmount(t.getAmount() * (-1));
        }

        double budget = 0;
        for(Transaction t : trns) {
            if(t.getType().toString().contains("PAYMENT") || t.getType().toString().contains("PURCHASE")) {
                if(t.getType().toString().contains("REGULAR")) {
                    if(!isAllNoDate) {
                        LocalDate d = t.getDate().plusDays(t.getTransactionInterval());
                        if(t.getDate().getMonth() != d.getMonth()) budget -= t.getAmount();
                        else {
                            //todo
                        }
                    }
                    else budget -= (ChronoUnit.DAYS.between(t.getDate(), t.getEndDate()) / t.getTransactionInterval()) * t.getAmount();
                }
                else {
                    budget -= t.getAmount();
                }
            }
            else {
                if(t.getType().toString().contains("REGULAR")) {
                    if(!isAllNoDate) {
                        LocalDate d = t.getDate().plusDays(t.getTransactionInterval());
                        if(t.getDate().getMonth() != d.getMonth()) budget += t.getAmount();
                        else {
                            //todo
                        }
                    }
                    else budget += (ChronoUnit.DAYS.between(t.getDate(), t.getEndDate()) / t.getTransactionInterval()) * t.getAmount();
                }
                else {
                    budget += t.getAmount();
                }
            }
        }
        budget = Math.round(budget * 100.0) / 100.0;
        return budget;
    }

    public double getAmountForLimit (boolean isAllNoDate) {
        ArrayList<Transaction> trns;
        if (isAllNoDate) trns = getTransactions();
        else trns = getTransactionsByDate();

        for (Transaction t : trns) {
            if (t.getAmount() < 0) t.setAmount(t.getAmount() * (-1));
        }

        double budget = 0;
        for (Transaction t : trns) {
            if (t.getType().toString().contains("PAYMENT") || t.getType().toString().contains("PURCHASE")) {
                if (t.getType().toString().contains("REGULAR")) {
                    if (!isAllNoDate) {
                        LocalDate d = t.getDate().plusDays(t.getTransactionInterval());
                        if (t.getDate().getMonth() != d.getMonth()) budget += t.getAmount();
                        else {
                            //todo
                        }
                    } else
                        budget += (ChronoUnit.DAYS.between(t.getDate(), t.getEndDate()) / t.getTransactionInterval()) * t.getAmount();
                } else {
                    budget += t.getAmount();
                }
            }
        }
        budget = Math.round(budget * 100.0) / 100.0;
        return budget;
    }
}
