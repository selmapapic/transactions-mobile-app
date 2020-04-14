package com.example.rma20celosmanovicselma04.graphs;

import android.content.Context;

import com.example.rma20celosmanovicselma04.data.ITransactionsInteractor;
import com.example.rma20celosmanovicselma04.data.Transaction;
import com.example.rma20celosmanovicselma04.transactionsList.TransactionsPresenter;
import com.github.mikephil.charting.data.BarEntry;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
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

    private double calculateAmountOfRegularForMonth(Transaction t, int month) {
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

    private double expenseGraphMonthValues (int month) {
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
                Transaction tr = t.clone();
                if(t.getDate().isBefore(LocalDate.now().with(firstDayOfYear())) || t.getEndDate().isAfter(LocalDate.now().with(lastDayOfYear()))) { //popravka pocetnih datuma
                    if(t.getDate().isBefore(LocalDate.now().with(firstDayOfYear()))) {
                        tr.setDate(LocalDate.now().with(firstDayOfYear()));
                    }
                    if(t.getEndDate().isAfter(LocalDate.now().with(lastDayOfYear()))) {
                        tr.setEndDate(LocalDate.now().with(lastDayOfYear()));
                    }
                    values.add(calculateAmountOfRegularForMonth(tr, month));
                }
                else if(t.getDate().getMonthValue() <= month && !t.getDate().isBefore(LocalDate.now().with(firstDayOfYear())) && t.getEndDate().getMonthValue() >= month){   //ako je u trenutnoj godini zapocelo prije trenutnog mjeseca
                    values.add(calculateAmountOfRegularForMonth(tr, month));
                }
            }
        }
        return values.stream().mapToDouble(Double::doubleValue).sum();
    }

    private double incomeGraphMonthValues (int month) {
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
                Transaction tr = t.clone();
                if(t.getDate().isBefore(LocalDate.now().with(firstDayOfYear())) || t.getEndDate().isAfter(LocalDate.now().with(lastDayOfYear()))) { //popravka pocetnih datuma
                    if(t.getDate().isBefore(LocalDate.now().with(firstDayOfYear()))) {
                        tr.setDate(LocalDate.now().with(firstDayOfYear()));
                    }
                    if(t.getEndDate().isAfter(LocalDate.now().with(lastDayOfYear()))) {
                        tr.setEndDate(LocalDate.now().with(lastDayOfYear()));
                    }
                    values.add(calculateAmountOfRegularForMonth(tr, month));
                }
                else if(t.getDate().getMonthValue() <= month && !t.getDate().isBefore(LocalDate.now().with(firstDayOfYear())) && t.getEndDate().getMonthValue() >= month){   //ako je u trenutnoj godini zapocelo prije trenutnog mjeseca
                    values.add(calculateAmountOfRegularForMonth(tr, month));
                }
            }
        }
        return values.stream().mapToDouble(Double::doubleValue).sum();
    }

    private double combinedGraphMonthValues(int month) {
        double valueTillNow = 0;
        for(int i = 1; i <= month; i++) {
            valueTillNow += incomeGraphMonthValues(i) - expenseGraphMonthValues(i);
        }
        return valueTillNow;
    }

    private double expenseGraphWeekValues (int week) {
        LocalDate d = LocalDate.now().withDayOfMonth(1);
        d = d.plusDays(7 * (week - 1));
        LocalDate dWeek = d.plusDays(7);
        if(d.getMonthValue() != dWeek.getMonthValue()) dWeek = LocalDate.now().with(lastDayOfMonth());
        ArrayList<Double> values = new ArrayList<>();
        ArrayList<Transaction> transactions = interactor.getTransactions();
        transactions = (ArrayList<Transaction>) transactions.stream().filter(transaction -> transaction.getType().toString().contains("PAYMENT") || transaction.getType().toString().contains("PURCHASE")).collect(Collectors.toList());
        for (Transaction t : transactions) {
            if((t.getDate().equals(d)  || (t.getDate().isAfter(d) && t.getDate().isBefore(dWeek))) && (t.getType().toString().contains("PURCHASE") || t.getType().toString().contains("INDIVIDUAL"))){
                values.add(t.getAmount());
            }
        }
        for(Transaction t : transactions) {
            if(t.getType().toString().contains("REGULAR")) {
                Transaction tr = t.clone();
                if(t.getDate().isBefore(LocalDate.now().with(firstDayOfYear())) || t.getEndDate().isAfter(LocalDate.now().with(lastDayOfYear()))) { //popravka pocetnih datuma
                    if(t.getDate().isBefore(LocalDate.now().with(firstDayOfYear()))) {
                        tr.setDate(LocalDate.now().with(firstDayOfYear()));
                    }
                    if(t.getEndDate().isAfter(LocalDate.now().with(lastDayOfYear()))) {
                        tr.setEndDate(LocalDate.now().with(lastDayOfYear()));
                    }
                    values.add(calculateAmountOfRegularWeek(tr, week));
                }
                else if(t.getDate().getMonthValue() <= d.getMonthValue() && !t.getDate().isBefore(LocalDate.now().with(firstDayOfYear())) && t.getEndDate().getMonthValue() >= dWeek.getMonthValue()){   //ako je u trenutnoj godini zapocelo prije trenutnog mjeseca
                    values.add(calculateAmountOfRegularWeek(tr, week));
                }
            }
        }
        return values.stream().mapToDouble(Double::doubleValue).sum();
    }

    private double incomeGraphWeekValues (int week) {
        LocalDate d = LocalDate.now().withDayOfMonth(1);
        d = d.plusDays(7 * (week - 1));
        LocalDate dWeek = d.plusDays(7);
        if(d.getMonthValue() != dWeek.getMonthValue()) dWeek = LocalDate.now().with(lastDayOfMonth());
        ArrayList<Double> values = new ArrayList<>();
        ArrayList<Transaction> transactions = interactor.getTransactions();
        transactions = (ArrayList<Transaction>) transactions.stream().filter(transaction -> transaction.getType().toString().contains("INCOME")).collect(Collectors.toList());
        for (Transaction t : transactions) {
            if((t.getDate().equals(d)  || (t.getDate().isAfter(d) && t.getDate().isBefore(dWeek))) && t.getType().toString().contains("INDIVIDUAL")){
                values.add(t.getAmount());
            }
        }
        for(Transaction t : transactions) {
            if(t.getType().toString().contains("REGULAR")) {
                Transaction tr = t.clone();
                if(t.getDate().isBefore(LocalDate.now().with(firstDayOfYear())) || t.getEndDate().isAfter(LocalDate.now().with(lastDayOfYear()))) { //popravka pocetnih datuma
                    if(t.getDate().isBefore(LocalDate.now().with(firstDayOfYear()))) {
                        tr.setDate(LocalDate.now().with(firstDayOfYear()));
                    }
                    if(t.getEndDate().isAfter(LocalDate.now().with(lastDayOfYear()))) {
                        tr.setEndDate(LocalDate.now().with(lastDayOfYear()));
                    }
                    values.add(calculateAmountOfRegularWeek(tr, week));
                }
                else if(t.getDate().getMonthValue() <= d.getMonthValue() && !t.getDate().isBefore(LocalDate.now().with(firstDayOfYear())) && t.getEndDate().getMonthValue() >= dWeek.getMonthValue()){   //ako je u trenutnoj godini zapocelo prije trenutnog mjeseca
                    values.add(calculateAmountOfRegularWeek(tr, week));
                }
            }
        }
        return values.stream().mapToDouble(Double::doubleValue).sum();
    }

    private double combinedGraphWeekValues(int week) {
        double valueTillNow = 0;
        for(int i = 1; i <= week; i++) {
            valueTillNow += incomeGraphWeekValues(i) - expenseGraphWeekValues(i);
        }
        return valueTillNow;
    }

    private Double calculateAmountOfRegularWeek(Transaction tr, int week) {
        LocalDate trnDate = tr.getDate();
        LocalDate d = LocalDate.now().withDayOfMonth(1);
        d = d.plusDays(7 * (week - 1));
        LocalDate dWeek = d.plusDays(7);
        if(d.getMonthValue() != dWeek.getMonthValue()) dWeek = LocalDate.now().with(lastDayOfMonth());
        double budget = 0;
        while(trnDate.getMonthValue() < d.getMonthValue()) {
            trnDate = trnDate.plusDays(tr.getTransactionInterval());
        }
        int i = 0;
        while(trnDate.getMonthValue() >= d.getMonthValue() && trnDate.getMonthValue() <= dWeek.getMonthValue() && trnDate.getDayOfMonth() >= d.getDayOfMonth() && trnDate.getDayOfMonth() <= dWeek.getDayOfMonth()) {
            i++;
            trnDate = trnDate.plusDays(tr.getTransactionInterval());
        }
        budget += (tr.getAmount() * i);
        return budget;
    }

    public ArrayList<BarEntry> getExpenseValuesForGraphByMonth () {
        ArrayList<Double> values = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            values.add(i, expenseGraphMonthValues(i + 1));
        }

        ArrayList<BarEntry> list = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            list.add(new BarEntry(i + 1, values.get(i).floatValue()));
        }
        return list;
    }

    public ArrayList<BarEntry> getIncomeValuesForGraphByMonth () {
        ArrayList<Double> values = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            values.add(i, incomeGraphMonthValues(i + 1));
        }

        ArrayList<BarEntry> list = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            list.add(new BarEntry(i + 1, values.get(i).floatValue()));
        }
        return list;
    }

    public ArrayList<BarEntry> getCombinedValuesForGraphByMonth() {
        ArrayList<Double> values = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            values.add(i, combinedGraphMonthValues(i + 1));
        }

        ArrayList<BarEntry> list = new ArrayList<>();
        for(int i = 0; i < 12; i++ ) {
            list.add(new BarEntry(i + 1, values.get(i).floatValue()));
        }
        return list;
    }

    public ArrayList<BarEntry> getExpenseValuesForGraphByWeek () {
        ArrayList<Double> values = new ArrayList<>();
        int lastWeekOfMonth = LocalDate.now().with(lastDayOfMonth()).get(WeekFields.ISO.weekOfMonth());
        for(int i = 0; i < lastWeekOfMonth; i++) {
            values.add(i, expenseGraphWeekValues(i + 1));
        }

        ArrayList<BarEntry> list = new ArrayList<>();
        for(int i = 0; i < lastWeekOfMonth; i++) {
            list.add(new BarEntry(i + 1, values.get(i).floatValue()));
        }
        return list;
    }

    public ArrayList<BarEntry> getIncomeValuesForGraphByWeek () {
        ArrayList<Double> values = new ArrayList<>();
        int lastWeekOfMonth = LocalDate.now().with(lastDayOfMonth()).get(WeekFields.ISO.weekOfMonth());
        for(int i = 0; i < lastWeekOfMonth; i++) {
            values.add(i, incomeGraphWeekValues(i + 1));
        }

        ArrayList<BarEntry> list = new ArrayList<>();
        for(int i = 0; i < lastWeekOfMonth; i++) {
            list.add(new BarEntry(i + 1, values.get(i).floatValue()));
        }
        return list;
    }

    public ArrayList<BarEntry> getCombinedValuesForGraphByWeek() {
        ArrayList<Double> values = new ArrayList<>();
        int lastWeekOfMonth = LocalDate.now().with(lastDayOfMonth()).get(WeekFields.ISO.weekOfMonth());
        for(int i = 0; i < lastWeekOfMonth; i++) {
            values.add(i, combinedGraphWeekValues(i + 1));
        }

        ArrayList<BarEntry> list = new ArrayList<>();
        for(int i = 0; i < lastWeekOfMonth; i++) {
            list.add(new BarEntry(i + 1, values.get(i).floatValue()));
        }
        return list;
    }

    @Override
    public void refreshGraphs (int monthWeekDay) {  //month = 1, week = 2, day = 3
        view.setExpenseGraph(monthWeekDay);
        view.setIncomeGraph(monthWeekDay);
        view.setCombinedGraph(monthWeekDay);
    }
}
