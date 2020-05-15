package com.witype.Dragger;

import com.witype.Dragger.entity.MobileDateUsageEntity;
import com.witype.Dragger.entity.RecordsBean;
import com.witype.Dragger.mvp.model.ApiModel;
import com.witype.Dragger.mvp.present.HomePresenter;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * 测试数据的筛选、分组、计算
 */
@RunWith(MockitoJUnitRunner.class)
public class DataFilterTest {

    /**
     * 允许的一年中有数据的季度数，
     */
    public static final int MAX_QUARTER_OF_YEAR = 4, MIN_QUARTER_OF_YEAR = 1;

    @Mock
    List<RecordsBean> mData;

    Random random;

    @Before
    public void setup() {
        random = new Random();
        mData = new ArrayList<>();
        int count = Math.max(random.nextInt(50), 20);
        int year = random.nextInt(2000);
        for (int i = 1; i <= count; i++) {
            RecordsBean spy = new RecordsBean();
            int quarter = Math.max(random.nextInt(4), 1);
//            doReturn(String.format("%s Q%s",year,quarter)).when(spy).getQuarter();
//            doReturn(String.valueOf(random.nextDouble())).when(spy).getVolume_of_mobile_data();
//            doReturn(quarter).when(spy).getQuarterQStr();
//            doReturn(year).when(spy).getQuarterYStr();
            spy.setQuarter(String.format("%s Q%s", year, quarter));
            spy.setVolume_of_mobile_data(random.nextDouble());
            mData.add(spy);
            year = i % 4 == 0 ? random.nextInt(2000) : year;
        }
    }

    /**
     * 测试过滤年份
     */
    @Test
    public void testFilterYear() {
        log("┌─── testFilterYear ───────────────────────────────────────────────────");
        int mStart = mData.get(random.nextInt(mData.size())).getQuarterYearNum();
        int mEndAt = mData.get(random.nextInt(mData.size())).getQuarterYearNum();
        int startAt = Math.min(mStart, mEndAt);
        int endAt = Math.max(mStart, mEndAt);
        //满足条件的筛选
        int filterCount = Observable.just(mData)
                .doOnNext(new Consumer<List<RecordsBean>>() {
                    @Override
                    public void accept(List<RecordsBean> recordsBeans) throws Exception {
                        log("|\t");
                        log(String.format("|\t┌─── input %s─────────────────────────────────────────────────────", recordsBeans.size()));
                        log(String.format("|\t▊ begin filter %s => %s", startAt, endAt));
                        for (RecordsBean recordsBean : recordsBeans) {
                            log(String.format("|\t| %s", recordsBean.toString()));
                        }
                        log("|\t└───────────────────────────────────────────────────────────");
                    }
                })
                .flatMap(new HomePresenter.DataRangeFilter(startAt, endAt))
                .doOnNext(new Consumer<List<RecordsBean>>() {
                    @Override
                    public void accept(List<RecordsBean> recordsBeans) throws Exception {
                        log(String.format("|\t┌─── after filter %s──────────────────────────────────────────────────", recordsBeans.size()));
                        for (RecordsBean recordsBean : recordsBeans) {
                            log(String.format("|\t| %s", recordsBean.toString()));
                            assertThat(recordsBean.getQuarterYearNum(), Matchers.lessThanOrEqualTo(endAt));
                            assertThat(recordsBean.getQuarterYearNum(), Matchers.greaterThanOrEqualTo(startAt));
                        }
                        log("|\t└───────────────────────────────────────────────────────────");
                    }
                })
                .flatMap(new Function<List<RecordsBean>, ObservableSource<RecordsBean>>() {
                    @Override
                    public ObservableSource<RecordsBean> apply(List<RecordsBean> recordsBeans) throws Exception {
                        return Observable.fromIterable(recordsBeans);
                    }
                })
                .test()
                .assertNoErrors()
                .valueCount();
        //查找相反条件的数据
        int contraryCount = Observable.fromIterable(mData)
                .filter(new Predicate<RecordsBean>() {
                    @Override
                    public boolean test(RecordsBean recordsBean) throws Exception {
                        int quarterFilter = recordsBean.getQuarterYearNum();
                        return quarterFilter < startAt || quarterFilter > endAt;
                    }
                })
                .test()
                .valueCount();
        log("|\t▊ assert count");
        log(String.format("|\t total %s , filter %s , contrary %s ", mData.size(), filterCount, contraryCount));
        assertEquals(filterCount + contraryCount, mData.size());
        log("└─────────────────────────────────────────────────────────────");
    }

    /**
     * test 排序
     */
    @Test
    public void testSortList() {
        log("┌─── testSortList ───────────────────────────────────────────────────");
        Observable.just(mData)
                .doOnNext(new Consumer<List<RecordsBean>>() {
                    @Override
                    public void accept(List<RecordsBean> recordsBeans) throws Exception {
                        log(String.format("|\t┌─── input item %s ──────────────────────────────────────────────────", recordsBeans.size()));
                        for (RecordsBean recordsBean : recordsBeans) {
                            log(String.format("|\t| %s", recordsBean.toString()));
                        }
                        log("|\t└───────────────────────────────────────────────────────────");
                        log("|\t▊ begin filter");
                    }
                })
                .flatMap(new HomePresenter.DataGroupFun())
                .flatMap((Function<List<RecordsBean>, ObservableSource<RecordsBean>>) recordsBeans -> {
                    log("|\t");
                    log(String.format("|\t┌─── item %s ──────────────────────────────────────────────────", recordsBeans.size()));
                    return Observable.fromIterable(recordsBeans)
                            .doOnNext(new Consumer<RecordsBean>() {
                                @Override
                                public void accept(RecordsBean recordsBean) throws Exception {
                                    log(String.format("|\t| %s", recordsBean.toString()));
                                }
                            })
                            .doOnComplete(new Action() {
                                @Override
                                public void run() throws Exception {
                                    log("|\t└───────────────────────────────────────────────────────────");
                                }
                            });
                })
                .scan(new BiFunction<RecordsBean, RecordsBean, RecordsBean>() {
                    @Override
                    public RecordsBean apply(RecordsBean recordsBean, RecordsBean recordsBean2) throws Exception {
                        int offset = recordsBean2.getCompareKey() - recordsBean.getCompareKey();
                        assertThat(offset, Matchers.greaterThanOrEqualTo(0));
                        return recordsBean2;
                    }
                })
                .test()
                .assertNoErrors();
        log("└─────────────────────────────────────────────────────────────");
    }

    /**
     * 测试:测试每年数据消耗的总和
     */
    @Test
    public void testDataQuarterOffsetFun() {
        log("┌─── testDataQuarterOffsetFun ────────────────────────────────");
        Observable.just(mData)
                .flatMap(new HomePresenter.DataGroupFun())
                .flatMap(new HomePresenter.DataQuarterOffsetFun())
                .doOnNext(new Consumer<List<RecordsBean>>() {
                    @Override
                    public void accept(List<RecordsBean> recordsBeans) throws Exception {
                        assertThat(recordsBeans.size(),Matchers.lessThanOrEqualTo(MAX_QUARTER_OF_YEAR));
                        assertThat(recordsBeans.size(),Matchers.greaterThanOrEqualTo(MIN_QUARTER_OF_YEAR));
                        log("|\t");
                        log(String.format("|\t┌─── input item %s ────────────────────────────────────────────────", recordsBeans.size()));
                        BigDecimal total = new BigDecimal(0);
                        for (RecordsBean recordsBean : recordsBeans) {
                            log(String.format("|\t| %s", recordsBean.toString()));
                            total = total.add(new BigDecimal(recordsBean.getVolume_of_mobile_data()));
                        }
                        log("|\t|");
                        log("|\t▊ assert total");
                        for (RecordsBean recordsBean : recordsBeans) {
                            assertEquals(String.valueOf(total.floatValue()), String.valueOf(new BigDecimal(recordsBean.getTotal_of_year()).floatValue()));
                            log(String.format("|\t| assert total of year : %s => %s", total.doubleValue(), recordsBean.getTotal_of_year()));
                        }
                    }
                })
                .flatMap(new Function<List<RecordsBean>, ObservableSource<RecordsBean>>() {
                    @Override
                    public ObservableSource<RecordsBean> apply(List<RecordsBean> recordsBeans) throws Exception {
                        return Observable.fromIterable(recordsBeans)
                                .doOnComplete(new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        log("|\t└───────────────────────────────────────────────────────────");
                                    }
                                });
                    }
                })
                .scan(new BiFunction<RecordsBean, RecordsBean, RecordsBean>() {
                    @Override
                    public RecordsBean apply(RecordsBean recordsBean, RecordsBean recordsBean2) throws Exception {

                        BigDecimal offset = new BigDecimal(recordsBean2.getVolume_of_mobile_data()).subtract(new BigDecimal(recordsBean.getVolume_of_mobile_data()));
                        if (offset.doubleValue() < 0) {
                            //确保当一年中某一个季度数据下降时数据计算正确
                            assertEquals(recordsBean2.getVolume_offset(), offset.doubleValue(), 16);
                        }
                        log("|\t|");
                        log("|\t▊ assert volume offset");
                        log(String.format("|\t| %s", recordsBean.toString()));
                        log(String.format("|\t| %s", recordsBean2.toString()));
                        log(String.format("|\t| assert volume offset : %s => %s", offset, recordsBean2.getVolume_offset()));
                        return recordsBean2;
                    }
                })
                .test()
                .assertNoErrors();
        log("└─────────────────────────────────────────────────────────────");
    }

    /**
     * 测试{@link ApiModel#getMobileDataUsage(String, int)}https://data.gov.sg获取数据，
     * see <a>https://data.gov.sg/api/action/datastore_search?resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f&limit=5</a>
     */
    @Test
    public void testGetRemoteData() {
        log("┌─── testGetRemoteData ────────────────────────────────");
        int startAt = 2008;
        int endAt = 2018;
        Retrofit build = new Retrofit.Builder().baseUrl("https://data.gov.sg")
                .client(new OkHttpClient.Builder().build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        build.create(ApiModel.class)
                .getMobileDataUsage(HomePresenter.RESOURCE_ID, 100)
                .flatMap(new Function<MobileDateUsageEntity, ObservableSource<List<RecordsBean>>>() {
                    @Override
                    public ObservableSource<List<RecordsBean>> apply(MobileDateUsageEntity mobileDateUsageEntity) throws Exception {
                        return Observable.just(mobileDateUsageEntity.getResult().getRecords());
                    }
                })
                .doOnNext(new Consumer<List<RecordsBean>>() {
                    @Override
                    public void accept(List<RecordsBean> recordsBeans) throws Exception {
                        log("|\t┌─── on get data ────────────────────────────────");
                        for (RecordsBean recordsBean : recordsBeans) {
                            log(String.format("|\t| %s", recordsBean.toString()));
                        }
                        log("|\t└───────────────────────────────────────────────────────────");
                        log("|\t▊ begin year filter test");
                    }
                })
                .flatMap(new HomePresenter.DataRangeFilter(startAt, endAt))
                .doOnNext(new Consumer<List<RecordsBean>>() {
                    @Override
                    public void accept(List<RecordsBean> recordsBeans) throws Exception {
                        log("|\t┌─── assert year filter ────────────────────────────────");
                        for (RecordsBean recordsBean : recordsBeans) {
                            assertThat(recordsBean.getQuarterYearNum(), Matchers.greaterThanOrEqualTo(startAt));
                            assertThat(recordsBean.getQuarterYearNum(), Matchers.lessThanOrEqualTo(endAt));
                            log(String.format("|\t| %s >= %s <= %s", startAt, recordsBean.getQuarterYearNum(), endAt));
                        }
                        log("|\t└───────────────────────────────────────────────────────────");
                    }
                })
                .doOnNext(new Consumer<List<RecordsBean>>() {
                    @Override
                    public void accept(List<RecordsBean> recordsBeans) throws Exception {
                        log("|\t");
                        log("|\t▊ begin group test");
                    }
                })
                .flatMap(new HomePresenter.DataGroupFun())
                .flatMap(new HomePresenter.DataQuarterOffsetFun())
                .flatMap(new Function<List<RecordsBean>, ObservableSource<RecordsBean>>() {
                    @Override
                    public ObservableSource<RecordsBean> apply(List<RecordsBean> recordsBeans) throws Exception {
                        assertThat(recordsBeans.size(),Matchers.lessThanOrEqualTo(MAX_QUARTER_OF_YEAR));
                        assertThat(recordsBeans.size(),Matchers.greaterThanOrEqualTo(MIN_QUARTER_OF_YEAR));
                        log("|\t");
                        log(String.format("|\t┌─── input item %s ────────────────────────────────────────────────", recordsBeans.size()));
                        BigDecimal total = new BigDecimal(0);
                        for (RecordsBean recordsBean : recordsBeans) {
                            log(String.format("|\t| %s", recordsBean.toString()));
                            total = total.add(new BigDecimal(recordsBean.getVolume_of_mobile_data()));
                        }
                        log("|\t|");
                        log("|\t▊ assert total");
                        for (RecordsBean recordsBean : recordsBeans) {
                            assertEquals(total.floatValue(), recordsBean.getTotal_of_year(), 16);
                            log(String.format("|\t| assert total of year : %s => %s", total.doubleValue(), recordsBean.getTotal_of_year()));
                        }

                        return Observable.fromIterable(recordsBeans)
                                .doOnComplete(new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        log("|\t└───────────────────────────────────────────────────────────");
                                    }
                                });
                    }
                })
                .scan(new BiFunction<RecordsBean, RecordsBean, RecordsBean>() {
                    @Override
                    public RecordsBean apply(RecordsBean recordsBean, RecordsBean recordsBean2) throws Exception {
                        //验证是否是升序排序
                        int offsetIndex = recordsBean2.getCompareKey() - recordsBean.getCompareKey();
                        assertThat(offsetIndex, Matchers.greaterThanOrEqualTo(0));

                        BigDecimal offset = new BigDecimal(recordsBean2.getVolume_of_mobile_data()).subtract(new BigDecimal(recordsBean.getVolume_of_mobile_data()));
                        if (offset.doubleValue() < 0) {
                            //确保当一年中某一个季度数据下降时数据计算正确
                            assertEquals(recordsBean2.getVolume_offset(), offset.doubleValue(), 16);
                        }
                        log("|\t|");
                        log("|\t▎ assert volume offset");
                        log(String.format("|\t| %s", recordsBean.toString()));
                        log(String.format("|\t| %s", recordsBean2.toString()));
                        log(String.format("|\t| assert volume offset : %s => %s", offset, recordsBean2.getVolume_offset()));

                        return recordsBean2;
                    }
                })
                .test()
                .assertNoErrors();
        log("└──── ALL TEST PASS ────────────────────────────────────────────────────");
    }


    private void log(String message) {
        System.out.println(message);
    }

}
