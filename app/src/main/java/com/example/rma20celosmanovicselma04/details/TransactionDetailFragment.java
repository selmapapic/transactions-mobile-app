package com.example.rma20celosmanovicselma04.details;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.rma20celosmanovicselma04.R;
import com.example.rma20celosmanovicselma04.data.Transaction;
import com.example.rma20celosmanovicselma04.data.TransactionType;
import com.example.rma20celosmanovicselma04.util.ConnectionCheck;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;

public class TransactionDetailFragment extends Fragment implements ITransactionDetailView {
    private ITransactionDetailPresenter presenter;
    private EditText titleFld, amountFld, intervalFld, dateFld, endDateFld, descriptionFld;
    private ArrayAdapter<String> typeAdapter;
    private Spinner spinnerType;
    private Button deleteBtn, saveBtn;
    private TextView offlineIzmjena;
    private boolean isConnected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);
        getPresenter().start();

        titleFld = (EditText) view.findViewById(R.id.titleFld);
        titleFld.setTag("title");
        amountFld = (EditText) view.findViewById(R.id.amountFld);
        amountFld.setTag("amount");
        intervalFld = (EditText) view.findViewById(R.id.intervalFld);
        intervalFld.setTag("interval");
        dateFld = (EditText) view.findViewById(R.id.dateFld);
        dateFld.setTag("date");
        endDateFld = (EditText) view.findViewById(R.id.endDateFld);
        endDateFld.setTag("endDate");
        descriptionFld = (EditText) view.findViewById(R.id.descriptionFld);
        descriptionFld.setTag("description");
        spinnerType = (Spinner) view.findViewById(R.id.spinnerType);
        deleteBtn = (Button) view.findViewById(R.id.deleteBtn);
        saveBtn = (Button) view.findViewById(R.id.saveBtn);
        offlineIzmjena = (TextView) view.findViewById(R.id.offlineIzmjena);
        isConnected = ConnectionCheck.isConnected(getContext());

        typeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.detail_spinner_element, R.id.sortType, new ArrayList<>());
        spinnerType.setAdapter(typeAdapter);

        setFields();
        onChange = (OnChange) getActivity();

        titleFld.addTextChangedListener(fieldColor(titleFld));
        amountFld.addTextChangedListener(fieldColor(amountFld));
        dateFld.addTextChangedListener(fieldColor(dateFld));
        descriptionFld.addTextChangedListener(fieldColor(descriptionFld));
        intervalFld.addTextChangedListener(fieldColor(intervalFld));
        endDateFld.addTextChangedListener(fieldColor(endDateFld));
        spinnerType.setOnItemSelectedListener(spinnerColor());
        setTypeSpinner(getPresenter().getTypes());
        removeValidation();

        boolean addTrn = (boolean) getArguments().getBoolean("addTrn");
        if(!isConnected && !addTrn && getPresenter().isDelete()) {
            offlineIzmjena.setText("Offline brisanje");
            deleteBtn.setText("Undo");
        }

        return view;
    }

    public void setFields() {
        if(getArguments() != null && getArguments().containsKey("transaction")) {
            boolean addTrn = (boolean) getArguments().getBoolean("addTrn");
            if(!addTrn) {
                if(!isConnected) {
                    offlineIzmjena.setText("Offline izmjena");
                }
                else {
                    offlineIzmjena.setText("");
                }
                getPresenter().setTransaction(getArguments().getParcelable("transaction"));
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
                spinnerType.setSelection(getPresenter().getTypes().indexOf(getPresenter().getTransaction().getType().getTransactionName()));
                deleteBtn.setOnClickListener(deleteAction());
                saveBtn.setOnClickListener(saveAction(false));
            }
            else {
                if(!isConnected) {
                    offlineIzmjena.setText("Offline dodavanje");
                }
                else {
                    offlineIzmjena.setText("");
                }
                spinnerType.setSelection(0);
                deleteBtn.setEnabled(false);
                saveBtn.setOnClickListener(saveAction(true));
            }
        }
        else {
            if(!isConnected) {
                offlineIzmjena.setText("Offline dodavanje");
            }
            else {
                offlineIzmjena.setText("");
            }
            spinnerType.setSelection(0);
            deleteBtn.setEnabled(false);
            saveBtn.setOnClickListener(saveAction(true));
        }
    }

    public ITransactionDetailPresenter getPresenter () {
        if(presenter == null) {
            presenter = new TransactionDetailPresenter(getActivity(), this);
        }
        return presenter;
    }

    public void setTypeSpinner (ArrayList<String> types) {
        boolean addTrn = (boolean) getArguments().getBoolean("addTrn");
        typeAdapter.addAll(types);
        if(!addTrn) spinnerType.setSelection(typeAdapter.getPosition(getPresenter().getTransaction().getType().getTransactionName()));
    }

    public TextWatcher fieldColor (EditText edit) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit.getTag().equals("title")) validateTitle(edit); //title
                else if(edit.getTag().equals("amount")) validateAmount(edit);   //amount
                else if(edit.getTag().equals("date")) validateDate(edit, true);      //date
                else if(edit.getTag().equals("description")) validateDescription(edit);  //description
                else if(edit.getTag().equals("interval")) validateInterval(edit); //interval
                else if(edit.getTag().equals("endDate")) validateDate(edit, false);  //endDate
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
        else {
            edit.setBackgroundResource(R.drawable.field_color_valid);
            edit.setError(null);
        }
    }

    public void validateDate (EditText edit, boolean type) { ////ako je true, znaci da je date a ako je false onda je endDate
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        if(!type) {
            if((!spinnerType.getSelectedItem().toString().contains("Regular") && edit.getText().length() > 0) ||
                    (spinnerType.getSelectedItem().toString().contains("Regular") && edit.getText().length() == 0) || edit.getText().length() > 10) {
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
            if(edit.getText().length() == 0 || edit.getText().length() > 10) {
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
        if((spinnerType.getSelectedItem().toString().contains("income") && edit.getText().length() > 0)) {
            edit.setError("This field must be empty");
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
                boolean addTrn = getArguments().getBoolean("addTrn");
                if(!addTrn) {
                    if (!getPresenter().getTransaction().getType().getTransactionName().equals(spinnerType.getSelectedItem().toString())) {
                        spinnerType.setBackgroundResource(R.drawable.spinner_color_valid);
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
            if(deleteBtn.getText().equals("DELETE")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setMessage("Are you sure you want to permanently delete this transaction?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    getPresenter().removeTransaction(getPresenter().getTransaction());
                    onChange.onSaveOrDelete();
                    clearAllFields();
                    getFragmentManager().popBackStack();
                    clearAllFields();
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            else if(deleteBtn.getText().equals("Undo")){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setMessage("Are you sure you want to undo deleting this transaction?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    getPresenter().changeForUndo(getPresenter().getTransaction());
                    onChange.onSaveOrDelete();
                    clearAllFields();
                    getFragmentManager().popBackStack();
                    clearAllFields();
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        };
    }

    public View.OnClickListener saveAction(boolean isAdd) {
        return v -> {

            isConnected = ConnectionCheck.isConnected(getContext());

            if(validateDateDistances()) {
                validateAll();
            }

            ArrayList<Object> errors = new ArrayList<>();
            boolean hasNoErrors = isHasNoErrors(errors);

            if(hasNoErrors) {
                Transaction trn = getNewTransaction();
                if((spinnerType.getSelectedItem().toString().contains("payment") || spinnerType.getSelectedItem().toString().contains("Purchase")) &&
                        getPresenter().limitExceeded(trn, isAdd)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Your limit is exceeded.");
                    builder.setMessage("Are you sure you want to add this transaction?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        if(!isAdd) {
                            getPresenter().getTransaction().setId(((Transaction) getArguments().get("transaction")).getId());
                            getPresenter().getTransaction().setInternalId(((Transaction) getArguments().get("transaction")).getInternalId());
                            getPresenter().changeTransaction(getPresenter().getTransaction(), trn);
                            removeValidation();
                            onChange.onSaveOrDelete();
                            getPresenter().setTransaction(trn);
                        }
                        else {
                            getPresenter().addTransaction(trn, isConnected);
                            onChange.onSaveOrDelete();
                            clearAllFields();
                        }
                    });
                    builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else {
                    if(!isAdd) {
                        getPresenter().getTransaction().setId(((Transaction) getArguments().get("transaction")).getId());
                        getPresenter().getTransaction().setInternalId(((Transaction) getArguments().get("transaction")).getInternalId());
                        getPresenter().changeTransaction(getPresenter().getTransaction(), trn);
                        removeValidation();
                        onChange.onSaveOrDelete();
                        getPresenter().setTransaction(trn);
                    }
                    else {
                        getPresenter().addTransaction(trn, isConnected);
                        onChange.onSaveOrDelete();
                        clearAllFields();
                    }
                }
            }
        };
    }

    public boolean validateDateDistances() {
        if(dateFld.getText().length() > 0 && endDateFld.getText().length() > 0) {
            try {
                if (LocalDate.parse(dateFld.getText().toString()).isAfter(LocalDate.parse(endDateFld.getText().toString()))) {
                    endDateFld.setBackgroundResource(R.drawable.field_stroke);
                    endDateFld.setError("End date cannot be before date");
                    return false;
                }
            } catch (Exception e){
                endDateFld.setBackgroundResource(R.drawable.field_stroke);
                endDateFld.setError("End date cannot be before date");
            }
        }
        return true;
    }

    public boolean isHasNoErrors(ArrayList<Object> errors) {
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
        return hasNoErrors;
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
        titleFld.setError(null);
        amountFld.setBackgroundResource(R.drawable.field_stroke);
        amountFld.setError(null);
        intervalFld.setBackgroundResource(R.drawable.field_stroke);
        intervalFld.setError(null);
        dateFld.setBackgroundResource(R.drawable.field_stroke);
        dateFld.setError(null);
        descriptionFld.setBackgroundResource(R.drawable.field_stroke);
        descriptionFld.setError(null);
        endDateFld.setBackgroundResource(R.drawable.field_stroke);
        endDateFld.setError(null);
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

    private void clearAllFields () {
        titleFld.setText("");
        intervalFld.setText("");
        amountFld.setText("");
        descriptionFld.setText("");
        dateFld.setText("");
        endDateFld.setText("");
        removeValidation();
    }

    private TransactionDetailFragment.OnChange onChange;
    public interface OnChange {
        void onSaveOrDelete ();
    }
}

