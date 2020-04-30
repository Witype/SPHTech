## 功能介绍
- App启动后从[https://data.gov.sg/dataset/mobile-data-usage](https://data.gov.sg/api/action/datastore_search?resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f&limit=5) 获取数据，按照一年4个季度显示列表；
- 如果一年中任何一个季度的数据量下降，则在条目中显示一个可点击的图像，点击后显示数据对话框；
- 支持数据本地缓存

## 功能和依赖库
1. [`RxJava2`](https://github.com/ReactiveX/RxJava)
2. [`RxAndroid2`](https://github.com/ReactiveX/RxAndroid)
3. [`Mvp` Google's official` Mvp` architecture project (this is the Dagger branch).](https://github.com/googlesamples/android-architecture/tree/todo-mvp-dagger/)
4. [`Dagger2`](https://github.com/google/dagger)
5. [`Retrofit`](https://github.com/square/retrofit)
6. [`Okhttp`](https://github.com/square/okhttp)
7. [`Butterknife`](https://github.com/JakeWharton/butterknife)
8.  [`Timber`](https://github.com/JakeWharton/timber)

## TO LIST
- [ ]  基础框架
  - [x] Dagger2+MVP+RETROFIT+OKHTTP+RXJAVA+RXANDROID
  - [ ] Activity&Presenter LifeCycle管理
  - [ ] 页面基础加载、提示
  - [ ] 接口结构化、非结构化数据处理
- [ ] 页面开发
  - [ ] 列表页面
  - [ ] 弹窗图标
- [ ] 测试
  - [ ] 逻辑代码单元测试
  - [ ] UI即兼容测试
  - [ ] 老化测试
  - [ ] 安全测试
- [ ] 测试报告

## 
* **Email**: <witype716@qq.com>  
