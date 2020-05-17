package com.example.rma20celosmanovicselma04.data;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class TransactionsIntreactor extends AsyncTask<String, Integer, Void> implements ITransactionsInteractor{

    public interface OnTransactionsSearchDone{
        public void onDone(ArrayList<Transaction> results);
    }

    public LocalDate getCurrentDate() {
        return TransactionsModel.getCurrentDate();
    }
    public void setCurrentDate (LocalDate date) { TransactionsModel.setCurrentDate(date); }
    HashMap<Integer, String> map = new HashMap<>();
    private ArrayList<Transaction> transactions;

    private OnTransactionsSearchDone caller;
    public TransactionsIntreactor(OnTransactionsSearchDone p) {
        caller = p;
        transactions = new ArrayList<Transaction>();
        System.out.println("tu smo bili");
    };

    public TransactionsIntreactor() {
    }

    @Override
    protected void onPostExecute(Void aVoid){
        super.onPostExecute(aVoid);
        caller.onDone(transactions);
    }


    @Override
    public ArrayList<Transaction> getTransactions() {
        return TransactionsModel.transactions;
    }

    public ArrayList<Transaction> getTransactionsByDate (LocalDate date) {
        LocalDate curr;
        if(date == null) curr = TransactionsModel.currentDate;
        else curr = date;

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
        ArrayList<Transaction> trns = getTransactions();

        for(Transaction t : trns) {
            if (t.getAmount() < 0) t.setAmount(t.getAmount() * (-1));
        }

        double budget = 0;
        for(Transaction t : trns) {
            if(t.getType().toString().contains("PAYMENT") || t.getType().toString().contains("PURCHASE")) {
                if(t.getType().toString().contains("REGULAR")) {
                    budget -= (ChronoUnit.DAYS.between(t.getDate(), t.getEndDate()) / t.getTransactionInterval()) * t.getAmount();
                }
                else {
                    budget -= t.getAmount();
                }
            }
            else {
                if(t.getType().toString().contains("REGULAR")) {
                    budget += (ChronoUnit.DAYS.between(t.getDate(), t.getEndDate()) / t.getTransactionInterval()) * t.getAmount();
                }
                else {
                    budget += t.getAmount();
                }
            }
        }
        budget = Math.round(budget * 100.0) / 100.0;
        return budget;
    }

    public double getAmountForLimit (boolean isAllNoDate, LocalDate date) { //is all no date - da li zelim da uzmem stanje svih transakcija, tj da nisu po odredjenom datumu
        ArrayList<Transaction> trns;
        if (isAllNoDate) trns = getTransactions();
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
        budget = Math.round(budget * 100.0) / 100.0;
        return budget;
    }

    @Override
    protected Void doInBackground(String... strings) {
        String query = strings[0];

        if(strings[1].equals("allTrn")) {    //dohvatanje svih trn
            String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" + strings[2] + "/" + query;
            try {
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection)
                        url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String result = convertStreamToString(in);
                JSONObject jo = new JSONObject(result);
                JSONArray results = jo.getJSONArray("transactions");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject trn = results.getJSONObject(i);
                    int id = trn.getInt("id");
                    LocalDate date = LocalDate.parse(trn.getString("date").substring(0,10));
                    String title = trn.getString("title");
                    Double amount = trn.getDouble("amount");
                    String description = trn.getString("itemDescription");
                    Integer interval;
                    if("null".equals(trn.getString("transactionInterval"))) {
                        interval = null;
                    }
                    else {
                        interval = Integer.parseInt(trn.getString("transactionInterval"));
                    }
                    LocalDate endDate;
                    if("null".equals(trn.getString("endDate"))) {
                        endDate = null;
                    }
                    else {
                        endDate = LocalDate.parse(trn.getString("endDate").substring(0,10));
                    }
                    //todo
                    TransactionType type = getTransactionType(trn.getInt("TransactionTypeId"));
                    transactions.add(new Transaction(id, date, amount, title, type, description, interval, endDate));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private TransactionType getTransactionType(int typeId) {
        String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/transactionTypes";
        try {
            URL url = new URL(url1);
            HttpURLConnection urlConnection = (HttpURLConnection)
                    url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String result = convertStreamToString(in);
            JSONObject jo = new JSONObject(result);
            JSONArray results = jo.getJSONArray("rows");
            for (int i = 0; i < results.length(); i++) {
                JSONObject type = results.getJSONObject(i);
                Integer id = type.getInt("id");
                String name = type.getString("name");
                map.put(id, name);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return TransactionType.getType(map.get(typeId));
    }

    private String convertStreamToString(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();
    }
}
