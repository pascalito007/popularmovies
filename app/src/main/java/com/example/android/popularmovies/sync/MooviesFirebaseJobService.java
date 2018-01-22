package com.example.android.popularmovies.sync;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.preference.PreferenceManager;

import com.example.android.popularmovies.R;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by jem001 on 13/09/2017.
 */

public class MooviesFirebaseJobService extends JobService {

    private AsyncTask<Void, Void, Void> mFetchMooviesTask;

    @Override
    public boolean onStartJob(JobParameters job) {
        mFetchMooviesTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                MoovieSyncTask.syncReviews(getApplicationContext(), sharedPreferences.getString(getString(R.string.pref_movies_key), getString(R.string.pref_movie_popular_value)));
                return null;
            }
        };
        mFetchMooviesTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mFetchMooviesTask != null) mFetchMooviesTask.cancel(true);
        return true;
    }
}
