package com.sam_chordas.android.stockhawk.service.HistoricData;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gagan on 7/25/2016.
 */
public class Symbol implements Parcelable{

    private String date;
    private float close;

    public Symbol(String date, float close) {
        this.date = date;
        this.close = close;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeFloat(this.close);
    }

    public Symbol() {
    }

    protected Symbol(Parcel in) {
        this.date = in.readString();
        this.close = in.readFloat();
    }

    public static final Creator<Symbol> CREATOR = new Creator<Symbol>() {
        @Override
        public Symbol createFromParcel(Parcel source) {
            return new Symbol(source);
        }

        @Override
        public Symbol[] newArray(int size) {
            return new Symbol[size];
        }
    };
}
