package com.example.rma20celosmanovicselma04.graphs;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public interface IGraphsPresenter {
    void refreshGraphs (int monthWeekDay);
    ArrayList<BarEntry> getExpenseValuesForGraphByMonth ();
    ArrayList<BarEntry> getIncomeValuesForGraphByMonth ();
    ArrayList<BarEntry> getCombinedValuesForGraphByMonth();
    ArrayList<BarEntry> getExpenseValuesForGraphByWeek ();
    ArrayList<BarEntry> getIncomeValuesForGraphByWeek ();
    ArrayList<BarEntry> getCombinedValuesForGraphByWeek();
    ArrayList<BarEntry> getExpenseValuesForGraphByDay ();
    ArrayList<BarEntry> getIncomeValuesForGraphByDay ();
    ArrayList<BarEntry> getCombinedValuesForGraphByDay ();
    String[] getWeekDays();
}
