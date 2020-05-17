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
import java.util.Map;
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
            for(Integer page = 0;; page++) {
                String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" + strings[2] + "/" + "transactions?page=" + page.toString();
                try {
                    JSONObject jo = getJsonObject(url1);
                    JSONArray results = jo.getJSONArray("transactions");
                    if(results.length() == 0) break;
                    for (int i = 0; i < results.length(); i++) {
                        addTransactionToArray(results, i);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(strings[1].equals("sortFilter")) {  //dohvatanje sa filterom i sortom
            if(query.contains("typeId=")) {
                query = replaceNameWithId(query);
            }
            for(Integer page = 0;; page++) {
                String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" + strings[2] + "/" + "transactions/" + query + page.toString();
                try {
                    JSONObject jo = getJsonObject(url1);
                    JSONArray results = jo.getJSONArray("transactions");
                    if(results.length() == 0) break;
                    for (int i = 0; i < results.length(); i++) {
                        addTransactionToArray(results, i);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            ArrayList<Transaction> regulars = new ArrayList<>();
            int idReg = getTypeId("Regular payment");
            for(Integer page = 0;; page++) {
                String url2 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" + strings[2] + "/" + "transactions/filter?typeId=" + idReg;
                try {
                    JSONObject jo = getJsonObject(url2);
                    JSONArray results = jo.getJSONArray("transactions");
                    if(results.length() == 0) break;
                    for (int i = 0; i < results.length(); i++) {
                        addToRegulars(regulars, results, i);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            idReg = getTypeId("Regular income");
            for(Integer page = 0;; page++) {
                String url2 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" + strings[2] + "/" + "transactions/filter?typeId=" + idReg;
                try {
                    JSONObject jo = getJsonObject(url2);
                    JSONArray results = jo.getJSONArray("transactions");
                    if(results.length() == 0) break;
                    for (int i = 0; i < results.length(); i++) {
                        addToRegulars(regulars, results, i);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            for(Transaction t : regulars) {         //spajanje regulars sa ostalim
                if(!transactions.contains(t)) addToThisMonth(t, Integer.parseInt(getMonthFromQuery(query)), Integer.parseInt(getYearFromQuery(query)));
            }
        }
        return null;
    }

    private String getMonthFromQuery(String query) {
        String [] arr = query.split("&");
        String temp = arr[0];
        String [] arr2 = temp.split("=");
        return arr2[1];
    }

    private String getYearFromQuery(String query) {
        String [] arr = query.split("&");
        String temp = arr[1];
        String [] arr2 = temp.split("=");
        return arr2[1];
    }

    private String replaceNameWithId(String query) {
        String [] arr = query.split("&");
        String s = arr[2];
        s = s.substring(7);
        query = query.replace(s, getTypeId(s).toString());  //uzimanje typeId umjesto typeName
        return query;
    }

    private void addToThisMonth(Transaction t, int month, int year) {
        if(t.getDate().getMonthValue() < month && t.getDate().getYear() <= year) {
            LocalDate d = t.getDate();
            while(d.getMonthValue() < month) {
                d = d.plusDays(t.getTransactionInterval());
            }
            if(d.getMonthValue() == month) {
                while(d.getMonthValue() == month) {
                    transactions.add(t);
                }
            }
        }
    }

    private void addToRegulars(ArrayList<Transaction> regulars, JSONArray results, int i) throws JSONException {
        JSONObject trn = results.getJSONObject(i);
        int id = trn.getInt("id");
        LocalDate date = LocalDate.parse(trn.getString("date").substring(0, 10));
        String title = trn.getString("title");
        Double amount = trn.getDouble("amount");
        String description = trn.getString("itemDescription");
        Integer interval;
        if ("null".equals(trn.getString("transactionInterval"))) {
            interval = null;
        } else {
            interval = Integer.parseInt(trn.getString("transactionInterval"));
        }
        LocalDate endDate;
        if ("null".equals(trn.getString("endDate"))) {
            endDate = null;
        } else {
            endDate = LocalDate.parse(trn.getString("endDate").substring(0, 10));
        }
        TransactionType type = getType(trn.getInt("TransactionTypeId"));
        regulars.add(new Transaction(id, date, amount, title, type, description, interval, endDate));
    }

    private void addTransactionToArray(JSONArray results, int i) throws JSONException {
        addToRegulars(transactions, results, i);
    }

    private JSONObject getJsonObject(String url1) throws IOException, JSONException {
        URL url = new URL(url1);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        String result = convertStreamToString(in);
        return new JSONObject(result);
    }

    private void makeTransactionTypeMap() {
        String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/transactionTypes";
        try {
            JSONObject jo = getJsonObject(url1);
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
    }

    public Integer getTypeId (String nameType) {
        makeTransactionTypeMap();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if(entry.getValue().equals(nameType)) return entry.getKey();
        }
        return null;
    }

    public TransactionType getType (int typeId) {
        makeTransactionTypeMap();
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
