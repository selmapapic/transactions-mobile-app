package com.example.rma20celosmanovicselma04.details;

import android.content.Context;

import com.example.rma20celosmanovicselma04.R;
import com.example.rma20celosmanovicselma04.data.Account;
import com.example.rma20celosmanovicselma04.data.ITransactionsInteractor;
import com.example.rma20celosmanovicselma04.data.Transaction;
import com.example.rma20celosmanovicselma04.data.TransactionType;
import com.example.rma20celosmanovicselma04.data.TransactionsIntreactor;
import com.example.rma20celosmanovicselma04.data.TransactionsModel;
import com.example.rma20celosmanovicselma04.transactionsList.TransactionsPresenter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TransactionDetailPresenter implements ITransactionDetailPresenter, TransactionsIntreactor.OnTransactionsSearchDone {
    private ITransactionsInteractor interactor;
    private ITransactionDetailView view;
    private Context context;

    private Transaction transaction;
    private Account account;
    private ArrayList<Transaction> transactions;

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
        POSTTransaction(trn, null, true);
        account.setBudget(account.getBudget() - getTransactionAmountBudget(trn));
        searchAccount(null, account);
    }

    public void changeTransaction (Transaction oldTrn, Transaction newTrn) {
        POSTTransaction(newTrn, oldTrn, false);
        account.setBudget(account.getBudget() - getTransactionAmountBudget(oldTrn));
        account.setBudget(account.getBudget() + getTransactionAmountBudget(newTrn));
        searchAccount(null, account);
    }

    public void addTransaction(Transaction trn) {
        POSTTransaction(trn, null, false);
        account.setBudget(account.getBudget() + getTransactionAmountBudget(trn));
        searchAccount(null, account);
    }

    public boolean limitExceeded (Transaction currentTrn, boolean isAdd) {
        Double monthSum = getAmountForLimit(false, currentTrn.getDate());
        monthSum += currentTrn.getAmount();
        if(!isAdd) monthSum -= getTransaction().getAmount();
        Double allSum = getAmountForLimit(true, currentTrn.getDate());
        allSum += currentTrn.getAmount();
        if(!isAdd) allSum -= getTransaction().getAmount();
        return account.getMonthLimit() < monthSum || account.getTotalLimit() < allSum;
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

    @Override
    public void POSTTransaction (Transaction newTrn, Transaction oldTrn, boolean isDelete) {
        if(isDelete) {
            new TransactionsIntreactor((TransactionsIntreactor.OnTransactionsSearchDone) this).execute(null, "deleteTrn", context.getResources().getString(R.string.api_id), String.valueOf(newTrn.getId()));
            return;
        }
        if(oldTrn == null) {        //ne mijenja se nego se dodaje (nema neka old)
            String jsonFormat = getJSONFormat(newTrn);
            new TransactionsIntreactor((TransactionsIntreactor.OnTransactionsSearchDone) this).execute(jsonFormat, "addTrn", context.getResources().getString(R.string.api_id));
        }
        else {
            newTrn.setId(oldTrn.getId());
            String jsonFormat = getJSONFormat(newTrn);
            new TransactionsIntreactor((TransactionsIntreactor.OnTransactionsSearchDone) this).execute(jsonFormat, "addEditTrn", context.getResources().getString(R.string.api_id), String.valueOf(newTrn.getId()));
        }
    }

    private String getJSONFormat(Transaction trn) {
        String json = "";

        json += "{" + "\"date\": " + "\"" + getJSONDateFormat(trn.getDate()) + "\", ";
        json +=  "\"title\": " + "\"" + trn.getTitle() + "\", ";
        json +=  "\"amount\": " + trn.getAmount() + ", ";

        if(trn.getEndDate() != null) json += "\"endDate\": " + "\"" + getJSONDateFormat(trn.getEndDate()) + "\", ";
        else json += "\"endDate\": " + "null, ";
        if(trn.getItemDescription() != null) json += "\"itemDescription\": " + "\"" + trn.getItemDescription() + "\", ";
        else json += "\"itemDescription\": " + "null, ";
        if(trn.getTransactionInterval() != null) json += "\"transactionInterval\": " + trn.getTransactionInterval() + ", ";
        else json += "\"transactionInterval\": " + "null, ";

        json += "\"TransactionTypeId\": " + trn.getType().getTransactionName();
        return json;
    }

    private String getJSONDateFormat (LocalDate date) {
        return date.toString() + "T" + LocalTime.now();
    }

    @Override
    public void onDone(ArrayList<Transaction> results) {
        transactions = results;
        System.out.println(" EVO GA ON DONE ");
    }

    @Override
    public void onAccountDone(Account account) {
        this.account = account;
        System.out.println("desio se on acc done det pres");
    }

    @Override
    public void searchAccount(String query, Account edit){
        if(edit != null) {
            String jsonFormat = getJSONFormatAccount(edit);
            new TransactionsIntreactor((TransactionsIntreactor.OnTransactionsSearchDone) this).execute(jsonFormat, "editAccount", context.getResources().getString(R.string.api_id));
        }
        else {
            new TransactionsIntreactor((TransactionsIntreactor.OnTransactionsSearchDone) this).execute(query, "getAccount", context.getResources().getString(R.string.api_id));
        }
    }

    @Override
    public void start() {
        searchTransactions(null);
        searchAccount(null, null);
        System.out.println("Pozvao se start");
    }

    private String getJSONFormatAccount(Account account) {
        String json = "";

        json += "{" + "\"budget\": " + account.getBudget();
        json += "}";
        return json;
    }

    private double getTransactionAmountBudget(Transaction trn) {
        double budget = 0;

        if(trn.getType().toString().contains("PAYMENT") || trn.getType().toString().contains("PURCHASE")) {
            if(trn.getType().toString().contains("REGULAR")) {
                budget -= (ChronoUnit.DAYS.between(trn.getDate(), trn.getEndDate()) / trn.getTransactionInterval()) * trn.getAmount();
                budget -= trn.getAmount();
            }
            else {
                budget -= trn.getAmount();
            }
        }
        else {
            if(trn.getType().toString().contains("REGULAR")) {
                budget += (ChronoUnit.DAYS.between(trn.getDate(), trn.getEndDate()) / trn.getTransactionInterval()) * trn.getAmount();
                budget += trn.getAmount();
            }
            else {
                budget += trn.getAmount();
            }
        }

        return (Math.round(budget * 100.0) / 100.0);
    }

    @Override
    public void searchTransactions(String query){
        new TransactionsIntreactor((TransactionsIntreactor.OnTransactionsSearchDone) this).execute(query, "allTrn", context.getResources().getString(R.string.api_id));
    }

    public double getAmountForLimit (boolean isAllNoDate, LocalDate date) { //is all no date - da li zelim da uzmem stanje svih transakcija, tj da nisu po odredjenom datumu
        ArrayList<Transaction> trns;
        if (isAllNoDate) trns = transactions;
        else trns = getTransactionsByDate(date);

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
                            int i = 0;
                            while(t.getDate().getMonth() == d.getMonth()) {
                                i++;
                                d = d.plusDays(t.getTransactionInterval());
                            }
                            budget += (t.getAmount() * i) + t.getAmount();
                        }
                    } else
                        budget += (ChronoUnit.DAYS.between(t.getDate(), t.getEndDate()) / t.getTransactionInterval()) * t.getAmount();
                } else {
                    budget += t.getAmount();
                }
            }
        }
        return Math.round(budget * 100.0) / 100.0;
    }

    public ArrayList<Transaction> getTransactionsByDate (LocalDate date) {
        LocalDate curr;
        if(date == null) curr = TransactionsModel.currentDate;
        else curr = date;

        ArrayList<Transaction> allTransactions = transactions;

        return (ArrayList<Transaction>) allTransactions.stream().
                filter(tr -> (tr.getDate().getYear() == curr.getYear() && tr.getDate().getMonth() == curr.getMonth()) ||
                        (tr.getType().toString().contains("REGULAR") && (tr.getEndDate().getMonth().getValue() == curr.getMonth().getValue() && tr.getEndDate().getYear() == curr.getYear() ||
                                (tr.getDate().isBefore(curr) && tr.getEndDate().isAfter(curr))))).
                collect(Collectors.toList());
    }
}
