package com.example.rma20celosmanovicselma04.budget;

import android.content.Context;
import android.os.AsyncTask;

import com.example.rma20celosmanovicselma04.R;
import com.example.rma20celosmanovicselma04.data.Account;
import com.example.rma20celosmanovicselma04.data.AccountModel;
import com.example.rma20celosmanovicselma04.data.ITransactionsInteractor;
import com.example.rma20celosmanovicselma04.data.Transaction;
import com.example.rma20celosmanovicselma04.data.TransactionsIntreactor;
import com.example.rma20celosmanovicselma04.util.ConnectionCheck;

import java.util.ArrayList;

public class BudgetPresenter implements IBudgetPresenter, TransactionsIntreactor.OnTransactionsSearchDone{
    private IBudgetView view;
    private static ITransactionsInteractor interactor;
    private Context context;

    public BudgetPresenter(IBudgetView view, Context context) {
        this.view = view;
        this.interactor = new TransactionsIntreactor();
        this.context = context;
    }

    public void start () {
        if(ConnectionCheck.isConnected(context)) {
            searchAccount(null, null);
        }
        else {
            view.setBudgetText(AccountModel.account.getBudget());
            view.setTotalLimitFld(AccountModel.account.getTotalLimit());
            view.setMonthLimitFld(AccountModel.account.getMonthLimit());
        }
    }

    @Override
    public void saveNewChanges(Double totalLimit, Double monthLimit) {
        if(ConnectionCheck.isConnected(context)) {
            searchAccount(null, new Account(0, totalLimit, monthLimit));
        }
        else {
            AccountModel.account.setBudget(Double.parseDouble(view.getBudgetText()));
            AccountModel.account.setTotalLimit(totalLimit);
            AccountModel.account.setMonthLimit(monthLimit);
            interactor.UpdateAccountInDb(new Account(AccountModel.account.getBudget(), AccountModel.account.getTotalLimit(), AccountModel.account.getMonthLimit(),
                    AccountModel.account.getId(), AccountModel.account.getInternalId()), context);
            Account acc = interactor.getAccountFromDb(context);
            view.setBudgetText(acc.getBudget());
            view.setTotalLimitFld(acc.getTotalLimit());
            view.setMonthLimitFld(acc.getMonthLimit());
        }
    }

    @Override
    public void refreshFields() {
        if(ConnectionCheck.isConnected(context)) {
            searchAccount(null, null);
        }
        else {
            view.setBudgetText(AccountModel.account.getBudget());
            view.setTotalLimitFld(AccountModel.account.getTotalLimit());
            view.setMonthLimitFld(AccountModel.account.getMonthLimit());
        }
    }


    @Override
    public void onDone(ArrayList<Transaction> results) { }

    @Override
    public void onAccountDone(Account account) {
        if(account != null) {
            view.setBudgetText(account.getBudget());
            view.setTotalLimitFld(account.getTotalLimit());
            view.setMonthLimitFld(account.getMonthLimit());
        }
    }

    @Override
    public void onTrnDoneForGraphs(ArrayList<Transaction> transactions) {

    }

    @Override
    public void searchAccount(String query, Account edit){
        if(edit != null) {
            String jsonFormat = getJSONFormat(edit);
            new TransactionsIntreactor((TransactionsIntreactor.OnTransactionsSearchDone) this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonFormat, "editAccount", context.getResources().getString(R.string.api_id));

        }
        else {
            new TransactionsIntreactor((TransactionsIntreactor.OnTransactionsSearchDone) this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,query, "getAccount", context.getResources().getString(R.string.api_id));
        }
    }

    private String getJSONFormat(Account account) {
        String json = "";

        json += "{" + "\"monthLimit\": " + account.getMonthLimit() + ", ";
        json +=  "\"totalLimit\": " + account.getTotalLimit();
        json += "}";
        return json;
    }
}
