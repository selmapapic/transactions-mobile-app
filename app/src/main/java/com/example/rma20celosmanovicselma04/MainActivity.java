package com.example.rma20celosmanovicselma04;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.rma20celosmanovicselma04.budget.BudgetFragment;
import com.example.rma20celosmanovicselma04.data.Transaction;
import com.example.rma20celosmanovicselma04.details.TransactionDetailFragment;
import com.example.rma20celosmanovicselma04.graphs.GraphsFragment;
import com.example.rma20celosmanovicselma04.transactionsList.TransactionListFragment;


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
            Bundle bundle = new Bundle();
            bundle.putBoolean("twoPaneMode", false);
            listFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.transactions_main, listFragment,"list").commit();
        }
        else{
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        FrameLayout details = findViewById(R.id.transactions_detail);
        if (details != null) {
            twoPaneMode = true;
            Bundle bundle = new Bundle();
            bundle.putBoolean("twoPaneMode", true);
            listFragment.setArguments(bundle);
            Bundle arguments = new Bundle();
            arguments.putBoolean("addTrn", true);
            TransactionDetailFragment detailFragment = (TransactionDetailFragment) fragmentManager.findFragmentById(R.id.transactions_detail);
            if (detailFragment==null) {
                detailFragment = new TransactionDetailFragment();
                detailFragment.setArguments(arguments);
                fragmentManager.beginTransaction().replace(R.id.transactions_detail, detailFragment).commit();
            }
            else {
                detailFragment = new TransactionDetailFragment();
                detailFragment.setArguments(arguments);
                fragmentManager.beginTransaction().replace(R.id.transactions_detail, detailFragment).commit();
            }
        }
        else {
            twoPaneMode = false;
        }

        if(!twoPaneMode) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("twoPaneMode", false);
            listFragment.setArguments(bundle);
        }
        else {
            Bundle bundle = new Bundle();
            bundle.putBoolean("twoPaneMode", true);
            listFragment.setArguments(bundle);
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
    public void onNextClicked(int page) {
        if(!twoPaneMode) {
            if (page == 2) {
                BudgetFragment budgetFragment = new BudgetFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.transactions_main, budgetFragment).addToBackStack(null).commit();
            } else if (page == 1) {
                TransactionListFragment listFragment = new TransactionListFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("twoPaneMode", false);
                listFragment.setArguments(bundle);
                if (!twoPaneMode) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.transactions_main, listFragment).addToBackStack(null).commit();
                }
            } else if (page == 3) {
                GraphsFragment graphsFragment = new GraphsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.transactions_main, graphsFragment).addToBackStack(null).commit();
            }
        }
    }

    @Override
    public void onPreviousClicked(int page) {
        if(!twoPaneMode) {
            if (page == 1) {
                TransactionListFragment listFragment = new TransactionListFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("twoPaneMode", false);
                listFragment.setArguments(bundle);
                if (!twoPaneMode) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.transactions_main, listFragment).addToBackStack(null).commit();
                }
            } else if (page == 2) {
                BudgetFragment budgetFragment = new BudgetFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.transactions_main, budgetFragment).addToBackStack(null).commit();
            } else if (page == 3) {
                GraphsFragment graphsFragment = new GraphsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.transactions_main, graphsFragment).addToBackStack(null).commit();
            }
        }
    }

    @Override
    public void onSaveOrDelete () {
        FragmentManager fragmentManager = getSupportFragmentManager();
        TransactionListFragment listFragment = (TransactionListFragment) fragmentManager.findFragmentByTag("list");
        listFragment.getPresenter().refreshFilterAndSort(listFragment.getFilterSpinner(), listFragment.getSortSpinner());
    }
}
