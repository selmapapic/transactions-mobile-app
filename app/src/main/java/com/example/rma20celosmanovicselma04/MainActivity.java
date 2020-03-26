package com.example.rma20celosmanovicselma04;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ITransactionsView {
    private ITransactionsPresenter presenter;
    private TransactionsAdapter transactionsAdapter;
    private Button leftButton, rightButton;
    private TextView monthText;
    private ListView transactionListView;
    private Spinner filterSpinner, sortSpinner;
    private FilterAdapter filterAdapter;
    private ArrayAdapter<String> sortAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        leftButton = (Button) findViewById(R.id.leftButton);
        rightButton = (Button) findViewById(R.id.rightButton);
        monthText = (TextView) findViewById(R.id.monthText);
        transactionListView = (ListView) findViewById(R.id.transactionListView);
        filterSpinner = (Spinner) findViewById(R.id.filterSpinner);
        sortSpinner = (Spinner) findViewById(R.id.sortSpinner);

        leftButton.setOnClickListener(leftAction());
        rightButton.setOnClickListener(rightAction());
        filterSpinner.setOnItemSelectedListener(spinnerTypeAction());
        sortSpinner.setOnItemSelectedListener(spinnerSortAction());

        transactionsAdapter = new TransactionsAdapter(this, R.layout.transactions_list_element, new ArrayList<>());
        transactionListView.setAdapter(transactionsAdapter);

        filterAdapter = new FilterAdapter(this, R.layout.transaction_spinner_element, new ArrayList<>());
        filterSpinner.setAdapter(filterAdapter);

        sortAdapter = new ArrayAdapter<>(this, R.layout.sort_spinner_element, R.id.sortType, new ArrayList<>());
        sortSpinner.setAdapter(sortAdapter);

        getPresenter().refreshTransactionsByMonthAndYear();
        getPresenter().refreshFilterAndSort((String) filterSpinner.getSelectedItem(), (String) sortSpinner.getSelectedItem());
        getPresenter().start();
    }

    public ITransactionsPresenter getPresenter () {
        if(presenter == null) {
            presenter = new TransactionsPresenter(this, this);
        }
        return presenter;
    }

    @Override
    public void setTransactions(ArrayList<Transaction> transactions) {
        transactionsAdapter.setTransactions(transactions);
    }

    public void setFilterSpinner (ArrayList<String> types) {
        filterAdapter.setTransactionType(types);
    }

    public void setSortSpinner (ArrayList<String> sort) {
        sortAdapter.addAll(sort);
    }

    @Override
    public void notifyTransactionsListDataSetChanged() {
        transactionsAdapter.notifyDataSetChanged();
    }

    public View.OnClickListener leftAction () {
        return v -> {
            getPresenter().changeMonthBackward();
            getPresenter().refreshTransactionsByMonthAndYear();
            getPresenter().refreshFilterAndSort((String) filterSpinner.getSelectedItem(), (String) sortSpinner.getSelectedItem());
        };
    }

    public View.OnClickListener rightAction () {
        return v -> {
            getPresenter().changeMonthForward();
            getPresenter().refreshTransactionsByMonthAndYear();
            getPresenter().refreshFilterAndSort((String) filterSpinner.getSelectedItem(), (String) sortSpinner.getSelectedItem());
        };
    }

    public AdapterView.OnItemSelectedListener spinnerTypeAction() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getPresenter().refreshFilterAndSort((String) filterSpinner.getSelectedItem(), (String) sortSpinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        };
    }

    public AdapterView.OnItemSelectedListener spinnerSortAction() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getPresenter().refreshFilterAndSort((String) filterSpinner.getSelectedItem(), (String) sortSpinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        };
    }

    @Override
    public void refreshDate(String date) {
        monthText.setText(date);
    }

}
