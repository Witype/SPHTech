## 功能介绍
- App启动后从[https://data.gov.sg/dataset/mobile-data-usage](https://data.gov.sg/api/action/datastore_search?resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f&limit=5) 获取数据，按照一年4个季度显示列表；
- 如果一年中任何一个季度的数据量下降，则在条目中显示一个可点击的图像，点击后显示数据对话框；
- 支持数据本地缓存


![image](https://j1-media.s3.cn-north-1.amazonaws.com.cn/resource/2020/05-15/ebedc1c3d6bbe67b646b73cb544d2987.gif)

## 功能和依赖库
1. [`RxJava2`](https://github.com/ReactiveX/RxJava)
2. [`RxAndroid2`](https://github.com/ReactiveX/RxAndroid)
3. [`Mvp` Google's official` Mvp` Dagger2案例.](https://github.com/googlesamples/android-architecture/tree/todo-mvp-dagger/)
4. [`Dagger2`](https://github.com/google/dagger)
5. [`Retrofit`](https://github.com/square/retrofit)
6. [`Okhttp`](https://github.com/square/okhttp)
7. [`Butterknife`](https://github.com/JakeWharton/butterknife)
8.  [`Timber`](https://github.com/JakeWharton/timber)
9.  [`RxCache`](https://github.com/VictorAlbertos/RxCache)

## TO LIST
- [ ]  基础框架
  - [x] Dagger2+MVP+RETROFIT+OKHTTP+RXJAVA+RXANDROID
  - [x] Activity&Presenter LifeCycle管理
  - [x] 页面基础加载、提示
  - [x] 接口结构化、非结构化数据处理
- [ ] 页面开发
  - [x] 显示列表数据
  - [x] 显示可点击的图片
  - [x] 数据缓存，在没有网络的情况下缓存30分钟
- [ ] 测试
  - [x] 核心逻辑代码单元测试。ps：各个框架都有相应的单元测试用例，因此第三方库忽略测试
  - [x] UI即兼容测试
  - [x] 新增RxJava模块单元测试用例，（网络错误、服务器内部错误）
  - [ ] 老化测试

## 特点
1. 相较于google的mvp框架，此框架只需要定义一个CallModel即可全局使用，而google需要定义很多的constract，如果各个模块之间存在公用的model则需要重新定义，比较麻烦。
2. 另外，presenter不单单只是适用于Activity，可以适用于多种情况，service、工具类，和activity定义使用一样简单
3. 灵活的处理结构化与非结构化数据，配合不同的compose、function，灵活切换
4. dagger2在此框架非常重要，对开发者apt、java基础能力要求较高，但是如果只是开发则非常容易。

## 
* **Email**: <witype716@qq.com>  
