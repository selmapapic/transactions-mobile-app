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

    public static TransactionType getType (String type) {
        if(type == null || type.equals("Filter by")) return null;
        if(type.equals("Regular income")) return TransactionType.REGULARINCOME;
        else if(type.equals("Regular payment")) return TransactionType.REGULARPAYMENT;
        else if (type.equals("Purchase")) return TransactionType.PURCHASE;
        else if(type.equals("Individual payment")) return TransactionType.INDIVIDUALPAYMENT;
        else if (type.equals("Individual income")) return TransactionType.INDIVIDUALINCOME;
        return null;
    }
}
