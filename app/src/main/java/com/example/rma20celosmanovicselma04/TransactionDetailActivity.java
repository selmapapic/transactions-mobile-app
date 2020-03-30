package com.example.rma20celosmanovicselma04;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

        boolean addTrn = (boolean) getIntent().getExtras().get("addTrn");

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

        if(!addTrn) {
            getPresenter().create((LocalDate) getIntent().getExtras().get("dateFld"), getIntent().getExtras().getDouble("amountFld"), getIntent().getStringExtra("titleFld"), TransactionType.getType(getIntent().getStringExtra("spinnerType")),
                    getIntent().getStringExtra("descriptionFld"), getIntent().getExtras().getInt("intervalFld"), (LocalDate) getIntent().getExtras().get("endDateFld"));
            titleFld.setText(getPresenter().getTransaction().getTitle());
            amountFld.setText(getPresenter().getTransaction().getAmount().toString());
            dateFld.setText(getPresenter().getTransaction().getDate().toString());
            if(!getPresenter().getTransaction().getType().toString().contains("INCOME")) {
                descriptionFld.setText(getPresenter().getTransaction().getItemDescription());
            }
            else {
                descriptionFld.setText("");
            }
            if (getPresenter().getTransaction().getType().toString().contains("REGULAR")) {
                intervalFld.setText(getPresenter().getTransaction().getTransactionInterval().toString());
                endDateFld.setText(getPresenter().getTransaction().getEndDate().toString());
            } else {
                intervalFld.setText("");
                endDateFld.setText("");
            }

            deleteBtn.setOnClickListener(deleteAction());
            saveBtn.setOnClickListener(saveAction(false));
        }
        else {
            spinnerType.setSelection(0);
            deleteBtn.setEnabled(false);
            saveBtn.setOnClickListener(saveAction(true));
        }

        titleFld.addTextChangedListener(fieldColor(titleFld));
        amountFld.addTextChangedListener(fieldColor(amountFld));
        dateFld.addTextChangedListener(fieldColor(dateFld));
        descriptionFld.addTextChangedListener(fieldColor(descriptionFld));
        intervalFld.addTextChangedListener(fieldColor(intervalFld));
        endDateFld.addTextChangedListener(fieldColor(endDateFld));
        spinnerType.setOnItemSelectedListener(spinnerColor());

        getPresenter().start();
    }

    public ITransactionDetailPresenter getPresenter () {
        if(presenter == null) {
            presenter = new TransactionDetailPresenter(this, this);
        }
        return presenter;
    }

    public void setTypeSpinner (ArrayList<String> types) {
        boolean addTrn = (boolean) getIntent().getExtras().get("addTrn");
        typeAdapter.addAll(types);
        if(!addTrn) spinnerType.setSelection(typeAdapter.getPosition(getPresenter().getTransaction().getType().getTransactionName()));
    }

    public TextWatcher fieldColor (EditText edit) {
        System.out.println(edit.getId());
        System.out.println(edit.getText());
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit.getId() == 2131165351) validateTitle(edit); //title
                else if(edit.getId() == 2131165215) validateAmount(edit);   //amount
                else if(edit.getId() == 2131165236) validateDate(edit, true);      //date
                else if(edit.getId() == 2131165240) validateDescription(edit);  //description
                else if(edit.getId() == 2131165267) validateInterval(edit); //interval
                else if(edit.getId() == 2131165246) validateDate(edit, false);  //endDate
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };
    }

    public void validateTitle (EditText edit) {
        if(edit.getText().length() < 3 || edit.getText().length() > 15) {
            edit.setError("Your input is invalid");
            edit.setBackgroundResource(R.drawable.field_stroke);
        }
        else edit.setBackgroundResource(R.drawable.field_color_valid);
    }

    public void validateAmount (EditText edit) {
        if(edit.getText().length() > 0) edit.setBackgroundResource(R.drawable.field_color_valid);
        else {
            edit.setError("Your input is invalid");
            edit.setBackgroundResource(R.drawable.field_stroke);
        }
    }

    public void validateInterval (EditText edit) {
        if((!spinnerType.getSelectedItem().toString().contains("Regular") && edit.getText().length() > 0) || (spinnerType.getSelectedItem().toString().contains("Regular") && edit.getText().length() == 0)) {
            edit.setError("Your input is invalid");
            edit.setBackgroundResource(R.drawable.field_stroke);
        }
        else edit.setBackgroundResource(R.drawable.field_color_valid);
    }

    public void validateDate (EditText edit, boolean type) { //ako je true, znaci da je date a ako je false onda je endDate
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        if(!type) {
            if((!spinnerType.getSelectedItem().toString().contains("Regular") && edit.getText().length() > 0) || (spinnerType.getSelectedItem().toString().contains("Regular") && edit.getText().length() == 0)) {
                edit.setError("Your input is invalid");
                edit.setBackgroundResource(R.drawable.field_stroke);
            }
            else if(!spinnerType.getSelectedItem().toString().contains("Regular") && edit.getText().length() == 0) {
                edit.setBackgroundResource(R.drawable.field_color_valid);
            }
            else {
                try {
                    format.parse(edit.getText().toString());
                    edit.setBackgroundResource(R.drawable.field_color_valid);
                } catch (ParseException e) {
                    edit.setError("Your input is invalid");
                    edit.setBackgroundResource(R.drawable.field_stroke);
                }
            }
        }
        else {
            if(edit.getText().length() == 0) {
                edit.setError("Your input is invalid");
                edit.setBackgroundResource(R.drawable.field_stroke);
            }
            else {
                try {
                    format.parse(edit.getText().toString());
                    edit.setBackgroundResource(R.drawable.field_color_valid);
                } catch (ParseException e) {
                    edit.setError("Your input is invalid");
                    edit.setBackgroundResource(R.drawable.field_stroke);
                }
            }
        }
    }

    public void validateDescription (EditText edit) {
        if(spinnerType.getSelectedItem().toString().contains("income") && edit.getText().length() > 0) {
            edit.setError("Your input is invalid");
            edit.setBackgroundResource(R.drawable.field_stroke);
        }
        else {
            edit.setBackgroundResource(R.drawable.field_color_valid);
            edit.setError(null);
        }
    }

    public AdapterView.OnItemSelectedListener spinnerColor () {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean addTrn = (boolean) getIntent().getExtras().get("addTrn");
                if(!addTrn) {
                    if (!getPresenter().getTransaction().getType().getTransactionName().equals(spinnerType.getSelectedItem().toString())) {
                        spinnerType.setBackgroundResource(R.drawable.spinner_color_valid);
                    validateInterval(intervalFld);
                    validateDescription(descriptionFld);
                    validateDate(endDateFld, false);
                    }
                }
                else {
                    spinnerType.setBackgroundResource(R.drawable.spinner_color_valid);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        };
    }

    public View.OnClickListener deleteAction () {
        return v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(TransactionDetailActivity.this);

            builder.setMessage("Are you sure you want to permanently delete this transaction?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", (dialog, which) -> {
                getPresenter().removeTransaction(getPresenter().getTransaction());
                finish();
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        };
    }

    public View.OnClickListener saveAction(boolean isAdd) {
        return v -> {
            if(isAdd) {
                validateAll();
            }

            ArrayList<Object> errors = new ArrayList<>();
            errors.add(titleFld.getError());
            errors.add(amountFld.getError());
            errors.add(intervalFld.getError());
            errors.add(dateFld.getError());
            errors.add(descriptionFld.getError());
            errors.add(endDateFld.getError());
            boolean hasNoErrors = true;
            for(Object o : errors) {
                if (o != null) {
                    hasNoErrors = false;
                    break;
                }
            }

            if(hasNoErrors) {
                Transaction trn = getNewTransaction();

                if(getPresenter().limitExceeded(Double.parseDouble(amountFld.getText().toString()), isAdd)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TransactionDetailActivity.this);
                    builder.setTitle("Your limit is exceeded.");
                    builder.setMessage("Are you sure you want to add this transaction?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        if(!isAdd) {
                            getPresenter().changeTransaction(getPresenter().getTransaction(), trn);
                            removeValidation();
                        }
                        else {
                            getPresenter().addTransaction(trn);
                            finish();
                        }
                    });
                    builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else {
                    if(!isAdd) {
                        getPresenter().changeTransaction(getPresenter().getTransaction(), trn);
                        removeValidation();
                    }
                    else {
                        getPresenter().addTransaction(trn);
                        finish();
                    }
                }
            }
        };
    }

    public Transaction getNewTransaction() {
        LocalDate endDateNew;
        Integer intervalNew;
        String descriptionNew;
        if(endDateFld.getText().toString().length() == 0) {
            endDateNew = null;
        }
        else endDateNew = LocalDate.parse(endDateFld.getText().toString());

        if(intervalFld.getText().toString().length() == 0) {
            intervalNew = null;
        }
        else intervalNew =  Integer.parseInt(intervalFld.getText().toString());
        if(descriptionFld.getText().toString().length() == 0) {
            descriptionNew = null;
        }
        else descriptionNew = descriptionFld.getText().toString();

        return new Transaction(LocalDate.parse(dateFld.getText().toString()), Double.parseDouble(amountFld.getText().toString()), titleFld.getText().toString(), TransactionType.getType(spinnerType.getSelectedItem().toString()),
                descriptionNew, intervalNew, endDateNew);
    }

    public void removeValidation () {
        titleFld.setBackgroundResource(R.drawable.field_stroke);
        amountFld.setBackgroundResource(R.drawable.field_stroke);
        intervalFld.setBackgroundResource(R.drawable.field_stroke);
        dateFld.setBackgroundResource(R.drawable.field_stroke);
        descriptionFld.setBackgroundResource(R.drawable.field_stroke);
        endDateFld.setBackgroundResource(R.drawable.field_stroke);
        spinnerType.setBackgroundResource(R.drawable.spinner_bg_2);
    }

    public void validateAll () {
        validateTitle(titleFld);
        validateAmount(amountFld);
        validateDate(dateFld, true);
        validateInterval(intervalFld);
        validateDescription(descriptionFld);
        validateDate(endDateFld, false);
    }
}
