package com.example.rma20celosmanovicselma04;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;

import static com.example.rma20celosmanovicselma04.R.layout.transaction_detail;

public class TransactionDetailActivity extends AppCompatActivity {
    private ITransactionDetailPresenter presenter;
    private EditText titleFld, amountFld, intervalFld, dateFld, endDateFld, descriptionFld;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(transaction_detail);

        getPresenter().create((LocalDate) getIntent().getExtras().get("dateFld"), getIntent().getExtras().getDouble("amountFld"), getIntent().getStringExtra("titleFld"), TransactionType.REGULARINCOME,
                getIntent().getStringExtra("descriptionFld"), getIntent().getExtras().getInt("intervalFld"), (LocalDate) getIntent().getExtras().get("endDateFld"));

        titleFld = (EditText) findViewById(R.id.titleFld);
        amountFld = (EditText) findViewById(R.id.amountFld);
        intervalFld = (EditText) findViewById(R.id.intervalFld);
        dateFld = (EditText) findViewById(R.id.dateFld);
        endDateFld = (EditText) findViewById(R.id.endDateFld);
        descriptionFld = (EditText) findViewById(R.id.descriptionFld);

        titleFld.setText(getPresenter().getTransaction().getTitle());
        amountFld.setText(getPresenter().getTransaction().getAmount().toString());
        intervalFld.setText(getPresenter().getTransaction().getTransactionInterval().toString());
        dateFld.setText(getPresenter().getTransaction().getDate().toString());
        endDateFld.setText(getPresenter().getTransaction().getEndDate().toString());
        descriptionFld.setText(getPresenter().getTransaction().getItemDescription());
    }

    public ITransactionDetailPresenter getPresenter () {
        if(presenter == null) {
            presenter = new TransactionDetailPresenter(this);
        }
        return presenter;
    }
}
