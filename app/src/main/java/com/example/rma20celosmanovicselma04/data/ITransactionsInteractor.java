package com.example.rma20celosmanovicselma04.data;

import java.time.LocalDate;
import java.util.ArrayList;

public interface ITransactionsInteractor {
    LocalDate getCurrentDate();
    void setCurrentDate(LocalDate date);
    ArrayList<String> getTypes ();
    ArrayList<String> getSortTypes ();
    Integer getTypeId (String nameType);
    TransactionType getType (int typeId);
}
