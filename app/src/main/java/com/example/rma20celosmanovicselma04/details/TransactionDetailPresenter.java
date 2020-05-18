package com.example.rma20celosmanovicselma04.details;

import android.content.Context;

import com.example.rma20celosmanovicselma04.R;
import com.example.rma20celosmanovicselma04.data.ITransactionsInteractor;
import com.example.rma20celosmanovicselma04.data.Transaction;
import com.example.rma20celosmanovicselma04.data.TransactionType;
import com.example.rma20celosmanovicselma04.data.TransactionsIntreactor;
import com.example.rma20celosmanovicselma04.transactionsList.TransactionsPresenter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class TransactionDetailPresenter implements ITransactionDetailPresenter, TransactionsIntreactor.OnTransactionsSearchDone {
    private ITransactionsInteractor interactor;
    private ITransactionDetailView view;
    private Context context;

    private Transaction transaction;

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
        interactor.removeTransaction(trn);
    }

    public void changeTransaction (Transaction oldTrn, Transaction newTrn) {
//        interactor.changeTransaction(oldTrn, newTrn);
        POSTTransaction(newTrn, oldTrn);
    }

    public void addTransaction(Transaction trn) {
        POSTTransaction(trn, null);
    }

    public boolean limitExceeded (Transaction currentTrn, boolean isAdd) {
        Double monthSum = interactor.getAmountForLimit(false, currentTrn.getDate());
        monthSum += currentTrn.getAmount();
        if(!isAdd) monthSum -= getTransaction().getAmount();
        Double allSum = interactor.getAmountForLimit(true, currentTrn.getDate());
        allSum += currentTrn.getAmount();
        if(!isAdd) allSum -= getTransaction().getAmount();
        return interactor.getAccount().getMonthLimit() < monthSum || interactor.getAccount().getTotalLimit() < allSum;
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
    public void POSTTransaction (Transaction newTrn, Transaction oldTrn) {
        if(oldTrn == null) {        //ne mijenja se nego se dodaje (nema neka old)
            String jsonFormat = getJSONFormat(newTrn, false);
            new TransactionsIntreactor((TransactionsIntreactor.OnTransactionsSearchDone) this).execute(jsonFormat, "addTrn", context.getResources().getString(R.string.api_id));
        }
        else {
            newTrn.setId(oldTrn.getId());
            String jsonFormat = getJSONFormat(newTrn, true);
            new TransactionsIntreactor((TransactionsIntreactor.OnTransactionsSearchDone) this).execute(jsonFormat, "addTrn", context.getResources().getString(R.string.api_id));
        }

    }

    private String getJSONFormat(Transaction trn, boolean isWithId) {
        String json = "";
        if(isWithId) {
            //json +=
        }
        json += "{" + "\"date\": " + "\"" + getJSONDateFormat(trn.getDate()) + "\", ";
        json +=  "\"title\": " + "\"" + trn.getTitle() + "\", ";
        json +=  "\"amount\": " + trn.getAmount() + ", ";

        if(trn.getEndDate() != null) json += "\"endDate\": " + "\"" + getJSONDateFormat(trn.getEndDate()) + "\", ";
        else json += "\"endDate\": " + "null, ";
        if(trn.getItemDescription() != null) json += "\"itemDescription\": " + "\"" + trn.getItemDescription() + "\", ";
        else json += "\"itemDescription\": " + "null, ";
        if(trn.getTransactionInterval() != null) json += "\"transactionInterval\": " + trn.getTransactionInterval() + ", ";
        else json += "\"transactionInterval\": " + "null, ";

        json += "\"typeId\": " + trn.getType().getTransactionName();
        return json;
    }

    private String getJSONDateFormat (LocalDate date) {
        return date.toString() + "T" + LocalTime.now();
    }

    @Override
    public void onDone(ArrayList<Transaction> results) {

    }
}
