package com.example.rma20celosmanovicselma04.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.rma20celosmanovicselma04.budget.BudgetPresenter;
import com.example.rma20celosmanovicselma04.details.TransactionDetailPresenter;
import com.example.rma20celosmanovicselma04.util.TransactionsDBOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionsIntreactor extends AsyncTask<String, Integer, Void> implements ITransactionsInteractor {

    private ArrayList<Transaction> transactions;
    private Account account;
    private OnTransactionsSearchDone caller;
    HashMap<Integer, String> map = new HashMap<>();
    private ArrayList<Transaction> allTransactions = new ArrayList<>();

    public interface OnTransactionsSearchDone{
        void onDone(ArrayList<Transaction> results);
        void onAccountDone (Account account);
    }

    public LocalDate getCurrentDate() {
        return TransactionsModel.getCurrentDate();
    }
    public void setCurrentDate (LocalDate date) { TransactionsModel.setCurrentDate(date); }

    public TransactionsIntreactor(OnTransactionsSearchDone p) {
        caller = p;
        transactions = new ArrayList<Transaction>();
    };

    public TransactionsIntreactor() { }

    @Override
    protected void onPostExecute(Void aVoid){
        super.onPostExecute(aVoid);
        if(caller.getClass().equals(BudgetPresenter.class) || caller.getClass().equals(TransactionDetailPresenter.class)) {
            if(account != null) caller.onAccountDone(account);
            if(transactions != null) caller.onDone(transactions);
        }
        else {
            System.out.println(account + " on post exec");
            caller.onDone(transactions);
            if(account != null) caller.onAccountDone(account);
        }
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

    public Account getAccount () {
        return account;
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
                        allTransactions.add(new Transaction(id, date, amount, title, type, description, interval, endDate));
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            TransactionsModel.transactions.addAll(allTransactions);
            System.out.println("bla" + TransactionsModel.transactions.size());
            allTransactions.clear();
            System.out.println("bla2222    " + TransactionsModel.transactions.size());

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
                String url2 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" + strings[2] + "/" + "transactions/filter?typeId=" + idReg + "&page=" + page;
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
                String url2 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" + strings[2] + "/" + "transactions/filter?typeId=" + idReg + "&page=" + page;
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
                addToThisMonth(t, Integer.parseInt(getMonthFromQuery(query)), Integer.parseInt(getYearFromQuery(query)));
            }

            if(strings[3].equals("getAcc")) {
                getAccountFromWeb(strings[2]);
            }
        }
        else if(strings[1].contains("add")) {  //POST
            String url1;
            if(strings[1].contains("Edit")) url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" + strings[2] + "/transactions/" + strings[3];  //ako se edituje
            else url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" + strings[2] + "/transactions";      //ako se dodaje
            try {
                URL url = new URL(url1);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                OutputStream out = new DataOutputStream(connection.getOutputStream());
                String jsonString = replaceNameWithIdForPOST(strings[0]) + "}";
                byte[] bytes = jsonString.getBytes("utf-8");
                out.write(bytes, 0, bytes.length);

                try(BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(strings[1].equals("deleteTrn")) {
            String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" + strings[2] + "/transactions/" + strings[3];
            try {
                URL url = new URL(url1);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                try(BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(strings[1].equals("getAccount")) {
            System.out.println("do in bg account");
            getAccountFromWeb(strings[2]);
        }
        else if(strings[1].equals("editAccount")) {
            String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" + strings[2];
            try {
                URL url = new URL(url1);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                OutputStream out = new DataOutputStream(connection.getOutputStream());
                String jsonString = strings[0];
                byte[] bytes = jsonString.getBytes("utf-8");
                out.write(bytes, 0, bytes.length);

                try(BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void getAccountFromWeb(String api_id) {
        String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" + api_id;
        try {
            JSONObject jo = getJsonObject(url1);
            account = new Account(jo.getInt("id"), jo.getDouble("budget"), jo.getDouble("totalLimit"), jo.getDouble("monthLimit"));

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private String replaceNameWithIdForPOST(String string) {
        String arr[] = string.split("\"TransactionTypeId\": ");
        String name = arr[1];
        String repl = string.replace(name, getTypeId(name).toString());
        return repl;
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
        LocalDate date;
        if(transactions.size() == 0) {
            date = LocalDate.of(year, month, 15);
            date = date.with(TemporalAdjusters.lastDayOfMonth());
        }
        else date = transactions.get(0).getDate();
        boolean skip = false;
        if(t.getDate().getMonthValue() == month && t.getDate().getYear() == year) skip = true;
        if((t.getDate().isBefore(date) || t.getDate().equals(date)) && (t.getEndDate().isAfter(date) || (t.getEndDate().getYear() == date.getYear() && t.getEndDate().getMonthValue() == date.getMonthValue()))) {
            LocalDate d = t.getDate();
            while(d.getMonthValue() != month && d.isBefore(date)) {
                d = d.plusDays(t.getTransactionInterval());
            }
            if(d.getMonthValue() == month && d.isBefore(t.getEndDate())) {
                while(d.getMonthValue() == month && d.isBefore(t.getEndDate())) {
                    if(skip) {
                        skip = false;
                    }
                    else transactions.add(t);
                    d = d.plusDays(t.getTransactionInterval());
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
        allTransactions.add(new Transaction(id, date, amount, title, type, description, interval, endDate));
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

    @Override
    public void AddTransactionToDb(Transaction trn, Context applicationContext) {
        ContentResolver cr = applicationContext.getApplicationContext().getContentResolver();
        Uri transactionsURI = Uri.parse("content://rma.provider.transactions/elements");
        ContentValues values = new ContentValues();
        values.put(TransactionsDBOpenHelper.TRANSACTION_ID, trn.getId());
        values.put(TransactionsDBOpenHelper.TRANSACTION_TITLE, trn.getTitle());
        values.put(TransactionsDBOpenHelper.TRANSACTION_DATE, trn.getDate().toString());
        values.put(TransactionsDBOpenHelper.TRANSACTION_AMOUNT, trn.getAmount());
        values.put(TransactionsDBOpenHelper.TRANSACTION_INTERVAL, trn.getTransactionInterval());
        values.put(TransactionsDBOpenHelper.TRANSACTION_ITEM_DESCRIPTION, trn.getItemDescription());
        values.put(TransactionsDBOpenHelper.TRANSACTION_TYPE, trn.getType().toString());
        if(trn.getEndDate() == null) {
            values.put(TransactionsDBOpenHelper.TRANSACTION_END_DATE, (String) null);
        }
        else values.put(TransactionsDBOpenHelper.TRANSACTION_END_DATE, trn.getEndDate().toString());
        cr.insert(transactionsURI,values);

        TransactionsModel.transactions.add(trn);
    }

    @Override
    public void AddAccountToDb(Account acc, Context applicationContext) {
        ContentResolver cr = applicationContext.getApplicationContext().getContentResolver();
        String[] kolone = null;
        Uri uri = ContentUris.withAppendedId(Uri.parse("content://rma.provider.accounts/elements"),1);
        String where = null;
        String[] whereArgs = null;
        String order = null;
        Cursor cursor = cr.query(uri,kolone,where,whereArgs,order);
        ContentValues values = new ContentValues();

        if(cursor != null) {
            cursor.moveToFirst();
            if(cursor.isBeforeFirst()) {
                values.put(TransactionsDBOpenHelper.ACCOUNT_ID, acc.getId());
                values.put(TransactionsDBOpenHelper.ACCOUNT_BUDGET, acc.getBudget());
                values.put(TransactionsDBOpenHelper.ACCOUNT_TOTAL_LIMIT, acc.getTotalLimit());
                values.put(TransactionsDBOpenHelper.ACCOUNT_MONTH_LIMIT, acc.getMonthLimit());

                cr.insert(uri,values);
            }
            else {

            }
        }
        cursor.close();
    }

    @Override
    public Account getAccountFromDb(Context context, Integer id) {
        Account acc = null;
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        String[] kolone = null;
        Uri adresa = ContentUris.withAppendedId(Uri.parse("content://rma.provider.accounts/elements"),id);
        String where = null;
        String[] whereArgs = null;
        String order = null;
        Cursor cursor = cr.query(adresa,kolone,where,whereArgs,order);
        if (cursor != null){
            cursor.moveToFirst();
            int idPos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.ACCOUNT_ID);
            int internalId = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.ACCOUNT_INTERNAL_ID);
            int budgetPos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.ACCOUNT_BUDGET);
            int totalLimitPos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.ACCOUNT_TOTAL_LIMIT);
            int monthLimitPos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.ACCOUNT_MONTH_LIMIT);
            acc = new Account(cursor.getDouble(budgetPos), cursor.getDouble(totalLimitPos),
                    cursor.getDouble(monthLimitPos), cursor.getInt(idPos), cursor.getInt(internalId));
        }
        cursor.close();
        return acc;
    }


    @Override
    public void addToModel(ArrayList<Transaction> results) {
        TransactionsModel.transactions = results;
    }

    @Override
    public ArrayList<Transaction> getFromModel() {
        return TransactionsModel.transactions;
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