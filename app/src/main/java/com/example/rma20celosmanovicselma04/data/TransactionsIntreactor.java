package com.example.rma20celosmanovicselma04.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.rma20celosmanovicselma04.MainActivity;
import com.example.rma20celosmanovicselma04.budget.BudgetPresenter;
import com.example.rma20celosmanovicselma04.details.TransactionDetailPresenter;
import com.example.rma20celosmanovicselma04.util.ConnectionChecker;
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
import java.time.LocalTime;
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
    private ArrayList<Transaction> allTransactions = new ArrayList<>(); //sve trn

    public interface OnTransactionsSearchDone{
        void onDone(ArrayList<Transaction> results);
        void onAccountDone (Account account);
        void onTrnDoneForGraphs(ArrayList<Transaction> transactions);
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
            System.out.println(transactions.size() + " on post exec trn size");
            if(transactions.size() != 0) caller.onDone(transactions);
            if(account != null) caller.onAccountDone(account);
        }
        caller.onTrnDoneForGraphs(TransactionsModel.transactions);
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
        if(ConnectionChecker.isConnected(MainActivity.getAppContext())) {
            String query = strings[0];

            if (strings[1].equals("allTrn")) {    //dohvatanje svih trn
                getAllTransactions(strings[2]);
            } else if (strings[1].equals("sortFilter")) {  //dohvatanje sa filterom i sortom
                sortAndFilterTransactions(query, strings);
            } else if (strings[1].contains("add")) {  //POST
                addOrEditTransaction(strings);
            } else if (strings[1].equals("deleteTrn")) {
                deleteTransaction(strings);
            } else if (strings[1].equals("getAccount")) {
                System.out.println("do in bg account");
                getAccountFromWeb(strings[2]);
            } else if (strings[1].equals("editAccount")) {
                editAccount(strings);
            }
            else if(strings[1].equals("fromDbToWeb")) {
                fromDatabaseToWeb(strings);
            }
        }
        return null;
    }

    private void editAccount(String[] strings) {
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

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
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

    private void deleteTransaction(String[] strings) {
        String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" + strings[2] + "/transactions/" + strings[3];
        try {
            URL url = new URL(url1);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
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

    private void addOrEditTransaction(String[] strings) {
        String url1;
        if (strings[1].contains("Edit"))
            url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" + strings[2] + "/transactions/" + strings[3];  //ako se edituje
        else
            url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" + strings[2] + "/transactions";      //ako se dodaje
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

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
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

    private void sortAndFilterTransactions(String query, String[] strings) {
        if (query.contains("typeId=")) {
            query = replaceNameWithId(query);
        }
        for (Integer page = 0; ; page++) {
            String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" + strings[2] + "/" + "transactions/" + query + page.toString();
            try {
                JSONObject jo = getJsonObject(url1);
                JSONArray results = jo.getJSONArray("transactions");
                if (results.length() == 0) break;
                for (int i = 0; i < results.length(); i++) {
                    addTransactionToArray(results, i);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        ArrayList<Transaction> regulars = new ArrayList<>();
        int idReg = getTypeId("Regular payment");
        for (Integer page = 0; ; page++) {
            String url2 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" + strings[2] + "/" + "transactions/filter?typeId=" + idReg + "&page=" + page;
            try {
                JSONObject jo = getJsonObject(url2);
                JSONArray results = jo.getJSONArray("transactions");
                if (results.length() == 0) break;
                for (int i = 0; i < results.length(); i++) {
                    addToRegulars(regulars, results, i);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        idReg = getTypeId("Regular income");
        for (Integer page = 0; ; page++) {
            String url2 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" + strings[2] + "/" + "transactions/filter?typeId=" + idReg + "&page=" + page;
            try {
                JSONObject jo = getJsonObject(url2);
                JSONArray results = jo.getJSONArray("transactions");
                if (results.length() == 0) break;
                for (int i = 0; i < results.length(); i++) {
                    addToRegulars(regulars, results, i);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        for (Transaction t : regulars) {         //spajanje regulars sa ostalim
            addToThisMonth(t, Integer.parseInt(getMonthFromQuery(query)), Integer.parseInt(getYearFromQuery(query)));
        }

        if (strings[3].equals("getAcc")) {
            getAccountFromWeb(strings[2]);
        }
    }

    private void getAllTransactions(String string) {
        for (Integer page = 0; ; page++) {
            String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/" + string + "/" + "transactions?page=" + page.toString();
            try {
                JSONObject jo = getJsonObject(url1);
                JSONArray results = jo.getJSONArray("transactions");
                if (results.length() == 0) break;
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
        TransactionsModel.transactions.clear();
        TransactionsModel.transactions.addAll(allTransactions);
        System.out.println("bla" + TransactionsModel.transactions.size());
        allTransactions.clear();
        System.out.println("bla2222    " + TransactionsModel.transactions.size());
    }

    private void fromDatabaseToWeb(String[] strings) {
        ArrayList<Transaction> adds = getTransactionsByStatus("ADD", MainActivity.getAppContext());
        ArrayList<Transaction> updates = getTransactionsByStatus("UPDATE", MainActivity.getAppContext());
        ArrayList<Transaction> deletes = getTransactionsByStatus("DELETE", MainActivity.getAppContext());

        for (Transaction tr : adds) {
            String json = getJSONFormat(tr);
            strings[0] = json;
            addOrEditTransaction(strings);
        }

        for (Transaction tr : updates) {
            String json = getJSONFormat(tr);
            strings[0] = json;
            if(tr.getId() == 0) {
                strings[1] = "addTrn";
            }
            else {
                strings[1] = "Edit";
                strings[3] = String.valueOf(tr.getId());
            }
            addOrEditTransaction(strings);
        }

        for(Transaction tr : deletes) {
            if(tr.getId() != 0) {
                String json = getJSONFormat(tr);
                strings[0] = json;
                strings[3] = String.valueOf(tr.getId());
                deleteTransaction(strings);
            }
        }

        Account acc = getAccountFromDb(MainActivity.getAppContext());
        if(account != null) {
            String json = getJSONFormatAccount(acc);
            strings[0] = json;
            editAccount(strings);
        }
        clearDatabase();
    }

    private void clearDatabase() {
        ContentResolver cr = MainActivity.getAppContext().getApplicationContext().getContentResolver();
        Uri transactionsURI = Uri.parse("content://rma.provider.transactions/elements");
        cr.delete(transactionsURI, null, null);
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

    private String getJSONFormatAccount(Account account) {
        String json = "";

        json += "{" + "\"budget\": " + account.getBudget();
        json += "}";
        return json;
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
        //allTransactions.add(new Transaction(id, date, amount, title, type, description, interval, endDate));
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
        values.put(TransactionsDBOpenHelper.STATUS, "ADD");
        cr.insert(transactionsURI,values);
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
    public void UpdateTransactionInDb(Transaction trn, Context context, Transaction oldTrn) {
        if(oldTrn.getInternalId() == null) trn.setInternalId(null);
        else trn.setInternalId(oldTrn.getInternalId());
        if(oldTrn.getId() == null) trn.setId(null);
        else trn.setId(oldTrn.getId());

        ContentResolver cr = context.getApplicationContext().getContentResolver();
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


        if(!getStatus(trn, context).equals("DELETE")) {
            values.put(TransactionsDBOpenHelper.STATUS, "UPDATE");
        }

        String where = "_id=?";
        String [] whereArgs = {String.valueOf(trn.getInternalId())};


        if(trn.getInternalId() == null) {
            cr.insert(transactionsURI, values);
            while(TransactionsModel.transactions.contains(oldTrn)) {
                TransactionsModel.transactions.remove(oldTrn);
            }
        }
        else {
            cr.update(transactionsURI, values, where, whereArgs);
        }

    }

    @Override
    public void UpdateAccountInDb(Account account, Context context) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        Uri transactionsURI = Uri.parse("content://rma.provider.accounts/elements");
        ContentValues values = new ContentValues();
        values.put(TransactionsDBOpenHelper.ACCOUNT_ID, account.getId());
        values.put(TransactionsDBOpenHelper.ACCOUNT_BUDGET, account.getBudget());
        values.put(TransactionsDBOpenHelper.ACCOUNT_TOTAL_LIMIT, account.getTotalLimit());
        values.put(TransactionsDBOpenHelper.ACCOUNT_MONTH_LIMIT, account.getMonthLimit());

        String where = "_id=?";
        String [] whereArgs = {String.valueOf(account.getInternalId())};

        cr.update(transactionsURI, values, where, whereArgs);
    }

    @Override
    public Account getAccountFromDb(Context context) {
        Account acc = null;
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        String[] kolone = null;
        Uri adresa = Uri.parse("content://rma.provider.accounts/elements");
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

    public Cursor getTransactionsCursor (Context context, String where, String[] whereArgs) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        String[] kolone = new String[]{
                TransactionsDBOpenHelper.TRANSACTION_ID,
                TransactionsDBOpenHelper.TRANSACTION_INTERNAL_ID,
                TransactionsDBOpenHelper.TRANSACTION_TITLE,
                TransactionsDBOpenHelper.TRANSACTION_AMOUNT,
                TransactionsDBOpenHelper.TRANSACTION_DATE,
                TransactionsDBOpenHelper.TRANSACTION_END_DATE,
                TransactionsDBOpenHelper.TRANSACTION_INTERVAL,
                TransactionsDBOpenHelper.TRANSACTION_ITEM_DESCRIPTION,
                TransactionsDBOpenHelper.TRANSACTION_TYPE
        };
        Uri adresa = Uri.parse("content://rma.provider.transactions/elements");
        String order = null;
        Cursor cur = cr.query(adresa,kolone,where,whereArgs,order);
        return cur;
    }


    @Override
    public ArrayList<Transaction> getTransactionsFromDb(Context context) {
        ArrayList<Transaction> trns = new ArrayList<>();

        Cursor cursor = getTransactionsCursor(context, null, null);
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    int idPos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.TRANSACTION_ID);
                    int internalId = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.TRANSACTION_INTERNAL_ID);
                    int titlePos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.TRANSACTION_TITLE);
                    int datePos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.TRANSACTION_DATE);
                    int endDatePos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.TRANSACTION_END_DATE);
                    int typePos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.TRANSACTION_TYPE);
                    int transactionIntervalPos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.TRANSACTION_INTERVAL);
                    int amountPos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.TRANSACTION_AMOUNT);
                    int itemDescriptionPos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.TRANSACTION_ITEM_DESCRIPTION);

                    LocalDate endDate;
                    if(cursor.getString(endDatePos) == null) {
                        endDate = null;
                    }
                    else endDate = LocalDate.parse(cursor.getString(endDatePos));
                    //provjeritiiiiiii
                    trns.add(new Transaction(cursor.getInt(idPos), LocalDate.parse(cursor.getString(datePos)), cursor.getDouble(amountPos),
                            cursor.getString(titlePos), TransactionType.valueOf(cursor.getString(typePos)), cursor.getString(itemDescriptionPos), cursor.getInt(transactionIntervalPos),
                            endDate, cursor.getInt(internalId)));
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        return trns;
    }

    @Override
    public void SetDeleteStatus(Transaction trn, Context context) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
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
        values.put(TransactionsDBOpenHelper.STATUS, "DELETE");

        String where = "_id=?";
        String [] whereArgs = {String.valueOf(trn.getInternalId())};


        if(trn.getInternalId() == null) {
            cr.insert(transactionsURI, values);
            while(TransactionsModel.transactions.contains(trn)) {
                TransactionsModel.transactions.remove(trn);
            }
        }
        else {
            cr.update(transactionsURI, values, where, whereArgs);
        }
    }

    @Override
    public void changeForUndoDb(Transaction trn, Context applicationContext) {
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
        values.put(TransactionsDBOpenHelper.STATUS, "UPDATE");

        String where = "_id=?";
        String [] whereArgs = {String.valueOf(trn.getInternalId())};


        if(trn.getInternalId() == null) {
            cr.insert(transactionsURI, values);
            while(TransactionsModel.transactions.contains(trn)) {
                TransactionsModel.transactions.remove(trn);
            }
        }
        else {
            cr.update(transactionsURI, values, where, whereArgs);
        }
    }

    public String getStatus(Transaction trn, Context context) {
        if(trn.getInternalId() != null) {
            String status = "";
            ContentResolver cr = context.getApplicationContext().getContentResolver();
            String[] kolone = null;
            Uri adresa = ContentUris.withAppendedId(Uri.parse("content://rma.provider.transactions/elements"), trn.getInternalId());
            String where = null;
            String whereArgs[] = null;
            String order = null;
            Cursor cursor = cr.query(adresa, kolone, where, whereArgs, order);
            if (cursor != null) {
                cursor.moveToFirst();
                if (!cursor.isBeforeFirst()) {
                    int statusPos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.STATUS);
                    status = cursor.getString(statusPos);
                    return status;
                } else {
                    return "ERROR";
                }
            }
            cursor.close();
        }
        return "ERROR - internal id is null";
    }

    public ArrayList<Transaction> getTransactionsByStatus (String status, Context context) {
        ArrayList<Transaction> trns = new ArrayList<>();

        Cursor cursor = getTransactionsCursor(context, "status=?", new String[]{status});

        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    int idPos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.TRANSACTION_ID);
                    int internalId = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.TRANSACTION_INTERNAL_ID);
                    int titlePos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.TRANSACTION_TITLE);
                    int datePos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.TRANSACTION_DATE);
                    int endDatePos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.TRANSACTION_END_DATE);
                    int typePos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.TRANSACTION_TYPE);
                    int transactionIntervalPos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.TRANSACTION_INTERVAL);
                    int amountPos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.TRANSACTION_AMOUNT);
                    int itemDescriptionPos = cursor.getColumnIndexOrThrow(TransactionsDBOpenHelper.TRANSACTION_ITEM_DESCRIPTION);

                    LocalDate endDate;
                    if(cursor.getString(endDatePos) == null) {
                        endDate = null;
                    }
                    else endDate = LocalDate.parse(cursor.getString(endDatePos));
                    //provjeritiiiiiii
                    trns.add(new Transaction(cursor.getInt(idPos), LocalDate.parse(cursor.getString(datePos)), cursor.getDouble(amountPos),
                            cursor.getString(titlePos), TransactionType.valueOf(cursor.getString(typePos)), cursor.getString(itemDescriptionPos), cursor.getInt(transactionIntervalPos),
                            endDate, cursor.getInt(internalId)));
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        return trns;

    }

    @Override
    public void addToModel(ArrayList<Transaction> results) {
        TransactionsModel.transactions.clear();
        TransactionsModel.transactions.addAll(results);
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