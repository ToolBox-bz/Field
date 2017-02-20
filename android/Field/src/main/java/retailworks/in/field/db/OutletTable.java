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
 * The Outlet table is used here to hold a store info.
 *
 *<code><pre>
 * CLASS:
 * 	Extends TableBase which provides basic table inserts, deletes, etc.
 *
 * RESPONSIBILITIES:
 * 	Insert, delete, update, fetch  Rule Table records
 *  Converts cursor of OutletTable records to a OutletTuple.
 *
 * COLABORATORS:
 * 	None
 *
 * USAGE:
 * 	See each method.
 *</pre></code>
 */
public class OutletTable extends TableBase implements Constants, DbSyntax {

    /** This is the name of the table in the database */
    public static final String TABLE_NAME        = "Outlets";

    /** Currently not used, but could be for joining this table with other tables. */
    public static final String SQL_REF 	 			= " r";

    public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE_NAME+"/");

    public interface Columns {

        public static final String _ID              = "_id";
        public static final String CODE             = "outletcode";
        public static final String BEAT             = "beat";
        public static final String NAME             = "outletname";
        public static final String ADDRESS          = "address";
        public static final String CHAIN            = "chain";
        public static final String CLASSIFICATION   = "classification";
        public static final String GRADE            = "outletgrade";
        public static final String TYPE             = "outlettype";
        public static final String STATUS           = "status";
        public static final String WEEKOFF          = "weekoff";
        public static final String CITY             = "city";
        public static final String ZIP              = "zip";
        public static final String ZONE             = "zone";
        public static final String STATE            = "state";
        public static final String COUNTRY          = "country";
        public static final String CONTACT          = "contact";
        public static final String MAIL             = "mail";
        public static final String PHONE            = "phone";
        public static final String LATITUDE         = "latitude";
        public static final String LONGITUDE        = "longitude";

    }


    private static final String[] COLUMN_NAMES = {
            Columns._ID, Columns.CODE, Columns.NAME, Columns.TYPE, Columns.GRADE,
            Columns.GRADE, Columns.CLASSIFICATION, Columns.CHAIN, Columns.BEAT, Columns.ADDRESS,
            Columns.COUNTRY, Columns.ZONE, Columns.STATE,Columns.CITY,Columns.ZIP,
            Columns.STATE, Columns.STATUS, Columns.WEEKOFF, Columns.CONTACT,
            Columns.MAIL, Columns.PHONE, Columns.LATITUDE, Columns.LONGITUDE
    };

    public static String[] getColumnNames() {
        return Utils.copyOf(COLUMN_NAMES);
    }

    /** SQL statement to create the Table */
    public static final String CREATE_TABLE_SQL =
            CREATE_TABLE + TABLE_NAME + LP +
                    Columns._ID                     + PKEY_TYPE                             + CONT +
                    Columns.CODE                    + TEXT_TYPE + UNIQUE                    + CONT +
                    Columns.BEAT                    + TEXT_TYPE                             + CONT +
                    Columns.NAME                    + TEXT_TYPE                             + CONT +
                    Columns.TYPE                    + TEXT_TYPE                             + CONT +
                    Columns.GRADE                   + TEXT_TYPE                             + CONT +
                    Columns.CLASSIFICATION          + TEXT_TYPE                             + CONT +
                    Columns.CHAIN                   + TEXT_TYPE                             + CONT +
                    Columns.ADDRESS                 + TEXT_TYPE                             + CONT +
                    Columns.STATUS                  + TEXT_TYPE                             + CONT +
                    Columns.WEEKOFF                 + TEXT_TYPE                             + CONT +
                    Columns.CITY                    + TEXT_TYPE                             + CONT +
                    Columns.ZIP                     + INTEGER_TYPE                          + CONT +
                    Columns.STATE                   + TEXT_TYPE                             + CONT +
                    Columns.ZONE                    + TEXT_TYPE                             + CONT +
                    Columns.COUNTRY                 + TEXT_TYPE                             + CONT +
                    Columns.CONTACT                 + TEXT_TYPE                             + CONT +
                    Columns.MAIL                    + TEXT_TYPE                             + CONT +
                    Columns.PHONE                   + INTEGER_TYPE                          + CONT +
                    Columns.LATITUDE                + TEXT_TYPE                             + CONT +
                    Columns.LONGITUDE               + TEXT_TYPE                             + RP;

    /** Basic constructor */
    public OutletTable(String tag) {
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