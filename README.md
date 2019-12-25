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
    
#### retrofit源码调用流程：

    1. new Retrofit.Builder().xxx.build()
	存储baseURL,call.Factory（OkHttpClient）
	
    2. retrofit.create(xxx.class)
	Proxy.newProxyInstance（xxxxx）,创建xxx.class动态代理类
	
    3. xxx.get("", "")
	xxx.get("", "")->调用代理类方法->loadServiceMethod(method)从缓存中取方法 -> 
	
        无缓存 -> 创建ServiceMethod并存储在集合中（创建build存储retrofit/method/methodAnnotations/parameterAnnotationsArray,最后build的时候，
                 遍历methodAnnotations->parseMethodAnnotation->parseHttpMethodAndPath存储httpMethod/relativeUrl/hasBody
                 遍历parameterAnnotationsArray->遍历parseParameter->parseParameterAnnotation根据注解取值，返回newParameterHandler.xxx<>(xxx)，存储parameterHandlers->返回ServiceMethod）
	
        有缓存 -> 取出ServiceMethod -> new OkHttpCall（ServiceMethod,args）-> 存储serviceMethod/args,执行serviceMethod.toCall(args)
                【构建RequestBuilder请求对象，传入httpMethod/baseUrl/relativeUrl/hasBody，
                        遍历parameterHandlers，执行handlers[i].apply -> RequestBuilder.addxxx(name, xxx);
                        (RequestBuilder是okhttp操作的真正的类，里面就包括okhttp添加头，boy,参数等操作)，最后执行call.Factory（OkHttpClient）.newCall(requestBuilder.build())
                        (requestBuilder.build-> 里面真实执行了URL拼接，body构造，最后构建okhttp请求队列requestBuilder.url(url).method(method,body).build())】
                ->返回实现了okhttp的call接口的OkHttpCall
	
    4. call.execute()
	OkHttpCall.execute ->rawCall.execute-> call.execute
 
