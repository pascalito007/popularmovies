package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.popularmovies.adapter.MovieAdapter;
import com.example.android.popularmovies.data.MoovieContrat;
import com.example.android.popularmovies.databinding.ActivityMainBinding;
import com.example.android.popularmovies.fragment.MovieDetailFragment;
import com.example.android.popularmovies.sync.MoovieSynUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, MovieAdapter.MovieItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int ID_MOVIE_LOADER = 22;
    private MovieAdapter mAdapter;
    private ActivityMainBinding mBinding;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!isTablet()) {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (findViewById(R.id.detail_container) != null) {
            mTwoPane = true;
            setSupportActionBar(mBinding.mainFrame.toolbar);

            Cursor cursor = getContentResolver().query(MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI, null, MoovieContrat.MoovieEntry.COLUMN_FLAG_CRITERIA + " = ?", new String[]{sharedPreferences.getString(getString(R.string.pref_movies_key), getString(R.string.pref_movie_popular_value))}, null);
            if (cursor.getCount() != 0) {
                showFragment(cursor);
            } else {
                Cursor cursor1 = getContentResolver().query(MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI, null, MoovieContrat.MoovieEntry.COLUMN_FLAG_FAVORIT + " = ?", new String[]{"1"}, null);
                if (cursor1.getCount() != 0) {
                    showFragment(cursor1);
                }
                cursor1.close();
            }
            cursor.close();
        }

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        mBinding.mainFrame.rvMovies.setLayoutManager(layoutManager);
        mBinding.mainFrame.rvMovies.setHasFixedSize(true);
        mAdapter = new MovieAdapter(this);
        mBinding.mainFrame.rvMovies.setAdapter(mAdapter);
        showLoading();
        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);
        MoovieSynUtils.initialize(this, sharedPreferences.getString(getString(R.string.pref_movies_key), getString(R.string.pref_movie_popular_value)));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        String value = sharedPreferences.getString(getString(R.string.pref_movies_key), getString(R.string.pref_movie_popular_value));

        if (value.equals("popular")) {
            setTitle("Popular Movies");
        } else if (value.equals("top_rated")) {
            setTitle("Top Rated Movies");
        } else if (value.equals("favorit")) {
            setTitle("My Favorits Movies");
        }


    }


    public boolean isTablet() {
        try {
            // Compute screen size
            DisplayMetrics dm = this.getResources().getDisplayMetrics();
            float screenWidth = dm.widthPixels / dm.xdpi;
            float screenHeight = dm.heightPixels / dm.ydpi;
            double size = Math.sqrt(Math.pow(screenWidth, 2) +
                    Math.pow(screenHeight, 2));
            // Tablet devices should have a screen size greater than 6 inches
            Log.d("size", size + "");
            return size >= 6;
        } catch (Throwable t) {
            Log.e("NOT KNOWN", "Failed to compute screen size", t);
            return false;
        }

    }


    public void showFragment(Cursor cursor) {
        cursor.moveToFirst();
        Bundle bundle = new Bundle();
        bundle.putInt(NetworkUtils.MOOVIE_ID, cursor.getInt(cursor.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID)));
        bundle.putString(Intent.EXTRA_TEXT, cursor.getString(cursor.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_TITLE)));
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_container, fragment)
                .commit();
    }

    private void showLoading() {
        mBinding.mainFrame.rvMovies.setVisibility(View.INVISIBLE);
        mBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);
        mBinding.tvErrorMessage.setVisibility(View.INVISIBLE);
    }

    public void showMovieDataView() {
        mBinding.tvErrorMessage.setVisibility(View.INVISIBLE);
        mBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
        mBinding.mainFrame.rvMovies.setVisibility(View.VISIBLE);
    }

    public void showErrorMessage() {
        mBinding.tvErrorMessage.setVisibility(View.VISIBLE);
        mBinding.mainFrame.rvMovies.setVisibility(View.INVISIBLE);
        mBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onListItemClick(Integer movieId, String title) {
        if (mTwoPane) {
            Bundle bundle = new Bundle();
            bundle.putInt(NetworkUtils.MOOVIE_ID, movieId);
            bundle.putString(Intent.EXTRA_TEXT, title);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, title);
            intent.putExtra(NetworkUtils.MOOVIE_ID, movieId);
            startActivity(intent);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case ID_MOVIE_LOADER:
                Uri movieUri = MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI;
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                String value = sharedPreferences.getString(getString(R.string.pref_movies_key), getString(R.string.pref_movie_popular_value));
                if (value.equals(getString(R.string.pref_movie_favorits_value))) {
                    return new CursorLoader(this, MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI.buildUpon().appendPath("favorits").build(), null, null, null, null);
                } else {
                    return new CursorLoader(this, movieUri, null, MoovieContrat.MoovieEntry.COLUMN_FLAG_CRITERIA + " = ?", new String[]{sharedPreferences.getString(getString(R.string.pref_movies_key), getString(R.string.pref_movie_popular_value))}, null);
                }
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        if (data.getCount() != 0) {
            showMovieDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_movies_key))) {
            String value = sharedPreferences.getString(key, getString(R.string.pref_movie_popular_value));
            if (value.equals(getString(R.string.pref_movie_favorits_value))) {
                this.getContentResolver().notifyChange(MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI.buildUpon().appendPath("favorits").build(), null);
            } else {
                Cursor cursor = this.getContentResolver().query(MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI, null, MoovieContrat.MoovieEntry.COLUMN_FLAG_CRITERIA + " = ?", new String[]{sharedPreferences.getString(getString(R.string.pref_movies_key), getString(R.string.pref_movie_popular_value))}, null);
                if (cursor.getCount() == 0) {
                    MoovieSynUtils.startImmediateSync(this, sharedPreferences.getString(key, getString(R.string.pref_movie_popular_value)));
                } else if (cursor.getCount() != 0 && value.equals(getString(R.string.pref_movie_popular_value))) {
                    this.getContentResolver().notifyChange(MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI.buildUpon().appendPath("popular").build(), null);
                } else if (cursor.getCount() != 0 && value.equals(getString(R.string.pref_movie_rated_value))) {
                    this.getContentResolver().notifyChange(MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI.buildUpon().appendPath("top_rated").build(), null);
                }
                cursor.close();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }


}
