package retailworks.in.field.utils;

/**
 * Created by Neiv on 10/16/2015.
 */

import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class SimpleHttpClient
{
    public static final int HTTP_TIMEOUT = 10000;
    private static HttpClient mHttpClient;


    /**
     * connects to the server and posts the json object
     * this is a synchronized method
     *
     * @param url
     * @param paramJSONObject
     * @return
     * @throws Exception
     */
    public static synchronized String executeHttpPost(String url, JSONObject paramJSONObject)
            throws Exception
    {
        paramJSONObject.put(Constants.COMPANY, Constants.COMPANY_NAME);

        try
        {
            HttpClient httpClient = getHttpClient();

            HttpParams httpParams = mHttpClient.getParams();

            HttpConnectionParams.setConnectionTimeout(httpParams, HTTP_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, 2 * HTTP_TIMEOUT);
            ConnManagerParams.setTimeout(httpParams, HTTP_TIMEOUT);

            HttpPost post = new HttpPost(url);
            post.setParams(httpParams);
            post.setEntity(new StringEntity(paramJSONObject.toString()));

            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");

            //Log.v(Constants.APP_TAG, "Syncing to Server: \n" + paramJSONObject.toString());
            HttpResponse resp = httpClient.execute(post);
            //Log.v(Constants.APP_TAG, "Server resp: " + resp);
            return EntityUtils.toString(resp.getEntity());
        }
        catch (NoHttpResponseException e) {
            return  e.getMessage();
        }
        finally {

        }
    }

    private static HttpClient getHttpClient()
    {
        if (mHttpClient == null) {
            mHttpClient = new DefaultHttpClient();
        }

        return mHttpClient;
    }
}