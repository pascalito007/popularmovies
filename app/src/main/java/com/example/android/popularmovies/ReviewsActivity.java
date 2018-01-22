package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.android.popularmovies.adapter.ReviewsAdapter;
import com.example.android.popularmovies.data.MoovieContrat;
import com.example.android.popularmovies.databinding.ActivityReviewsBinding;
import com.example.android.popularmovies.sync.MoovieSynUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ReviewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int REVIEWS_LOADER_ID = 66;
    Uri reviewsUri;
    Integer moovieId;
    ActivityReviewsBinding mBinding;
    private ReviewsAdapter mAdapter;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_reviews);
        setSupportActionBar(mBinding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            title = intent.getStringExtra(Intent.EXTRA_TEXT);
            moovieId = intent.getIntExtra(NetworkUtils.MOOVIE_ID, 0);
            MoovieSynUtils.startReviewsSync(this, String.valueOf(moovieId));
            setTitle(title);
            mBinding.reviewsLayout.tvVoteAverage.setText(intent.getStringExtra("vote_avg"));
            String release = intent.getStringExtra("release_date");
            SimpleDateFormat format = new SimpleDateFormat("yyyy");
            try {
                mBinding.reviewsLayout.tvYear.setText(format.format(format.parse(release)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            reviewsUri = MoovieContrat.MoovieEntry.REVIEWS_CONTENT_URI.buildUpon().appendPath(String.valueOf(moovieId)).build();

        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.reviewsLayout.rvReviews.setLayoutManager(layoutManager);
        mBinding.reviewsLayout.rvReviews.setHasFixedSize(true);
        mAdapter = new ReviewsAdapter();
        mBinding.reviewsLayout.rvReviews.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(REVIEWS_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case REVIEWS_LOADER_ID:
                return new CursorLoader(this, reviewsUri, null, MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + "= ?", new String[]{String.valueOf(moovieId)}, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            return;
        }
        if (data.getCount() != 0) {
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra(NetworkUtils.MOOVIE_ID, moovieId);
            intent.putExtra(Intent.EXTRA_TEXT, title);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra(NetworkUtils.MOOVIE_ID, moovieId);
        intent.putExtra(Intent.EXTRA_TEXT, title);
        setResult(RESULT_OK, intent);

    }
}
