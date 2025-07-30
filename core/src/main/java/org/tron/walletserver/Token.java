package org.tron.walletserver;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Token implements Parcelable, Comparable<Token> {
    private String name;
    private long amount;
//    private Price price;
    private double trxAmount;
    private long id;
    private boolean isSelected;

    private long precision;

    protected Token(Parcel in) {
        name = in.readString();
        amount = in.readLong();
        trxAmount = in.readDouble();
        id = in.readLong();
        isSelected = in.readByte() != 0;
        precision = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(amount);
        dest.writeDouble(trxAmount);
        dest.writeLong(id);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeLong(precision);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Token> CREATOR = new Creator<Token>() {
        @Override
        public Token createFromParcel(Parcel in) {
            return new Token(in);
        }

        @Override
        public Token[] newArray(int size) {
            return new Token[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

//    public Price getPrice() {
//        return price;
//    }
//
//    public void setPrice(Price price) {
//        this.price = price;
//    }

    public double getTrxAmount() {
        return trxAmount;
    }

    public void setTrxAmount(double trxAmount) {
        this.trxAmount = trxAmount;
    }

    public long getId() {
        return id;
    }

    public long getPrecision() {
        return precision;
    }

    public void setPrecision(long precision) {
        this.precision = precision;
    }

    public void setId(long id) {
        this.id = id;

    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Token(String name, long amount, long id) {
        this.name = name;
        this.amount = amount;
        this.id = id;
    }


    public Token(String name, double trxAmount, boolean isSelected) {
        this.name = name;
        this.trxAmount = trxAmount;
        this.isSelected = isSelected;
    }

    public Token(String name, long amount) {

        this.name = name;
        this.amount = amount;
    }

    public Token(String name, long amount, boolean isSelected) {

        this.name = name;
        this.amount = amount;
        this.isSelected = isSelected;
    }

    public Token(String name, long amount, boolean isSelected, long id) {
        this.name = name;
        this.amount = amount;
        this.isSelected = isSelected;
        this.id = id;
    }


    @Override
    public int compareTo(@NonNull Token o) {
        long cPrecision = this.getPrecision();
        long oPrecision = o.getPrecision();
        double cAmount;
        double oAmount;
        if (cPrecision == 0) {
            cAmount = this.amount;
        } else {
            cAmount = this.amount / (Math.pow(10, cPrecision));
        }


        if (oPrecision == 0) {
            oAmount = o.amount;
        } else {
            oAmount = o.amount / (Math.pow(10, oPrecision));
        }

        if ((oAmount - cAmount) > 0) {
            return 1;
        } else if ((oAmount - cAmount) < 0) {
            return -1;
        } else {
            return 0;
        }
    }

}
