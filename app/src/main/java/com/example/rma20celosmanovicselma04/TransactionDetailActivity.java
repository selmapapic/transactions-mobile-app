package com.example.rma20celosmanovicselma04;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.util.ArrayList;

import static com.example.rma20celosmanovicselma04.R.layout.transaction_detail;

public class TransactionDetailActivity extends AppCompatActivity implements ITransactionDetailView {
    private ITransactionDetailPresenter presenter;
    private EditText titleFld, amountFld, intervalFld, dateFld, endDateFld, descriptionFld;
    private ArrayAdapter<String> typeAdapter;
    private Spinner spinnerType;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(transaction_detail);
        getPresenter().create((LocalDate) getIntent().getExtras().get("dateFld"), getIntent().getExtras().getDouble("amountFld"), getIntent().getStringExtra("titleFld"), TransactionType.getType(getIntent().getStringExtra("spinnerType")),
                getIntent().getStringExtra("descriptionFld"), getIntent().getExtras().getInt("intervalFld"), (LocalDate) getIntent().getExtras().get("endDateFld"));

        titleFld = (EditText) findViewById(R.id.titleFld);
        amountFld = (EditText) findViewById(R.id.amountFld);
        intervalFld = (EditText) findViewById(R.id.intervalFld);
        dateFld = (EditText) findViewById(R.id.dateFld);
        endDateFld = (EditText) findViewById(R.id.endDateFld);
        descriptionFld = (EditText) findViewById(R.id.descriptionFld);
        spinnerType = (Spinner) findViewById(R.id.spinnerType);

        typeAdapter = new ArrayAdapter<String>(this, R.layout.detail_spinner_element, R.id.sortType, new ArrayList<>());
        spinnerType.setAdapter(typeAdapter);

        titleFld.setText(getPresenter().getTransaction().getTitle());
        amountFld.setText(getPresenter().getTransaction().getAmount().toString());
        dateFld.setText(getPresenter().getTransaction().getDate().toString());

        if(getPresenter().getTransaction().getType().toString().contains("REGULAR")) {
            intervalFld.setText(getPresenter().getTransaction().getTransactionInterval().toString());
            endDateFld.setText(getPresenter().getTransaction().getEndDate().toString());
        }
        else {
            intervalFld.setText("");
            endDateFld.setText("");
        }

        descriptionFld.setText(getPresenter().getTransaction().getItemDescription());

        getPresenter().start();
    }

    public ITransactionDetailPresenter getPresenter () {
        if(presenter == null) {
            presenter = new TransactionDetailPresenter(this, this);
        }
        return presenter;
    }

    public void setTypeSpinner (ArrayList<String> types) {
        typeAdapter.addAll(types);
        int pos = typeAdapter.getPosition(getPresenter().getTransaction().getType().getTransactionName());
        System.out.println(pos);
        spinnerType.setSelection(typeAdapter.getPosition(getPresenter().getTransaction().getType().getTransactionName()));
    }
}
