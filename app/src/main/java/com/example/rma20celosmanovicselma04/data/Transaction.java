package com.example.rma20celosmanovicselma04.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDate;
import java.util.Objects;

public class Transaction implements Parcelable {
    private Integer id;
    private LocalDate date;
    private Double amount;
    private String title;
    private TransactionType type;
    private String itemDescription;
    private Integer transactionInterval;
    private LocalDate endDate;
    private Integer internalId;

    public Transaction(int id, LocalDate date, Double amount, String title, TransactionType type, String itemDescription, Integer transactionInterval, LocalDate endDate) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.title = title;
        this.type = type;
        this.itemDescription = itemDescription;
        this.transactionInterval = transactionInterval;
        this.endDate = endDate;
    }

    public Integer getInternalId() {
        return internalId;
    }

    public void setInternalId(Integer internalId) {
        this.internalId = internalId;
    }

    public Transaction(int id, LocalDate date, Double amount, String title, TransactionType type, String itemDescription, Integer transactionInterval, LocalDate endDate, Integer internalId) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.title = title;
        this.type = type;
        this.itemDescription = itemDescription;
        this.transactionInterval = transactionInterval;
        this.endDate = endDate;
        this.internalId = internalId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Transaction(LocalDate date, Double amount, String title, TransactionType type, String itemDescription, Integer transactionInterval, LocalDate endDate) {
        this.amount = amount;
        this.title = title;
        this.date = date;
        this.type = type;
        if(type.equals(TransactionType.INDIVIDUALINCOME)) this.itemDescription = null;
        else this.itemDescription = itemDescription;

        if(type.equals(TransactionType.REGULARINCOME) || type.equals(TransactionType.REGULARPAYMENT)) this.transactionInterval = transactionInterval;
        else this.transactionInterval = null;

        if(type.equals(TransactionType.REGULARPAYMENT) || type.equals(TransactionType.REGULARINCOME)) this.endDate = endDate;
        else this.endDate = null;
    }

    public Transaction() {
    }

    protected Transaction(Parcel in) {
        id = in.readInt();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            internalId = null;
        } else {
            internalId = in.readInt();
        }
        if (in.readByte() == 0) {
            amount = null;
        } else {
            amount = in.readDouble();
        }
        title = in.readString();
        itemDescription = in.readString();
        if (in.readByte() == 0) {
            transactionInterval = null;
        } else {
            transactionInterval = in.readInt();
        }
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public Integer getTransactionInterval() {
        return transactionInterval;
    }

    public void setTransactionInterval(int transactionInterval) {
        this.transactionInterval = transactionInterval;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(title, that.title) &&
                type == that.type &&
                Objects.equals(itemDescription, that.itemDescription) &&
                Objects.equals(transactionInterval, that.transactionInterval) &&
                Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, amount, title, type, itemDescription, transactionInterval, endDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        if (amount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(amount);
        }
        dest.writeString(title);
        dest.writeString(itemDescription);
        if (transactionInterval == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(transactionInterval);
        }
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        if (internalId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(internalId);
        }
    }

    public Transaction clone () {
        return new Transaction(this.getId(), this.getDate(), this.getAmount(), this.getTitle(), this.getType(), this.getItemDescription(), this.getTransactionInterval(), this.getEndDate());
    }
}
