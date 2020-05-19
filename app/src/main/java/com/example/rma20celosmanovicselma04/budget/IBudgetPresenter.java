package com.example.rma20celosmanovicselma04.budget;

public interface IBudgetPresenter {
    void start ();
    void saveNewChanges(Double totalLimit, Double monthLimit);
    void refreshFields();
    void searchAccount(String query);
}
