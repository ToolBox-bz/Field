package retailworks.in.field.utils;

/**
 * Created by Neiv on 10/16/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import retailworks.in.field.R;
import retailworks.in.field.app.LoginActivity;
import retailworks.in.field.db.DbSyntax;

public class Utils implements Constants {

    private static final String LOG_TAG = APP_TAG + Utils.class.getSimpleName();

    /** Setter - set the shared preference passed in (sharedPrefValue) to the value passed in.
     *
     * @param context - Context
     * @param sharedPrefString - shared preference string for which the value is to be set
     * @param state - true or false
     */
    public static void setSharedPrefStateValue(final Context context, final String sharedPrefString, boolean state) {
        SharedPreferences sp = context.getSharedPreferences(APP_TAG, Context.MODE_PRIVATE);
        sp.edit().putBoolean(sharedPrefString, state).apply();
        if(LOG_DEBUG) Log.d(LOG_TAG , "Setting:"+sharedPrefString+" to "+state);
    }

    /** Getter - returns if the preference passed in (sharedPrefValue) is set to true or false.
     *
     * @param context - Context
     * @param sharedPrefString - shared preference string for which the value is to be retrieved
     * @return - true or false.
     */
    public static boolean getSharedPrefStateValue(final Context context, final String sharedPrefString) {
        SharedPreferences sp = context.getSharedPreferences(APP_TAG, Context.MODE_PRIVATE);
        boolean sharedPrefValue = sp.getBoolean(sharedPrefString, false);
        if(LOG_DEBUG) Log.d(LOG_TAG, "Fetching: "+sharedPrefString+" = "+sharedPrefValue);
        return sharedPrefValue;
    }

    /** Setter - set the shared preference passed in (sharedPrefValue) to the value passed in.
     *
     * @param context - Context
     * @param sharedPrefString - shared preference string for which the value is to be set
     * @param val - true or false
     */
    public static void setSharedPrefStringValue(final Context context, final String sharedPrefString, String val) {
        SharedPreferences sp = context.getSharedPreferences(APP_TAG, Context.MODE_PRIVATE);
        sp.edit().putString(sharedPrefString, val).apply();
        if(LOG_DEBUG) Log.d(LOG_TAG , "Setting:"+sharedPrefString+" to "+val);
    }

    /** Getter - returns if the preference passed in (sharedPrefValue) is set to true or false.
     *
     * @param context - Context
     * @param sharedPrefString - shared preference string for which the value is to be retrieved
     * @return - true or false.
     */
    public static String getSharedPrefStringValue(final Context context, final String sharedPrefString) {
        SharedPreferences sp = context.getSharedPreferences(APP_TAG, Context.MODE_PRIVATE);
        String sharedPrefValue = sp.getString(sharedPrefString, null);
        if(LOG_DEBUG) Log.d(LOG_TAG, "Fetching: "+sharedPrefString+" = "+sharedPrefValue);
        return sharedPrefValue;
    }

    /** makes copy of a String array
     *
     * @param original - original String array
     * @return
     */
    public static final String[] copyOf(final String[] original) {

        if (original == null) throw new IllegalArgumentException("original array cannot be null");

        String[] result = new String[original.length];
        for (int i = 0; i < original.length; i++) {
            result[i] = new String(original[i].getBytes());
        }
        return result;
    }


    /**
     * converts the HashMap values into ContenValues
     *
     * @param map
     * @return content values
     */
    public static ContentValues HashMapToContentValues(HashMap<String, String> map)
    {
        ContentValues localContentValues = new ContentValues();
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry)iter.next();
            localContentValues.put((String)entry.getKey(), (String)entry.getValue());
        }
        return localContentValues;
    }


    static long days(Date date1, Date date2)
    {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        int i = c1.get(Calendar.DAY_OF_WEEK);
        c1.add(Calendar.DAY_OF_WEEK, -i);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        int k = c1.get(Calendar.DAY_OF_WEEK);
        c1.add(Calendar.DAY_OF_WEEK, -k);
        long l1 = (c1.getTimeInMillis() - c1.getTimeInMillis()) / 86400000L;
        long l2 = 2L * l1 / 7L;
        if (i == 1) {
            i = 2;
        }
        for (;;)
        {
            int j = k;
            if (k == 1) {
                j = 2;
            }
            return l1 - l2 - i + j;
            //if (k != 1) {
              //  i = 6;
            //}
        }
    }


    public static Bitmap fetchBitmapFromStorage(Context ct, String name) {

        File dir = new ContextWrapper(ct).getDir("imageDir", 0);
        File list = dir.getAbsoluteFile();
        String path = new File(dir, name + ".png").getAbsolutePath();
        return BitmapFactory.decodeFile(path);
    }

    public static String getEmpCode(Context paramContext)
    {
        return getSharedPrefStringValue(paramContext, Constants.EMPCODE);
    }

    public static String getPassword(Context paramContext)
    {
        return getSharedPrefStringValue(paramContext, Constants.PASSWORD);
    }


    public static String getUserName(Context paramContext)
    {
        return getSharedPrefStringValue(paramContext, Constants.USERNAME);
    }


    public static void setEmpCode(Context paramContext, String paramString)
    {
        setSharedPrefStringValue(paramContext, Constants.EMPCODE, paramString);
    }

    public static void setPassword(Context paramContext, String paramString)
    {
        setSharedPrefStringValue(paramContext, Constants.PASSWORD, paramString);
    }

    public static void setUserName(Context paramContext, String paramString)
    {
        setSharedPrefStringValue(paramContext, Constants.USERNAME, paramString);
    }

    public static String removeBrackets(String paramString)
    {
        return paramString.replace("[", "").replace("]", "");
    }

    public static String saveToInternalSorage(Context ct, Bitmap bitmapImage, String name){

        ContextWrapper cw = new ContextWrapper(ct);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,name+".png");

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    public static boolean  saveBitmapToInternalStorage(Context ct, Bitmap bitmap, String picName) {
        FileOutputStream fos;
        try {

            //File dir = new ContextWrapper(ct).getDir("imageDir", 0) + "/" + ;

            fos = ct.openFileOutput(picName + ".png", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            return true;
        }
        catch (FileNotFoundException e) {
            Log.d(Constants.APP_TAG, "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d(Constants.APP_TAG, "io exception");
            e.printStackTrace();
        }

        return false;
    }

    public static Bitmap loadBitmap(Context context, String picName){
        Bitmap b = null;
        FileInputStream fis;
        try {
            fis = context.openFileInput(picName);
            b = BitmapFactory.decodeStream(fis);
            fis.close();

        }
        catch (FileNotFoundException e) {
            Log.d(Constants.APP_TAG, "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d(Constants.APP_TAG, "io exception");
            e.printStackTrace();
        }
        return b;
    }


    public static String[] getCVasStrings(ContentValues cv)
    {
        if(cv == null || cv.size() == 0) return null;

        String[] strings = new String[]{"",""};
        Iterator iter = cv.keySet().iterator();
        do {
            String str = (String)iter.next();

            strings[0] += DbSyntax.SQ + str + DbSyntax.SQ;
            strings[1] += DbSyntax.Q + cv.getAsString(str) + DbSyntax.Q;

            if (iter.hasNext()) {
                strings[0] +=  DbSyntax.CS;
                strings[1] +=  DbSyntax.CS;
            } else break;

        } while (true);

        return strings;
    }

    public static String getSqlQueryFromCV(String what, ContentValues cv, String table){

        String where = null;
        String[] keyvalues = getCVasStrings(cv);

        if(what.equals(DbSyntax.INSERT)) {
            where = DbSyntax.INSERT + DbSyntax.INTO + table +
                    DbSyntax.LP + keyvalues[0] + DbSyntax.RP +
                    DbSyntax.VALUES +
                    DbSyntax.LP + keyvalues[1] + DbSyntax.RP;
        }

        return where;
    }

    /**
     * Sends req to update the server and then updates local db if asked on successful response
     *
     * @param ctx - contect
     * @param type - table opn type - insert, update, etc
     * @param table - table name
     * @param values - content values
     * @param local - whether to update local db or not
     */
    public static void sendRemoteSyncReq(Context ctx, String type, String table,
                                         ContentValues values, ICallBack cb, boolean local){

        ContentValues cv = new ContentValues(values);

        cv.put(TYPE, type);
        cv.put(TABLE, table);
        cv.put(LOCAL, local);
        cv.put(TRIES,3);

        new ServerSync(ctx, cb).execute(cv);
    }

    public static void killActivityDialog(final Activity act, final int msg, View root)
    {
        if(root != null) {
            Snackbar.make(root, msg, Snackbar.LENGTH_LONG)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(msg == R.string.logout_msg) {
                                Intent intent = new Intent("CLOSE_ALL");
                                act.sendBroadcast(intent);
                                act.startActivity(new Intent(act, LoginActivity.class));
                            }
                            act.finish();
                        }
                    }).show();
            return;
        }

        AlertDialog.Builder localObject = new AlertDialog.Builder(act);
        View localView = act.getLayoutInflater().inflate(R.layout.simple_dialog, null);
        localObject.setView(localView);

        final AlertDialog dialog = localObject.create();
        ((TextView) localView.findViewById(R.id.dialog_text)).setText(msg);
        localView.findViewById(R.id.dialog_ok).setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                act.setResult(0);
                act.finish();
            }
        });
        localView.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }


    public static Snackbar showSnackBar(final Activity act, int msg, View root) {

        if(root != null) {
            Snackbar sb = Snackbar.make(root, msg, Snackbar.LENGTH_SHORT);

            if(act != null) {
                sb.setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        act.finish();
                    }
                });
            }

            sb.show();

            return sb;
        }
        return null;
    }


    public static void showSnackBar(final Activity act, String msg, View root) {

        if(root != null) {
            final Snackbar sb = Snackbar.make(root, msg, Snackbar.LENGTH_INDEFINITE);

            sb.setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(act != null)act.finish();
                        else sb.dismiss();

                    }
                });

            sb.show();

            return;
        }
    }

    public static Bitmap rotateBitmap(Bitmap bm){

        Matrix mtx = new Matrix();
        mtx.setRotate(90);

        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(),mtx,true);
    }

    public static String getMonthString(Context ct, int month){

        switch(month){
            default:
            case 1: return ct.getString(R.string.jan);
            case 2: return ct.getString(R.string.feb);
            case 3: return ct.getString(R.string.march);
            case 4: return ct.getString(R.string.april);
            case 5: return ct.getString(R.string.may);
            case 6: return ct.getString(R.string.june);
            case 7: return ct.getString(R.string.july);
            case 8: return ct.getString(R.string.aug);
            case 9: return ct.getString(R.string.sept);
            case 10: return ct.getString(R.string.oct);
            case 11: return ct.getString(R.string.nov);
            case 12: return ct.getString(R.string.dec);
        }
    }

    public static int getWorkingDaysBetweenTwoDates(Date startDate, Date endDate) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        int workDays = 0;

        //Return 0 if start and end are the same
        if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
            return 0;
        }

        if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            startCal.setTime(endDate);
            endCal.setTime(startDate);
        }

        do {
            if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                ++workDays;
            }
            startCal.add(Calendar.DAY_OF_MONTH, 1);
        } while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); //excluding end date

        return workDays;
    }

    public static boolean isEmpty(String str){
        boolean result = true;

        if(str == null || str.length() == 0 || str.equals(""))
            return result;
        else if(str.length() > 0)
            result = false  ;

        return result;
    }
}

