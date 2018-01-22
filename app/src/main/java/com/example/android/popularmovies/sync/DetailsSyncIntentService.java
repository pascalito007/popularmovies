package com.example.android.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmovies.utilities.NetworkUtils;

/**
 * Created by jem001 on 03/09/2017.
 */

public class DetailsSyncIntentService extends IntentService {
    public DetailsSyncIntentService() {
        super("DetailsSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String movieId = intent.getStringExtra(NetworkUtils.MOOVIE_ID);
        MoovieSyncTask.syncMoovieDetails(this, movieId);
    }
}
