/*

 * @(#)TableBase.java
 *
 * (c) COPYRIGHT 2009-2012 MOTOROLA INC.
 * MOTOROLA CONFIDENTIAL PROPRIETARY
 * MOTOROLA Advanced Technology and Software Operations
 *
 * REVISION HISTORY:
 * Author        Date       CR Number         Brief Description
 * ------------- ---------- ----------------- ------------------------------
 * a21693        2012/02/14                   Initial version
 *
 */
package retailworks.in.field.db;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import retailworks.in.field.utils.Constants;

/** class TableBase - Defines constants used by all tables implements Constants
 *<code><pre>
 * CLASS:
 *     TableBase - Defines constants used by all tables implements Constants
 *                 Functions required by Content Providers are defined here (generalization)
 *
 * RESPONSIBILITIES:
 *     Defines constants used by all tables
 *     Base class for each table required in Inference Manager
 *
 * USAGE:
 *     See each method.
 *
 * </pre></code>
 */
public abstract class TableBase {


    public String              TAG          = TableBase.class.getSimpleName();
    public static final String DB_NAME   = Constants.DBNAME + Constants.COMPANY_NAME;
    public static final String AUTHORITY    = Constants.PACKAGE + ".auth";
    public final Uri CONTENT_URI            = Uri.parse("content://"+AUTHORITY+"/"+ getTableName() + "/");
    //public abstract Uri getContentUri();


    /**
     * Constructor
     * @param tag     - Tag used for logging
     */
    public TableBase(String tag) {
        TAG      = tag;
    }

    /**
     *
     * @return - Content uri to be implemented by derived class
     */
    public abstract Uri getContentUri();

    /**
     *
     * @return - Table Name to be implemented by derived class
     */
    public abstract String getTableName();

    private interface UriMatch {
        static final int OUTLET_TABLE       = 0x1;
        static final int CALLCYCLE_TABLE    = 0x2;
        static final int VISIT_TABLE        = 0x3;
        static final int ATTENDANCE_TABLE   = 0x4;
        static final int PRODUCT_TABLE      = 0x5;
        static final int INVENTORY_TABLE    = 0x6;
        static final int ORDER_TABLE        = 0x7;
        static final int VISITOR_TABLE      = 0x8;
    }

    private static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, OutletTable.TABLE_NAME, UriMatch.OUTLET_TABLE);
        sUriMatcher.addURI(AUTHORITY, CallCycleTable.TABLE_NAME, UriMatch.CALLCYCLE_TABLE);
        sUriMatcher.addURI(AUTHORITY, VisitTable.TABLE_NAME, UriMatch.VISIT_TABLE);
        sUriMatcher.addURI(AUTHORITY, AttendanceTable.TABLE_NAME, UriMatch.ATTENDANCE_TABLE);
        sUriMatcher.addURI(AUTHORITY, ProductTable.TABLE_NAME, UriMatch.PRODUCT_TABLE);
        sUriMatcher.addURI(AUTHORITY, InventoryTable.TABLE_NAME, UriMatch.INVENTORY_TABLE);
        sUriMatcher.addURI(AUTHORITY, OrderTable.TABLE_NAME, UriMatch.ORDER_TABLE);
        sUriMatcher.addURI(AUTHORITY, VisitorTable.TABLE_NAME, UriMatch.VISITOR_TABLE);
    }

    /**
     *
     * @param tag       -  Tag for logging purposes
     * @param uri       -  uri that determines which table object to return
     * @return          -  The right table object
     */
    public static TableBase getTable(String tag, Uri uri) {
        int match = sUriMatcher.match(uri);
        TableBase table = null;

        switch (match) {
            case UriMatch.OUTLET_TABLE: {
                table = new OutletTable(tag);
                break;
            }

            case UriMatch.CALLCYCLE_TABLE: {
                table = new CallCycleTable(tag);
                break;
            }

            case UriMatch.VISIT_TABLE: {
                table = new VisitTable(tag);
                break;
            }

            case UriMatch.ATTENDANCE_TABLE: {
                table = new AttendanceTable(tag);
                break;
            }

            case UriMatch.PRODUCT_TABLE: {
                table = new ProductTable(tag);
                break;
            }

            case UriMatch.INVENTORY_TABLE: {
                table = new InventoryTable(tag);
                break;
            }

            case UriMatch.ORDER_TABLE: {
                table = new OrderTable(tag);
                break;
            }

            case UriMatch.VISITOR_TABLE: {
                table = new VisitorTable(tag);
                break;
            }

            default: {
                throw new IllegalArgumentException("Table Not found for uri=" + uri);
            }
        }
        return table;
    }

    public static Uri getUriFromTableName(String table)
    {
        return Uri.parse("content://" + AUTHORITY + "/" + table + "/");
    }

    /**
     * Content Resolver's delete functionality with URI supplied by children
     * @param selection
     * @param selectionArgs
     * @return - Number of rows deleted
     */
    public int delete(SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs) {
        int count = 0 ;

        if (db == null) {
            throw new IllegalArgumentException("db cannot be null");
        }

        synchronized(db) {
            count = db.delete(getTableName(), selection, selectionArgs);
        }

        return count;
    }

    /**
     * Content Resolver's insert functionality with URI supplied by children
     * @param values - column values to be inserted
     * @return - Uri of the row just inserted
     */
    public long insert(SQLiteDatabase db, ContentValues values) {
        long result = -1;

        try {
            synchronized (db) {
                result = db.insertOrThrow(this.getTableName(), DbSyntax.SPACE, values);
            }

        } catch (Exception e) {
            Log.w(TAG, "Insert failed for "+ values.getAsString(OutletTable.Columns.CODE));
            e.printStackTrace();

        }

        return result;
    }

    /**
     * Content Resolver's query functionality with URI supplied by children
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @param projectionMap
     * @return - Cursor containing the result set.  Caller to manage the cursor
     */
    public Cursor query(SQLiteDatabase db, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder, HashMap<String, String> projectionMap) {

        if (db == null) {
            throw new IllegalArgumentException("db cannot be null");
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(getTableName());
        qb.setProjectionMap(projectionMap);
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null,sortOrder);

        return c;
    }

    /**
     *
     * @param db - database
     * @param query - raw query
     * @param cols - columns
     * @return - cursor
     */
    public Cursor rawQuery(SQLiteDatabase db, String query, String[] cols) {

        if (db == null) {
            throw new IllegalArgumentException("db cannot be null");
        }

        return  db.rawQuery(query, cols);
    }

    /**
     * Content Resolver's update functionality with URI supplied by children
     * @param values
     * @param selection
     * @param selectionArgs
     * @return - Number of rows updated
     */
    public int update(SQLiteDatabase db, Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        int count;

        if (db == null) {
            throw new IllegalArgumentException("db cannot be null");
        }

        synchronized(db) {
            count = db.update(getTableName(), values, selection, selectionArgs);
        }
        return count;
    }
}
