package com.example.library;

import com.example.library.http.Field;
import com.example.library.http.GET;
import com.example.library.http.POST;
import com.example.library.http.Query;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import okhttp3.Call;
import okhttp3.HttpUrl;

/**
 * author:  ycl
 * date:  2019/12/24 14:51
 * desc:    缓存类，缓存请求参数，方法，注解，注解值
 */
public class ServiceMethod {

    private final Call.Factory callFactory;
    private final HttpUrl baseUrl;

    private String httpMethod;  // 方法的请求方式（"GET"、"POST"）
    private String relativeUrl; // 方法的注解的值("/ip/ipNew")
    private ParameterHandler<?>[] parameterHandlers;// 方法的参数的数组（每个对象包含：参数注解值、参数值）
    private  boolean hasBody; // 有无body


    public ServiceMethod(Builder builder) {
        this.callFactory = builder.retrofit.callFactory();
        this.baseUrl = builder.retrofit.baseUrl();
        this.httpMethod = builder.httpMethod;
        this.relativeUrl = builder.relativeUrl;
        this.parameterHandlers = builder.parameterHandlers;
        this.hasBody = builder.hasBody;
    }

    // 发起请求 ， 当前是缓存对象，传入的参数都有了，只需要请求即可
    public Call toCall(Object[] args) {
        // 实例化RequestBuilder对象，拼接完整url(包含参数和值)
        RequestBuilder requestBuilder = new RequestBuilder(httpMethod, baseUrl, relativeUrl,hasBody);

        ParameterHandler[] handlers = parameterHandlers;

        int argumentCount = args != null ? args.length : 0;
        //Proxy方法的参数个数是否等于参数的数组（手动添加）的长度，此处理解为校验
        if (argumentCount != handlers.length) {
            throw new IllegalArgumentException("Argument count (" + argumentCount
                    + ") doesn't match expected count (" + handlers.length + ")");
        }

        //循环拼接每个参数名和参数值
        for (int i = 0; i < argumentCount; i++) {
            try {
                handlers[i].apply(requestBuilder, args[i].toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return callFactory.newCall(requestBuilder.build());
    }

    public static final class Builder {

        private final Retrofit retrofit;
        private final Method method;// 带注解的方法
        private final Annotation[] methodAnnotations;  // 方法的所有注解
        private final Annotation[][] parameterAnnotationsArray; // 方法参数的所有注解

        private String httpMethod;  // 方法的请求方式（"GET"、"POST"）
        private String relativeUrl; // 方法的注解的值("/ip/ipNew")
        private ParameterHandler<?>[] parameterHandlers;// 方法的参数的数组（每个对象包含：参数注解值、参数值）
        private  boolean hasBody; // 有无body

        public Builder(Retrofit retrofit, Method method) {
            this.retrofit = retrofit;
            this.method = method;
            this.methodAnnotations = method.getAnnotations();
            this.parameterAnnotationsArray = method.getParameterAnnotations();
        }

        public ServiceMethod build() {
            for (Annotation annotation : methodAnnotations) {
                parseMethodAnnotation(annotation);
            }

            int parameterCount = parameterAnnotationsArray.length;
            parameterHandlers = new ParameterHandler<?>[parameterCount];
            for (int p = 0; p < parameterCount; p++) {
                Annotation[] parameterAnnotations = parameterAnnotationsArray[p];
                if (parameterAnnotations == null) {
                    throw new IllegalArgumentException("No Retrofit annotation found.");
                }
                parameterHandlers[p] = parseParameter(p, parameterAnnotations);
            }
            return new ServiceMethod(this);
        }

        private ParameterHandler<?> parseParameter(int p, Annotation[] annotations) {
            ParameterHandler<?> result = null;
            for (Annotation annotation : annotations) {
                ParameterHandler<?> annotationAction = parseParameterAnnotation( annotation);
                if (annotationAction == null) {
                    continue;
                }
                if (result != null) {
                    throw new IllegalArgumentException( "Multiple Retrofit annotations found, only one allowed.");
                }
                result = annotationAction;
            }

            if (result == null) {
                throw new IllegalArgumentException( "No Retrofit annotation found.");
            }
            return result;
        }

        private ParameterHandler<?> parseParameterAnnotation(Annotation annotation) {
            if (annotation instanceof Query) {
                Query query = (Query) annotation;
                String name = query.value();
                return new ParameterHandler.Query<>(name);
            } else if (annotation instanceof Field) {
                Field field = (Field) annotation;
                String name = field.value();
                return new ParameterHandler.Field<>(name);
            }
            return null;
        }

        private void parseMethodAnnotation(Annotation annotation) {
            if (annotation instanceof GET) {
                parseHttpMethodAndPath("GET", ((GET) annotation).value(), false);
            } else if (annotation instanceof POST) {
                parseHttpMethodAndPath("POST", ((POST) annotation).value(), true);
            }
        }

        private void parseHttpMethodAndPath(String httpMethod, String value, boolean hasBody) {
            this.httpMethod = httpMethod;
            this.relativeUrl = value;
            this.hasBody = hasBody;
        }
    }
}
