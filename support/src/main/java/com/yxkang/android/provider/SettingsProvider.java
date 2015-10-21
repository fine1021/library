package com.yxkang.android.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

/**
 * SettingsProvider
 */
public class SettingsProvider extends ContentProvider {

    private static final String TAG = "SettingsProvider";

    private static final String AUTHORITY = "com.yxkang.android.provider.settings";

    private static final int GLOBAL = 1;

    private static final int GLOBAL_ID = 2;

    private static final String TABLE_GLOBAL = "global";

    private static final UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/settings");

    private DataBaseHelper dataBaseHelper;

    static {
        sMatcher.addURI(AUTHORITY, "*/global", GLOBAL);
        sMatcher.addURI(AUTHORITY, "*/global/#", GLOBAL_ID);
    }

    @Override
    public boolean onCreate() {
        dataBaseHelper = new DataBaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        switch (sMatcher.match(uri)) {
            case GLOBAL:
                return db.query(TABLE_GLOBAL, projection, selection, selectionArgs, null, null, sortOrder);
            case GLOBAL_ID:
                long id = ContentUris.parseId(uri);
                String whereClause = "_id=" + id;
                if (!TextUtils.isEmpty(selection)) {
                    whereClause = whereClause + " and " + selection;
                }
                return db.query(TABLE_GLOBAL, projection, whereClause, selectionArgs, null, null, sortOrder);
            default:
                throw new IllegalArgumentException("Unknown Uri : " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (sMatcher.match(uri)) {
            case GLOBAL:
                return "vnd.android.cursor.dir/com.yxkang.android.provider.settings";
            case GLOBAL_ID:
                return "vnd.android.cursor.item/com.yxkang.android.provider.settings";
            default:
                throw new IllegalArgumentException("Unknown Uri : " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        switch (sMatcher.match(uri)) {
            case GLOBAL:
                long rowId = db.insert(TABLE_GLOBAL, "_id", values);
                if (rowId > 0) {
                    Uri uri1 = ContentUris.withAppendedId(uri, rowId);
                    getContext().getContentResolver().notifyChange(uri1, null);
                    return uri1;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri : " + uri);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
