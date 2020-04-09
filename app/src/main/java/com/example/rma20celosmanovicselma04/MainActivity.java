package com.example.rma20celosmanovicselma04;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class MainActivity extends AppCompatActivity implements TransactionListFragment.OnItemClick, TransactionDetailFragment.OnChange{
    private boolean twoPaneMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment listFragment = fragmentManager.findFragmentByTag("list");
        if (listFragment==null){
            listFragment = new TransactionListFragment();
            fragmentManager.beginTransaction().replace(R.id.transactions_main, listFragment,"list").commit();
        }
        else{
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        FrameLayout details = findViewById(R.id.transactions_detail);
        if (details != null) {
            twoPaneMode = true;
            Bundle arguments = new Bundle();
            arguments.putBoolean("addTrn", true);
            TransactionDetailFragment detailFragment = (TransactionDetailFragment) fragmentManager.findFragmentById(R.id.transactions_detail);
            if (detailFragment==null) {
                detailFragment = new TransactionDetailFragment();
                detailFragment.setArguments(arguments);
                fragmentManager.beginTransaction().replace(R.id.transactions_detail, detailFragment).commit();
            }
        }
        else {
            twoPaneMode = false;
        }
    }

    @Override
    public void onItemClicked(Transaction transaction) {
        Bundle arguments = new Bundle();
        arguments.putParcelable("transaction", transaction);
        arguments.putBoolean("addTrn", false);
        TransactionDetailFragment detailFragment = new TransactionDetailFragment();
        detailFragment.setArguments(arguments);
        if (twoPaneMode){
            getSupportFragmentManager().beginTransaction().replace(R.id.transactions_detail, detailFragment).commit();
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.transactions_main, detailFragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onButtonClicked() {
        Bundle arguments = new Bundle();
        arguments.putBoolean("addTrn", true);
        TransactionDetailFragment detailFragment = new TransactionDetailFragment();
        detailFragment.setArguments(arguments);
        if (twoPaneMode){
            getSupportFragmentManager().beginTransaction().replace(R.id.transactions_detail, detailFragment).commit();
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.transactions_main, detailFragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onSaveOrDelete () {
        FragmentManager fragmentManager = getSupportFragmentManager();
        TransactionListFragment listFragment = (TransactionListFragment) fragmentManager.findFragmentByTag("list");
        listFragment.getPresenter().refreshFilterAndSort(listFragment.getFilterSpinner(), listFragment.getSortSpinner());
    }
}
