package com.example.rma20celosmanovicselma04;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;

@RequiresApi(api = Build.VERSION_CODES.O)
public class TransactionsIntreactor implements ITransactionsInteractor {
    private static LocalDate currentDate = LocalDate.now();

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void nextMonth () {
        currentDate = currentDate.plusMonths(1);
    }

    public void previousMonth () {
        currentDate = currentDate.minusMonths(1);
    }

    public String turnToString () {
        return currentDate.getMonth().name() + ", " + currentDate.getYear();
    }
}
