package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.adapter.GenreAdapter;
import com.example.android.popularmovies.adapter.TrailerAdapter;
import com.example.android.popularmovies.data.MoovieContrat;
import com.example.android.popularmovies.databinding.ActivityDetailsBinding;
import com.example.android.popularmovies.sync.MoovieSynUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, TrailerAdapter.TrailerItemClickListener {

    private static final int GENRE_LOADER_ID = 33;
    private static final int TRAILERS_LOADER_ID = 44;
    private static final int MOOVIE_LOADER_ID = 55;
    Uri mTrailersUri, mGenreUri, mMoovieListUri;
    private GenreAdapter mGenreAdapter;
    private TrailerAdapter mTrailersAdapter;
    Integer moovieId;
    String title;
    private static final int REQUEST_CODE = 4014;

    ActivityDetailsBinding mBinding;


    public static final String[] TRAILERS_PROJECTION = {
            MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID,
            MoovieContrat.MoovieEntry.COLUMN_KEY,
            MoovieContrat.MoovieEntry.COLUMN_NAME,
            MoovieContrat.MoovieEntry.COLUMN_TYPE,
    };


    public static final int INDEX_TRAILER_KEY = 1;
    public static final int INDEX_TRAILER_TITLE = 2;
    public static final int INDEX_TRAILER_TYPE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        mBinding.rvGenre.setHasFixedSize(true);
        mBinding.rvTrailers.setHasFixedSize(true);


        setSupportActionBar(mBinding.toolbar);

        Intent intent = getIntent();
        if (intent != null) {
            title = intent.getStringExtra(Intent.EXTRA_TEXT);
            moovieId = intent.getIntExtra(NetworkUtils.MOOVIE_ID, 0);
            if (moovieId == 0) finish();
            MoovieSynUtils.startDetailsSync(this, String.valueOf(moovieId));
            setTitle(title);
            mGenreUri = MoovieContrat.MoovieEntry.GENRE_CONTENT_URI.buildUpon().appendPath(String.valueOf(moovieId)).build();
            mTrailersUri = MoovieContrat.MoovieEntry.TRAILERS_CONTENT_URI.buildUpon().appendPath(String.valueOf(moovieId)).build();
            mMoovieListUri = MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI.buildUpon().appendPath(String.valueOf(moovieId)).build();

        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        mGenreAdapter = new GenreAdapter();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mBinding.rvGenre.setLayoutManager(layoutManager);
        mBinding.rvGenre.setAdapter(mGenreAdapter);

        mTrailersAdapter = new TrailerAdapter(this);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mBinding.rvTrailers.setLayoutManager(layoutManager2);
        mBinding.rvTrailers.setAdapter(mTrailersAdapter);


        getSupportLoaderManager().initLoader(GENRE_LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(TRAILERS_LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(MOOVIE_LOADER_ID, null, this);

        Cursor cursor = this.getContentResolver().query(MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI.buildUpon().appendPath(String.valueOf(moovieId)).build(), null, MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{String.valueOf(moovieId)}, null);
        cursor.getCount();
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            Integer fav = cursor.getInt(cursor.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_FLAG_FAVORIT));
            if (fav == 0) {
                mBinding.fab.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
            } else if (fav == 1) {
                mBinding.fab.setBackgroundTintList(getResources().getColorStateList(android.R.color.white));
            }
        }
        cursor.close();

        mBinding.reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReviews(view);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case TRAILERS_LOADER_ID:
                return new CursorLoader(this, mTrailersUri, TRAILERS_PROJECTION, MoovieContrat.MoovieEntry.COLUMN_TYPE + " = ? AND " + MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{"Trailer", String.valueOf(moovieId)}, null);
            case MOOVIE_LOADER_ID:
                return new CursorLoader(this, mMoovieListUri, null, MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{String.valueOf(moovieId)}, null);
            case GENRE_LOADER_ID:
                return new CursorLoader(this, mGenreUri, null, MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{String.valueOf(moovieId)}, null);
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
        switch (loader.getId()) {
            case TRAILERS_LOADER_ID:
                mTrailersAdapter.swapCursor(data);
                break;
            case GENRE_LOADER_ID:
                mGenreAdapter.swapCursor(data);
                break;
            case MOOVIE_LOADER_ID:
                Glide.with(this).load(NetworkUtils.BASE_IMAGE_URL + data.getString(data.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_BACKDROP_PATH))).into(mBinding.backPoster);
                Glide.with(this).load(NetworkUtils.BASE_IMAGE_URL + data.getString(data.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_POSTER_PATH))).into(mBinding.descDiv.poster);
                mBinding.descDiv.tvTitle.setText(data.getString(data.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_TITLE)));
                mBinding.descDiv.tvVoteAverage.setText(data.getString(data.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_VOTE_AVERAGE)));
                mBinding.descDiv.tvRelease.setText(data.getString(data.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_RELEASE_DATE)));
                mBinding.tvSmalDescription.setText(data.getString(data.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_OVERVIEW)));
                mBinding.descDiv.tvPreviewDescription.setText(data.getString(data.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_TAGLINE)));
                mBinding.descDiv.tvDuration.setText(data.getString(data.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_RUNTIME)));
                mBinding.circleItems.tvLanguage.setText(data.getString(data.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_ORIGINAL_LANGUAGE)));
                String popularity = data.getString(data.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_POPULARITY));
                java.text.DecimalFormat df = new java.text.DecimalFormat("0.0");
                mBinding.circleItems.tvPopularity.setText(df.format(Double.parseDouble(popularity)).replace(",", "."));
                mBinding.circleItems.tvVote.setText(data.getString(data.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_VOTE_COUNT)));
                break;
            default:
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mGenreAdapter.swapCursor(null);
        mTrailersAdapter.swapCursor(null);
    }

    public void showReviews(View view) {
        Intent intent = new Intent(this, ReviewsActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, title);
        intent.putExtra(NetworkUtils.MOOVIE_ID, moovieId);
        intent.putExtra("vote_avg", mBinding.descDiv.tvVoteAverage.getText().toString());
        intent.putExtra("release_date", mBinding.descDiv.tvRelease.getText().toString());
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onClick(String key) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(NetworkUtils.BASE_APP_VIDEO_YOUTUBE_URL + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(NetworkUtils.BASE_WEB_VIDEO_YOUTUBE_URL + key));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    public void changeFabColor(Integer fav, ContentValues contentValues) {

        if (fav == 0) {
            contentValues.put(MoovieContrat.MoovieEntry.COLUMN_FLAG_FAVORIT, 1);
            this.getContentResolver().update(MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI.buildUpon().appendPath(String.valueOf(moovieId)).build(), contentValues, MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{String.valueOf(moovieId)});
            mBinding.fab.setBackgroundTintList(getResources().getColorStateList(android.R.color.white));
        } else if (fav == 1) {
            contentValues.put(MoovieContrat.MoovieEntry.COLUMN_FLAG_FAVORIT, 0);
            this.getContentResolver().update(MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI.buildUpon().appendPath(String.valueOf(moovieId)).build(), contentValues, MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{String.valueOf(moovieId)});
            mBinding.fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

        }
    }


    public void rateMovie(View view) {
        Cursor cursor = this.getContentResolver().query(MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI.buildUpon().appendPath(String.valueOf(moovieId)).build(), null, MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{String.valueOf(moovieId)}, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            ContentValues contentValues = new ContentValues();
            Integer fav = cursor.getInt(cursor.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_FLAG_FAVORIT));
            if (fav == 0) {
                changeFabColor(0, contentValues);
                Snackbar.make(view, "Added to favorits", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else if (fav == 1) {
                changeFabColor(1, contentValues);
                Snackbar.make(view, "Removed from favorits", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
        cursor.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data.hasExtra(NetworkUtils.MOOVIE_ID) && data.hasExtra(Intent.EXTRA_TEXT)) {
                moovieId = data.getIntExtra(NetworkUtils.MOOVIE_ID, 0);
                title = data.getStringExtra(Intent.EXTRA_TEXT);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            Cursor cursor = this.getContentResolver().query(MoovieContrat.MoovieEntry.TRAILERS_CONTENT_URI.buildUpon().appendPath(String.valueOf(moovieId)).build(), null, MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{String.valueOf(moovieId)}, null);
            cursor.getCount();
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                        .setType("text/plain")
                        .setText("Check this amaizing trailer from " + title + ":" + cursor.getString(cursor.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_NAME)) + "  " + NetworkUtils.BASE_WEB_VIDEO_YOUTUBE_URL + cursor.getString(cursor.getColumnIndex(MoovieContrat.MoovieEntry.COLUMN_KEY)))
                        .getIntent();
                startActivity(shareIntent);
                cursor.close();
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }
}
