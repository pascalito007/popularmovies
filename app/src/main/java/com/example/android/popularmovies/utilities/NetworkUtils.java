package com.example.android.popularmovies.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MoovieContrat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by jem001 on 02/08/2017.
 */

public class NetworkUtils {
    public static final String POPULAR_BASE_URL = "https://api.themoviedb.org/3/movie/popular";
    public static final String TOP_RATED_BASE_URL = "https://api.themoviedb.org/3/movie/top_rated";
    public static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185";
    public static final String BASE_IMAGE_YOUTUBE_URL = "https://img.youtube.com/vi";
    public static final String BASE_APP_VIDEO_YOUTUBE_URL = "vnd.youtube:";
    public static final String BASE_WEB_VIDEO_YOUTUBE_URL = "http://www.youtube.com/watch?v=";

    final static String QUERY_PARAM = "api_key";
    final static String BASE_OBJECT_LIST = "results";
    final static String OBJECT_GENRE_LIST = "genres";
    final static String ID = "id";
    final static String KEY = "key";
    final static String NAME = "name";
    final static String TYPE = "type";
    final static String AUTHOR = "author";
    final static String CONTENT = "content";
    final static String VOTE_AVERAGE = "vote_average";
    final static String ORIGINAL_TITLE = "original_title";
    final static String POSTER_PATH = "poster_path";
    final static String OVERVIEW = "overview";
    final static String RELEASE_DATE = "release_date";
    final static String VOTE_COUNT = "vote_count";
    final static String BACKDROP_PATH = "backdrop_path";
    final static String POPULARITY = "popularity";
    final static String ORIGINAL_LANGUAGE = "original_language";
    final static String RUNTIME = "runtime";
    final static String TAGLINE = "tagline";
    public static final String MOOVIE_ID = "moovie_id";


    public static URL builPopulardUrl(String param) {
        Uri builtUrl = Uri.parse(POPULAR_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, param)
                .build();
        URL url;
        try {
            url = new URL(builtUrl.toString());
        } catch (MalformedURLException e) {
            return null;
        }
        return url;
    }

    public static URL builCriteriaUrl(String api_key, String criteria, Context context) {
        URL url = null;
        if (criteria.equals(context.getString(R.string.pref_movie_popular_value))) {
            url = builPopulardUrl(api_key);
        } else if (criteria.equals(context.getString(R.string.pref_movie_rated_value))) {
            url = builTopRateddUrl(api_key);
        }

        return url;
    }

    public static URL trailersBaseUrl(String movieId, String apiKey) {
        Uri builtUrl = Uri.parse(BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath("videos")
                .appendQueryParameter(QUERY_PARAM, apiKey)
                .build();
        Log.d("trailers generated", builtUrl.toString());
        URL url;
        try {
            url = new URL(builtUrl.toString());
        } catch (MalformedURLException e) {
            return null;
        }
        return url;
    }

    public static URL reviewsBaseUrl(String movieId, String apiKey) {
        Uri builtUrl = Uri.parse(BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath("reviews")
                .appendQueryParameter(QUERY_PARAM, apiKey)
                .build();
        Log.d("reviews generated", builtUrl.toString());
        URL url;
        try {
            url = new URL(builtUrl.toString());
        } catch (MalformedURLException e) {
            return null;
        }
        return url;
    }

    public static URL genreBaseUrl(String movieId, String apiKey) {
        Uri builtUrl = Uri.parse(BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendQueryParameter(QUERY_PARAM, apiKey)
                .build();
        Log.d("genre generated", builtUrl.toString());
        URL url;
        try {
            url = new URL(builtUrl.toString());
        } catch (MalformedURLException e) {
            return null;
        }
        return url;
    }

    public static URL builTopRateddUrl(String param) {
        Uri builtUrl = Uri.parse(TOP_RATED_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, param)
                .build();
        URL url;
        try {
            url = new URL(builtUrl.toString());
        } catch (MalformedURLException e) {
            return null;
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }

        } finally {
            urlConnection.disconnect();
        }
    }


    public static ContentValues[] getMoviesFromJson(String json,String criteria) {
        try {
            JSONObject moviesJson = new JSONObject(json);
            JSONArray moviesArray = moviesJson.getJSONArray(BASE_OBJECT_LIST);
            ContentValues[] list = new ContentValues[moviesArray.length()];
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieObject = moviesArray.getJSONObject(i);

                ContentValues contentValues = new ContentValues();

                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID, movieObject.getInt(ID));
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_TITLE, movieObject.getString(ORIGINAL_TITLE));
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_OVERVIEW, movieObject.getString(OVERVIEW));
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_POSTER_PATH, movieObject.getString(POSTER_PATH));
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_RELEASE_DATE, movieObject.getString(RELEASE_DATE));
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_VOTE_AVERAGE, movieObject.getDouble(VOTE_AVERAGE));
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_BACKDROP_PATH, movieObject.getString(BACKDROP_PATH));
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_ORIGINAL_LANGUAGE, movieObject.getString(ORIGINAL_LANGUAGE));
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_VOTE_COUNT, movieObject.getInt(VOTE_COUNT));
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_POPULARITY, movieObject.getDouble(POPULARITY));
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_FLAG_CRITERIA, criteria);
                list[i] = contentValues;
            }
            return list;
        } catch (JSONException e) {
            return null;
        }
    }

    public static ContentValues[] getMoviesFromJsonTrailers(String json, Integer moovieId) {
        try {
            JSONObject moviesJson = new JSONObject(json);
            JSONArray moviesArray = moviesJson.getJSONArray(BASE_OBJECT_LIST);
            ContentValues[] list = new ContentValues[moviesArray.length()];
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieObject = moviesArray.getJSONObject(i);
                ContentValues contentValues = new ContentValues();

                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID, moovieId);
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_KEY, movieObject.getString(KEY));
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_NAME, movieObject.getString(NAME));
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_TYPE, movieObject.getString(TYPE));

                list[i] = contentValues;
            }
            return list;
        } catch (JSONException e) {
            return null;
        }
    }

    public static ContentValues[] getMoviesFromJsonReviews(String json, Integer moovieId) {
        try {
            JSONObject moviesJson = new JSONObject(json);
            JSONArray moviesArray = moviesJson.getJSONArray(BASE_OBJECT_LIST);
            ContentValues[] list = new ContentValues[moviesArray.length()];
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieObject = moviesArray.getJSONObject(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_AUTHOR, movieObject.getString(AUTHOR));
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_CONTENT, movieObject.getString(CONTENT));
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID, moovieId);
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_REVIEW_ID, movieObject.getString(ID));
                list[i] = contentValues;
            }
            return list;
        } catch (JSONException e) {
            return null;
        }
    }

    public static ContentValues[] getMoviesFromJsonGenre(String json, Integer moovieId) {
        try {
            JSONObject moviesJson = new JSONObject(json);
            JSONArray moviesArray = moviesJson.getJSONArray(OBJECT_GENRE_LIST);
            ContentValues[] list = new ContentValues[moviesArray.length()];
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieObject = moviesArray.getJSONObject(i);

                ContentValues contentValues = new ContentValues();
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID, moovieId);
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_GENRE_ID, movieObject.getInt(ID));
                contentValues.put(MoovieContrat.MoovieEntry.COLUMN_NAME, movieObject.getString(NAME));
                list[i] = contentValues;
            }
            return list;
        } catch (JSONException e) {
            return null;
        }
    }

    public static ContentValues getMoviesFromJsonGenreObject(String json) {
        try {
            JSONObject moviesJson = new JSONObject(json);
            ContentValues moovieContentValues = new ContentValues();
            moovieContentValues.put(MoovieContrat.MoovieEntry.COLUMN_RUNTIME, moviesJson.getString(RUNTIME));
            moovieContentValues.put(MoovieContrat.MoovieEntry.COLUMN_TAGLINE, moviesJson.getString(TAGLINE));
            return moovieContentValues;
        } catch (JSONException e) {
            return null;
        }
    }
}
