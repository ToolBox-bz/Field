package retailworks.in.field.db;


/**
 * Created by Neiv on 10/16/2015.
 */
import android.net.Uri;
import retailworks.in.field.utils.Constants;

public class InventoryTable 
        extends TableBase
        implements Constants, DbSyntax
{
    public static final String TABLE_NAME        = "Inventory";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME + "/");

    public static abstract interface Columns
    {
        public static final String DATE         = "inventorydate";
        public static final String EMPCODE      = "empcode";
        public static final String CODE = "outletcode";
        public static final String VISIT_ID     = "visit_id";
        public static final String _ID          = "_id";
    }

    public static final String CREATE_TABLE_SQL =
            CREATE_TABLE + TABLE_NAME + LP +
                    Columns._ID                 + PKEY_TYPE                 + CONT +
                    Columns.EMPCODE             + TEXT_TYPE                 + CONT +
                    Columns.CODE + TEXT_TYPE                 + CONT +
                    Columns.VISIT_ID            + INTEGER_TYPE + NOT_NULL   + CONT +
                    Columns.DATE                + CURRENT_DATETIME          + RP;

    public InventoryTable(String paramString) {
        super(paramString);
    }

    public Uri getContentUri() {
        return CONTENT_URI;
    }

    public String getTableName() {
        return TABLE_NAME;
    }

    public Uri getTableUri() {
        return CONTENT_URI;
    }
}

