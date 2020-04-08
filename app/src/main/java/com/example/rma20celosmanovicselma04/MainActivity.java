package com.example.rma20celosmanovicselma04;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class MainActivity extends AppCompatActivity implements TransactionListFragment.OnItemClick{
    private boolean twoPaneMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FrameLayout details = findViewById(R.id.transactions_detail);
        if (details != null) {
            twoPaneMode = true;
            TransactionDetailFragment detailFragment = (TransactionDetailFragment) fragmentManager.findFragmentById(R.id.transactions_detail);
            if (detailFragment==null) {
                detailFragment = new TransactionDetailFragment();
                fragmentManager.beginTransaction().replace(R.id.transactions_detail, detailFragment).commit();
            }
        }
        else {
            twoPaneMode = false;
        }
        Fragment listFragment = fragmentManager.findFragmentByTag("list");
        if (listFragment==null){
            listFragment = new TransactionListFragment();
            fragmentManager.beginTransaction().replace(R.id.transactions_main, listFragment,"list").commit();
        }
        else{
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void onItemClicked(Transaction transaction) {
        Bundle arguments = new Bundle();
        arguments.putParcelable("transaction", transaction);
        TransactionDetailFragment detailFragment = new TransactionDetailFragment();
        detailFragment.setArguments(arguments);
        if (twoPaneMode){
            getSupportFragmentManager().beginTransaction().replace(R.id.transactions_detail, detailFragment).commit();
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.transactions_main, detailFragment).addToBackStack(null).commit();
        }
    }
}
