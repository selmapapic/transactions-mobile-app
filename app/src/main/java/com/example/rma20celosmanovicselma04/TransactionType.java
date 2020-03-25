package com.example.rma20celosmanovicselma04;

public enum TransactionType {
    INDIVIDUALPAYMENT("Individual payment"), REGULARPAYMENT("Regular payment"), PURCHASE("Purchase"), INDIVIDUALINCOME("Individual income"), REGULARINCOME("Regular income");
    private final String transactionName;

    TransactionType(String transactionName) {
        this.transactionName = transactionName;
    }

    public String getTransactionName() {
        return transactionName;
    }
}
