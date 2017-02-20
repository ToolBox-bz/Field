/*
 * @(#)OutletTable.java
 *
 * (c) COPYRIGHT 2010-2012 TOOLBOX INC.
 * TOOLBOX CONFIDENTIAL PROPRIETARY
 *
 * REVISION HISTORY:
 * Author        Date       CR Number         Brief Description
 * ------------- ---------- ----------------- ------------------------------
 * Rohit        2015/08/16  NA                Initial version
 *
 */
package retailworks.in.field.db;

import android.net.Uri;

import retailworks.in.field.utils.Constants;
import retailworks.in.field.utils.Utils;


/** This class allows access and updates to the Visit table.
 * Basically, it abstracts the Visit tuple instance.
 *
 * The Visit table is used here to hold a Visit info.
 *
 *<code><pre>
 * CLASS:
 * 	Extends TableBase which provides basic table inserts, deletes, etc.
 *
 * RESPONSIBILITIES:
 * 	Insert, delete, update, fetch  Rule Table records
 *  Converts cursor of Call CycleTable records to a Call CycleTuple.
 *
 * COLABORATORS:
 * 	None
 *
 * USAGE:
 * 	See each method.
 *</pre></code>
 */
public class VisitTable extends TableBase implements Constants, DbSyntax {

    /** This is the name of the table in the database */
    public static final String TABLE_NAME        = "Visits";

    /** Currently not used, but could be for joining this table with other tables. */
    public static final String SQL_REF 	 			= " r";

    public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE_NAME+"/");

    public interface VisitStatus {
        public static String UNVISITED = "Unvisited";
        public static String OPEN      = "Open";
        public static String CLOSED    = "Closed";
        public static String NOT_FOUND = "Not Found";
        public static String REJECTED  = "Rejected";
    }

    public interface Columns {

        public static final String _ID              = "_id";

        /** emp code who visited */
        public static final String EMPCODE          = "empcode";

        /** outlet code */
        public static final String CODE             = "outletcode";

        /** date of visit */
        public static final String DATE             = "visitdate";

        /** call start time */
        public static final String START_TIME       = "starttime";

        /** call end time */
        public static final String END_TIME         = "endtime";

        /** visit type */
        public static final String TYPE             = "visittype";

        /** outlet status */
        public static final String STATUS           = "outletstatus";

        public static final String CYCLE            = "cycle";

        public static final String NAME             = "outletname";

        public static final String ADDRESS          = "outletaddress";

        public static final String INVENTORY_ID     = "inventory_id";

        public static final String ORDERS_ID        = "orders_id";

        public static final String LATITUDE         = "latitude";

        public static final String LONGITUDE        = "longitude";

    }

    private static final String[] COLUMN_NAMES = {
            Columns._ID, Columns.EMPCODE, Columns.CODE, Columns.TYPE, Columns.DATE, Columns.START_TIME,
            Columns.END_TIME,Columns.TYPE,Columns.STATUS, Columns.CYCLE, Columns.NAME, Columns.ADDRESS,
            Columns.INVENTORY_ID, Columns.ORDERS_ID, Columns.LATITUDE, Columns.LONGITUDE
    };

    public static String[] getColumnNames() {
        return Utils.copyOf(COLUMN_NAMES);
    }

    /** SQL statement to create the Table */
    public static final String CREATE_TABLE_SQL =

            CREATE_TABLE + TABLE_NAME + LP +

                    Columns._ID                     + PKEY_TYPE                             + CONT +
                    Columns.EMPCODE                 + INTEGER_TYPE                          + CONT +
                    Columns.CODE                    + TEXT_TYPE                             + CONT +
                    Columns.NAME                    + TEXT_TYPE                             + CONT +
                    Columns.ADDRESS                 + TEXT_TYPE                             + CONT +
                    Columns.TYPE                    + INTEGER_TYPE                          + CONT +
                    Columns.STATUS                  + TEXT_TYPE                             + CONT +
                    Columns.DATE                    + DATE_TYPE                             + CONT +
                    Columns.CYCLE                   + INTEGER_TYPE                          + CONT +
                    Columns.START_TIME              + TIME_TYPE                             + CONT +
                    Columns.END_TIME                + TIME_TYPE                             + CONT +
                    Columns.INVENTORY_ID            + INTEGER_TYPE                          + CONT +
                    Columns.ORDERS_ID               + INTEGER_TYPE                          + CONT +
                    Columns.LATITUDE                + TEXT_TYPE                             + CONT +
                    Columns.LONGITUDE               + TEXT_TYPE                             + RP;

    /** Basic constructor */
    public VisitTable(String tag) {
        super(tag);
    }

    /** Get the table name for this table. */
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    /** required by TableBase */
    public Uri getTableUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getContentUri() {return CONTENT_URI;}
}