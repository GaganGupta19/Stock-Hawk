package com.sam_chordas.android.stockhawk.service.HistoricData;

/**
 * Created by Gagan on 8/5/2016.
 */
public class HistoricalDataLimit {
    public static final String oneMonth = "range=1m/json";
    public static final String threeMonths = "range=3m/json";
    public static final String sixMonths = "range=6m/json";
    public static final String oneYear = "range=1y/json";
    public static final String twoYears = "range=2y/json";
    public static final String fiveYears ="range=5y/json";
    public static final String max ="range=my/json";
    public static final String URL = "http://chartapi.finance.yahoo.com/instrument/1.0/";
    public static final String END_URL = "/chartdata;type=quote;";
}
