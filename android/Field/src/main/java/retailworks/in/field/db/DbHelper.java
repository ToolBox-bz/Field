/*
 * @(#)DbHelper.java
 *
 * (c) COPYRIGHT 2010-2012 MOTOROLA INC.
 * MOTOROLA CONFIDENTIAL PROPRIETARY
 * MOTOROLA Advanced Technology and Software Operations
 *
 * REVISION HISTORY:
 * Author        Date       CR Number         Brief Description
 * ------------- ---------- ----------------- ------------------------------
 * A21693        2012/02/14 NA                Initial version
 *
 */

package retailworks.in.field.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import retailworks.in.field.utils.Constants;

/**
 * DbHelper <code><pre>
 * CLASS:
 *     RulePublisherProvider
 *
 * RESPONSIBILITIES:
 *     Helper class for Rule Publisher database
 *
 * USAGE:
 *     See each method.
 *
 * </pre></code>
 */
public class DbHelper implements Constants {

    private static final String TAG = APP_TAG + DbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "t.db";
    private static final int DATABASE_VERSION = 1;
    private DatabaseHelper sDatabaseHelper;
    private static DbHelper sDbHelper;

    /**
     * Private constructor
     *
     * @param context
     */
    private DbHelper(Context context) {
        if (sDatabaseHelper == null) {
            sDatabaseHelper = new DatabaseHelper(context);
        }
    }

    /**
     * Returns the instance of this class
     *
     * @param context
     * @return - instance of this class
     */
    public static synchronized DbHelper getInstance(Context context) {

        if (sDbHelper == null)
            sDbHelper = new DbHelper(context);

        return sDbHelper;
    }

    /**
     * This class creates/upgrades DP
     *
     * @author a21693
     *
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        Context ct = null;

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            ct = context;
        }

        public void onCreate(SQLiteDatabase db) {

            db.execSQL(OutletTable.CREATE_TABLE_SQL);
            db.execSQL(VisitTable.CREATE_TABLE_SQL);
            db.execSQL(CallCycleTable.CREATE_TABLE_SQL);
            db.execSQL(AttendanceTable.CREATE_TABLE_SQL);
            db.execSQL(OrderTable.CREATE_TABLE_SQL);
            db.execSQL(InventoryTable.CREATE_TABLE_SQL);
            db.execSQL(ProductTable.CREATE_TABLE_SQL);
            db.execSQL(VisitorTable.CREATE_TABLE_SQL);

            if (LOG_DEBUG) Log.d(TAG, "DB onCreate!");

        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Not required
            Log.e(TAG, "Database upgrade called but unimplemented");
            /**
             * Recommendations for onUpgrade 1. Create a back up of the table if
             * the upgrade operation is not straight forward i.Q., they cannot
             * be executed with single SQL command. Examples of straight forward
             * upgrades are adding a column, dropping a table etc.. Examples of
             * non-straight forward upgrades are renaming a column, dropping a
             * column, changing the type etc... 2. Create a new table with
             * required changes 3. Restore the data from the backed up table 4.
             * Delete the old table 5. If the upgrade operation is straight
             * forward, tables need not be backed up and deleted 6. Remember
             * onUpgrade can get called for any versions, i.Q., 1 to 2, 2 to 3
             * or 1 to 3.
             *
             * Refer to timeframe sensor DB's onUpgrade() for a sample
             * implementation
             */
        }
    }

    /*
     * Deletes a row in the table
     *
     * (non-Javadoc)
     *
     * @see android.content.ContentProvider#delete(android.net.Uri,
     * java.lang.String, java.lang.String[])
     */
    public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {
        if (LOG_DEBUG)
            Log.i(TAG, "Deleting " + uri.toString() + " for " + selection);

        int count = -1;
        try {
            TableBase table = TableBase.getTable(TAG, uri);
            SQLiteDatabase db = sDatabaseHelper.getWritableDatabase();

            if (table != null) {
                count = table.delete(db, uri, selection, selectionArgs);
            } else {
                Log.e(TAG, "Unable to get a table object");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.ContentProvider#insert(android.net.Uri,
     * android.content.ContentValues)
     */
    public synchronized long insert(Uri uri, ContentValues values) {

        if (LOG_VERBOSE)
            Log.i(TAG, "Inserting into " + uri.toString());

        long row = -1;
        try {
            TableBase table = TableBase.getTable(TAG, uri);
            SQLiteDatabase db = sDatabaseHelper.getWritableDatabase();

            if (table != null) {
                row = table.insert(db, values);
            } else {
                Log.e(TAG, "Unable to get a table object");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return row;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.ContentProvider#query(android.net.Uri,
     * java.lang.String[], java.lang.String, java.lang.String[],
     * java.lang.String)
     */
    public synchronized Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        Cursor c = null;
        try {
            TableBase table = TableBase.getTable(TAG, uri);
            SQLiteDatabase db = sDatabaseHelper.getWritableDatabase();

            if (table != null && db != null) {
                c = table.query(db, uri, projection, selection, selectionArgs,
                        sortOrder, null);
            } else {
                Log.e(TAG, "Unable to get a table object");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return c;
    }

    /**
     *
     * @param query
     * @return
     */
    public synchronized Cursor rawQuery(String query) {

        Cursor c = null;
        try {
            SQLiteDatabase db = sDatabaseHelper.getWritableDatabase();

            if (db == null) {
                throw new IllegalArgumentException("db cannot be null");
            }

            c = db.rawQuery(query, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return c;
    }

    public void execSQL(String query)
    {
        try
        {
            SQLiteDatabase db = sDatabaseHelper.getWritableDatabase();
            if (db == null) {
                throw new IllegalArgumentException("db cannot be null");
            }

            db. execSQL(query);
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see android.content.ContentProvider#update(Uri,
     * ContentValues, String, String[])
     */
    public synchronized int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {

        if (LOG_VERBOSE)
            Log.i(TAG, "Updating " + uri.toString() + " for " + selection);

        int count = -1;
        try {

            TableBase table = TableBase.getTable(TAG, uri);
            SQLiteDatabase db = sDatabaseHelper.getWritableDatabase();

            if (table != null) {
                count = table.update(db, uri, values, selection, selectionArgs);
            } else {
                Log.e(TAG, "Unable to get a table object");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }
}