package com.example.rma20celosmanovicselma04.transactionsList;

import android.content.Context;
import android.os.AsyncTask;

import com.example.rma20celosmanovicselma04.R;
import com.example.rma20celosmanovicselma04.data.Account;
import com.example.rma20celosmanovicselma04.data.AccountModel;
import com.example.rma20celosmanovicselma04.data.ITransactionsInteractor;
import com.example.rma20celosmanovicselma04.data.Transaction;
import com.example.rma20celosmanovicselma04.data.TransactionType;
import com.example.rma20celosmanovicselma04.data.TransactionsIntreactor;
import com.example.rma20celosmanovicselma04.util.ConnectionChecker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;


public class TransactionsPresenter implements ITransactionsPresenter, TransactionsIntreactor.OnTransactionsSearchDone {
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
        //searchAccount(null);
    }

    public ArrayList<Transaction> getTransactionsByType (ArrayList<Transaction> trns, String type) {
        return (ArrayList<Transaction>) trns.stream().filter(tr -> tr.getType().equals(TransactionType.getType(type))).collect(Collectors.toList());
    }

    private ArrayList<Transaction> filterAndSort (String filter, String sort, ArrayList<Transaction> trns) {
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

    private ArrayList<Transaction> sortTransactions (ArrayList<Transaction> trns, String type) {
        if(type.equals("Price - Ascending")) trns = sortByPrice(trns, true);
        else if(type.equals("Price - Descending")) trns = sortByPrice(trns, false);
        else if (type.equals("Title - Ascending")) trns = sortByTitle(trns, true);
        else if (type.equals("Title - Descending")) trns = sortByTitle(trns, false);
        else if (type.equals("Date - Ascending")) trns = sortByDate(trns, true);
        else if(type.equals("Date - Descending")) trns = sortByDate(trns, false);

        return trns;
    }

    private ArrayList<Transaction> sortByPrice (ArrayList<Transaction> trns, boolean way) {
        if(way) return (ArrayList<Transaction>) trns.stream().sorted(Comparator.comparingDouble(Transaction::getAmount)).collect(Collectors.toList());
        return (ArrayList<Transaction>) trns.stream().sorted(Comparator.comparingDouble(Transaction::getAmount).reversed()).collect(Collectors.toList());
    }

    private ArrayList<Transaction> sortByTitle (ArrayList<Transaction> trns, boolean way) {
        if(way) return (ArrayList<Transaction>) trns.stream().sorted(Comparator.comparing(Transaction::getTitle, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList());
        return (ArrayList<Transaction>) trns.stream().sorted(Comparator.comparing(Transaction::getTitle, String.CASE_INSENSITIVE_ORDER).reversed()).collect(Collectors.toList());
    }

    private ArrayList<Transaction> sortByDate (ArrayList<Transaction> trns, boolean way) {
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
        System.out.println("set curr budg");
        if(!ConnectionChecker.isConnected(context)) {
            Account acc = getInteractor().getAccountFromDb(context);
            view.setBudgetLimit(acc.getBudget(), acc.getTotalLimit());
        }
        else searchAccount(null);
    }

    @Override
    public void onDone(ArrayList<Transaction> results) {
        ArrayList<Transaction> finalTrns = filterAndSort(view.getFilterSpinner(), view.getSortSpinner(), results);
        //interactor.addToModel(results);
        view.setTransactions(finalTrns);
        view.notifyTransactionsListDataSetChanged();
    }

    @Override
    public void onAccountDone(Account account) {
        System.out.println("ON ACC DONe");
        if(ConnectionChecker.isConnected(context)) {
            System.out.println("on acc done if");
            view.setBudgetLimit(account.getBudget(), account.getTotalLimit());
            interactor.AddAccountToDb(account, context.getApplicationContext());
            Account acc = interactor.getAccountFromDb(context.getApplicationContext());
            AccountModel.account = new Account(acc.getBudget(), acc.getTotalLimit(), acc.getMonthLimit(), acc.getId(), acc.getInternalId());
        }
    }

    @Override
    public void onTrnDoneForGraphs(ArrayList<Transaction> transactions) {

    }

    @Override
    public void searchTransactions(String query){
        if(query == null) {
            new TransactionsIntreactor((TransactionsIntreactor.OnTransactionsSearchDone) this).execute(query, "allTrn", context.getResources().getString(R.string.api_id));
        }
        else {
            new TransactionsIntreactor((TransactionsIntreactor.OnTransactionsSearchDone) this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query, "sortFilter", context.getResources().getString(R.string.api_id), "getAcc");
            new TransactionsIntreactor((TransactionsIntreactor.OnTransactionsSearchDone) this).execute(query, "allTrn", context.getResources().getString(R.string.api_id));
        }
    }

    @Override
    public void searchAccount(String query){
        System.out.println("search acc");
        //new TransactionsIntreactor((TransactionsIntreactor.OnTransactionsSearchDone) this).execute(query, "getAccount", context.getResources().getString(R.string.api_id));
        new TransactionsIntreactor((TransactionsIntreactor.OnTransactionsSearchDone) this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query, "getAccount", context.getResources().getString(R.string.api_id));
    }

    @Override
    public void refreshAllTransactions(String filter, String sort) {
//        Account acc = interactor.getAccountFromDb(context);
//        AccountModel.account = new Account(acc.getBudget(), acc.getTotalLimit(), acc.getMonthLimit(), acc.getId(), acc.getInternalId());
        System.out.println("pozvalo se refresh");
        LocalDate d = interactor.getCurrentDate();
        int currMonth = d.getMonthValue();
        String currMonthStr = String.valueOf(currMonth);
        if(!(currMonth > 9 && currMonth <= 12)) currMonthStr = "0" + currMonthStr;
        String currYear = String.valueOf(d.getYear());
        if(ConnectionChecker.isConnected(context.getApplicationContext())) {        //ako ima konekcije
            if ((filter == null || filter.equals("Filter by")) && (sort == null || sort.equals("Sort by"))) {
                searchTransactions("filter?month=" + currMonthStr + "&year=" + currYear + "&page=");
                return;
            } else if ((filter == null || filter.equals("Filter by") && !(sort == null || sort.equals("Sort by")))) {
                String sortParam = getSortParam(sort);
                searchTransactions("filter?month=" + currMonthStr + "&year=" + currYear + "&sort=" + sortParam + "&page=");
                return;
            } else if ((sort == null || sort.equals("Sort by") && !(filter == null || filter.equals("Filter by")))) {
                searchTransactions("filter?month=" + currMonthStr + "&year=" + currYear + "&typeId=" + filter + "&page=");
                return;
            } else {
                String sortParam = getSortParam(sort);
                searchTransactions("filter?month=" + currMonthStr + "&year=" + currYear + "&typeId=" + filter + "&sort=" + sortParam + "&page=");
            }
        }
        else {
            System.out.println(interactor.getFromModel().size() + "glupi size");
            ArrayList<Transaction> finalTrns = interactor.getTransactionsByDate(null);
            finalTrns.addAll(interactor.getTransactionsFromDb(context));
            view.setTransactions(finalTrns);
            view.notifyTransactionsListDataSetChanged();
        }
    }

    private String getSortParam(String sort) {
        String[] arr = sort.split(" ");
        String param = arr[0].toLowerCase();
        if(param.equals("price")) param = "amount";
        String way = arr[2].toLowerCase();
        if(way.equals("ascending")) return param + ".asc";
        return param + ".desc";
    }
}
