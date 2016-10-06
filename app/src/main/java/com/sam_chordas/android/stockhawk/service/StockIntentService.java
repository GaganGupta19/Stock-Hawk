package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {

  public static final String EXTRA_TAG = "tag";
  public static final String EXTRA_SYMBOL = "symbol";
  public static final String ACTION_INIT = "init";
  public static final String ACTION_ADD = "add";

  public StockIntentService(){
    super(StockIntentService.class.getName());
  }

  public StockIntentService(String name) {
    super(name);
  }

  @Override protected void onHandleIntent(Intent intent) {
    Log.d(StockIntentService.class.getSimpleName(), getResources().getString(R.string.stockIntentService));
    StockTaskService stockTaskService = new StockTaskService(this);
    Bundle args = new Bundle();
    if (intent.getStringExtra(EXTRA_TAG).equals(ACTION_ADD)){
      String[] symbol = intent.getStringExtra(EXTRA_SYMBOL).split("\\s+");
      args.putString(EXTRA_SYMBOL, symbol[0]);
    }
    // We can call OnRunTask from the intent service to force it to run immediately instead of
    // scheduling a task.
    stockTaskService.onRunTask(new TaskParams(intent.getStringExtra(EXTRA_TAG), args));
  }
}
