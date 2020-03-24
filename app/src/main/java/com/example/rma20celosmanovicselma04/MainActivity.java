package com.example.rma20celosmanovicselma04;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ITransactionsView {
    private ITransactionsPresenter presenter;
    private TransactionsAdapter adapter;
    private Button leftButton, rightButton;
    private TextView monthText;
    private ListView transactionListView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        leftButton = (Button) findViewById(R.id.leftButton);
        rightButton = (Button) findViewById(R.id.rightButton);
        monthText = (TextView) findViewById(R.id.monthText);
        transactionListView = (ListView) findViewById(R.id.transactionListView);

        getPresenter().start();
        leftButton.setOnClickListener(leftAction());
        rightButton.setOnClickListener(rightAction());

        adapter = new TransactionsAdapter(this, R.layout.transactions_list_element, new ArrayList<Transaction>());
        transactionListView.setAdapter(adapter);
        getPresenter().refreshTransactions();
    }

    public ITransactionsPresenter getPresenter () {
        if(presenter == null) {
            presenter = new TransactionsPresenter(this, this);
        }
        return presenter;
    }

    @Override
    public void setTransactions(ArrayList<Transaction> transactions) {
        adapter.setTransactions(transactions);
    }

    @Override
    public void notifyMovieListDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public View.OnClickListener leftAction () {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().changeMonthBackward();
            }
        };
    }

    public View.OnClickListener rightAction () {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().changeMonthForward();
            }
        };
    }

    @Override
    public void refreshDate(String date) {
        monthText.setText(date);
    }


}
