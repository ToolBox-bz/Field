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


/** This class allows access and updates to the Outlet table.
 * Basically, it abstracts the Outlet tuple instance.
 *
 * The Call Cycle table is used here to hold a Call Cycle info.
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
public class CallCycleTable extends TableBase implements Constants, DbSyntax {

    /** This is the name of the table in the database */
    public static final String TABLE_NAME        = "Callcycle";

    /** Currently not used, but could be for joining this table with other tables. */
    public static final String SQL_REF 	 			= " r";

    public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE_NAME+"/");

    public interface Columns {

        public static final String _ID          = "_id";

        /** duration for the Call Cycle in no# of days */
        public static final String DURATION     = "duration";

        /** beat for the Call Cycle */
        public static final String BEAT         = "beat";

        /** beat for the Call Cycle */
        public static final String DAY          = "day";

        /** Call Cycle start date */
        public static final String START_DATE   = "startdate";

        /** beat for the Call Cycle */
        public static final String EMP_CODE     = "empcode";

    }

    private static final String[] COLUMN_NAMES = {
            Columns._ID, Columns.EMP_CODE, Columns.DURATION, Columns.BEAT, Columns.DAY,
            Columns.START_DATE
    };

    public static String[] getColumnNames() {
        return Utils.copyOf(COLUMN_NAMES);
    }

    /** SQL statement to create the Table */
    public static final String CREATE_TABLE_SQL =
            CREATE_TABLE +
                    TABLE_NAME + " (" +
                    Columns._ID                     + PKEY_TYPE                             + CONT +
                    Columns.EMP_CODE                + INTEGER_TYPE                          + CONT +
                    Columns.DURATION                + INTEGER_TYPE                          + CONT +
                    Columns.BEAT                    + TEXT_TYPE                             + CONT +
                    Columns.DAY                     + INTEGER_TYPE                          + CONT +
                    Columns.START_DATE              + DATE_TIME_TYPE                        +
                    ")";

    /** Basic constructor */
    public CallCycleTable(String tag) {
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