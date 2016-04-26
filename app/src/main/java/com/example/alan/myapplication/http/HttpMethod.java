package com.example.alan.myapplication.http;

/**
 * Created by LMQ on 2015/7/16.
 */
public enum HttpMethod {
    GET(0),
    POST(1),
    PUT(2),
    DELETE(3),
    HEAD(4),
    OPTIONS(5),
    TRACE(6),
    PATCH(7);
    int volleyValue;
    HttpMethod(int volleyValue) {
        this.volleyValue = volleyValue;
    }

    public int getVolleyValue(){
        return volleyValue;
    }
}
