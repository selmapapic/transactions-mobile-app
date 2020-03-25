package com.example.rma20celosmanovicselma04;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ITransactionsView {
    private ITransactionsPresenter presenter;
    private TransactionsAdapter transactionsAdapter;
    private Button leftButton, rightButton;
    private TextView monthText;
    private ListView transactionListView;
    private Spinner filterSpinner;
    private FilterAdapter filterAdapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        leftButton = (Button) findViewById(R.id.leftButton);
        rightButton = (Button) findViewById(R.id.rightButton);
        monthText = (TextView) findViewById(R.id.monthText);
        transactionListView = (ListView) findViewById(R.id.transactionListView);
        filterSpinner = (Spinner) findViewById(R.id.filterSpinner);

        leftButton.setOnClickListener(leftAction());
        rightButton.setOnClickListener(rightAction());
        filterSpinner.setOnItemSelectedListener(spinnerAction());

        transactionsAdapter = new TransactionsAdapter(this, R.layout.transactions_list_element, new ArrayList<>());
        transactionListView.setAdapter(transactionsAdapter);

        filterAdapter = new FilterAdapter(this, R.layout.transaction_spinner_element, new ArrayList<>());
        filterSpinner.setAdapter(filterAdapter);

        getPresenter().refreshTransactionsByMonthAndYear();
        getPresenter().refreshTransactionsByType((String) filterSpinner.getSelectedItem());
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


    @Override
    public void notifyTransactionsListDataSetChanged() {
        transactionsAdapter.notifyDataSetChanged();
    }

    public View.OnClickListener leftAction () {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().changeMonthBackward();
                getPresenter().refreshTransactionsByMonthAndYear();
            }
        };
    }

    public View.OnClickListener rightAction () {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().changeMonthForward();
                getPresenter().refreshTransactionsByMonthAndYear();
            }
        };
    }

    public AdapterView.OnItemSelectedListener spinnerAction () {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getPresenter().refreshTransactionsByType((String) filterSpinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }


    @Override
    public void refreshDate(String date) {
        monthText.setText(date);
    }


}
