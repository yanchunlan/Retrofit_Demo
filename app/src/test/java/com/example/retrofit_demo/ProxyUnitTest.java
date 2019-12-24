package com.example.retrofit_demo;

import org.junit.Test;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * author:  ycl
 * date:  2019/12/24 11:32
 * desc:
 */
public class ProxyUnitTest {

    interface HOST {
        @GET("/ip/ipNew")
        Call<ResponseBody> get(@Query("ip") String ip, @Query("key") String key);

        @POST("/ip/ipNew")
        @FormUrlEncoded
        Call<ResponseBody> post(@Field("ip") String ip, @Field("key") String key);
    }

    @Test
    public void proxyTest() throws IOException {

        HOST host = (HOST) Proxy.newProxyInstance(HOST.class.getClassLoader(),
                new Class[]{HOST.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("--------------------------------------------------");
                        System.out.println("invoke method.getName(): " + method.getName());
                        GET get = method.getAnnotation(GET.class);
                        System.out.println("invoke GET.value(): " + (get!=null?get.value():null));

                        POST post = method.getAnnotation(POST.class);
                        System.out.println("invoke POST.value(): " + (post!=null?post.value():null));

                        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                        for (Annotation[] annotation : parameterAnnotations) {
                            System.out.println("invoke parameterAnnotation: "+ Arrays.toString(annotation));
                        }
                        System.out.println("invoke args: "+Arrays.toString(args));
                        System.out.println("--------------------------------------------------");
                        return null;
                    }
                });
        // 调用
         host.get("11.22.33.44", "appkey");
         host.post("11.22.33.44", "appkey");
    }
}
