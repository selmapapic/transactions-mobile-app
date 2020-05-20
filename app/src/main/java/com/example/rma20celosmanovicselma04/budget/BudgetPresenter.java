package com.example.rma20celosmanovicselma04.budget;

import android.content.Context;

import com.example.rma20celosmanovicselma04.R;
import com.example.rma20celosmanovicselma04.data.Account;
import com.example.rma20celosmanovicselma04.data.ITransactionsInteractor;
import com.example.rma20celosmanovicselma04.data.Transaction;
import com.example.rma20celosmanovicselma04.data.TransactionsIntreactor;

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
        searchAccount(null, null);
    }

    @Override
    public void saveNewChanges(Double totalLimit, Double monthLimit) {
        searchAccount(null, new Account(0, totalLimit, monthLimit));
        //interactor.getAccount().setTotalLimit(totalLimit);
        //interactor.getAccount().setMonthLimit(monthLimit);
    }

    @Override
    public void refreshFields() {
        //view.setTotalLimitFld(interactor.getAccount().getTotalLimit());
        //view.setMonthLimitFld(interactor.getAccount().getMonthLimit());
        searchAccount(null, null);
    }


    @Override
    public void onDone(ArrayList<Transaction> results) { }

    @Override
    public void onAccountDone(Account account) {
        //todo
        //view.setBudgetText(interactor.getCurrentBudget(true));
        if(account != null) {
            view.setBudgetText(account.getBudget());
            view.setTotalLimitFld(account.getTotalLimit());
            view.setMonthLimitFld(account.getMonthLimit());
        }
    }

    @Override
    public void searchAccount(String query, Account edit){
        if(edit != null) {
            String jsonFormat = getJSONFormat(edit);
            new TransactionsIntreactor((TransactionsIntreactor.OnTransactionsSearchDone) this).execute(jsonFormat, "editAccount", context.getResources().getString(R.string.api_id));
        }
        else {
            new TransactionsIntreactor((TransactionsIntreactor.OnTransactionsSearchDone) this).execute(query, "getAccount", context.getResources().getString(R.string.api_id));
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
