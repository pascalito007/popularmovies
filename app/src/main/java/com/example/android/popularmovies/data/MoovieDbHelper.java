package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jem001 on 27/08/2017.
 */

public class MoovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "moovie.db";
    public static final int DATABASE_VERSION = 2;

    public MoovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //CREATE TABLE MOOVIELIST
        final String SQL_CREATE_MOOVIELIST_TABLE = "CREATE TABLE " + MoovieContrat.MoovieEntry.MOOVIELIST_TABLE_NAME + " (" +
                MoovieContrat.MoovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " INTEGER NOT NULL, " +
                MoovieContrat.MoovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
                MoovieContrat.MoovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MoovieContrat.MoovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MoovieContrat.MoovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                MoovieContrat.MoovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoovieContrat.MoovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoovieContrat.MoovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MoovieContrat.MoovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoovieContrat.MoovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " +
                MoovieContrat.MoovieEntry.COLUMN_TAGLINE + " TEXT , " +
                MoovieContrat.MoovieEntry.COLUMN_RUNTIME + " INTEGER , " +
                MoovieContrat.MoovieEntry.COLUMN_FLAG_CRITERIA + " TEXT , " +
                MoovieContrat.MoovieEntry.COLUMN_FLAG_FAVORIT + " BOOLEAN NOT NULL DEFAULT 0 , " +
                " UNIQUE (" + MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + ") ON CONFLICT REPLACE);";

        //CREATE TABLE REVIEWS
        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + MoovieContrat.MoovieEntry.REVIEWS_TABLE_NAME + " (" +
                MoovieContrat.MoovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " INTEGER NOT NULL, " +
                MoovieContrat.MoovieEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                MoovieContrat.MoovieEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                MoovieContrat.MoovieEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                " UNIQUE (" + MoovieContrat.MoovieEntry.COLUMN_REVIEW_ID + ") ON CONFLICT REPLACE);";

        //CREATE TABLE GENRE
        final String SQL_CREATE_GENRE_TABLE = "CREATE TABLE " + MoovieContrat.MoovieEntry.GENRE_TABLE_NAME + " (" +
                MoovieContrat.MoovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " INTEGER NOT NULL, " +
                MoovieContrat.MoovieEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL, " +
                MoovieContrat.MoovieEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                " UNIQUE (" + MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + ","+ MoovieContrat.MoovieEntry.COLUMN_GENRE_ID+") ON CONFLICT REPLACE);";

        //CREATE TABLE TRAILERS
        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + MoovieContrat.MoovieEntry.TRAILERS_TABLE_NAME + " (" +
                MoovieContrat.MoovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoovieContrat.MoovieEntry.COLUMN_MOOVIE_ID + " TEXT NOT NULL, " +
                MoovieContrat.MoovieEntry.COLUMN_KEY + " TEXT NOT NULL, " +
                MoovieContrat.MoovieEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                MoovieContrat.MoovieEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                " UNIQUE (" + MoovieContrat.MoovieEntry.COLUMN_KEY + ") ON CONFLICT REPLACE);";


        sqLiteDatabase.execSQL(SQL_CREATE_GENRE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOOVIELIST_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(SQL_DROP_TABLE + MoovieContrat.MoovieEntry.GENRE_TABLE_NAME);
        sqLiteDatabase.execSQL(SQL_DROP_TABLE + MoovieContrat.MoovieEntry.TRAILERS_TABLE_NAME);
        sqLiteDatabase.execSQL(SQL_DROP_TABLE + MoovieContrat.MoovieEntry.MOOVIELIST_TABLE_NAME);
        sqLiteDatabase.execSQL(SQL_DROP_TABLE + MoovieContrat.MoovieEntry.REVIEWS_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
