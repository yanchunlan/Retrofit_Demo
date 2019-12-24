package com.example.library;

import com.example.library.http.GET;
import com.example.library.http.POST;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import okhttp3.Call;

/**
 * author:  ycl
 * date:  2019/12/24 14:51
 * desc:
 */
public class ServiceMethod {


    public ServiceMethod(Builder builder) {


    }

    // 发起请求 ， 当前是缓存对象，传入的参数都有了，只需要请求即可
    public Call toCall(Object[] args) {
        return null;
    }

    public static final class Builder {

        final Retrofit retrofit;
        final Method method;// 带注解的方法
        final Annotation[] methodAnnotations;  // 方法的所有注解
        final Annotation[][] parameterAnnotationsArray; // 方法参数的所有注解

        String httpMethod;  // 方法的请求方式（"GET"、"POST"）
        String relativeUrl; // 方法的注解的值("/ip/ipNew")
        ParameterHandler<?>[] parameterHandlers;// 方法的参数的数组（每个对象包含：参数注解值、参数值）
        boolean hasBody;

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
//                ParameterHandler<?> annotationAction = parseParameterAnnotation(
//                        p, annotations, annotation);
//                if (annotationAction == null) {
//                    continue;
//                }
//                if (result != null) {
//                    throw new IllegalArgumentException( "Multiple Retrofit annotations found, only one allowed.");
//                }
//                result = annotationAction;
            }

            if (result == null) {
                throw new IllegalArgumentException( "No Retrofit annotation found.");
            }
            return result;
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
