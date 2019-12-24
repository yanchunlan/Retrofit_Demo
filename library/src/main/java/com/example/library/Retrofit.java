package com.example.library;

import androidx.annotation.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * author:  ycl
 * date:  2019/12/24 14:40
 * desc:
 */
public class Retrofit {
    // 缓存请求的方法
    // key:请求方法，如host.get() value: 该方法的属性封装，如：方法名、方法注解、参数注解、参数
    private final Map<Method, ServiceMethod> serviceMethodCache = new ConcurrentHashMap<>();

    private final okhttp3.Call.Factory callFactory;
    private final HttpUrl baseUrl;

    public Retrofit(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.callFactory = builder.callFactory;
    }

    public okhttp3.Call.Factory callFactory() {
        return callFactory;
    }

    public HttpUrl baseUrl() {
        return baseUrl;
    }

    public <T> T create(final Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service }, new InvocationHandler() {
                    @Override public Object invoke(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }
                        // 动态代理和反射的最底层原理是：$Proxy4
                        ServiceMethod serviceMethod = loadServiceMethod(method);
                        return new OkHttpCall(serviceMethod, args);
                    }
                });
    }

    // 获取方法所有内容：方法名、方法注解、参数注解、参数
    private ServiceMethod loadServiceMethod(Method method) {
        ServiceMethod result = serviceMethodCache.get(method);
        if (result != null) return result;

        synchronized (serviceMethodCache) {
            result = serviceMethodCache.get(method);
            if (result == null) {
                result = new ServiceMethod.Builder(this, method).build();
                serviceMethodCache.put(method, result);
            }
        }
        return result;
    }

    public static final class Builder {
        private @Nullable
        okhttp3.Call.Factory callFactory;
        private HttpUrl baseUrl;

        public Builder callFactory(okhttp3.Call.Factory factory) {
            this.callFactory = factory;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            HttpUrl httpUrl = HttpUrl.parse(baseUrl);
            if (httpUrl == null) {
                throw new IllegalArgumentException("Illegal URL: " + baseUrl);
            }
            this.baseUrl = httpUrl;
            return this;
        }

        public Retrofit build() {
            if (baseUrl == null) {
                throw new IllegalStateException("Base URL required.");
            }
            if (callFactory == null) {
                callFactory = new OkHttpClient();
            }
            return new Retrofit(this);
        }
    }
}
