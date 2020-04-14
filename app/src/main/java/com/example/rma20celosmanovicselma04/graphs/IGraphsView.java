package com.example.rma20celosmanovicselma04.graphs;

public interface IGraphsView {
    void setExpenseGraph (int monthWeekDay); //month = 1, week = 2, day = 3
    void setIncomeGraph(int monthWeekDay);
    void setCombinedGraph (int monthWeekDay);
}
