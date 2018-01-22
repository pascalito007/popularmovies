package com.example.android.popularmovies.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MoovieContrat;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by jem001 on 03/09/2017.
 */

public class MoovieSyncTask {


    synchronized public static void syncMoovieList(Context context, String criteria) {
        URL moovieURL = null;
        Log.d("criteria", criteria);
        if (!criteria.equals(context.getString(R.string.pref_movie_favorits_value))) {
            moovieURL = NetworkUtils.builCriteriaUrl(BuildConfig.THE_MOVIE_DB_API_TOKEN, criteria, context);

            String jsonResult = null;
            try {
                jsonResult = NetworkUtils.getResponseFromHttpUrl(moovieURL);
                ContentValues[] listData = NetworkUtils.getMoviesFromJson(jsonResult, criteria);

                if (listData != null && listData.length != 0) {
                    //Cursor cursor = context.getContentResolver().query(MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI, null, MoovieContrat.MoovieEntry.COLUMN_FLAG_CRITERIA + " = ?", new String[]{criteria}, null);
                    context.getContentResolver().delete(MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI, MoovieContrat.MoovieEntry.COLUMN_FLAG_CRITERIA + " = ?", new String[]{criteria});
                    context.getContentResolver().bulkInsert(MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI, listData);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized public static void syncMoovieDetails(Context context, String movieId) {
        URL trailersURL = NetworkUtils.trailersBaseUrl(movieId, BuildConfig.THE_MOVIE_DB_API_TOKEN);
        URL genreURL = NetworkUtils.genreBaseUrl(movieId, BuildConfig.THE_MOVIE_DB_API_TOKEN);
        String trailersJsonResult = null;
        String genreJsonResult = null;
        try {
            trailersJsonResult = NetworkUtils.getResponseFromHttpUrl(trailersURL);
            genreJsonResult = NetworkUtils.getResponseFromHttpUrl(genreURL);

            ContentValues[] listDataTrailers = NetworkUtils.getMoviesFromJsonTrailers(trailersJsonResult, Integer.parseInt(movieId));
            ContentValues[] listDataGenre = NetworkUtils.getMoviesFromJsonGenre(genreJsonResult, Integer.parseInt(movieId));


            if (listDataTrailers != null && listDataTrailers.length != 0) {
                //Cursor trailersCursor = context.getContentResolver().query(MoovieContrat.MoovieEntry.TRAILERS_CONTENT_URI.buildUpon().appendPath(movieId).build(), null, MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ? AND " + MoovieContrat.MoovieEntry.COLUMN_TYPE + " = ?", new String[]{movieId, "Trailer"}, null);
                    context.getContentResolver().delete(MoovieContrat.MoovieEntry.TRAILERS_CONTENT_URI.buildUpon().appendPath(movieId).build(), MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{movieId});
                    context.getContentResolver().bulkInsert(MoovieContrat.MoovieEntry.TRAILERS_CONTENT_URI, listDataTrailers);
            }

            if (listDataGenre != null && listDataGenre.length != 0) {
                //Cursor genreCursor = context.getContentResolver().query(MoovieContrat.MoovieEntry.GENRE_CONTENT_URI.buildUpon().appendPath(movieId).build(), null, MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{movieId}, null);
                    context.getContentResolver().delete(MoovieContrat.MoovieEntry.GENRE_CONTENT_URI.buildUpon().appendPath(movieId).build(), MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{String.valueOf(movieId)});
                    context.getContentResolver().bulkInsert(MoovieContrat.MoovieEntry.GENRE_CONTENT_URI, listDataGenre);
            }

            ContentValues videoContentValue = NetworkUtils.getMoviesFromJsonGenreObject(genreJsonResult);
            if (videoContentValue != null) {
                Uri updatedUri = MoovieContrat.MoovieEntry.MOVIELIST_CONTENT_URI.buildUpon().appendPath(movieId).build();
                context.getContentResolver().update(updatedUri, videoContentValue, MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{movieId});

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized public static void syncReviews(Context context, String movieId) {
        URL reviewsURL = NetworkUtils.reviewsBaseUrl(movieId, BuildConfig.THE_MOVIE_DB_API_TOKEN);
        String reviewsJsonResult = null;
        try {
            reviewsJsonResult = NetworkUtils.getResponseFromHttpUrl(reviewsURL);

            ContentValues[] listDataReviews = NetworkUtils.getMoviesFromJsonReviews(reviewsJsonResult, Integer.parseInt(movieId));

            if (listDataReviews != null && listDataReviews.length != 0) {
                //Cursor genreCursor = context.getContentResolver().query(MoovieContrat.MoovieEntry.REVIEWS_CONTENT_URI.buildUpon().appendPath(movieId).build(), null, MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{movieId}, null);
                    context.getContentResolver().delete(MoovieContrat.MoovieEntry.REVIEWS_CONTENT_URI.buildUpon().appendPath(movieId).build(), MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " = ?", new String[]{movieId});
                    context.getContentResolver().bulkInsert(MoovieContrat.MoovieEntry.REVIEWS_CONTENT_URI, listDataReviews);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
