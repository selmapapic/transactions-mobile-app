package com.example.rma20celosmanovicselma04.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.rma20celosmanovicselma04.R;
import com.example.rma20celosmanovicselma04.budget.BudgetFragment;
import com.example.rma20celosmanovicselma04.transactionsList.TransactionListFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    FragmentManager fm;
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int i) {
        i++;
        if(i % 2 == 1) {
            return new TransactionListFragment();
        }
        else if(i % 2 == 0) {
            BudgetFragment fragment = new BudgetFragment();
            fm.beginTransaction().replace(R.id.budget_fragment, fragment).addToBackStack(null).commit();
            return fragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}