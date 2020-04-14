package com.example.rma20celosmanovicselma04.graphs;

public interface IGraphsPresenter {
    double expenseGraphMonthValues (int month);
    void refreshExpenseGraphByMonth ();
    double incomeGraphMonthValues (int month);
    void refreshIncomeGraphByMonth();

    double combinedGraphMonthValues(int month);

    void refreshCombinedGraphByMonth();
}
