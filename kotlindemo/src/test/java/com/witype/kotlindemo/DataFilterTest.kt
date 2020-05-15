package com.witype.kotlindemo

import com.witype.kotlindemo.entity.RecordsBean
import com.witype.kotlindemo.mvp.presenter.HomePresenter
import io.mockk.MockKAnnotations
import io.reactivex.Observable
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import kotlin.random.Random

class DataFilterTest {

    val MAX_QUARTER_OF_YEAR: Int = 4
    val MIN_QUARTER_OF_YEAR: Int = 1;

    lateinit var mData: MutableList<RecordsBean>

    fun getYearRange() : Int {
        return (1000 .. 2000).random()
    }

    @Before
    fun setup() {
        mData = ArrayList()
        val count = (50 .. 100).random()
        var year = getYearRange();
        for (i in 1..count) {
            val spy = RecordsBean()
            val quarter = (1..4).random().coerceAtLeast(1)
            spy.quarter = String.format("%s Q%s", year, quarter)
            spy.volume_of_mobile_data = Random.nextDouble()
            mData.add(spy)
            year = if (i % 4 == 0) getYearRange() else year
        }
    }

    /**
     * 测试从数据集合中筛选出给定的开始时间到结束时间的数据（test filter data in given start and end）
     * step 1: 模拟startAt、endAt，（mock startAt & endAt range）
     * step 2: 通过[HomePresenter.DataRangeFilter]进行测试 （use [HomePresenter.DataRangeFilter] to get expect data）
     * step 3: 检查筛选结果 （assert the result equal to expect）
     * step 4: 对比数据，确保筛选的结果数据和被过滤的数据合起来等于总数据（assert filtered and result data equal to total count）
     */
    @Test
    fun testFilterYear() {
        //get random startAt and endAt
        val mStart: Int = mData[(0 .. mData.size).random()].quarterYearNum
        val mEndAt: Int = mData[(0 .. mData.size).random()].quarterYearNum
        val startAt = mStart.coerceAtMost(mEndAt)
        val endAt = mStart.coerceAtLeast(mEndAt)

        log("┌─── testFilterYear $startAt => $endAt ────────────────────────────────")

        val filterCount = Observable.just(mData)
                .doOnNext { t: MutableList<RecordsBean> ->
                    log("|\t┌─── input ${t.size} ────────────────────────────────")
                    t.forEach { item: RecordsBean -> log("|\t| $item") }
                    log("|\t└─────────────────────────────────────────────")
                }
                .doOnNext { log("|\t▊ begin to filter $startAt & $endAt") }
                .flatMap(HomePresenter.DataRangeFilter(startAt, endAt))
                .doOnNext { t: List<RecordsBean> ->
                    log("|\t┌─── filter ${t.size} ────────────────────────────────")
                    t.forEach { item: RecordsBean -> log("|\t| $item") }
                    log("|\t└─────────────────────────────────────────────")
                }
                .doOnNext { t: List<RecordsBean> ->
                    log("|\t▊ begin to assert")
                    log("|\t┌─── begin to assert ${t.size} ────────────────────────────────")
                    log("|\t| begin to assert year in $startAt ... $endAt")
                    t.forEach { item: RecordsBean ->
                        Assert.assertTrue(item.quarterYearNum in startAt..endAt)
                        log("|\t| ${item.quarterYearNum} in $startAt  $endAt")
                    }
                    log("|\t| success to assert year in $startAt ... $endAt")
                    log("|\t└─────────────────────────────────────────────")
                }
                .flatMap { t -> Observable.fromIterable(t) }
                .test()
                .assertNoErrors()
                .valueCount();

        val filteredCount = Observable.fromIterable(mData)
                .filter { t -> t.quarterYearNum !in startAt..endAt }
                .toList()
                .toObservable()
                .doOnNext { t: List<RecordsBean> ->
                    log("|\t")
                    log("|\t▊ begin get filtered data")
                    log("|\t┌─── filtered ${t.size} ────────────────────────────────")
                    t.forEach { item: RecordsBean -> log("|\t| $item") }
                    log("|\t└─────────────────────────────────────────────")
                }
                .flatMap { t -> Observable.fromIterable(t) }
                .test()
                .assertNoErrors()
                .valueCount()

        log("|\t▊ begin to assert count")
        log("|\t┌─── begin to assert  ────────────────────────────────")
        log("|\t| (filter count : $filterCount , filtered count : $filteredCount) = total count ${mData.size}")
        Assert.assertEquals(filterCount + filteredCount, mData.size)
        log("|\t└─────────────────────────────────────────────")

        log("└─── TEST PASS ──────────────────────────────────────────────────")
    }

    /**
     * 测试通过年份将数据进行分组 （test group data by year）
     * step 1: 使用[HomePresenter.DataGroupFun] 升序分组数据 (use [HomePresenter.DataGroupFun] group by year and order by year asc)
     * step 2: 确保按照年进行分组后每组的数据大于等于4（季度）;(assert group data has some year and count in [MAX_QUARTER_OF_YEAR] & [MIN_QUARTER_OF_YEAR])
     * step 3: 确保每组数据的年份是否一致 (assert each group item data in some year)
     * step 4: 断言分组结果是升序 (assert data group and order by year asc )
     */
    @Test
    fun testDataGroupByYear() {
        log("┌─── testDataGroupByYear  ────────────────────────────────")
        Observable.just(mData)
                .doOnNext { t: MutableList<RecordsBean> ->
                    log("|\t┌─── input ${t.size} ────────────────────────────────")
                    t.forEach { item: RecordsBean -> log("|\t| $item") }
                    log("|\t└─────────────────────────────────────────────")
                }
                .flatMap(HomePresenter.DataGroupFun(4))
                .flatMap { t: List<RecordsBean> ->
                    log("|\t")
                    log("|\t▊ begin get assert group count in $MIN_QUARTER_OF_YEAR .. $MAX_QUARTER_OF_YEAR")
                    log("|\t┌─────────────────────────────────────────")
                    log("|\t| item count : ${t.size} in $MIN_QUARTER_OF_YEAR .. $MAX_QUARTER_OF_YEAR")
                    Assert.assertTrue(t.size in MIN_QUARTER_OF_YEAR..MAX_QUARTER_OF_YEAR)
                    t.forEach { item: RecordsBean ->
                        log("|\t| $item")
                        t.forEach { innerItem: RecordsBean ->
                            Assert.assertTrue(item.quarterYearNum == innerItem.quarterYearNum)
                        }
                    }
                    Observable.fromIterable(t).doOnComplete {log("|\t└─────────────────────────────────────────────")}
                }
                .scan { t1: RecordsBean, t2: RecordsBean ->
                    log("|\t|")
                    log("|\t▊ begin assert order")
                    val i = t2.compareKey - t1.compareKey
                    Assert.assertThat(i, Matchers.greaterThanOrEqualTo(0))
                    log("|\t| $t1")
                    log("|\t| $t2")
                    t2
                }
                .test()
                .assertNoErrors()
        log("└─── TEST PASS ──────────────────────────────────────────────────")
    }

    /**
     * 测试数据计算结果 (test data calc result)
     * 1、年消耗数据 ( data usage total of year)
     * 2、季度同比增长量 (data usage offset with last quarter)
     *
     * step 1:先对数据按照年进行分组 (group data by year)
     * step 2: 通过[HomePresenter.DataQuarterOffsetFun] 进行计算 (use [HomePresenter.DataQuarterOffsetFun] calc result)
     * step 3: 检查年度消耗数据是否计算正常和赋值正确;(assert data result)
     * step 4: 检查季度同步数据是否计算正常;(assert data usage of quarter with last quarter )
     */
    @Test
    fun testDataQuarterOffset() {
        log("┌─── testDataQuarterOffsetFun ────────────────────────────────")
        Observable.just(mData)
                .doOnNext { t: MutableList<RecordsBean> ->
                    log("|\t┌─── input ${t.size} ────────────────────────────────")
                    t.forEach { item: RecordsBean -> log("|\t| $item") }
                    log("|\t└─────────────────────────────────────────────")
                }
                .flatMap(HomePresenter.DataGroupFun(4))
                .flatMap(HomePresenter.DataQuarterOffsetFun())
                .flatMap { it: List<RecordsBean> ->
                    log("|\t")
                    log("|\t┌─── input ${it.size} ────────────────────────────────")
                    var total = 0.0;
                    it.forEach { item ->
                        total += item.volume_of_mobile_data
                        log("|\t| $item")
                    }

                    log("|\t|")
                    log("|\t| total usage of ${it[0].quarterYearNum} => $total")
                    it.forEach { item: RecordsBean ->
                        log("|\t| ${item.quarter} totla => ${item.total_of_year}")
                    }
                    log("|\t└─────────────────────────────────────────────")
                    Observable.fromIterable(it)
                }
                .test()
                .assertNoErrors()
        log("└─── TEST PASS ──────────────────────────────────────────────────")
    }

    private fun log(message: String) {
        println(message)
    }
}