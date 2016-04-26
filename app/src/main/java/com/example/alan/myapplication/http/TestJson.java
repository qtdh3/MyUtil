package com.example.alan.myapplication.http;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

/**
 * Created by Linjf on 2016/3/9 0009.
 */
public class TestJson {

    Context context;
    private String TAG=TestJson.class.getName();
    RequestQueue requestQueue;
    Response.Listener<String> listener=new Response.Listener<String>() {
        @Override
        public void onResponse(String o) {
            Log.i(TAG,"listener onResponse->"+o);
        }
    };

    public TestJson(Context context) {
        this.context = context;
    }

    public void sendRequest(){
        BaseJsonRequest baseJsonRequest=new BaseJsonRequest(HttpMethod.GET.getVolleyValue(),
                "http://gc.ditu.aliyun.com/geocoding"+"?"+
                "a=苏州市","", listener, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(TAG,"onErrorResponse="+volleyError);
            }
        });
        requestQueue=Volley.newRequestQueue(context);
        requestQueue.add(baseJsonRequest);
        requestQueue.start();
    }
}
