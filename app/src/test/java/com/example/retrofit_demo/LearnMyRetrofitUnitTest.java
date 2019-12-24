package com.example.retrofit_demo;

import org.junit.Test;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * author:  ycl
 * date:  2019/12/24 10:36
 * desc:    自己手写的retrofit
 */
public class LearnMyRetrofitUnitTest {
    private final static String IP = "144.34.161.97";
    private final static String KEY = "aa205eeb45aa76c6afe3c52151b52160";
    private final static String BASE_URL = "http://apis.juhe.cn/";

    interface HOST {
        @GET("/ip/ipNew")
        Call get(@Query("ip") String ip, @Query("key") String key);

        @POST("/ip/ipNew")
        Call post(@Field("ip") String ip, @Field("key") String key);
    }

    @Test
    public void testRetrofit() throws IOException {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).build();
        HOST host = retrofit.create(HOST.class);
        // retrofit get
        {
            Call call = host.get(IP, KEY);
            Response response = call.execute();
            if (response != null && response.body() != null) {
                System.out.println("retrofit get response: \n" + response.body().string());
            }
        }
        // retrofit post
        {
            Call call = host.post(IP, KEY);
            Response response = call.execute();
            if (response != null && response.body() != null) {
                System.out.println("retrofit post response: \n" + response.body().string());
            }
        }
    }

}

