package com.example.library;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * author:  ycl
 * date:  2019/12/24 15:51
 * desc:
 */
class RequestBuilder {
    private final String method; // get/post
    private final HttpUrl baseUrl;
    private String relativeUrl; // 注解的值，目的是构建完整url
    private HttpUrl.Builder urlBuilder; // url构建者
    private FormBody.Builder formBuilder; // 表单构建者
    private final Request.Builder requestBuilder; // 构建完整请求request

    public RequestBuilder(String method, HttpUrl baseUrl, String relativeUrl, boolean hasBody) {
        this.method = method;
        this.baseUrl = baseUrl;
        this.relativeUrl = relativeUrl;
        this.requestBuilder = new Request.Builder();
        if (hasBody) this.formBuilder = new FormBody.Builder();
    }

    public void addQueryParam(String name, String value) {
        if (relativeUrl != null) {
            urlBuilder = baseUrl.newBuilder(relativeUrl);
            if (urlBuilder == null) {
                throw new IllegalArgumentException(
                        "Malformed URL. Base: " + baseUrl + ", Relative: " + relativeUrl);
            }
            relativeUrl = null;
        }
        urlBuilder.addQueryParameter(name, value);
    }

    public void addFormField(String name, String value) {
        formBuilder.add(name, value);
    }

    public Request build() {
        HttpUrl url;
        if (urlBuilder != null) {
            url = urlBuilder.build();
        } else {
            url = baseUrl.resolve(relativeUrl);
            if (url == null) {
                throw new IllegalArgumentException(
                        "Malformed URL. Base: " + baseUrl + ", Relative: " + relativeUrl);
            }
        }

        RequestBody body = null;
        if (formBuilder != null) {
            body = formBuilder.build();
        }

        return requestBuilder
                .url(url)
                .method(method, body)
                .build();
    }
}
