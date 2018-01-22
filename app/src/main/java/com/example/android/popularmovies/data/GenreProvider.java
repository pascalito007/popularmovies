package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by jem001 on 27/08/2017.
 */

public class GenreProvider extends ContentProvider {
    public static final int CODE_GENRE = 400;
    public static final int CODE_GENRE_WITH_ID = 401;


    public static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoovieDbHelper mOpenHelper;


    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoovieContrat.GENRE_CONTENT_AUTORITY;
        matcher.addURI(authority, MoovieContrat.PATH_GENRE, CODE_GENRE);
        matcher.addURI(authority, MoovieContrat.PATH_GENRE + "/#", CODE_GENRE_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_GENRE_WITH_ID:
                cursor = db.query(MoovieContrat.MoovieEntry.GENRE_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                cursor = null;
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case CODE_GENRE:
                long id = db.insert(MoovieContrat.MoovieEntry.GENRE_TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MoovieContrat.MoovieEntry.GENRE_CONTENT_URI, id);
                } else {
                    returnUri = null;
                }
                break;
            default:
                returnUri = null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_GENRE:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MoovieContrat.MoovieEntry.GENRE_TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowsInserted > 0) getContext().getContentResolver().notifyChange(uri, null);
                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted;

        //if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {
            case CODE_GENRE:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MoovieContrat.MoovieEntry.GENRE_TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_GENRE_WITH_ID:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(MoovieContrat.MoovieEntry.GENRE_TABLE_NAME,selection,selectionArgs);
                break;
            default:
                return 0;
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_GENRE_WITH_ID:
                int rowsUpdated = 0;
                rowsUpdated = db.update(MoovieContrat.MoovieEntry.GENRE_TABLE_NAME, contentValues, selection, selectionArgs);
                if (rowsUpdated > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return rowsUpdated;
                }
            default:
                return 0;
        }
    }
}
