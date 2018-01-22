package com.example.android.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by jem001 on 03/09/2017.
 */

public class MoovieSyncIntentService extends IntentService {
    public MoovieSyncIntentService() {
        super("MoovieSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        MoovieSyncTask.syncMoovieList(this,intent.getStringExtra("criteria"));
    }
}
