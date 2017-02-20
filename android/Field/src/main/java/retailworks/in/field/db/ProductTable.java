package retailworks.in.field.db;

/**
 * Created by Neiv on 10/16/2015.
 */

import android.net.Uri;
import retailworks.in.field.utils.Constants;
import retailworks.in.field.utils.Utils;

public class ProductTable
        extends TableBase
        implements Constants, DbSyntax
{
    public static final String TABLE_NAME        = "Products";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME + "/");

    public static abstract interface Columns
    {
        public static final String CODE             = "productcode";
        public static final String DESCRIPTION      = "description";
        public static final String NAME             = "productname";
        public static final String PIC_PATH         = "picpath";
        public static final String TYPE             = "producttype";
        public static final String _ID              = "_id";
    }

    private static final String[] COLUMN_NAMES = {
            Columns._ID, Columns.CODE, Columns.NAME, Columns.PIC_PATH, Columns.DESCRIPTION, Columns.TYPE};

    public static final String CREATE_TABLE_SQL =
            CREATE_TABLE + TABLE_NAME + LP +
                    Columns._ID         + PKEY_TYPE             + CONT +
                    Columns.CODE        + TEXT_TYPE + UNIQUE    + CONT +
                    Columns.NAME        + TEXT_TYPE             + CONT +
                    Columns.TYPE        + TEXT_TYPE             + CONT +
                    Columns.PIC_PATH    + TEXT_TYPE             + CONT +
                    Columns.DESCRIPTION + TEXT_TYPE             + RP;

    public ProductTable(String paramString) {
        super(paramString);
    }

    public static String[] getColumnNames() {
        return Utils.copyOf(COLUMN_NAMES);
    }

    public Uri getContentUri() {
        return CONTENT_URI;
    }

    public String getTableName() {
        return TABLE_NAME;
    }

    public Uri getTableUri(){
        return CONTENT_URI;
    }

}