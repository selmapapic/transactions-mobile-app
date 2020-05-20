package com.example.rma20celosmanovicselma04.budget;

import com.example.rma20celosmanovicselma04.data.Account;

public interface IBudgetPresenter {
    void start ();
    void saveNewChanges(Double totalLimit, Double monthLimit);
    void refreshFields();
    void searchAccount(String query, Account edit);
}
