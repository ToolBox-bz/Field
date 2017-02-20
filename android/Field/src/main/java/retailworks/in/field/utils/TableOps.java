package retailworks.in.field.utils;

/**
 * Created by Neiv on 10/17/2015.
 */


import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retailworks.in.field.db.DbHelper;
import retailworks.in.field.db.DbSyntax;
import retailworks.in.field.db.TableBase;

public class TableOps
        implements DbSyntax
{
    public static void insert(final Context ct, final String table, HashMap<String, String> map)
    {
        Boolean first = true;
        Iterator keyvalue = map.entrySet().iterator();
        String keys = LP;
        String values = LP;

        do {
            Map.Entry entry = (Map.Entry)keyvalue.next();

            // add comma if not the first entry
            if (!first){keys += CONT;values += CONT;}

            keys    += SQ + entry.getKey()      + SQ;
            values  +=  Q + entry.getValue()    + Q;

            // break if no more entries left, add RP in the end
            if (! keyvalue.hasNext()){
                keys += RP;values += RP;
                break;
            }
        } while (true);

        String query = INSERT + INTO + Constants.COMPANY_NAME + table + SPACE + keys + VALUES + values;
//        new ServerSync(ct).execute(new String[] { "INSERT ", query });

        new AsyncTask()
        {
            protected Object doInBackground(Object[] obj)
            {
                ContentValues cv = Utils.HashMapToContentValues((HashMap)obj[0]);
                DbHelper.getInstance(ct).insert(TableBase.getUriFromTableName(table), cv);
                return null;
            }
        }.execute(new Object[] { map });
    }

    public static void insertJsonArrayToLocalTable(Context ct, Uri paramUri, JSONArray paramJSONArray, String[] cols)
            throws JSONException
    {
        ContentValues cv = new ContentValues();
        int itemCount = 0;
        while (itemCount < paramJSONArray.length()) {

            JSONObject localJSONObject = paramJSONArray.getJSONObject(itemCount++);

            for (int objCount = 1; objCount < localJSONObject.length(); objCount++) {

                cv.put(cols[objCount], localJSONObject.getString(cols[objCount]));

            }
            DbHelper.getInstance(ct).insert(paramUri, cv);
        }

        return;
    }

    public static void update(final Context ct, final String table, HashMap<String, String> map)
    {
        String str = UPDATE + table + SPACE + SET +
                LP + Utils.removeBrackets(map.keySet().toString()) + RP +
                VALUES +
                LP + Utils.removeBrackets(map.values().toString()) + RP;

  //      new ServerSync(paramContext).execute(new String[] { "INSERT ", str });

        new AsyncTask()
        {
            protected Object doInBackground(Object[] obj)
            {
                ContentValues cv = Utils.HashMapToContentValues((HashMap)obj[0]);
                DbHelper.getInstance(ct).insert(TableBase.getUriFromTableName(table), cv);
                return null;
            }
        }.execute(new Object[] { map });
    }
}

