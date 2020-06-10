package com.example.rma20celosmanovicselma04.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

import com.example.rma20celosmanovicselma04.R;
import com.example.rma20celosmanovicselma04.data.Account;
import com.example.rma20celosmanovicselma04.data.Transaction;
import com.example.rma20celosmanovicselma04.data.TransactionsIntreactor;
import com.example.rma20celosmanovicselma04.transactionsList.TransactionsPresenter;

import java.util.ArrayList;

public class ConnectionCheck extends BroadcastReceiver implements TransactionsIntreactor.OnTransactionsSearchDone {

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(isConnected(context)) {
            //new TransactionsIntreactor((TransactionsIntreactor.OnTransactionsSearchDone) TransactionsPresenter.getPresenterVariable()).execute(null, "fromDbToWeb", context.getResources().getString(R.string.api_id), "", "");
            new TransactionsIntreactor((TransactionsIntreactor.OnTransactionsSearchDone) this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,null, "fromDbToWeb", context.getResources().getString(R.string.api_id), "", "");
            //TransactionsPresenter.transferFromDbToWebService();
        }
    }

    @Override
    public void onDone(ArrayList<Transaction> results) {
            TransactionsPresenter.presenterVariable.refresh();
    }

    @Override
    public void onAccountDone(Account account) {

    }

    @Override
    public void onTrnDoneForGraphs(ArrayList<Transaction> transactions) {

    }
}
