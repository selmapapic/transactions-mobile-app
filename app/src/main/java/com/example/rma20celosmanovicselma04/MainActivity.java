package com.example.rma20celosmanovicselma04;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;


public class MainActivity extends AppCompatActivity implements TransactionListFragment.OnItemClick, TransactionDetailFragment.OnChange{
    private boolean twoPaneMode = false;
    ViewPagerAdapter pageAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        BudgetFragment fragment = (BudgetFragment) fragmentManager.findFragmentById(R.id.budget_fragment);

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

        if(!twoPaneMode) {
            viewPager = findViewById(R.id.viewPager);
            pageAdapter = new ViewPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(pageAdapter);
            viewPager.setCurrentItem(0);
            viewPager.setOnPageChangeListener(circularListener());
        }
    }
    int mCurrentPosition, lastPageIndex = 1;
    private ViewPager.OnPageChangeListener circularListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position; // Declare mCurrentPosition as a global variable to track the current position of the item in the ViewPager
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // For going from the first item to the last item, when the 1st A goes to 1st C on the left, again we let the ViewPager do it's job until the movement is completed, we then set the current item to the 2nd C.
                // Set the current item to the item before the last item if the current position is 0
                if (mCurrentPosition == 0)                  viewPager.setCurrentItem(lastPageIndex - 1, false); // lastPageIndex is the index of the last item, in this case is pointing to the 2nd A on the list. This variable should be declared and initialzed as a global variable

                // For going from the last item to the first item, when the 2nd C goes to the 2nd A on the right, we let the ViewPager do it's job for us, once the movement is completed, we set the current item to the 1st A.
                // Set the current item to the second item if the current position is on the last
                if (mCurrentPosition == lastPageIndex)      viewPager.setCurrentItem(0, true);
            }
        };
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
