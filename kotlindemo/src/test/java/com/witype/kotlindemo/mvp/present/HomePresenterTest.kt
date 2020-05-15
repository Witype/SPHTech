package com.witype.kotlindemo.mvp.present

import com.google.gson.Gson
import com.witype.kotlindemo.RxJavaRule
import com.witype.kotlindemo.entity.MobileDateUsageEntity
import com.witype.kotlindemo.entity.RecordsBean
import com.witype.kotlindemo.entity.ResultBean
import com.witype.kotlindemo.integration.HttpModel
import com.witype.kotlindemo.integration.HttpObserver
import com.witype.kotlindemo.mvp.contract.HomeView
import com.witype.kotlindemo.mvp.model.ApiModel
import com.witype.kotlindemo.mvp.presenter.HomePresenter
import com.witype.mvp.integration.UIObservableTransformer
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.observers.TestObserver
import io.reactivex.plugins.RxJavaPlugins
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random


class HomePresenterTest {

    val LIMIT = 2018

    /**
     * 设置rxJava所有的执行线程为[io.reactivex.schedulers.Schedulers.trampoline]
     * set all rxjava scheduler run with [io.reactivex.schedulers.Schedulers.trampoline]
     */
    @get:Rule
    val rxJava: RxJavaRule = RxJavaRule()

    lateinit var mData: MutableList<RecordsBean>

    @MockK
    lateinit var model: HttpModel;

    lateinit var onGetMobileDataUsage: HomeView

    lateinit var onGetDataError: HomeView

    lateinit var showLoading: HomeView

    lateinit var dismissLoading: HomeView

    @MockK
    lateinit var mobileDateUsageEntity: MobileDateUsageEntity

    fun getYearRange(): Int {
        return (1000..2000).random()
    }

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        mData = ArrayList()
        val count = (50..100).random()
        var year = getYearRange();
        for (i in 1..count) {
            val spy = RecordsBean()
            val quarter = (1..4).random().coerceAtLeast(1)
            spy.quarter = String.format("%s Q%s", year, quarter)
            spy.volume_of_mobile_data = Random.nextDouble()
            mData.add(spy)
            year = if (i % 4 == 0) getYearRange() else year
        }

        //mock
        val resultBean = mockk<ResultBean>()
        every { resultBean.records } returns mData
        every { mobileDateUsageEntity.result } returns resultBean;

        val just: Observable<MobileDateUsageEntity> = Observable.just(mobileDateUsageEntity)

        every { model.getMobileDataUsage(HomePresenter.RESOURCE_ID, LIMIT) } returns just
        onGetMobileDataUsage = spyk()
        onGetDataError = spyk()
        showLoading = spyk()
        dismissLoading = spyk()
    }

    /**
     * 测试[HttpObserver] 配合[HomeView.onGetMobileDataUsage]
     * test [HttpObserver] with call [HomeView.onGetMobileDataUsage]
     */
    @Test
    fun testHomeViewCallSuccessWithHttpObservable() {
        log("┌─── testHomeViewCallSuccessWithHttpObservable ────────────────────────────────")

        val testObserver = TestObserver(httpObserver)
        model.getMobileDataUsage(HomePresenter.RESOURCE_ID, LIMIT)
                .subscribe(testObserver)
        testObserver.assertNoErrors()

        log("|\t▊ assert onGetMobileDataUsage called 1 , onGetDataError not call")
        verify(exactly = 1) { onGetMobileDataUsage.onGetMobileDataUsage() }
        verify { onGetDataError wasNot Called }
        log("└─── TEST PASS ──────────────────────────────────────────────────")
    }

    /**
     * 测试[HttpObserver] 配合[HomeView.onGetDataError]
     * test [HttpObserver] with call [HomeView.onGetDataError]
     */
    @Test
    fun testHomeViewCallFailureModelErrorWithHttpObservable() {
        log("┌─── testHomeViewCallFailureModelErrorWithHttpObservable ────────────────────────────────")

        val testObserver = TestObserver(httpObserver)
        model.getMobileDataUsage(HomePresenter.RESOURCE_ID, LIMIT)
                .map { t: MobileDateUsageEntity -> null }
                .subscribe(testObserver)
        testObserver.assertError(NullPointerException::class.java)

        log("|\t▊ assert onGetDataError called 1 , onGetMobileDataUsage not call")
        verify(exactly = 1) { onGetDataError.onGetDataError(any()) }
        verify { onGetMobileDataUsage wasNot Called }

        log("└─── TEST PASS ──────────────────────────────────────────────────")
    }

    /**
     * test 1:
     * 测试从 @see <a>https://data.gov.sg/api/action/datastore_search?resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f&limit=5</a> 获取数据并打印records
     * test 2:
     * 使用[com.witype.mvp.integration.UIObservableTransformer] 模拟界面的loading和dismiss
     *
     * test 1:
     * test get remote reource from
     * <a>https://data.gov.sg/api/action/datastore_search?resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f&limit=5</a> and print log
     * test 2:
     * use [com.witype.mvp.integration.UIObservableTransformer] mock UI loading and dismiss
     *
     */
    @Test
    fun testHttpObservableWithRemoteSuccess() {
        log("┌─── testHttpObservableWithRemoteSuccess ────────────────────────────────")
        val retrofit = mockRetrofit();

        val mobileDataUsage = retrofit.create(ApiModel::class.java)
                .getMobileDataUsage(HomePresenter.RESOURCE_ID, LIMIT)
        every { model.getMobileDataUsage(any(), any()) } returns mobileDataUsage

        val httpObserver = object : HttpObserver<RecordsBean>() {

            override fun onSuccess(t: RecordsBean) {
                //do something
            }

            override fun onError(e: Throwable) {
                log("|\t| onError => onGetDataError")
                onGetDataError.onGetDataError(e.message)
            }

            override fun onComplete() {
                log("|\t| onComplete => onGetMobileDataUsage")
                onGetMobileDataUsage.onGetMobileDataUsage()
            }
        }

        model.getMobileDataUsage(HomePresenter.RESOURCE_ID, LIMIT)
                .flatMap { t: MobileDateUsageEntity ->
                    log("|\t┌── response ────────────────────────────")
                    log("|\t| $t")
                    Observable.fromIterable(t.result?.records)
                }
                .doOnNext { t: RecordsBean? ->
                    log("|\t| $t")
                }
                .compose(bindUIEvent())
                .doOnComplete { log("|\t└─────────────────────────────────────────────────────") }
                .subscribe(TestObserver(httpObserver))
        log("|\t▊ assert onGetMobileDataUsage called 1 , onGetDataError not call")
        verify(exactly = 1) { onGetMobileDataUsage.onGetMobileDataUsage() }
        verify { onGetDataError wasNot Called }

        log("|\t▊ assert showLoading called 1 , dismissLoading call 1")
        verify(exactly = 1) { showLoading.showLoading() }
        verify(exactly = 1) { showLoading.dismissLoading() }

        log("|\t▊ assert call order showLoading => onGetMobileDataUsage => dismissLoading")
        verifySequence {
            showLoading.showLoading()
            showLoading.dismissLoading()
            onGetMobileDataUsage.onGetMobileDataUsage()
        }

        log("└─── TEST PASS ──────────────────────────────────────────────────")
    }

    /**
     * test 1:
     * 测试使用不完整的json数据获取[MobileDateUsageEntity]，并打印日志，模拟数据获取异常回调 [HomeView.onGetDataError]
     * test 2:
     * 使用[com.witype.mvp.integration.UIObservableTransformer] 模拟界面的loading和dismiss
     *
     * test 1:
     * test get [MobileDateUsageEntity] from snippet json and aspect call [HomeView.onGetDataError]
     * test 2:
     * use [com.witype.mvp.integration.UIObservableTransformer] mock UI loading and dismiss
     *
     */
    @Test
    fun testHttpObservableWithRemoteModelError() {
        log("┌─── testHttpObservableWithRemoteModelError ────────────────────────────────")

        //mock empty data
        val mobileDataUsage = Observable.just("{\"help\":\"https://data.gov.sg/api/3/action/help_show?name=datastore_search\",\"success\":true}")
                .flatMap { t ->
                    val data = Gson().fromJson(t, MobileDateUsageEntity::class.java)
                    Observable.just(data)
                }
        every { model.getMobileDataUsage(any(), any()) } returns mobileDataUsage

        val httpObserver = object : HttpObserver<RecordsBean>() {

            override fun onSuccess(t: RecordsBean) {
                //do something
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                log("|\t| onError => onGetDataError")
                onGetDataError.onGetDataError(e.message)
            }

            override fun onComplete() {
                log("|\t| onComplete => onGetMobileDataUsage")
                onGetMobileDataUsage.onGetMobileDataUsage()
            }
        }
        val testObserver = TestObserver(httpObserver);
        model.getMobileDataUsage(HomePresenter.RESOURCE_ID, LIMIT)
                .flatMap { t: MobileDateUsageEntity ->
                    log("|\t┌── response size ${t.result?.records?.size ?: 0} ────────────────────────────")
                    log("|\t| $t")
                    Observable.fromIterable(t.result?.records)
                }
                .doOnNext { t: RecordsBean? ->
                    log("|\t| $t")
                }
                .compose(bindUIEvent())
                .doOnComplete { log("|\t└─────────────────────────────────────────────────────") }
                .subscribe(testObserver)
        testObserver.assertError(NullPointerException::class.java)

        log("|\t▊ assert onGetDataError called 1 , onGetMobileDataUsage not call")
        verify(exactly = 1) { onGetDataError.onGetDataError(any()) }
        verify { onGetMobileDataUsage wasNot Called }

        log("|\t▊ assert showLoading called 1 , dismissLoading call 1")
        verify(exactly = 1) { showLoading.showLoading() }
        verify(exactly = 1) { showLoading.dismissLoading() }

        log("|\t▊ assert call order showLoading => onGetDataError => dismissLoading")
        verifySequence {
            showLoading.showLoading()
            showLoading.dismissLoading()
            onGetDataError.onGetDataError(any())
        }
        log("└─── TEST PASS ──────────────────────────────────────────────────")
    }

    /**
     * 测试从 @see <a>https://data.gov.sg/api/action/datastore_search?resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f&limit=5</a> 获取数据
     * 然后通过[HomePresenter.DataRangeFilter] 、 [HomePresenter.DataGroupFun]、[HomePresenter.DataQuarterOffsetFun]过滤数据
     *
     * test get remote data from @see <a>https://data.gov.sg/api/action/datastore_search?resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f&limit=5</a>
     * then filter by [HomePresenter.DataRangeFilter] 、 [HomePresenter.DataGroupFun]、[HomePresenter.DataQuarterOffsetFun]
     *
     * ps full test in [com.witype.kotlindemo.DataFilterTest]
     */
    @Test
    fun testHttpObservableAndFunWithRemote() {
        log("┌─── testHttpObservableAndFunWithRemote ────────────────────────────────")

        val retrofit = mockRetrofit()
        val startAt = 2008;
        val endAt = 2018;

        val mobileDataUsage = retrofit.create(ApiModel::class.java)
                .getMobileDataUsage(HomePresenter.RESOURCE_ID, LIMIT)
        every { model.getMobileDataUsage(any(), any()) } returns mobileDataUsage

        val httpObserver = object : HttpObserver<List<RecordsBean>>() {

            override fun onSuccess(t: List<RecordsBean>) {
                //do something
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                log("|\t| onError => onGetDataError")
                onGetDataError.onGetDataError(e.message)
            }

            override fun onComplete() {
                log("|\t| onComplete => onGetMobileDataUsage")
                onGetMobileDataUsage.onGetMobileDataUsage()
            }
        }

        val testObserver = TestObserver(httpObserver);
        model.getMobileDataUsage(HomePresenter.RESOURCE_ID, LIMIT)
                .map { t: MobileDateUsageEntity -> t.result?.records }
                .doOnNext { t: List<RecordsBean>? ->
                    log("|\t┌─── input ${t?.size} ────────────────────────────────")
                    t?.forEach { item: RecordsBean -> log("|\t| $item") }
                    log("|\t└─────────────────────────────────────────────")
                }
                .flatMap(HomePresenter.DataRangeFilter(startAt, endAt))
                .doOnNext { t: List<RecordsBean>? ->
                    log("|\t┌─── after filter ${t?.size} ────────────────────────────────")
                    t?.forEach { item: RecordsBean ->
                        log("|\t| $item")
                    }
                    log("|\t└─────────────────────────────────────────────")
                }
                .flatMap(HomePresenter.DataGroupFun(null))
                .doOnNext { t: List<RecordsBean>? ->
                    log("|\t┌─── group and calc item ${t?.size} ────────────────────────────────")
                    log("|\t| ▊ group data ${t?.size} ")
                    t?.forEach { item: RecordsBean -> log("|\t| $item") }
                }
                .flatMap(HomePresenter.DataQuarterOffsetFun())
                .doOnNext { t: List<RecordsBean>? ->
                    log("|\t| ▊ after calc ${t?.size} ")
                    log("|\t| quarter \t\t\t| usage \t\t\t| offset \t\t\t| total \t\t\t")
                    t?.forEach { item: RecordsBean ->
                        log("|\t| ${item.quarter} \t\t\t| ${item.volume_of_mobile_data} \t\t\t| ${item.volume_offset.toFloat().toString()} \t\t\t| ${item.total_of_year} \t\t\t")
                    }
                    log("|\t└─────────────────────────────────────────────")
                }
                .compose(bindUIEvent())
                .subscribe(testObserver)
        testObserver.assertNoErrors()

        log("|\t▊ assert onGetMobileDataUsage called 1 , onGetDataError not call")
        verify(exactly = 1) { onGetMobileDataUsage.onGetMobileDataUsage() }
        verify { onGetDataError wasNot Called }

        log("|\t▊ assert showLoading called 1 , dismissLoading call 1")
        verify(exactly = 1) { showLoading.showLoading() }
        verify(exactly = 1) { showLoading.dismissLoading() }

        log("|\t▊ assert call order showLoading => onGetMobileDataUsage => dismissLoading")
        verifySequence {
            showLoading.showLoading()
            showLoading.dismissLoading()
            onGetMobileDataUsage.onGetMobileDataUsage()
        }

        log("|\t*** DataRangeFilter 、DataGroupFun、DataQuarterOffsetFun testcase see DataFilterTest ***")

        log("└─── TEST PASS ──────────────────────────────────────────────────")
    }

    private fun mockRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("https://data.gov.sg")
                .client(OkHttpClient.Builder().build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    val httpObserver = object : HttpObserver<MobileDateUsageEntity>() {

        override fun onSuccess(t: MobileDateUsageEntity) {
            log("|\t| onSuccess => onGetMobileDataUsage")
            onGetMobileDataUsage.onGetMobileDataUsage()
        }

        override fun onError(e: Throwable) {
            log("|\t| onError => onGetDataError")
            onGetDataError.onGetDataError(e.message)
        }
    }

    fun <T> bindUIEvent(): UIObservableTransformer<T> {
        return object : UIObservableTransformer<T>() {
            override fun hasSubscribe() {
                log("|\t| hasSubscribe => showLoading")
                showLoading.showLoading()
            }

            override fun willComplete() {
                log("|\t| willComplete => dismissLoading")
                showLoading.dismissLoading()
            }
        }
    }

    private fun log(message: String) {
        println(message)
    }

    @After
    fun done() {
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset();
    }
}