package com.example.rma20celosmanovicselma04;

import java.util.ArrayList;

public interface ITransactionsView {
    void setTransactions(ArrayList<Transaction> movies);
    void notifyTransactionsListDataSetChanged();
    void refreshDate(String date);
    void setFilterSpinner (ArrayList<String> types);
}
