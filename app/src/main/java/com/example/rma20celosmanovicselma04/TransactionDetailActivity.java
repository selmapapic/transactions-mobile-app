package com.example.rma20celosmanovicselma04;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private Button deleteBtn, saveBtn;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(transaction_detail);

        System.out.println(getIntent().getExtras().getInt("intervalFld") + "interval fld bla bla" );
        getPresenter().create((LocalDate) getIntent().getExtras().get("dateFld"), getIntent().getExtras().getDouble("amountFld"), getIntent().getStringExtra("titleFld"), TransactionType.getType(getIntent().getStringExtra("spinnerType")),
                getIntent().getStringExtra("descriptionFld"), getIntent().getExtras().getInt("intervalFld"), (LocalDate) getIntent().getExtras().get("endDateFld"));

        titleFld = (EditText) findViewById(R.id.titleFld);
        amountFld = (EditText) findViewById(R.id.amountFld);
        intervalFld = (EditText) findViewById(R.id.intervalFld);
        dateFld = (EditText) findViewById(R.id.dateFld);
        endDateFld = (EditText) findViewById(R.id.endDateFld);
        descriptionFld = (EditText) findViewById(R.id.descriptionFld);
        spinnerType = (Spinner) findViewById(R.id.spinnerType);
        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        saveBtn = (Button) findViewById(R.id.saveBtn);

        typeAdapter = new ArrayAdapter<String>(this, R.layout.detail_spinner_element, R.id.sortType, new ArrayList<>());
        spinnerType.setAdapter(typeAdapter);

        titleFld.setText(getPresenter().getTransaction().getTitle());
        amountFld.setText(getPresenter().getTransaction().getAmount().toString());
        dateFld.setText(getPresenter().getTransaction().getDate().toString());
        descriptionFld.setText(getPresenter().getTransaction().getItemDescription());
        if(getPresenter().getTransaction().getType().toString().contains("REGULAR")) {
            intervalFld.setText(getPresenter().getTransaction().getTransactionInterval().toString());
            endDateFld.setText(getPresenter().getTransaction().getEndDate().toString());
        }
        else {
            intervalFld.setText("");
            endDateFld.setText("");
        }

        titleFld.addTextChangedListener(fieldColor(titleFld));
        amountFld.addTextChangedListener(fieldColor(amountFld));
        dateFld.addTextChangedListener(fieldColor(dateFld));
        descriptionFld.addTextChangedListener(fieldColor(descriptionFld));
        intervalFld.addTextChangedListener(fieldColor(intervalFld));
        endDateFld.addTextChangedListener(fieldColor(endDateFld));

        deleteBtn.setOnClickListener(deleteAction());

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

    public TextWatcher fieldColor (EditText edit) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edit.setBackgroundResource(R.drawable.field_color);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    public View.OnClickListener deleteAction () {
        return v -> {
            getPresenter().removeTransaction(getPresenter().getTransaction());
            finish();
        };
    }
}
