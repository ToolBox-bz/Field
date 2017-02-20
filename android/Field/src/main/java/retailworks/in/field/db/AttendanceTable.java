package retailworks.in.field.db;

/**
 * Created by Neiv on 10/16/2015.
 */
import android.net.Uri;
import retailworks.in.field.utils.Constants;
import retailworks.in.field.utils.Utils;

public class AttendanceTable
        extends TableBase
        implements Constants, DbSyntax
{

    public static final String TABLE_NAME        = "Attendance";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME + "/");

    public static abstract interface ATTENDANCE
    {
        public static final String ABSENT       = "Absent";
        public static final String AWOL         = "AWOL";
        public static final String MEETING      = "Meeting";
        public static final String ON_FIELD     = "OnField";
        public static final String ADMIN        = "Admin";
        public static final String TRAVEL       = "Travel";
    }

    public static abstract interface Columns
    {
        public static final String DATE         = "attendancedate";
        public static final String TIME         = "attendancetime";
        public static final String TYPE         = "attendancetype";
        public static final String EMPCODE      = "empcode";
        public static final String _ID          = "_id";
    }
    private static final String[] COLUMN_NAMES = { Columns._ID, Columns.DATE, Columns.TIME, Columns.TYPE };

    public static final String CREATE_TABLE_SQL = CREATE_TABLE + TABLE_NAME + LP +
                    Columns._ID         + PKEY_TYPE         + CONT +
                    Columns.DATE        + LONG_TYPE         + CONT +
                    Columns.TIME        + CURRENT_DATETIME  + CONT +
                    Columns.EMPCODE     + TEXT_TYPE         + CONT +
                    Columns.TYPE        + TEXT_TYPE         + RP;

    public AttendanceTable(String paramString) {
        super(paramString);
    }

    public static String[] getColumnNames() {
        return Utils.copyOf(COLUMN_NAMES);
    }

    public Uri getContentUri() {
        return CONTENT_URI;
    }

    public String getTableName() {
        return AttendanceTable.TABLE_NAME;
    }

    public Uri getTableUri() {
        return CONTENT_URI;
    }

}
