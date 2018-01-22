package com.example.android.popularmovies.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.data.MoovieContrat;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by jem001 on 03/09/2017.
 */

public class MoovieSynUtils {


    private static final int SYNC_INTERVAL_HOURS = 24;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 24;

    private static boolean sInitialized;
    private static final String MOOVIES_SYNC_TAG = "moovies-sync";


    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job syncMooviesJob = dispatcher.newJobBuilder()
                .setService(MooviesFirebaseJobService.class)
                .setTag(MOOVIES_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_HOURS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS
                ))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(syncMooviesJob);
    }


    public static void initialize(@NonNull final Context context, final String criteria) {

        if (sInitialized) return;
        sInitialized = true;
        scheduleFirebaseJobDispatcherSync(context);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                Uri moovieUri = MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI;
                Cursor cursor = context.getContentResolver().query(moovieUri, new String[]{MoovieContrat.MoovieEntry._ID}, null, null, null);
                if (cursor == null || cursor.getCount() == 0) {
                    startImmediateSync(context, criteria);
                }
                cursor.close();
                return null;
            }
        }.execute();

    }

    public static void startImmediateSync(@NonNull final Context context, String criteria) {
        Intent intent = new Intent(context, MoovieSyncIntentService.class);
        intent.putExtra("criteria", criteria);
        context.startService(intent);
    }

    public static void startDetailsSync(@NonNull final Context context, String movieId) {
        Intent intent = new Intent(context, DetailsSyncIntentService.class);
        intent.putExtra(NetworkUtils.MOOVIE_ID, movieId);
        context.startService(intent);
    }

    public static void startReviewsSync(@NonNull final Context context, String movieId) {
        Intent intent = new Intent(context, ReviewsSyncIntentService.class);
        intent.putExtra(NetworkUtils.MOOVIE_ID, movieId);
        context.startService(intent);
    }
}
