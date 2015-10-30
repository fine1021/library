package com.yxkang.android.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.AndroidException;
import android.util.Log;

/**
 * Settings
 */
public class Settings {

    private static final String TAG = "Settings";

    private static final String AUTHORITY = "com.example.provider.settings";

    public static class SettingNotFoundException extends AndroidException {
        public SettingNotFoundException(String msg) {
            super(msg);
        }
    }

    /**
     * Common base for tables of name/value settings.
     */
    public static class NameValueTable {

        public static final String NAME = "name";
        public static final String VALUE = "value";

        private static final String[] SELECT_VALUE = new String[]{VALUE};
        private static final String NAME_EQ_PLACEHOLDER = "name=?";

        protected static boolean putString(ContentResolver resolver, Uri uri, String name, String value) {
            // The database will take care of replacing duplicates.
            try {
                ContentValues values = new ContentValues();
                values.put(NAME, name);
                values.put(VALUE, value);
                resolver.insert(uri, values);
                return true;
            } catch (SQLException e) {
                Log.w(TAG, "Can't set key " + name + " in " + uri, e);
                return false;
            }
        }

        protected static String getString(ContentResolver cr, Uri uri, String name) {
            Cursor c = null;
            try {
                c = cr.query(uri, SELECT_VALUE, NAME_EQ_PLACEHOLDER, new String[]{name}, null);
                if (c == null) {
                    Log.w(TAG, "Can't get key " + name + " from " + uri);
                    return null;
                }
                return c.moveToNext() ? c.getString(0) : null;
            } catch (Exception e) {
                Log.w(TAG, "Can't get key " + name + " in " + uri, e);
                return null;
            } finally {
                if (c != null) c.close();
            }
        }

        public static Uri getUriFor(Uri uri, String name) {
            return Uri.withAppendedPath(uri, name);
        }
    }

    public static final class Global extends NameValueTable {

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/global");

        /**
         * Store a name/value pair into the database.
         *
         * @param resolver to access the database with
         * @param name     to store
         * @param value    to associate with the name
         * @return true if the value was set, false on database errors
         */
        public static boolean putString(ContentResolver resolver, String name, String value) {
            return putString(resolver, CONTENT_URI, name, value);
        }

        /**
         * Convenience function for retrieving a single settings value
         * as a {@code String}.  Note that internally setting values are always
         * stored as strings;
         * <br>
         * This version does not take a default value.  If the setting has not
         * been set,it throws {@link SettingNotFoundException}.
         *
         * @param cr   The ContentResolver to access.
         * @param name The name of the setting to retrieve.
         * @return The setting's current value.
         * @throws SettingNotFoundException Thrown if a setting by the given name can't be found
         */
        public static String getString(ContentResolver cr, String name) throws SettingNotFoundException {
            String v = getString(cr, CONTENT_URI, name);
            if (v == null) {
                throw new SettingNotFoundException(name);
            } else {
                return v;
            }
        }

        /**
         * Convenience function for retrieving a single settings value
         * as a {@code String}.  Note that internally setting values are always
         * stored as strings;  The default value will be returned if the setting is
         * not defined .
         *
         * @param cr   The ContentResolver to access.
         * @param name The name of the setting to retrieve.
         * @param def  Value to return if the setting is not defined.
         * @return The setting's current value, or 'def' if it is not defined.
         */
        public static String getString(ContentResolver cr, String name, String def) {
            String v = getString(cr, CONTENT_URI, name);
            return v != null ? v : def;
        }

        /**
         * Construct the content URI for a particular name/value pair,
         * useful for monitoring changes with a ContentObserver.
         *
         * @param name to look up in the table
         * @return the corresponding content URI, or null if not present
         */
        public static Uri getUriFor(String name) {
            return getUriFor(CONTENT_URI, name);
        }

        /**
         * Convenience function for retrieving a single settings value
         * as an integer.  Note that internally setting values are always
         * stored as strings; this function converts the string to an integer
         * for you.  The default value will be returned if the setting is
         * not defined or not an integer.
         *
         * @param cr   The ContentResolver to access.
         * @param name The name of the setting to retrieve.
         * @param def  Value to return if the setting is not defined.
         * @return The setting's current value, or 'def' if it is not defined
         * or not a valid integer.
         */
        public static int getInt(ContentResolver cr, String name, int def) {
            String v = getString(cr, CONTENT_URI, name);
            try {
                return v != null ? Integer.parseInt(v) : def;
            } catch (NumberFormatException e) {
                return def;
            }
        }

        /**
         * Convenience function for retrieving a single settings value
         * as an integer.  Note that internally setting values are always
         * stored as strings; this function converts the string to an integer
         * for you.
         * <p>
         * This version does not take a default value.  If the setting has not
         * been set, or the string value is not a number,
         * it throws {@link SettingNotFoundException}.
         *
         * @param cr   The ContentResolver to access.
         * @param name The name of the setting to retrieve.
         * @return The setting's current value.
         * @throws SettingNotFoundException Thrown if a setting by the given
         *                                  name can't be found or the setting value is not an integer.
         */
        public static int getInt(ContentResolver cr, String name) throws SettingNotFoundException {
            String v = getString(cr, CONTENT_URI, name);
            if (v == null) {
                throw new SettingNotFoundException(name);
            }
            try {
                return Integer.parseInt(v);
            } catch (NumberFormatException e) {
                throw new SettingNotFoundException(name);
            }
        }

        /**
         * Convenience function for updating a single value as an
         * integer. This will either create a new entry in the table if the
         * given name does not exist, or modify the value of the existing row
         * with that name.  Note that internally setting values are always
         * stored as strings, so this function converts the given value to a
         * string before storing it.
         *
         * @param cr    The ContentResolver to access.
         * @param name  The name of the setting to modify.
         * @param value The new value for the setting.
         * @return true if the value was set, false on database errors
         */
        public static boolean putInt(ContentResolver cr, String name, int value) {
            return putString(cr, name, Integer.toString(value));
        }

        /**
         * Convenience function for retrieving a single settings value
         * as a {@code long}.  Note that internally setting values are always
         * stored as strings; this function converts the string to a {@code long}
         * for you.  The default value will be returned if the setting is
         * not defined or not a {@code long}.
         *
         * @param cr   The ContentResolver to access.
         * @param name The name of the setting to retrieve.
         * @param def  Value to return if the setting is not defined.
         * @return The setting's current value, or 'def' if it is not defined
         * or not a valid {@code long}.
         */
        public static long getLong(ContentResolver cr, String name, long def) {
            String valString = getString(cr, CONTENT_URI, name);
            long value;
            try {
                value = valString != null ? Long.parseLong(valString) : def;
            } catch (NumberFormatException e) {
                value = def;
            }
            return value;
        }

        /**
         * Convenience function for retrieving a single settings value
         * as a {@code long}.  Note that internally setting values are always
         * stored as strings; this function converts the string to a {@code long}
         * for you.
         * <p>
         * This version does not take a default value.  If the setting has not
         * been set, or the string value is not a number,
         * it throws {@link SettingNotFoundException}.
         *
         * @param cr   The ContentResolver to access.
         * @param name The name of the setting to retrieve.
         * @return The setting's current value.
         * @throws SettingNotFoundException Thrown if a setting by the given
         *                                  name can't be found or the setting value is not an integer.
         */
        public static long getLong(ContentResolver cr, String name) throws SettingNotFoundException {
            String valString = getString(cr, CONTENT_URI, name);
            if (valString == null) {
                throw new SettingNotFoundException(name);
            }
            try {
                return Long.parseLong(valString);
            } catch (NumberFormatException e) {
                throw new SettingNotFoundException(name);
            }
        }

        /**
         * Convenience function for updating a single settings value as a long
         * integer. This will either create a new entry in the table if the
         * given name does not exist, or modify the value of the existing row
         * with that name.  Note that internally setting values are always
         * stored as strings, so this function converts the given value to a
         * string before storing it.
         *
         * @param cr    The ContentResolver to access.
         * @param name  The name of the setting to modify.
         * @param value The new value for the setting.
         * @return true if the value was set, false on database errors
         */
        public static boolean putLong(ContentResolver cr, String name, long value) {
            return putString(cr, name, Long.toString(value));
        }

        /**
         * Convenience function for retrieving a single settings value
         * as a floating point number.  Note that internally setting values are
         * always stored as strings; this function converts the string to an
         * float for you. The default value will be returned if the setting
         * is not defined or not a valid float.
         *
         * @param cr   The ContentResolver to access.
         * @param name The name of the setting to retrieve.
         * @param def  Value to return if the setting is not defined.
         * @return The setting's current value, or 'def' if it is not defined
         * or not a valid float.
         */
        public static float getFloat(ContentResolver cr, String name, float def) {
            String v = getString(cr, CONTENT_URI, name);
            try {
                return v != null ? Float.parseFloat(v) : def;
            } catch (NumberFormatException e) {
                return def;
            }
        }

        /**
         * Convenience function for retrieving a single settings value
         * as a float.  Note that internally setting values are always
         * stored as strings; this function converts the string to a float
         * for you.
         * <p>
         * This version does not take a default value.  If the setting has not
         * been set, or the string value is not a number,
         * it throws {@link SettingNotFoundException}.
         *
         * @param cr   The ContentResolver to access.
         * @param name The name of the setting to retrieve.
         * @return The setting's current value.
         * @throws SettingNotFoundException Thrown if a setting by the given
         *                                  name can't be found or the setting value is not a float.
         */
        public static float getFloat(ContentResolver cr, String name) throws SettingNotFoundException {
            String v = getString(cr, CONTENT_URI, name);
            if (v == null) {
                throw new SettingNotFoundException(name);
            }
            try {
                return Float.parseFloat(v);
            } catch (NumberFormatException e) {
                throw new SettingNotFoundException(name);
            }
        }

        /**
         * Convenience function for updating a single settings value as a
         * floating point number. This will either create a new entry in the
         * table if the given name does not exist, or modify the value of the
         * existing row with that name.  Note that internally setting values
         * are always stored as strings, so this function converts the given
         * value to a string before storing it.
         *
         * @param cr    The ContentResolver to access.
         * @param name  The name of the setting to modify.
         * @param value The new value for the setting.
         * @return true if the value was set, false on database errors
         */
        public static boolean putFloat(ContentResolver cr, String name, float value) {
            return putString(cr, name, Float.toString(value));
        }

    }
}
