package retailworks.in.field.app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retailworks.in.field.R;
import retailworks.in.field.db.CallCycleTable;
import retailworks.in.field.db.DbHelper;
import retailworks.in.field.db.DbSyntax;
import retailworks.in.field.db.InventoryTable;
import retailworks.in.field.db.OrderTable;
import retailworks.in.field.db.OutletTable;
import retailworks.in.field.db.ProductTable;
import retailworks.in.field.db.VisitTable;
import retailworks.in.field.utils.Constants;
import retailworks.in.field.utils.SimpleHttpClient;
import retailworks.in.field.utils.TableOps;
import retailworks.in.field.utils.Utils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOGC = Constants.APP_TAG + LoginActivity.class.getSimpleName();

    private CheckBox cb = null;
    private String empcode = null;
    private TextView pword = null;
    private TextView status = null;
    private TextView uname = null;

    static abstract interface LoginStatus {

        public static final int BAD_CONNECTION  = 0;
        public static final int CONNECTING      = 10;
        public static final int CONNECTED       = 20;
        public static final int VERIFYING       = 30;
        public static final int SUCCESS         = 40;
        public static final int LOADING_CAllS   = 50;
        public static final int LOADING_OUTLETS = 60;
        public static final int LOADING_VISITS  = 70;
        public static final int LOADING_PRODUCTS= 80;
        public static final int LOADING_IMAGES  = 90;
        public static final int BAD_CREDENTIALS = 100;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ((Button)findViewById(R.id.loginButton)).setOnClickListener(this);
        this.uname = ((TextView)findViewById(R.id.username));
        this.pword = ((TextView)findViewById(R.id.password));
        this.status = ((TextView)findViewById(R.id.loginstatus));
        this.cb = ((CheckBox)findViewById(R.id.user_checkbox));
        this.cb.setChecked(Utils.getSharedPrefStateValue(this, Constants.CHECKBOX));
        if (this.cb.isChecked()) {
            this.uname.setText(Utils.getSharedPrefStringValue(this, Constants.EMPCODE));
            this.pword.setText(Utils.getSharedPrefStringValue(this, Constants.PASSWORD));
        }
        
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
  //      setSupportActionBar(toolbar);

    }

    public boolean onCreateOptionsMenu(Menu paramMenu)
    {
        getMenuInflater().inflate(R.menu.login_menu, paramMenu);
        return super.onCreateOptionsMenu(paramMenu);
    }

    public boolean onOptionsItemSelected(MenuItem paramMenuItem)
    {
        paramMenuItem.getItemId();
        return super.onOptionsItemSelected(paramMenuItem);
    }

    public void onClick(View paramView)
    {
        String name     = this.uname.getText().toString();
        String password = this.pword.getText().toString();

        if(name.equals(Utils.getEmpCode(this)) &&
                password.equals(Utils.getPassword(this))) {
            showHomeActivity();
        } else {
            new UserLoginTask().execute(new String[]{password, name});
        }
    }


    private void showHomeActivity(){

        Intent in = new Intent(LoginActivity.this, HomeActivity.class);
        in.putExtra(Constants.TITLE, Utils.getUserName(LoginActivity.this));

        startActivity(in);

        finish();
    }

    private class UserLoginTask
            extends AsyncTask<String, Integer, String> {

        private ProgressBar mProgress;

        private UserLoginTask() {}

        protected void onPreExecute()
        {
            super.onPreExecute();

            this.mProgress = ((ProgressBar)LoginActivity.this.findViewById(R.id.login_progress));
            this.mProgress.setVisibility(View.VISIBLE);

            empcode = LoginActivity.this.uname.getText().toString();
            LoginActivity.this.status.setText(R.string.connecting);
        }

        protected void onProgressUpdate(Integer... paramVarArgs)
        {
            int progress = paramVarArgs[0].intValue();
            this.mProgress.setProgress(progress);

            switch (progress)
            {
                default:
                    // do nothing
                break;

                case LoginStatus.CONNECTING:
                    LoginActivity.this.status.setText(R.string.connecting);
                    break;
                case LoginStatus.CONNECTED:
                    LoginActivity.this.status.setText(R.string.connected);
                    break;
                case LoginStatus.VERIFYING:
                    LoginActivity.this.status.setText(R.string.verifying);
                    break;
                case LoginStatus.SUCCESS:
                    LoginActivity.this.status.setText(R.string.success);
                    showHomeActivity();
                    break;
                case LoginStatus.LOADING_CAllS:
                    LoginActivity.this.status.setText(R.string.loading_calls);
                    break;
                case LoginStatus.LOADING_OUTLETS:
                    LoginActivity.this.status.setText(R.string.loading_outlets);
                    break;
                case LoginStatus.LOADING_VISITS:
                    LoginActivity.this.status.setText(R.string.loading_visits);
                    break;
                case LoginStatus.LOADING_PRODUCTS:
                    LoginActivity.this.status.setText(R.string.loading_products);
                    break;
                case LoginStatus.LOADING_IMAGES:
                    LoginActivity.this.status.setText(R.string.loading_images);
                    break;
                case LoginStatus.BAD_CREDENTIALS:
                    LoginActivity.this.status.setText(R.string.bad_credentials);
                    break;
            }

            Log.i(LOGC, status.getText().toString());
        }

        @Override
        protected String doInBackground(String... params) {
            String result = Constants.FAIL;
            boolean refresh = false;

            refresh = ! Utils.getSharedPrefStateValue(LoginActivity.this, Constants.REFRESH);

            String response = null;
            try {
                JSONObject json = new JSONObject();

                json.put(Constants.PASSWORD, params[0]);
                json.put(Constants.EMPCODE,  params[1]);
                json.put(Constants.REFRESH, refresh);

                publishProgress(LoginStatus.CONNECTING);

                response = SimpleHttpClient.executeHttpPost(Constants.LOGIN_URL, json);

                if (response == null) {
                    publishProgress(LoginStatus.BAD_CONNECTION);
                    return result;
                }

                publishProgress(LoginStatus.CONNECTED);

                result = processHttpResponse(response);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.w(LOGC, e.getMessage());
            } catch (Exception e) {
                result = e.getLocalizedMessage();
                Log.w(LOGC, e.getMessage());
            }
            return result;
        }


        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result == null) {
                LoginActivity.this.status.setText(R.string.not_connected);
                mProgress.setProgress(00);
                return;
            }

            if (result.equals(Constants.SUCCESS)) {

                //showHomeActivity();
                return;
            }
            if (result.equals(Constants.FAIL)) {
                LoginActivity.this.status.setText(R.string.bad_credentials);
                mProgress.setProgress(100);
                return;
            } else { // we should never get here
                mProgress.setProgress(0);
                LoginActivity.this.status.setText(result);
            }
        }

        private void saveLoginData(String name){

            Utils.setUserName(LoginActivity.this, name);

            if (LoginActivity.this.cb.isChecked()) {
                Utils.setEmpCode(LoginActivity.this, LoginActivity.this.uname.getText().toString());
                Utils.setPassword(LoginActivity.this, LoginActivity.this.pword.getText().toString());
                Utils.setSharedPrefStateValue(LoginActivity.this, Constants.CHECKBOX, true);
            } else {
                Utils.setEmpCode(LoginActivity.this, null);
                Utils.setPassword(LoginActivity.this, null);
            }
        }

        private String processHttpResponse(String response) throws JSONException {
            String result = Constants.FAIL;

            publishProgress(LoginStatus.VERIFYING);

            JSONObject responseJsonObject = new JSONObject(response);
            JSONObject  resultJsonObj = (JSONObject) responseJsonObject.getJSONArray("response").get(0);

            if (resultJsonObj.has(Constants.RESULT))
                result = resultJsonObj.get(Constants.RESULT).toString();

            if(result.equals(Constants.FAIL)){
                publishProgress(LoginStatus.BAD_CREDENTIALS);
                Log.w(Constants.APP_TAG, response);
            }
            else if(result.equals(Constants.SUCCESS)){
                result = Constants.SUCCESS;
                publishProgress(LoginStatus.SUCCESS);
                String username = resultJsonObj.get(Constants.USERNAME).toString();
                saveLoginData(username);

                if (responseJsonObject.length() > 1) {
                    publishProgress(LoginStatus.LOADING_CAllS);
                    updateLocalTables(responseJsonObject);
                }
            }

            return result;
        }

        private String updateLocalTables(JSONObject json) throws JSONException {
            String result = Constants.SUCCESS;

            if (json.has(OutletTable.TABLE_NAME))
            {
                JSONArray pjp = json.getJSONArray(OutletTable.TABLE_NAME);
                DbHelper.getInstance(LoginActivity.this).delete(OutletTable.CONTENT_URI, null, null);

                String[] cols = OutletTable.getColumnNames();
                TableOps.insertJsonArrayToLocalTable(LoginActivity.this, OutletTable.CONTENT_URI, pjp, cols);
            }

            publishProgress(LoginStatus.LOADING_OUTLETS);

            if (json.has(CallCycleTable.TABLE_NAME))
            {
                JSONArray pjp = json.getJSONArray(CallCycleTable.TABLE_NAME);
                DbHelper.getInstance(LoginActivity.this).delete(CallCycleTable.CONTENT_URI, null, null);

                String[] cols = CallCycleTable.getColumnNames();
                TableOps.insertJsonArrayToLocalTable(LoginActivity.this, CallCycleTable.CONTENT_URI, pjp, cols);


                updateVisitTable();
            }

            publishProgress(LoginStatus.LOADING_VISITS);
            if (json.has(ProductTable.TABLE_NAME))
            {
                JSONArray pjp = json.getJSONArray(ProductTable.TABLE_NAME);
                DbHelper.getInstance(LoginActivity.this).delete(ProductTable.CONTENT_URI, null, null);

                String[] cols = ProductTable.getColumnNames();
                TableOps.insertJsonArrayToLocalTable(LoginActivity.this, ProductTable.CONTENT_URI, pjp, cols);

                publishProgress(LoginStatus.LOADING_PRODUCTS);
                addProductsToTable();
                publishProgress(LoginStatus.LOADING_IMAGES);
                downloadProductImages();
            }
            publishProgress(95);
            Utils.setSharedPrefStateValue(LoginActivity.this, Constants.REFRESH, true);
            return result;
        }

        private void addProductsToTable()
        {
            String[] cols = new String[] { ProductTable.Columns.CODE };
            Cursor proCursor = DbHelper.getInstance(LoginActivity.this).query(ProductTable.CONTENT_URI, cols, null, null, null);
            if ((proCursor != null) && (proCursor.moveToFirst())) {

                do {
                    String ordersSql    = DbSyntax.ALTER_TABLE + OrderTable.TABLE_NAME + DbSyntax.ADD;
                    String inventorySql = DbSyntax.ALTER_TABLE + InventoryTable.TABLE_NAME + DbSyntax.ADD;

                    String code = proCursor.getString(proCursor.getColumnIndexOrThrow(ProductTable.Columns.CODE));

                    ordersSql    += code + DbSyntax.INTEGER_TYPE + DbSyntax.DEFAULT_0;
                    inventorySql += code + DbSyntax.INTEGER_TYPE + DbSyntax.DEFAULT_0;

                    DbHelper.getInstance(LoginActivity.this).execSQL(ordersSql);
                    DbHelper.getInstance(LoginActivity.this).execSQL(inventorySql);


                    if (!proCursor.moveToNext()) {
                        break;
                    }

                } while(true);

            }
        }

        private void downloadProductImages()
        {
            int progress = 90;
            String[] cols = new String[] { ProductTable.Columns.CODE, ProductTable.Columns.PIC_PATH };
            Cursor localCursor = DbHelper.getInstance(LoginActivity.this).query(ProductTable.CONTENT_URI, cols, null, null, null);
            if ((localCursor != null) && (localCursor.moveToFirst())) {
                do {
                    String code = localCursor.getString(localCursor.getColumnIndexOrThrow(ProductTable.Columns.CODE));
                    String path = localCursor.getString(localCursor.getColumnIndexOrThrow(ProductTable.Columns.PIC_PATH));

                    path = Constants.BASE_URL + "/" + Constants.COMPANY_NAME + "/" + code + ".png";

                    // download and save product icon bitmap as productcode.png
                    Bitmap bm = downloadBitmap(path);
                    Utils.saveToInternalSorage(LoginActivity.this, bm, code);
               //     publishProgress(progress++);
                } while (localCursor.moveToNext());
            }
            Log.i(LOGC, "Product Images loaded!");
        }

        private Bitmap downloadBitmap(String url) {

            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet localHttpGet = new HttpGet(url);
            try
            {
                HttpResponse resp = client.execute(localHttpGet);
                int i = resp.getStatusLine().getStatusCode();
                if (i != 200)
                {
                    Log.w("ImageDownloader", "Error " + i + " while retrieving bitmap from " + url);
                    return null;
                }

                HttpEntity entity = resp.getEntity();
                if (entity == null) {
                    client = null;
                    return null;
                }
                InputStream localInputStream = entity.getContent();
                return BitmapFactory.decodeStream(localInputStream);
            } catch (Exception e) {
                Log.e("ImageDownloader", "Something went wrong while retrieving bitmap from " + url + ": "
                            + e.getMessage());
            }finally {
                localHttpGet.abort();
            }

            return null;
        }


        private void updateVisitTable()
        {
            int maxdays = 0;
            int duration;
            Calendar cDate = Calendar.getInstance();
            DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

            // get the max callcycle day number
            Cursor pjpCursor = DbHelper.getInstance(LoginActivity.this).query(
                    CallCycleTable.CONTENT_URI, new String[]{CallCycleTable.Columns.DURATION, CallCycleTable.Columns.DAY},
                    null, null, CallCycleTable.Columns.DAY + DbSyntax.DESC + DbSyntax.LIMIT + 1);

            if (pjpCursor != null && pjpCursor.moveToFirst()) {
                maxdays = (byte) pjpCursor.getInt(pjpCursor.getColumnIndexOrThrow(CallCycleTable.Columns.DAY));
                Log.i(LOGC,"MaxCycleDays="+maxdays);
            }
            pjpCursor.close();

            // fetch all entries in the call cycle table
            String[] cols = new String[] { CallCycleTable.Columns.DURATION, CallCycleTable.Columns.DAY,
                    CallCycleTable.Columns.BEAT, CallCycleTable.Columns.START_DATE };
            pjpCursor = DbHelper.getInstance(LoginActivity.this).query(CallCycleTable.CONTENT_URI,
                    cols, null, null, CallCycleTable.Columns.DAY + DbSyntax.ASC);

            if (pjpCursor != null && pjpCursor.moveToFirst()) {

                String strDate = pjpCursor.getString(pjpCursor.getColumnIndexOrThrow(CallCycleTable.Columns.START_DATE));
                Log.i(LOGC,"Start date ="+strDate);
                try {
                    Date sdate = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
                    cDate.setTime(sdate);
                } catch (ParseException e) {
                    Log.e(LOGC, "Cycle Date error! " + e.getMessage());
                    return;
                }

                duration = pjpCursor.getInt(pjpCursor.getColumnIndexOrThrow(CallCycleTable.Columns.DURATION));
                Log.i(LOGC,"duration="+duration);
                int cycles = duration/maxdays;
                Log.i(LOGC,"Cycles="+cycles);

                for(int i=0; i < cycles; i++) {
                    byte prevDay = 1, currDay;
                    do {
                        currDay = (byte) pjpCursor.getInt(pjpCursor.getColumnIndexOrThrow(CallCycleTable.Columns.DAY));
                        if (currDay > prevDay) {
                            byte diff = (byte) (currDay - prevDay);
                            Log.i(LOGC, "Date diff=" + diff);
                            // increment by diff day (this value should always be 1)
                            cDate.add(Calendar.DAY_OF_MONTH, diff);
                            Log.i(LOGC, "New Date =" + dFormat.format(cDate.getTime()));
                            if (cDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                                cDate.add(Calendar.DAY_OF_MONTH, 1); // skip sunday
                            }
                        }
                        prevDay = currDay;

                        String beat = pjpCursor.getString(pjpCursor.getColumnIndexOrThrow(CallCycleTable.Columns.BEAT));
                        String sql = CallCycleTable.Columns.BEAT + DbSyntax.EQUAL +
                                DbSyntax.Q + beat + DbSyntax.Q;

                        cols = new String[]{OutletTable.Columns.CODE, OutletTable.Columns.NAME, OutletTable.Columns.ADDRESS};
                        Cursor oCursor = DbHelper.getInstance(LoginActivity.this).query(
                                OutletTable.CONTENT_URI, cols, sql, null, null);

                        if ((oCursor != null) && (oCursor.moveToFirst())) {
                            ContentValues cv = new ContentValues();
                            do {
                                cv.put(VisitTable.Columns.CODE,
                                        oCursor.getString(oCursor.getColumnIndexOrThrow(OutletTable.Columns.CODE)));

                                cv.put(VisitTable.Columns.NAME,
                                        oCursor.getString(oCursor.getColumnIndexOrThrow(OutletTable.Columns.NAME)));
                                cv.put(VisitTable.Columns.ADDRESS,
                                        oCursor.getString(oCursor.getColumnIndexOrThrow(OutletTable.Columns.ADDRESS)));
                                cv.put(VisitTable.Columns.STATUS, VisitTable.VisitStatus.UNVISITED);
                                cv.put(VisitTable.Columns.EMPCODE, empcode);

                                // update the planned visit date
                                cv.put(VisitTable.Columns.DATE, dFormat.format(cDate.getTime()));

                                //Log.i(LOGC, "visit =" + cv.toString());
                                DbHelper.getInstance(LoginActivity.this).insert(VisitTable.CONTENT_URI, cv);
                            } while (oCursor.moveToNext());
                        }
                        oCursor.close();
                        Log.e(LOGC, "Moving to next pjp row");
                    } while (pjpCursor.moveToNext());
                    Log.e(LOGC, "Starting new cycle=" + i);
                    pjpCursor.moveToFirst();
                }
                pjpCursor.close();
            }
        }
    }
}