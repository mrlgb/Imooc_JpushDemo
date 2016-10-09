package com.example.jpushclient;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.util.Arrays;

/**
 * 作者：dell on 2016/9/29 10:17
 * 邮箱：475838004@qq.com
 */
public class JPushClient {
    private final String TAG="--JPushClient--";
    private String baseUrl="https://api.jpush.cn/v3/push";
    private String appKey;
    private String masterSecret;
    private String author;
    public JPushClient(Context context,String appKey,String masterSecret) {
        String key=appKey+":"+masterSecret;
        author="Basic "+ Arrays.toString(Base64.encode(key.getBytes(), Base64.DEFAULT));
        Log.d(TAG, "JPushClient: author "+author);

    }



}
