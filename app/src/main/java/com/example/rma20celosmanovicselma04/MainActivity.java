package com.example.rma20celosmanovicselma04;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ITransactionsView {
    private ITransactionsPresenter presenter;

    private Button leftButton, rightButton;
    private TextView monthText;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new TransactionsPresenter(this, this);

        leftButton = (Button) findViewById(R.id.leftButton);
        rightButton = (Button) findViewById(R.id.rightButton);
        monthText = (TextView) findViewById(R.id.monthText);
        monthText.setText(presenter.dateToString());
        leftButton.setOnClickListener(leftAction());
        rightButton.setOnClickListener(rightAction());
    }

    @Override
    public void setTransactions(ArrayList<Transaction> movies) {

    }

    @Override
    public void notifyMovieListDataSetChanged() {

    }

    public View.OnClickListener leftAction () {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthText.setText(presenter.changeMonthBackward());
            }
        };
    }

    public View.OnClickListener rightAction () {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthText.setText(presenter.changeMonthForward());
            }
        };
    }


}
