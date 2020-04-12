package com.example.rma20celosmanovicselma04;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class BudgetFragment extends Fragment implements IBudgetView{
    private IBudgetPresenter presenter;
    private TextView budgetText;
    private EditText totalLimitFld, monthLimitFld;
    private Button saveBtn;
    private ImageButton resetBtn;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.budget_fragment, container, false);

        totalLimitFld = (EditText) view.findViewById(R.id.totalLimitFld);
        monthLimitFld = (EditText) view.findViewById(R.id.monthLimitFld);
        budgetText = (TextView) view.findViewById(R.id.budgetText);
        saveBtn = (Button) view.findViewById(R.id.saveBtn);
        resetBtn = (ImageButton) view.findViewById(R.id.resetBtn);

        resetBtn.setOnClickListener(resetFields());
        getPresenter().start();

        return view;
    }

    private View.OnClickListener resetFields() {
        return v -> getPresenter().start();
    }

    public IBudgetPresenter getPresenter() {
        if (presenter == null) {
            presenter = new BudgetPresenter(this, getActivity());
        }
        return presenter;
    }

    public void setBudgetText(Double budget) {
        budgetText.setText(budget.toString());
    }

    public void setTotalLimitFld (Double totalLimit) {
        totalLimitFld.setText(totalLimit.toString());
    }

    public void setMonthLimitFld (Double monthLimit) {
        monthLimitFld.setText(monthLimit.toString());
    }
}
