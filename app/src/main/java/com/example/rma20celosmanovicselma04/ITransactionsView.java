package com.example.rma20celosmanovicselma04;

import java.util.ArrayList;

public interface ITransactionsView {
    void setTransactions(ArrayList<Transaction> movies);
    void notifyMovieListDataSetChanged();
    void refreshDate(String date);
}
