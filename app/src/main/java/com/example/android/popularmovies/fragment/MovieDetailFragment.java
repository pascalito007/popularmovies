package com.example.android.popularmovies.fragment;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.DetailsActivity;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapter.GenreAdapter;
import com.example.android.popularmovies.adapter.TrailerAdapter;
import com.example.android.popularmovies.data.MoovieContrat;
import com.example.android.popularmovies.databinding.ActivityDetailsBinding;
import com.example.android.popularmovies.sync.MoovieSynUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

/**
 * Created by jem001 on 12/09/2017.
 */

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, TrailerAdapter.TrailerItemClickListener {
    private static final int GENRE_LOADER_ID = 33;
    private static final int TRAILERS_LOADER_ID = 44;
    private static final int MOOVIE_LOADER_ID = 55;
    Uri mTrailersUri, mGenreUri, mMoovieListUri;
    private GenreAdapter mGenreAdapter;
    private TrailerAdapter mTrailersAdapter;
    Integer moovieId;
    String title;

    ActivityDetailsBinding mBinding;
    SharedPreferences sharedPreferences;


    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(NetworkUtils.MOOVIE_ID)) {
            title = getArguments().getString(Intent.EXTRA_TEXT);
            moovieId = getArguments().getInt(NetworkUtils.MOOVIE_ID, 0);
            MoovieSynUtils.startDetailsSync(getActivity(), String.valueOf(moovieId));
            mGenreUri = MoovieContrat.MoovieEntry.GENRE_CONTENT_URI.buildUpon().appendPath(String.valueOf(moovieId)).build();
            mTrailersUri = MoovieContrat.MoovieEntry.TRAILERS_CONTENT_URI.buildUpon().appendPath(String.valueOf(moovieId)).build();
            mMoovieListUri = MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI.buildUpon().appendPath(String.valueOf(moovieId)).build();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.activity_details, container, false);
        View view = mBinding.getRoot();
        if (mBinding.toolbarLayout != null)
            mBinding.toolbarLayout.setTitle(title);

        if (getArguments().containsKey(NetworkUtils.MOOVIE_ID)) {
            mBinding.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rateMovie(view);
                    String value = sharedPreferences.getString(getString(R.string.pref_movies_key), getString(R.string.pref_movie_popular_value));
                    if (value.equals(getString(R.string.pref_movie_favorits_value))) {
                        getContext().getContentResolver().notifyChange(MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI.buildUpon().appendPath("favorits").build(), null);
                    }

                }
            });

            mBinding.reviewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showReviews(view);
                }
            });
            mBinding.rvGenre.setHasFixedSize(true);
            mBinding.rvTrailers.setHasFixedSize(true);


            mGenreAdapter = new GenreAdapter();

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            mBinding.rvGenre.setLayoutManager(layoutManager);
            mBinding.rvGenre.setAdapter(mGenreAdapter);

            mTrailersAdapter = new TrailerAdapter(this);
            RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            mBinding.rvTrailers.setLayoutManager(layoutManager2);
            mBinding.rvTrailers.setAdapter(mTrailersAdapter);


            getLoaderManager().initLoader(GENRE_LOADER_ID, null, this);
            getLoaderManager().initLoader(TRAILERS_LOADER_ID, null, this);
            getLoaderManager().initLoader(MOOVIE_LOADER_ID, null, this);


            Cursor cursor = getActivity().getContentResolver().query(MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI.buildUpon().appendPath(String.valueOf(moovieId)).build(), null, MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{String.valueOf(moovieId)}, null);
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
        }
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case TRAILERS_LOADER_ID:
                return new CursorLoader(getActivity(), mTrailersUri, DetailsActivity.TRAILERS_PROJECTION, MoovieContrat.MoovieEntry.COLUMN_TYPE + " = ? AND " + MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{"Trailer", String.valueOf(moovieId)}, null);
            case MOOVIE_LOADER_ID:
                return new CursorLoader(getActivity(), mMoovieListUri, null, MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{String.valueOf(moovieId)}, null);
            case GENRE_LOADER_ID:
                return new CursorLoader(getActivity(), mGenreUri, null, MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{String.valueOf(moovieId)}, null);
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

    public void showReviews(View view) {

        Bundle bundle = new Bundle();
        bundle.putInt(NetworkUtils.MOOVIE_ID, moovieId);
        bundle.putString(Intent.EXTRA_TEXT, title);
        bundle.putString("vote_avg", mBinding.descDiv.tvVoteAverage.getText().toString());
        bundle.putString("release_date", mBinding.descDiv.tvRelease.getText().toString());
        ReviewsFragment fragment = new ReviewsFragment();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.detail_container, fragment)
                .commit();
    }

    public void changeFabColor(Integer fav, ContentValues contentValues) {

        if (fav == 0) {
            contentValues.put(MoovieContrat.MoovieEntry.COLUMN_FLAG_FAVORIT, 1);
            getActivity().getContentResolver().update(MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI.buildUpon().appendPath(String.valueOf(moovieId)).build(), contentValues, MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{String.valueOf(moovieId)});
            mBinding.fab.setBackgroundTintList(getResources().getColorStateList(android.R.color.white));
        } else if (fav == 1) {
            contentValues.put(MoovieContrat.MoovieEntry.COLUMN_FLAG_FAVORIT, 0);
            getActivity().getContentResolver().update(MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI.buildUpon().appendPath(String.valueOf(moovieId)).build(), contentValues, MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{String.valueOf(moovieId)});
            mBinding.fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

        }
    }


    public void rateMovie(View view) {
        Cursor cursor = getActivity().getContentResolver().query(MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI.buildUpon().appendPath(String.valueOf(moovieId)).build(), null, MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{String.valueOf(moovieId)}, null);
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
}
