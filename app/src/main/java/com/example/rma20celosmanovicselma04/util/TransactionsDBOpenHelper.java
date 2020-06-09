package com.example.rma20celosmanovicselma04.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TransactionsDBOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Transactions18304DB.db";
    public static final int DATABASE_VERSION = 1;

    public TransactionsDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public TransactionsDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String TRANSACTION_TABLE = "transactions";
    public static final String TRANSACTION_ID = "id";
    public static final String TRANSACTION_INTERNAL_ID = "_id";
    public static final String TRANSACTION_TITLE = "title";
    public static final String TRANSACTION_AMOUNT = "amount";
    public static final String TRANSACTION_DATE = "date";
    public static final String TRANSACTION_TYPE = "type";
    public static final String TRANSACTION_END_DATE = "endDate";
    public static final String TRANSACTION_ITEM_DESCRIPTION = "itemDescription";
    public static final String TRANSACTION_INTERVAL = "transactionInterval";
    public static final String STATUS = "status";

    private static final String TRANSACTION_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TRANSACTION_TABLE + " ("  + TRANSACTION_INTERNAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TRANSACTION_ID + " INTEGER, "
                    + TRANSACTION_TITLE + " TEXT NOT NULL, "
                    + TRANSACTION_AMOUNT + " NUMBER NOT NULL, "
                    + TRANSACTION_DATE + " DATE NOT NULL, "
                    + TRANSACTION_TYPE + " TEXT NOT NULL, "
                    + TRANSACTION_END_DATE + " DATE, "
                    + TRANSACTION_ITEM_DESCRIPTION + " TEXT, "
                    + TRANSACTION_INTERVAL + " INTEGER, "
                    + STATUS + " TEXT NOT NULL);";

    private static final String TRANSACTION_DROP = "DROP TABLE IF EXISTS " + TRANSACTION_TABLE;


    public static final String ACCOUNT_TABLE = "accounts";
    public static final String ACCOUNT_ID = "id";
    public static final String ACCOUNT_INTERNAL_ID = "_id";
    public static final String ACCOUNT_BUDGET = "budget";
    public static final String ACCOUNT_TOTAL_LIMIT = "totalLimit";
    public static final String ACCOUNT_MONTH_LIMIT = "monthLimit";


    private static final String ACCOUNT_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + ACCOUNT_TABLE + " (" + ACCOUNT_INTERNAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ACCOUNT_ID + " INTEGER UNIQUE, "
                    + ACCOUNT_BUDGET + " NUMBER NOT NULL, "
                    + ACCOUNT_TOTAL_LIMIT + " NUMBER NOT NULL, "
                    + ACCOUNT_MONTH_LIMIT + " NUMBER NOT NULL);";


    private static final String ACCOUNT_TABLE_DROP = "DROP TABLE IF EXISTS " + ACCOUNT_TABLE;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TRANSACTION_TABLE_CREATE);
        db.execSQL(ACCOUNT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TRANSACTION_DROP);
        db.execSQL(ACCOUNT_TABLE_DROP);
        onCreate(db);
    }
}
