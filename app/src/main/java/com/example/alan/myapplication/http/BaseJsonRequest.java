package com.example.alan.myapplication.http;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Linjf on 2016/3/9 0009.
 */
public class BaseJsonRequest extends JsonRequest{
    private String TAG=BaseJsonRequest.class.getName();

    public BaseJsonRequest(int method, String url, String requestBody, Response.Listener listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse networkResponse) {

        String parsed;
        try {
            parsed = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
        } catch (UnsupportedEncodingException var4) {
            parsed = new String(networkResponse.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(networkResponse));
//
//
//        String parsed;
//        String charset=HttpHeaderParser
//                .parseCharset(networkResponse.headers);
//        try {
//            parsed = new String(networkResponse.data, charset);
//            Log.i(TAG,"Charset="+charset);
//        } catch (UnsupportedEncodingException e) {
//            parsed = new String(networkResponse.data);
//        }
//        Log.i(TAG,"networkResponse->\n"+networkResponse
//        +"\ndata"+networkResponse.data+"\nstatusCode"+networkResponse.statusCode+"\nheaders"+networkResponse.headers);
//        Log.i(TAG, "data->" + parsed);
//        try {
//            JSONObject jsonObject=new JSONObject(parsed);
//            Log.i(TAG,"lon="+jsonObject.getDouble("lon"));
//            Log.i(TAG,"lon="+jsonObject.getString("lon"));
//            Log.i(TAG,"lon="+jsonObject.get("lon"));
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
////        JSONObject jsonObject=new Gson().fromJson(parsed,);       // Gson 可以转化json串到制定类，
//// 还没对 返回报文做封装，可以先不用。
//
//        return null;
    }
}
