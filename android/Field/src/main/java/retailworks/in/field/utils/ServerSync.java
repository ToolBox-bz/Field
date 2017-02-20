package retailworks.in.field.utils;

/**
 * Created by Neiv on 10/17/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import retailworks.in.field.db.DbHelper;
import retailworks.in.field.db.DbSyntax;
import retailworks.in.field.db.TableBase;

import org.json.JSONObject;


/**
 * string[0] - Table operation - INSERT or UPDATE, etc
 * string[1] - sql query
 * string[2] - local table name
 * string[3] - no# tries if failed
 **/
 public class ServerSync
        extends AsyncTask<ContentValues, Integer, ContentValues>
    implements Constants, DbSyntax
{
    private Context ctx = null;

    ICallBack callBack;

    public ServerSync(Context ct, ICallBack cb) {

        this.ctx = ct;
        callBack = cb;
    }

    /**
     * string[0] - Table operation - INSERT or UPDATE, etc
     * string[1] - sql query
     * string[2] - local table name
     * string[3] - no# tries if failed
     *
     * @param values
     * @return
     */
    protected ContentValues doInBackground(ContentValues... values)
    {
        try {

            ContentValues cv = new ContentValues(values[0]);

            String qType = cv.getAsString(TYPE);
            String table = cv.getAsString(TABLE);

            cv.remove(TYPE);
            cv.remove(TABLE);
            cv.remove(TRIES);
            cv.remove(LOCAL);
            cv.remove(RESULT);

            String query = Utils.getSqlQueryFromCV(qType,cv,table);

            JSONObject json = new JSONObject();
            json.put(Constants.TYPE, qType);
            json.put(Constants.QUERY, query);
            json.put(Constants.COMPANY, COMPANY_NAME);

            String response = SimpleHttpClient.executeHttpPost(Constants.SYNC_URL, json);

            if (response.equals(Constants.SUCCESS)){
                Log.i(APP_TAG, qType + table + ": " + response);
                if(values[0].getAsBoolean(LOCAL)) {

                    if (DbHelper.getInstance(ctx).insert(TableBase.getUriFromTableName(table), cv) > 0)
                        Log.e(APP_TAG, "LOCAL DB " + qType + table + ": " + response);

                }
            } else {
                Log.e(APP_TAG, qType + table + ": " + response + " for " + query);
            }
            values[0].put(RESULT,response);
        }
        catch (Exception e) {
            Log.e(APP_TAG, e.getMessage());
        }

        return values[0];
    }

    protected void onPostExecute(ContentValues values) {

        super.onPostExecute(values);

        String result = values.getAsString(RESULT);
        if (result.equals(SUCCESS)){
            if(callBack != null) callBack.workDone(true);
        } else {

            // lets try again
            byte tries = values.getAsByte(TRIES);
            if(tries-- > 0) {
                values.put(TRIES,tries);
                new ServerSync(this.ctx, callBack).execute(values);
            }

            if(callBack != null) callBack.workDone(false);
        }
    }
}