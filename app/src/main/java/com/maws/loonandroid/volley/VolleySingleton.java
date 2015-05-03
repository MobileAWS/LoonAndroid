package com.maws.loonandroid.volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.maws.loonandroid.LoonAndroid;

public class VolleySingleton {

    public static final String SERVER_URL = "https://caresentinel-maws.herokuapp.com/";
    private static final int NUM_RETRIES = 3;

    private static final VolleySingleton instance = new VolleySingleton(LoonAndroid.globalApplicationContext);
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    public static DefaultRetryPolicy getRetryPolicy(){
        return new DefaultRetryPolicy(10000,
                NUM_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    private VolleySingleton(Context context) {
        requestQueue = Volley.newRequestQueue(context);

        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }


    public static VolleySingleton getInstance() {
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
