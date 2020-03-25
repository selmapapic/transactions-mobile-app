package com.example.rma20celosmanovicselma04;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends ArrayAdapter<String> {
    private int resource;

    public ImageView icon;
    public TextView transactionType;

    public FilterAdapter(@NonNull Context context, int _resource, @NonNull List<String> objects) {
        super(context, _resource, R.id.transactionType, objects);
        resource = _resource;
    }

    public void setTransactionType(ArrayList<String> types) {
        this.clear();
        this.addAll(types);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);

    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        LinearLayout newView;
        if (convertView == null) {
            newView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater)getContext().getSystemService(inflater);
            li.inflate(resource, newView, true);
        } else {
            newView = (LinearLayout)convertView;
        }

        String type = getItem(position);

        icon = newView.findViewById(R.id.icon);
        transactionType = newView.findViewById(R.id.transactionType);

        transactionType.setText(type);

        if(type.equals("Regular income")) {
            icon.setImageResource(R.drawable.regular_income);
        }
        else if(type.equals("Individual income")) {
            icon.setImageResource(R.drawable.individual_income);
        }
        else if(type.equals("Purchase")) {
            icon.setImageResource(R.drawable.purchase);
        }
        else if(type.equals("Regular payment")) {
            icon.setImageResource(R.drawable.regular_payment);
        }
        else if(type.equals("Individual payment")) {
            icon.setImageResource(R.drawable.individual_payment);
        }
        else icon.setImageResource(R.drawable.transparent);
        return newView;
    }
}
