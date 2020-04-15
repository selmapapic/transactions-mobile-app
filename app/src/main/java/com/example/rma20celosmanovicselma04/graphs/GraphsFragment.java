package com.example.rma20celosmanovicselma04.graphs;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

import com.example.rma20celosmanovicselma04.R;
import com.example.rma20celosmanovicselma04.transactionsList.TransactionListFragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

public class GraphsFragment extends Fragment implements IGraphsView{
    private IGraphsPresenter presenter;
    private BarChart incomeGraph, expenseGraph, combinedGraph;
    private RadioGroup radioGroup;
    private Button settingsBtn, homeBtn;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graphs_fragment, container, false);

        expenseGraph = (BarChart) view.findViewById(R.id.expenseGraph);
        incomeGraph = (BarChart) view.findViewById(R.id.incomeGraph);
        combinedGraph = (BarChart) view.findViewById(R.id.combinedGraph);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        settingsBtn = (Button) view.findViewById(R.id.settingsBtn);
        homeBtn = (Button) view.findViewById(R.id.homeBtn);

        radioGroup.check(R.id.monthBtn);

        onItemClick = (TransactionListFragment.OnItemClick) getActivity();

        settingsBtn.setOnClickListener(settingsAction());
        homeBtn.setOnClickListener(homeAction());

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

    private View.OnClickListener homeAction() {
        return v -> onItemClick.onNextClicked(1);
    }

    private View.OnClickListener settingsAction() {
        return v -> onItemClick.onPreviousClicked(2);
    }

    private RadioGroup.OnCheckedChangeListener radioListener() {
        return (group, checkedId) -> {
            if(checkedId == R.id.monthBtn) {
                getPresenter().refreshGraphs(1);
                incomeGraph.invalidate();
                expenseGraph.invalidate();
                combinedGraph.invalidate();
            }
            else if(checkedId == R.id.weekBtn) {
                getPresenter().refreshGraphs(2);
                incomeGraph.invalidate();
                expenseGraph.invalidate();
                combinedGraph.invalidate();
            }
            else if(checkedId == R.id.dayBtn) {
                getPresenter().refreshGraphs(3);
                incomeGraph.invalidate();
                expenseGraph.invalidate();
                combinedGraph.invalidate();
            }
        };
    }

    public void setExpenseGraph (int monthWeekDay) {
        graphSetOptions(expenseGraph);
        BarDataSet bar = null;
        if(monthWeekDay == 1) {
            bar = new BarDataSet(getPresenter().getExpenseValuesForGraphByMonth(), "Monthly expenses");
            setMonthAxis(expenseGraph);
        }
        else if(monthWeekDay == 2) {
            bar = new BarDataSet(getPresenter().getExpenseValuesForGraphByWeek(), "Weekly expenses");
            setWeekAxis(expenseGraph);
        }
        else if(monthWeekDay == 3) {
            bar = new BarDataSet(getPresenter().getExpenseValuesForGraphByDay(), "Daily expenses");
            setDayAxis(expenseGraph);
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
            setMonthAxis(incomeGraph);
        }
        else if(monthWeekDay == 2) {
            bar = new BarDataSet(getPresenter().getIncomeValuesForGraphByWeek(), "Weekly income");
            setWeekAxis(incomeGraph);
        }
        else if(monthWeekDay == 3) {
            bar = new BarDataSet(getPresenter().getIncomeValuesForGraphByDay(), "Daily income");
            setDayAxis(incomeGraph);
        }
        bar.setColors(ColorTemplate.PASTEL_COLORS);

        BarData data = new BarData(bar);
        data.setBarWidth(0.9f);
        incomeGraph.setDrawValueAboveBar(true);

        incomeGraph.setData(data);
        incomeGraph.setDrawValueAboveBar(true);
    }

    public void setCombinedGraph (int monthWeekDay) {

        graphSetOptions(combinedGraph);

        BarDataSet bar = null;
        if(monthWeekDay == 1) {
            bar = new BarDataSet(getPresenter().getCombinedValuesForGraphByMonth(), "Monthly expense and income");
            setMonthAxis(combinedGraph);
        }
        else if(monthWeekDay == 2) {
            bar = new BarDataSet(getPresenter().getCombinedValuesForGraphByWeek(), "Weekly expense and income");
            setWeekAxis(combinedGraph);
        }
        else if(monthWeekDay == 3) {
            bar = new BarDataSet(getPresenter().getCombinedValuesForGraphByDay(), "Daily expense and income");
            setDayAxis(combinedGraph);
        }
        bar.setColors(ColorTemplate.PASTEL_COLORS);

        BarData data = new BarData(bar);
        data.setBarWidth(0.9f);

        combinedGraph.setData(data);
    }

    private void setWeekAxis(BarChart graph) {
        XAxis xAxis = graph.getXAxis();
        xAxis.setValueFormatter(null);
        xAxis.setGranularity(1f);
    }

    private void setMonthAxis(BarChart graph) {
        XAxis xAxis = graph.getXAxis();
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        String xVal[]={"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xVal[(int) value-1];
            }
        });
    }

    private void setDayAxis(BarChart graph) {
        XAxis xAxis = graph.getXAxis();
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        String xVal[] = getPresenter().getWeekDays();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xVal[(int) value-1];
            }
        });
    }

    private void graphSetOptions(BarChart graph) {
        graph.setDrawBarShadow(false);
        graph.setDrawValueAboveBar(true);
        graph.setPinchZoom(false);
        graph.setDrawGridBackground(true);
        graph.setDescription(null);
        graph.setBorderColor(Color.WHITE);
    }

    private TransactionListFragment.OnItemClick onItemClick;
    public interface OnItemClick {
        void onNextClicked(int page);
        void onPreviousClicked(int page);
    }
}
