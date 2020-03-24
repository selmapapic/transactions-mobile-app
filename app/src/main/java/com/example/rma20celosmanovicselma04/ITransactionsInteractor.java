package com.example.rma20celosmanovicselma04;

import java.time.LocalDate;

public interface ITransactionsInteractor {
    LocalDate getCurrentDate();
    void nextMonth ();
    void previousMonth();
    String turnToString ();
}
