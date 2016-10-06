package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.QuoteCursorAdapter;
import com.sam_chordas.android.stockhawk.rest.RecyclerViewItemClickListener;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.service.StockIntentService;
import com.sam_chordas.android.stockhawk.service.StockTaskService;
import com.sam_chordas.android.stockhawk.touch_helper.SimpleItemTouchHelperCallback;
import com.sam_chordas.android.stockhawk.widget.StockQuoteWidget;

import butterknife.ButterKnife;
import butterknife.OnClick;

/*
* Updated by Gagan Gupta on 7/29/2016.
*/
public class MyStocksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

  /**
   * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
   */

  /**
   * Used to store the last screen title. For use in {@link #restoreActionBar()}.
   */
  private CharSequence mTitle;
  private Intent mServiceIntent;
  private ItemTouchHelper mItemTouchHelper;
  private static final int CURSOR_LOADER_ID = 0;
  private CoordinatorLayout mCoordinateLayout;
  private QuoteCursorAdapter mCursorAdapter;
  private Context mContext;
  private Cursor mCursor;
  private MaterialDialog mMaterialDialog;
  LinearLayout noConnection;
  LinearLayout noStocks;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my_stocks);
    ButterKnife.bind(this);
    mContext = this;
    mCoordinateLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
    // The intent service is for executing immediate pulls from the Yahoo API
    // GCMTaskService can only schedule tasks, they cannot execute immediately
    mServiceIntent = new Intent(this, StockIntentService.class);
    if (savedInstanceState == null) {
      // Run the initialize task service so that some stocks appear upon an empty database
      mServiceIntent.putExtra(StockIntentService.EXTRA_TAG, StockIntentService.ACTION_INIT);
      if (Utils.isNetworkAvailable(mContext)) {
        startService(mServiceIntent);
      } else {
        networkToast();
      }
    }
    noStocks = (LinearLayout) findViewById(R.id.noStocks);
    noConnection = (LinearLayout) findViewById(R.id.noConnection);
    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

    mCursorAdapter = new QuoteCursorAdapter(this, null);
    recyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this,
            new RecyclerViewItemClickListener.OnItemClickListener() {
              @Override
              public void onItemClick(View v, int position) {
                if (mCursor.moveToPosition(position)) {
                  Intent intent = new Intent(mContext, StockDetailsActivity.class);
                  intent.putExtra(QuoteColumns.SYMBOL, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL)));
                  intent.putExtra(QuoteColumns.BIDPRICE, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)));
                  startActivity(intent);
                }
              }
            }));
    recyclerView.setAdapter(mCursorAdapter);
    ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCursorAdapter);
    mItemTouchHelper = new ItemTouchHelper(callback);
    mItemTouchHelper.attachToRecyclerView(recyclerView);

    mTitle = getTitle();
    if (Utils.isNetworkAvailable(mContext)) {
      long period = 3600L;
      long flex = 10L;
      String periodicTag = getResources().getString(R.string.periodic);
      PeriodicTask periodicTask = new PeriodicTask.Builder()
              .setService(StockTaskService.class)
              .setPeriod(period)
              .setFlex(flex)
              .setTag(periodicTag)
              .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
              .setRequiresCharging(false)
              .build();
      GcmNetworkManager.getInstance(this).schedule(periodicTask);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
  }

  public void networkToast() {
    Snackbar snackbar = Snackbar
            .make(mCoordinateLayout, R.string.network_toast, Snackbar.LENGTH_LONG);
    snackbar.show();
  }

  public void restoreActionBar() {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    actionBar.setDisplayShowTitleEnabled(true);
    actionBar.setTitle(mTitle);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.my_stocks, menu);
    restoreActionBar();
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_change_units) {
      // this is for changing stock changes from percent value to dollar value
      Utils.showPercent = !Utils.showPercent;
      this.getContentResolver().notifyChange(QuoteProvider.Quotes.CONTENT_URI, null);
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    // This narrows the return to only the stocks that are most current.
    noConnection.setVisibility(View.GONE);
    noStocks.setVisibility(View.GONE);
    return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
            new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                    QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
            QuoteColumns.ISCURRENT + " = ?",
            new String[]{"1"},
            null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mCursorAdapter.swapCursor(data);
    mCursor = data;
    setView();
    updateStocksWidget();
  }

  private void updateStocksWidget() {
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext.getApplicationContext());
    int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(this, StockQuoteWidget.class));
    if (ids.length > 0) {
      appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.stock_list);
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    mCursorAdapter.swapCursor(null);
  }

  public void setView() {
    noConnection.setVisibility(View.GONE);
    noStocks.setVisibility(View.GONE);

    if (mCursorAdapter.getItemCount() == 0) {
      if (!Utils.isNetworkAvailable(mContext))
        noConnection.setVisibility(View.VISIBLE);
      else
        noStocks.setVisibility(View.VISIBLE);
    }
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.fab)
  public void showDialog() {
    if (Utils.isNetworkAvailable(mContext)) {
      mMaterialDialog = new MaterialDialog.Builder(mContext).title(R.string.symbol_search)
              .content(R.string.content_test)
              .inputType(InputType.TYPE_CLASS_TEXT)
              .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                @Override
                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                  storeStock(input.toString());
                }
              }).build();
      mMaterialDialog.show();
    } else
      networkToast();
  }
  public void storeStock(final String input){

    new AsyncTask<Void, Void, Boolean>() {
      @Override
      protected Boolean doInBackground(Void... params) {
        Cursor cursor = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.SYMBOL},
                QuoteColumns.SYMBOL + "= ?",
                new String[]{input},
                null);
        if (cursor != null) {
          cursor.close();
          return cursor.getCount() != 0;
        }
        return Boolean.FALSE;
      }

      @Override
      protected void onPostExecute(Boolean stockAlreadySaved) {
        if (stockAlreadySaved) {
          Snackbar.make(mCoordinateLayout, R.string.stock_already_saved,
                  Snackbar.LENGTH_LONG).show();
        } else {
          mServiceIntent.putExtra(StockIntentService.EXTRA_TAG, StockIntentService.ACTION_ADD);
          mServiceIntent.putExtra(StockIntentService.EXTRA_SYMBOL, input);
          startService(mServiceIntent);
        }
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
  }
}