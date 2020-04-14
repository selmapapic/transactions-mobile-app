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
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

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
        getPresenter().refreshIncomeGraphByMonth();
        getPresenter().refreshExpenseGraphByMonth();
        getPresenter().refreshCombinedGraphByMonth();

        
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
                getPresenter().refreshExpenseGraphByMonth();
            }
            else if(checkedId == R.id.weekBtn) {

            }
            else if(checkedId == R.id.dayBtn) {

            }
        };
    }

    public void setExpenseGraph () {
        ArrayList<Double> values = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            values.add(i, getPresenter().expenseGraphMonthValues(i + 1));
        }

        graphSetOptions(expenseGraph);

        ArrayList<BarEntry> list = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            list.add(new BarEntry(i + 1, values.get(i).floatValue()));
        }

        BarDataSet bar = new BarDataSet(list, "Monthly expenses");
        bar.setColors(ColorTemplate.PASTEL_COLORS);

        BarData data = new BarData(bar);
        data.setBarWidth(0.9f);

        expenseGraph.setData(data);
    }

    public void setIncomeGraph () {
        ArrayList<Double> values = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            values.add(i, getPresenter().incomeGraphMonthValues(i + 1));
        }

        graphSetOptions(incomeGraph);

        ArrayList<BarEntry> list = new ArrayList<>();
        for(int i = 0; i < 12; i++ ) {
            list.add(new BarEntry(i + 1, values.get(i).floatValue()));
        }

        BarDataSet bar = new BarDataSet(list, "Monthly income");
        bar.setColors(ColorTemplate.PASTEL_COLORS);

        BarData data = new BarData(bar);
        data.setBarWidth(0.9f);

        incomeGraph.setData(data);
        incomeGraph.setDrawValueAboveBar(true);
    }

    public void setCombinedGraph () {
        ArrayList<Double> values = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            values.add(i, getPresenter().combinedGraphMonthValues(i + 1));
        }

        graphSetOptions(combinedGraph);

        ArrayList<BarEntry> list = new ArrayList<>();
        for(int i = 0; i < 12; i++ ) {
            list.add(new BarEntry(i + 1, values.get(i).floatValue()));
        }

        BarDataSet bar = new BarDataSet(list, "Monthly expense and income");
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
