package org.tron.net;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class IHttpClient {
    private static final int DEFAULT_TIMEOUT = 8;

    public static OkHttpClient.Builder getHttpClientBuilder() {
//        File cacheFile = new File(HttpCache.getRootCacheDir(), "cache");
//        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100);//100Mb

        OkHttpClient.Builder Ibuilder = new OkHttpClient.Builder();
        Ibuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        Ibuilder.addInterceptor(chain -> {
            Request.Builder builder = getBuilder(chain);
            Request request = builder.build();
            Response response = chain.proceed(request);

            return response;
        });
        return Ibuilder;
    }

    private static Request.Builder getBuilder(Interceptor.Chain chain) throws UnsupportedEncodingException {
        Request org = chain.request();

        Request.Builder builder = org.newBuilder()
                .addHeader("","");

        String baseUrl = org.url().toString();
//        AppContextUtil.getContext().getSharedPreferences();
        baseUrl.replaceAll("base&url","");
        return builder;
    }
}



