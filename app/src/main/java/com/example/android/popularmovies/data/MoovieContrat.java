package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jem001 on 27/08/2017.
 */


public class MoovieContrat {
public static final String MOOVIE_LIST_CONTENT_AUTORITY="com.example.android.popularmovies.moovie_list";
public static final String REVIEWS_CONTENT_AUTORITY="com.example.android.popularmovies.reviews";
public static final String TRAILERS_CONTENT_AUTORITY="com.example.android.popularmovies.trailers";
public static final String GENRE_CONTENT_AUTORITY="com.example.android.popularmovies.genre";
    public static final Uri MOOVIE_LIST_BASE_CONTENT_URI=Uri.parse("content://"+MOOVIE_LIST_CONTENT_AUTORITY);
    public static final Uri GENRE_BASE_CONTENT_URI=Uri.parse("content://"+GENRE_CONTENT_AUTORITY);
    public static final Uri REVIEWS_BASE_CONTENT_URI=Uri.parse("content://"+REVIEWS_CONTENT_AUTORITY);
    public static final Uri TRAILERS_BASE_CONTENT_URI=Uri.parse("content://"+TRAILERS_CONTENT_AUTORITY);


    public static final String PATH_TRAILERS="trailers";
    public static final String PATH_GENRE="genre";
    public static final String PATH_MOVIELIST="movielist";
    public static final String PATH_REVIEWS="reviews";

    public static final class MoovieEntry implements BaseColumns {

        public static final Uri TRAILERS_CONTENT_URI=TRAILERS_BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();
        public static final Uri GENRE_CONTENT_URI=GENRE_BASE_CONTENT_URI.buildUpon().appendPath(PATH_GENRE).build();
        public static final Uri REVIEWS_CONTENT_URI=REVIEWS_BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();
        public static final Uri MOVIELIST_CONTENT_URI=MOOVIE_LIST_BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIELIST).build();

        //STANDARD COLUMNS NAME
        public static final String COLUMN_MOOVIE_ID = "id";
        public static final String COLUMN_NAME = "name";
        /*
            TABLE MOOVIELIST
         */
        public static final String MOOVIELIST_TABLE_NAME = "moovielist";

        // COLUMNS
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_TAGLINE = "tagline";
        public static final String COLUMN_FLAG_CRITERIA = "flag";
        public static final String COLUMN_FLAG_FAVORIT = "favorit";


        /*
            TABLE MOOVIE
         */
        public static final String GENRE_TABLE_NAME = "genre";

        //COLUMNS
        public static final String COLUMN_GENRE_ID = "genre_id";


        /*
            TABLE TRAILERS
         */
        //Trailers TABLE NAME
        public static final String TRAILERS_TABLE_NAME = "trailers";

        //COLUMNS
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_TYPE = "type";

        /*
            TABLE REVIEWS
         */
        public static final String REVIEWS_TABLE_NAME = "reviews";

        //COLUMNS
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_REVIEW_ID = "review_id";
    }
}
