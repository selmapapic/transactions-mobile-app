package com.example.rma20celosmanovicselma04.graphs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graphs_fragment, container, false);

        expenseGraph = (BarChart) view.findViewById(R.id.expenseGraph);
        incomeGraph = (BarChart) view.findViewById(R.id.incomeGraph);
        combinedGraph = (BarChart) view.findViewById(R.id.combinedGraph);

        combinedGraph.setDrawBarShadow(false);
        combinedGraph.setDrawValueAboveBar(true);
        combinedGraph.setMaxVisibleValueCount(12);
        combinedGraph.setPinchZoom(false);
        combinedGraph.setDrawGridBackground(true);

        ArrayList<BarEntry> list = new ArrayList<>();
        list.add(new BarEntry(1, 40));
        list.add(new BarEntry(2, 50));
        list.add(new BarEntry(3, 70));
        list.add(new BarEntry(4, 10));
        list.add(new BarEntry(5, 20));

        BarDataSet bar = new BarDataSet(list, "Data set 1");
        bar.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(bar);
        data.setBarWidth(0.3f);

        combinedGraph.setData(data);
        return view;
    }


}
