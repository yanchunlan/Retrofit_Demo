package com.example.retrofit_demo;

import com.example.library.Retrofit;
import com.example.library.http.Field;
import com.example.library.http.GET;
import com.example.library.http.POST;
import com.example.library.http.Query;

import org.junit.Test;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

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
                System.out.println("myRetrofit get response: \n" + response.body().string());
            }
        }
        // retrofit post
        {
            Call call = host.post(IP, KEY);
            Response response = call.execute();
            if (response != null && response.body() != null) {
                System.out.println("myRetrofit post response: \n" + response.body().string());
            }
        }
    }

}

