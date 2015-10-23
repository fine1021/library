package com.example.provider.settings;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import java.util.List;

/**
 * SettingsProvider
 */
public class SettingsProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.provider.settings";

    private static final int GLOBAL = 1;

    private static final int GLOBAL_ID = 2;

    private static final UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private DataBaseHelper dataBaseHelper;

    static {
        sMatcher.addURI(AUTHORITY, "global", GLOBAL);
        sMatcher.addURI(AUTHORITY, "global/#", GLOBAL_ID);
    }

    @Override
    public boolean onCreate() {
        dataBaseHelper = new DataBaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        switch (sMatcher.match(uri)) {
            case GLOBAL:
                return db.query(args.table, projection, args.selection, args.selectionArgs, null, null, sortOrder);
            case GLOBAL_ID:
                long id = ContentUris.parseId(uri);
                String whereClause = "_id=" + id;
                if (!TextUtils.isEmpty(args.selection)) {
                    whereClause = whereClause + " and " + args.selection;
                }
                return db.query(args.table, projection, whereClause, args.selectionArgs, null, null, sortOrder);
            default:
                throw new IllegalArgumentException("Unknown Uri : " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (sMatcher.match(uri)) {
            case GLOBAL:
                return "vnd.android.cursor.dir/global";
            case GLOBAL_ID:
                return "vnd.android.cursor.item/global";
            default:
                throw new IllegalArgumentException("Unknown Uri : " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SqlArguments args = new SqlArguments(uri);
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        switch (sMatcher.match(uri)) {
            case GLOBAL:
                long rowId = db.insert(args.table, null, values);
                if (rowId > 0) {
                    Uri newUri = ContentUris.withAppendedId(uri, rowId);
                    if (getContext() != null) {
                        getContext().getContentResolver().notifyChange(newUri, null);
                    }
                    return newUri;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri : " + uri);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        int deleteCount;
        switch (sMatcher.match(uri)) {
            case GLOBAL:
                deleteCount = db.delete(args.table, args.selection, args.selectionArgs);
                break;
            case GLOBAL_ID:
                long id = ContentUris.parseId(uri);
                String whereClause = "_id=" + id;
                if (!TextUtils.isEmpty(args.selection)) {
                    whereClause = whereClause + " and " + args.selection;
                }
                deleteCount = db.delete(args.table, whereClause, args.selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri : " + uri);
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        int modifyCount;
        switch (sMatcher.match(uri)) {
            case GLOBAL:
                modifyCount = db.update(args.table, values, args.selection, args.selectionArgs);
                break;
            case GLOBAL_ID:
                long id = ContentUris.parseId(uri);
                String whereClause = "_id=" + id;
                if (!TextUtils.isEmpty(args.selection)) {
                    whereClause = whereClause + " and " + args.selection;
                }
                modifyCount = db.update(args.table, values, whereClause, args.selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri : " + uri);
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return modifyCount;
    }

    /**
     * Decode a content URL into the table, projection, and arguments
     * used to access the corresponding database rows.
     */
    private static class SqlArguments {
        public String table;
        public final String selection;
        public final String[] selectionArgs;

        SqlArguments(Uri uri, String selection, String[] selectionArgs) {
            List<String> list = uri.getPathSegments();
            if (list.size() == 1) {
                this.table = list.get(0);
                this.selection = selection;
                this.selectionArgs = selectionArgs;
            } else {
                throw new IllegalArgumentException("Invalid URI: " + uri);
            }
        }

        SqlArguments(Uri uri) {
            List<String> list = uri.getPathSegments();
            if (list.size() == 1) {
                this.table = list.get(0);
                this.selection = null;
                this.selectionArgs = null;
            } else {
                throw new IllegalArgumentException("Invalid URI: " + uri);
            }
        }
    }
}
