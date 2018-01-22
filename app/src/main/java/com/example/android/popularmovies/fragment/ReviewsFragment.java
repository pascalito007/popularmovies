package com.example.android.popularmovies.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapter.ReviewsAdapter;
import com.example.android.popularmovies.data.MoovieContrat;
import com.example.android.popularmovies.databinding.ActivityReviewsBinding;
import com.example.android.popularmovies.sync.MoovieSynUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by jem001 on 13/09/2017.
 */

public class ReviewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int REVIEWS_LOADER_ID = 66;
    Uri reviewsUri;
    Integer moovieId;
    ActivityReviewsBinding mBinding;
    private ReviewsAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.activity_reviews, container, false);
        View view = mBinding.getRoot();
        if (getArguments().containsKey(NetworkUtils.MOOVIE_ID)) {
            String title = getArguments().getString(Intent.EXTRA_TEXT);
            moovieId = getArguments().getInt(NetworkUtils.MOOVIE_ID);
            MoovieSynUtils.startReviewsSync(getActivity(), String.valueOf(moovieId));
            mBinding.toolbar.setTitle(title);
            mBinding.reviewsLayout.tvVoteAverage.setText(getArguments().getString("vote_avg"));
            String release = getArguments().getString("release_date");
            SimpleDateFormat format = new SimpleDateFormat("yyyy");
            try {
                mBinding.reviewsLayout.tvYear.setText(format.format(format.parse(release)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            reviewsUri = MoovieContrat.MoovieEntry.REVIEWS_CONTENT_URI.buildUpon().appendPath(String.valueOf(moovieId)).build();

        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mBinding.reviewsLayout.rvReviews.setLayoutManager(layoutManager);
        mBinding.reviewsLayout.rvReviews.setHasFixedSize(true);
        mAdapter = new ReviewsAdapter();
        mBinding.reviewsLayout.rvReviews.setAdapter(mAdapter);
        getLoaderManager().initLoader(REVIEWS_LOADER_ID, null, this);
        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case REVIEWS_LOADER_ID:
                return new CursorLoader(getActivity(), reviewsUri, null, MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + "= ?", new String[]{String.valueOf(moovieId)}, null);
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
}
