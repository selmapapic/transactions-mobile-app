package com.example.rma20celosmanovicselma04.graphs;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

import com.example.rma20celosmanovicselma04.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

public class GraphsFragment extends Fragment implements IGraphsView{
    private IGraphsPresenter presenter;
    private BarChart incomeGraph, expenseGraph, combinedGraph;
    private RadioGroup radioGroup;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graphs_fragment, container, false);

        expenseGraph = (BarChart) view.findViewById(R.id.expenseGraph);
        incomeGraph = (BarChart) view.findViewById(R.id.incomeGraph);
        combinedGraph = (BarChart) view.findViewById(R.id.combinedGraph);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        radioGroup.check(R.id.monthBtn);

        radioGroup.setOnCheckedChangeListener(radioListener());
        getPresenter().refreshGraphs(1);

        return view;
    }

    public IGraphsPresenter getPresenter() {
        if (presenter == null) {
            presenter = new GraphsPresenter(getActivity(), this);
        }
        return presenter;
    }

    private RadioGroup.OnCheckedChangeListener radioListener() {
        return (group, checkedId) -> {
            if(checkedId == R.id.monthBtn) {
                getPresenter().refreshGraphs(1);
            }
            else if(checkedId == R.id.weekBtn) {
                getPresenter().refreshGraphs(2);
            }
            else if(checkedId == R.id.dayBtn) {
                getPresenter().refreshGraphs(3);
            }
        };
    }

    public void setExpenseGraph (int monthWeekDay) {
        graphSetOptions(expenseGraph);

        BarDataSet bar = null;
        if(monthWeekDay == 1) {
             bar = new BarDataSet(getPresenter().getExpenseValuesForGraphByMonth(), "Monthly expenses");
        }
        else if(monthWeekDay == 2) {

        }
        else if(monthWeekDay == 3) {

        }
        bar.setColors(ColorTemplate.PASTEL_COLORS);

        BarData data = new BarData(bar);
        data.setBarWidth(0.9f);

        expenseGraph.setData(data);
    }

    public void setIncomeGraph (int monthWeekDay) {
        graphSetOptions(incomeGraph);

        BarDataSet bar = null;
        if(monthWeekDay == 1) {
            bar = new BarDataSet(getPresenter().getIncomeValuesForGraphByMonth(), "Monthly income");
        }
        else if(monthWeekDay == 2) {

        }
        else if(monthWeekDay == 3) {

        }
        bar.setColors(ColorTemplate.PASTEL_COLORS);

        BarData data = new BarData(bar);
        data.setBarWidth(0.9f);

        incomeGraph.setData(data);
        incomeGraph.setDrawValueAboveBar(true);
    }

    public void setCombinedGraph (int monthWeekDay) {

        graphSetOptions(combinedGraph);

        BarDataSet bar = null;
        if(monthWeekDay == 1) {
            bar = new BarDataSet(getPresenter().getCombinedValuesForGraphByMonth(), "Monthly expense and income");
        }
        else if(monthWeekDay == 2) {

        }
        else if(monthWeekDay == 3) {

        }
        bar.setColors(ColorTemplate.PASTEL_COLORS);

        BarData data = new BarData(bar);
        data.setBarWidth(0.9f);

        combinedGraph.setData(data);
    }

    private void graphSetOptions(BarChart graph) {
        //graph.setDrawBarShadow(false);
        graph.setDrawValueAboveBar(true);
        graph.setMaxVisibleValueCount(12);
        //graph.setPinchZoom(false);
        graph.setDrawGridBackground(true);
        graph.setDescription(null);
        graph.setBorderColor(Color.WHITE);

    }
}
