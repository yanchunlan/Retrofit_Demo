# Retrofit_Demo

手写retrofit框架

#### 测试类（test包内）： 

- 原始okHttp/retrofit： `RetrofitUnitTest`
- 原始获取Get/Post参数： `ProxyUnitTest`
- 自己手写的retrofit： `LearnMyRetrofitUnitTest`

#### retrofit源码总结： 
    
    // 1. 构建retrofit请求对象
    Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();
                
    // 2. 构建HOST的动态代理，并返回HOST的代理类
    HOST host = retrofit.create(HOST.class);
    
    // 3. 调用HOST方法，代理启用，从缓存中取得之前缓存的方法数据，并返回okHttp的call对象，并构造成功了内部的okHttp的request
    Call<ResponseBody> call = host.get(IP, KEY);
    
    // 4. 包装类call的方法执行，会调用真实类okHttp的execute方法，真实的去执行请求
    retrofit2.Response<ResponseBody> response = call.execute();
