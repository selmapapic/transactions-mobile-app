package com.example.rma20celosmanovicselma04.budget;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.rma20celosmanovicselma04.R;
import com.example.rma20celosmanovicselma04.Swipe;
import com.example.rma20celosmanovicselma04.transactionsList.TransactionListFragment;

public class BudgetFragment extends Fragment implements IBudgetView{
    private IBudgetPresenter presenter;
    private TextView budgetText;
    private EditText totalLimitFld, monthLimitFld;
    private Button saveBtn, homeBtn, graphsBtn;
    private ImageButton resetBtn;
    private GestureDetector gestureDetector;
    private Swipe swipe = new Swipe();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.budget_fragment, container, false);

        totalLimitFld = (EditText) view.findViewById(R.id.totalLimitFld);
        monthLimitFld = (EditText) view.findViewById(R.id.monthLimitFld);
        budgetText = (TextView) view.findViewById(R.id.budgetText);
        saveBtn = (Button) view.findViewById(R.id.saveBtn);
        resetBtn = (ImageButton) view.findViewById(R.id.resetBtn);
        homeBtn = (Button) view.findViewById(R.id.homeBtn);
        graphsBtn = (Button) view.findViewById(R.id.graphsBtn);

        onItemClick = (TransactionListFragment.OnItemClick) getActivity();

        graphsBtn.setOnClickListener(graphsAction());
        homeBtn.setOnClickListener(homeAction());
        saveBtn.setOnClickListener(saveAction());
        resetBtn.setOnClickListener(resetFields());

        gestureDetector = new GestureDetector(getContext(), swipe);
        swipe.setNext(3);
        swipe.setPrevious(1);
        swipe.setOnItemClick(onItemClick);
        swipe.setGestureDetector(gestureDetector);
        view.setOnTouchListener(swipe.getLis());
        getPresenter().start();

        return view;
    }

    private View.OnClickListener saveAction() {
        return v -> {
            if(totalLimitFld.getText().length() == 0) {
                totalLimitFld.setError("This field cannot be empty");
            }
            else {
                totalLimitFld.setError(null);
            }
            if(monthLimitFld.getText().length() == 0) {
                monthLimitFld.setError("This field cannot be empty");
            }
            else {
                monthLimitFld.setError(null);
            }
            if(totalLimitFld.getError() == null && monthLimitFld.getError() == null) {
                getPresenter().saveNewChanges(Double.parseDouble(String.valueOf(totalLimitFld.getText())), Double.parseDouble(String.valueOf(monthLimitFld.getText())));
                getPresenter().refreshFields();
            }
        };
    }

    private View.OnClickListener graphsAction() {
        return v -> onItemClick.onNextClicked(3);
    }

    private View.OnClickListener homeAction() {
        return v -> onItemClick.onPreviousClicked(1);
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

    private TransactionListFragment.OnItemClick onItemClick;
    public interface OnItemClick {
        void onNextClicked(int page);
        void onPreviousClicked(int page);
    }
}
