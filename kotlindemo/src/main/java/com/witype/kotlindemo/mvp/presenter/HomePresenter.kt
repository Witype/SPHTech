package com.witype.kotlindemo.mvp.presenter

import com.trello.rxlifecycle3.android.ActivityEvent
import com.witype.kotlindemo.entity.RecordsBean
import com.witype.kotlindemo.integration.HttpObserver
import com.witype.kotlindemo.mvp.contract.HomeView
import com.witype.kotlindemo.mvp.ui.QuarterAdapter
import com.witype.mvp.integration.scope.ActivityScope
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.observables.GroupedObservable
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

@ActivityScope
class HomePresenter @Inject constructor(view: HomeView) : BasePresenter<HomeView>(view) {

    private var startQuarter = DEFAULT_START_YEAR
    private var endQuarter = DEFAULT_END_YEAR

    @set:Inject
    lateinit var quarterAdapter: QuarterAdapter

    fun setEndQuarter(endQuarter: Int) {
        this.endQuarter = endQuarter
    }

    fun setStartQuarter(startQuarter: Int) {
        this.startQuarter = startQuarter
    }

    fun getMobileDataUsage(limit: Int) {
        model.getMobileDataUsage(RESOURCE_ID, limit) //需要和ActivityEvent绑定，这样在Activity销毁时能够取消订阅
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUIEvent())
                .observeOn(Schedulers.single())
                .map { mobileDateUsageEntity -> mobileDateUsageEntity.result!!.records }
                .flatMap(DataRangeFilter(startQuarter, endQuarter))
                .flatMap(DataGroupFun())
                .flatMap(DataQuarterOffsetFun())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : HttpObserver<List<RecordsBean>>() {
                    override fun onSuccess(recordsBeans: List<RecordsBean>) {
                        quarterAdapter.addData(recordsBeans)
                    }

                    override fun onHttpError(message: String?, httpCode: Int) {
                        super.onHttpError(message, httpCode)
                        view.onGetDataError(message)
                    }

                    override fun onComplete() {
                        super.onComplete()
                        quarterAdapter.notifyDataSetChanged()
                    }
                })
    }

    /**
     * 通过年份过滤数据
     * 从startAt—>endAt
     */
    class DataRangeFilter(startAt: Int, endAt: Int) : Function<List<RecordsBean>?, ObservableSource<List<RecordsBean>>> {

        private val startAt: Int = startAt.coerceAtMost(endAt)

        private val endAt: Int = endAt.coerceAtLeast(startAt)

        @Throws(Exception::class)
        override fun apply(recordsBeans: List<RecordsBean>): ObservableSource<List<RecordsBean>> {
            return Observable.fromIterable(recordsBeans)
                    .filter { recordsBean ->
                        val quarterFilter = recordsBean.quarterYearNum
                        quarterFilter in startAt..endAt
                    }
                    .toList()
                    .toObservable()
        }

    }

    /**
     * 将数据按照年进行排序和分组
     * step 1： 对数据按照年份进行分组；
     * step 2： 对分组的数据进行排序，去list[0]的数据，ps：通过分组的数据不会为空，
     * step 3： 对整个List<List>分组数据进行排序；
     * 使用RxJava
     * [Observable.groupBy]     @see #http://reactivex.io/documentation/operators/groupby.html
     * [Observable.toSortedList]        @see #http://reactivex.io/documentation/operators/to.html
     * [Observable.flatMap]     @see #http://reactivex.io/documentation/operators/flatmap.html
    </List> */
    class DataGroupFun : Function<List<RecordsBean>?, ObservableSource<List<RecordsBean>>> {
        @Throws(Exception::class)
        override fun apply(recordsBeans: List<RecordsBean>): ObservableSource<List<RecordsBean>> {
            return Observable.fromIterable(recordsBeans)
                    .groupBy { recordsBean -> recordsBean.quarterYearNum }
                    .flatMap<List<RecordsBean>>(Function<GroupedObservable<Int, RecordsBean>, ObservableSource<List<RecordsBean>>> { integerRecordsBeanGroupedObservable -> integerRecordsBeanGroupedObservable.toSortedList(sortListFun).toObservable() })
                    .toSortedList { recordsBeans, t1 -> ascCompare(recordsBeans[0], t1[0]) }
                    .toObservable()
                    .flatMap { lists ->
                        Observable.fromIterable(lists)
                                .window(1)
                                .flatMap { listObservable -> listObservable }
                    }
        }

        /**
         * 对分组出来的数据进行排序
         */
        private val sortListFun: Comparator<RecordsBean> = Comparator { recordsBean, t1 -> ascCompare(recordsBean, t1) }
    }

    /**
     * 计算年度自己与上一个季度的差值和去年的综合
     * scale    @see http://reactivex.io/documentation/operators/scan.html
     */
    class DataQuarterOffsetFun : Function<List<RecordsBean>, ObservableSource<List<RecordsBean>>> {

        lateinit var total: BigDecimal

        @Throws(Exception::class)
        override fun apply(recordsBeans: List<RecordsBean>): ObservableSource<List<RecordsBean>> {
            total = BigDecimal(0.0)
            return Observable.fromIterable(recordsBeans)
                    .doOnNext { recordsBean ->
                        recordsBean.quarterQuaterNum
                        recordsBean.quarterYearNum
                        total = total.add(BigDecimal(recordsBean.volume_of_mobile_data))
                    }
                    .scan { recordsBean, recordsBean2 ->
                        val offset: Double = BigDecimal(recordsBean2.volume_of_mobile_data).subtract(BigDecimal(recordsBean.volume_of_mobile_data)).toDouble()
                        recordsBean2.volume_offset = offset
                        //                            BigDecimal add = new BigDecimal(recordsBean.getTotal_of_year()).add(new BigDecimal(recordsBean2.getTotal_of_year()));
//                            recordsBean.setTotal_of_year(add.floatValue());
//                            recordsBean2.setTotal_of_year(add.floatValue());
                        recordsBean2
                    }
                    .toList()
                    .toObservable()
                    .doOnNext { recordsBeans ->
                        for (recordsBean in recordsBeans) {
                            recordsBean.total_of_year = total.toDouble()
                        }
                    }
        }
    }

    companion object {
        const val RESOURCE_ID = "a807b7ab-6cad-4aa6-87d0-e283a7353a0f"
        const val DEFAULT_START_YEAR = 2008
        const val DEFAULT_END_YEAR = 2018

        /**
         * 排序的执行方法
         * @param recordsBean
         * @param t1
         * @return
         */
        private fun ascCompare(recordsBean: RecordsBean, t1: RecordsBean): Int {
            return recordsBean.compareKey - t1.compareKey
        }
    }
}