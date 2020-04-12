package com.example.rma20celosmanovicselma04;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class BudgetFragment extends Fragment implements IBudgetView{
    private IBudgetPresenter presenter;
    private TextView budgetText;
    private EditText totalLimitFld, monthLimitFld;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.budget_fragment, container, false);

        totalLimitFld = (EditText) view.findViewById(R.id.totalLimitFld);
        monthLimitFld = (EditText) view.findViewById(R.id.monthLimitFld);
        budgetText = (TextView) view.findViewById(R.id.budgetText);



        return view;
    }

    public IBudgetPresenter getPresenter() {
        if (presenter == null) {
            presenter = new BudgetPresenter(this, getActivity());
        }
        return presenter;
    }


}
