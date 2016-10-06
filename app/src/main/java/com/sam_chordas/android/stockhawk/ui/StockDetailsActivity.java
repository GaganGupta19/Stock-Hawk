package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.rest.RecyclerViewItemClickListener;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.service.HistoricData.FetchHistoricData;
import com.sam_chordas.android.stockhawk.service.HistoricData.HistoricalDataLimit;
import com.sam_chordas.android.stockhawk.service.HistoricData.Stock;
import com.sam_chordas.android.stockhawk.service.HistoricData.Symbol;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
/*
 * Created by Gagan on 7/26/2016.
 */

public class StockDetailsActivity extends AppCompatActivity implements FetchHistoricData.HistoricalDataCallback {

    private static final String TAG = StockDetailsActivity.class.getSimpleName();

    private FetchHistoricData historicData;
    private ArrayList<Symbol> mStockSymbols;
    @BindView(R.id.chart) LineChart mLineChart;
    @BindView(R.id.details) LinearLayout linearLayout;
    @BindView(R.id.mStockName) TextView stockName;
    @BindView(R.id.mStockSymbol) TextView stockSymbol;
    @BindView(R.id.mFirstTrade) TextView firstTrade;
    @BindView(R.id.mLastTrade) TextView lastTrade;
    @BindView(R.id.mCurrency) TextView currency;
    @BindView(R.id.mBidPrice) TextView mBidPrice;
    @BindView(R.id.mExchangeName) TextView exchangeName;
    @BindView(R.id.timeline) RecyclerView mRecyclerView;

    private ButtonAdapter mButtonAdapter;
    private ArrayList<String> mButtonNames;
    private LinearLayoutManager mLinearLayoutManager;
    private String symbol;
    private String bidPrice;
    private Boolean check_connection;
    private ActionBar actionBar;
    private Stock mStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mButtonNames = new ArrayList<>();
        mButtonNames.add(getString(R.string.month1));
        mButtonNames.add(getString(R.string.month3));
        mButtonNames.add(getString(R.string.month6));
        mButtonNames.add(getString(R.string.year1));
        mButtonNames.add(getString(R.string.year2));
        mButtonNames.add(getString(R.string.year5));
        mButtonNames.add(getString(R.string.maxTime));

        mButtonAdapter = new ButtonAdapter(this, mButtonNames);
        mRecyclerView.setAdapter(mButtonAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this,
                new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        switch (position){
                            case 0:
                                if(check_connection)historicData.getHistoricData(symbol , HistoricalDataLimit.oneMonth);
                                break;
                            case 1:
                                if(check_connection)historicData.getHistoricData(symbol , HistoricalDataLimit.threeMonths);
                                break;
                            case 2:
                                if(check_connection)historicData.getHistoricData(symbol , HistoricalDataLimit.sixMonths);
                                break;
                            case 3:
                                if(check_connection)historicData.getHistoricData(symbol , HistoricalDataLimit.oneYear);
                                break;
                            case 4:
                                if(check_connection)historicData.getHistoricData(symbol , HistoricalDataLimit.twoYears);
                                break;
                            case 5:
                                if(check_connection)historicData.getHistoricData(symbol , HistoricalDataLimit.fiveYears);
                                break;
                            case 6:
                                if(check_connection)historicData.getHistoricData(symbol , HistoricalDataLimit.max);
                                break;
                        }
                    }
                }));

        symbol = getIntent().getStringExtra(QuoteColumns.SYMBOL);
        bidPrice = getIntent().getStringExtra(QuoteColumns.BIDPRICE);

        stockSymbol.setText(symbol);
        mBidPrice.setText(bidPrice);
        check_connection = Utils.isNetworkAvailable(this);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        historicData = new FetchHistoricData(this, this);

        if(check_connection) {
            historicData.getHistoricData(symbol , HistoricalDataLimit.oneMonth);
        }else{
            historicData.setHistoricalDataStatus(FetchHistoricData.STATUS_ERROR_NO_NETWORK);
            onFailure();
        }
    }

    @Override
    public void onSuccess(Stock stock) {

        mStock = stock;
        mStockSymbols = stock.getStockSymbols();
        stockName.setText(stock.getCompanyName());
        firstTrade.setText(Utils.dateConversion(stock.getFirstTrade()));
        lastTrade.setText(Utils.dateConversion(stock.getLastTrade()));
        currency.setText(stock.getCurrency());
        exchangeName.setText(stock.getExchangeName());

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> xvalues = new ArrayList<>();

        for (int i = 0; i < mStockSymbols.size(); i++) {

            Symbol stockSymbol = mStockSymbols.get(i);
            double yValue = stockSymbol.getClose();

            xvalues.add(Utils.dateConversion(stockSymbol.getDate()));
            entries.add(new Entry((float) yValue, i));
        }


        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        YAxis left = mLineChart.getAxisLeft();
        left.setEnabled(true);
        left.setLabelCount(5, true);

        xAxis.setTextColor(Color.WHITE);
        left.setTextColor(Color.WHITE);

        mLineChart.getAxisRight().setEnabled(false);

        mLineChart.getLegend().setTextSize(12f);

        LineDataSet dataSet = new LineDataSet(entries, symbol);
        LineData lineData = new LineData(xvalues, dataSet);

        dataSet.setColor(Color.WHITE);
        dataSet.setValueTextColor(Color.WHITE);
        lineData.setValueTextColor(Color.WHITE);
        mLineChart.setDescriptionColor(Color.WHITE);

        lineData.setDrawValues(true);
        dataSet.setDrawCircles(false);

        mLineChart.setDescription(getString(R.string.desc));
        mLineChart.setData(lineData);
        mLineChart.animateX(1000);
    }

    @Override
    public void onFailure() {
        String errorMessage = "";

        @FetchHistoricData.HistoricalDataStatuses
        int status = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt(FetchHistoricData.HISTORICAL_DATA_STATUS, -1);

        switch (status) {
            case FetchHistoricData.STATUS_ERROR_JSON:
                errorMessage += getString(R.string.data_error_json);
                break;
            case FetchHistoricData.STATUS_ERROR_NO_NETWORK:
                errorMessage += getString(R.string.data_no_internet);
                break;
            case FetchHistoricData.STATUS_ERROR_PARSE:
                errorMessage += getString(R.string.data_error_parse);
                break;
            case FetchHistoricData.STATUS_ERROR_UNKNOWN:
                errorMessage += getString(R.string.data_unknown_error);
                break;
            case FetchHistoricData.STATUS_ERROR_SERVER:
                errorMessage += getString(R.string.data_server_down);
                break;
            case FetchHistoricData.STATUS_OK:
                errorMessage += getString(R.string.data_no_error);
                break;
            default:
                break;
        }

        mLineChart.setNoDataText(errorMessage);

        final Snackbar snackbar = Snackbar
                .make(linearLayout, getString(R.string.no_data_show) + errorMessage, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        historicData.getHistoricData(symbol,HistoricalDataLimit.oneMonth);
                    }
                })
                .setActionTextColor(Color.GREEN);

        View subview = snackbar.getView();
        TextView tv = (TextView) subview.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.RED);
        snackbar.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_share:
                if(mStock != null) {
                    String data =
                            getResources().getString(R.string.stock_name) + " : " + mStock.getCompanyName()
                                    + "\n" + getResources().getString(R.string.stock_symbol) + " : " + symbol
                                    + "\n" + getResources().getString(R.string.currency) + " : " + mStock.getCurrency()
                                    + "\n" + getResources().getString(R.string.bid_price) + " : " + bidPrice
                                    + "\n" + getResources().getString(R.string.first_trade) + " : " + Utils.dateConversion(mStock.getFirstTrade())
                                    + "\n" + getResources().getString(R.string.last_trade) + " : " + Utils.dateConversion(mStock.getLastTrade());
                    startActivity(Intent.createChooser(shareIntent(data), getResources().getString(R.string.share)));
                    return true;
                }
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Intent shareIntent(String data) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, (getResources().getString(R.string.share_statement) + " " + data));
        return sharingIntent;
    }
}
