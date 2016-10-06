package com.sam_chordas.android.stockhawk.service.HistoricData;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Gagan on 7/25/2016.
 */
public class Stock implements Parcelable{
    private String companyName;
    private String exchangeName;
    private String firstTrade;
    private String lastTrade;
    private String currency;
    private double previousClosePrice;
    private ArrayList<Symbol> stockSymbols;

    public Stock(String companyName, String exchangeName, String firstTrade, String lastTrade, String currency, double previousClosePrice, ArrayList<Symbol> stockSymbols) {
        this.companyName = companyName;
        this.exchangeName = exchangeName;
        this.firstTrade = firstTrade;
        this.lastTrade = lastTrade;
        this.currency = currency;
        this.previousClosePrice = previousClosePrice;
        this.stockSymbols = stockSymbols;
    }

    public ArrayList<Symbol> getStockSymbols() {
        return stockSymbols;
    }

    public void setStockSymbols(ArrayList<Symbol> stockSymbols) {
        this.stockSymbols = stockSymbols;
    }

    public double getPreviousClosePrice() {
        return previousClosePrice;
    }

    public void setPreviousClosePrice(double previousClosePrice) {
        this.previousClosePrice = previousClosePrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLastTrade() {
        return lastTrade;
    }

    public void setLastTrade(String lastTrade) {
        this.lastTrade = lastTrade;
    }

    public String getFirstTrade() {
        return firstTrade;
    }

    public void setFirstTrade(String firstTrade) {
        this.firstTrade = firstTrade;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.companyName);
        dest.writeString(this.exchangeName);
        dest.writeString(this.firstTrade);
        dest.writeString(this.lastTrade);
        dest.writeString(this.currency);
        dest.writeDouble(this.previousClosePrice);
        dest.writeTypedList(this.stockSymbols);
    }

    protected Stock(Parcel in) {
        this.companyName = in.readString();
        this.exchangeName = in.readString();
        this.firstTrade = in.readString();
        this.lastTrade = in.readString();
        this.currency = in.readString();
        this.previousClosePrice = in.readDouble();
        this.stockSymbols = in.createTypedArrayList(Symbol.CREATOR);
    }

    public static final Parcelable.Creator<Stock> CREATOR = new Parcelable.Creator<Stock>() {
        @Override
        public Stock createFromParcel(Parcel source) {
            return new Stock(source);
        }

        @Override
        public Stock[] newArray(int size) {
            return new Stock[size];
        }
    };
}
