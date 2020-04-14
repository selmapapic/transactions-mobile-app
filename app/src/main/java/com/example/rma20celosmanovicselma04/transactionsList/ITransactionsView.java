package com.example.rma20celosmanovicselma04.transactionsList;

import com.example.rma20celosmanovicselma04.data.Transaction;

import java.util.ArrayList;

public interface ITransactionsView {
    void setTransactions(ArrayList<Transaction> transactions);
    void notifyTransactionsListDataSetChanged();
    void refreshDate(String date);
    void setFilterSpinner (ArrayList<String> types);
    void setSortSpinner (ArrayList<String> sort);
    void setBudgetLimit (Double amt, Double limit);
    void setBudget (Double budget);
}
