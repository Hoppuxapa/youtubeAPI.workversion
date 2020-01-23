package com.youtube.config;

import okhttp3.OkHttpClient;

public interface Config {
    int HEIGHT = 700;
    int WIDTH = 850;

    OkHttpClient client = new OkHttpClient();

    String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    String DATE_FORMAT_SHOW = "dd/L/yyyy 'Time:' HH:mm";
}
