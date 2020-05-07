package com.witype.Dragger;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.witype.Dragger.entity.BaseHttpResponseEntity;
import com.witype.Dragger.entity.MobileDateUsageEntity;
import com.witype.Dragger.entity.RecordsBean;
import com.witype.Dragger.integration.HttpObserver;
import com.witype.Dragger.integration.StructureFunction;
import com.witype.Dragger.integration.UIObservableTransformer;
import com.witype.Dragger.integration.exception.ResponseException;
import com.witype.Dragger.mvp.present.HomePresenter;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import io.reactivex.Observable;

import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import timber.log.Timber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    public static final String TAG = "Test_";

    public static final String json = "{\"help\":\"https://data.gov.sg/api/3/action/help_show?name=datastore_search\",\"success\":true,\"result\":{\"resource_id\":\"a807b7ab-6cad-4aa6-87d0-e283a7353a0f\",\"fields\":[{\"type\":\"int4\",\"id\":\"_id\"},{\"type\":\"text\",\"id\":\"quarter\"},{\"type\":\"numeric\",\"id\":\"volume_of_mobile_data\"}],\"records\":[{\"volume_of_mobile_data\":\"0.000384\",\"quarter\":\"2004-Q3\",\"_id\":1},{\"volume_of_mobile_data\":\"0.000543\",\"quarter\":\"2004-Q4\",\"_id\":2},{\"volume_of_mobile_data\":\"0.00062\",\"quarter\":\"2005-Q1\",\"_id\":3},{\"volume_of_mobile_data\":\"0.000634\",\"quarter\":\"2005-Q2\",\"_id\":4},{\"volume_of_mobile_data\":\"0.000718\",\"quarter\":\"2005-Q3\",\"_id\":5},{\"volume_of_mobile_data\":\"0.000801\",\"quarter\":\"2005-Q4\",\"_id\":6},{\"volume_of_mobile_data\":\"0.00089\",\"quarter\":\"2006-Q1\",\"_id\":7},{\"volume_of_mobile_data\":\"0.001189\",\"quarter\":\"2006-Q2\",\"_id\":8},{\"volume_of_mobile_data\":\"0.001735\",\"quarter\":\"2006-Q3\",\"_id\":9},{\"volume_of_mobile_data\":\"0.003323\",\"quarter\":\"2006-Q4\",\"_id\":10},{\"volume_of_mobile_data\":\"0.012635\",\"quarter\":\"2007-Q1\",\"_id\":11},{\"volume_of_mobile_data\":\"0.029992\",\"quarter\":\"2007-Q2\",\"_id\":12},{\"volume_of_mobile_data\":\"0.053584\",\"quarter\":\"2007-Q3\",\"_id\":13},{\"volume_of_mobile_data\":\"0.100934\",\"quarter\":\"2007-Q4\",\"_id\":14},{\"volume_of_mobile_data\":\"0.171586\",\"quarter\":\"2008-Q1\",\"_id\":15},{\"volume_of_mobile_data\":\"0.248899\",\"quarter\":\"2008-Q2\",\"_id\":16},{\"volume_of_mobile_data\":\"0.439655\",\"quarter\":\"2008-Q3\",\"_id\":17},{\"volume_of_mobile_data\":\"0.683579\",\"quarter\":\"2008-Q4\",\"_id\":18},{\"volume_of_mobile_data\":\"1.066517\",\"quarter\":\"2009-Q1\",\"_id\":19},{\"volume_of_mobile_data\":\"1.357248\",\"quarter\":\"2009-Q2\",\"_id\":20},{\"volume_of_mobile_data\":\"1.695704\",\"quarter\":\"2009-Q3\",\"_id\":21},{\"volume_of_mobile_data\":\"2.109516\",\"quarter\":\"2009-Q4\",\"_id\":22},{\"volume_of_mobile_data\":\"2.3363\",\"quarter\":\"2010-Q1\",\"_id\":23},{\"volume_of_mobile_data\":\"2.777817\",\"quarter\":\"2010-Q2\",\"_id\":24},{\"volume_of_mobile_data\":\"3.002091\",\"quarter\":\"2010-Q3\",\"_id\":25},{\"volume_of_mobile_data\":\"3.336984\",\"quarter\":\"2010-Q4\",\"_id\":26},{\"volume_of_mobile_data\":\"3.466228\",\"quarter\":\"2011-Q1\",\"_id\":27},{\"volume_of_mobile_data\":\"3.380723\",\"quarter\":\"2011-Q2\",\"_id\":28},{\"volume_of_mobile_data\":\"3.713792\",\"quarter\":\"2011-Q3\",\"_id\":29},{\"volume_of_mobile_data\":\"4.07796\",\"quarter\":\"2011-Q4\",\"_id\":30},{\"volume_of_mobile_data\":\"4.679465\",\"quarter\":\"2012-Q1\",\"_id\":31},{\"volume_of_mobile_data\":\"5.331562\",\"quarter\":\"2012-Q2\",\"_id\":32},{\"volume_of_mobile_data\":\"5.614201\",\"quarter\":\"2012-Q3\",\"_id\":33},{\"volume_of_mobile_data\":\"5.903005\",\"quarter\":\"2012-Q4\",\"_id\":34},{\"volume_of_mobile_data\":\"5.807872\",\"quarter\":\"2013-Q1\",\"_id\":35},{\"volume_of_mobile_data\":\"7.053642\",\"quarter\":\"2013-Q2\",\"_id\":36},{\"volume_of_mobile_data\":\"7.970536\",\"quarter\":\"2013-Q3\",\"_id\":37},{\"volume_of_mobile_data\":\"7.664802\",\"quarter\":\"2013-Q4\",\"_id\":38},{\"volume_of_mobile_data\":\"7.73018\",\"quarter\":\"2014-Q1\",\"_id\":39},{\"volume_of_mobile_data\":\"7.907798\",\"quarter\":\"2014-Q2\",\"_id\":40},{\"volume_of_mobile_data\":\"8.629095\",\"quarter\":\"2014-Q3\",\"_id\":41},{\"volume_of_mobile_data\":\"9.327967\",\"quarter\":\"2014-Q4\",\"_id\":42},{\"volume_of_mobile_data\":\"9.687363\",\"quarter\":\"2015-Q1\",\"_id\":43},{\"volume_of_mobile_data\":\"9.98677\",\"quarter\":\"2015-Q2\",\"_id\":44},{\"volume_of_mobile_data\":\"10.902194\",\"quarter\":\"2015-Q3\",\"_id\":45},{\"volume_of_mobile_data\":\"10.677166\",\"quarter\":\"2015-Q4\",\"_id\":46},{\"volume_of_mobile_data\":\"10.96733\",\"quarter\":\"2016-Q1\",\"_id\":47},{\"volume_of_mobile_data\":\"11.38734\",\"quarter\":\"2016-Q2\",\"_id\":48},{\"volume_of_mobile_data\":\"12.14232\",\"quarter\":\"2016-Q3\",\"_id\":49},{\"volume_of_mobile_data\":\"12.86429\",\"quarter\":\"2016-Q4\",\"_id\":50},{\"volume_of_mobile_data\":\"13.29757\",\"quarter\":\"2017-Q1\",\"_id\":51},{\"volume_of_mobile_data\":\"14.54179\",\"quarter\":\"2017-Q2\",\"_id\":52},{\"volume_of_mobile_data\":\"14.88463\",\"quarter\":\"2017-Q3\",\"_id\":53},{\"volume_of_mobile_data\":\"15.77653\",\"quarter\":\"2017-Q4\",\"_id\":54},{\"volume_of_mobile_data\":\"16.47121\",\"quarter\":\"2018-Q1\",\"_id\":55},{\"volume_of_mobile_data\":\"18.47368\",\"quarter\":\"2018-Q2\",\"_id\":56},{\"volume_of_mobile_data\":\"19.97554729\",\"quarter\":\"2018-Q3\",\"_id\":57},{\"volume_of_mobile_data\":\"20.43921113\",\"quarter\":\"2018-Q4\",\"_id\":58},{\"volume_of_mobile_data\":\"20.53504752\",\"quarter\":\"2019-Q1\",\"_id\":59}],\"_links\":{\"start\":\"/api/action/datastore_search?resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f\",\"next\":\"/api/action/datastore_search?offset=100&resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f\"},\"total\":59}}";
    public static final String structureJson = "{\"message\":\"success\",\"code\":0,\"data\":{\"help\":\"https://data.gov.sg/api/3/action/help_show?name=datastore_search\",\"success\":true,\"result\":{\"resource_id\":\"a807b7ab-6cad-4aa6-87d0-e283a7353a0f\",\"fields\":[{\"type\":\"int4\",\"id\":\"_id\"},{\"type\":\"text\",\"id\":\"quarter\"},{\"type\":\"numeric\",\"id\":\"volume_of_mobile_data\"}],\"records\":[{\"volume_of_mobile_data\":\"0.000384\",\"quarter\":\"2004-Q3\",\"_id\":1},{\"volume_of_mobile_data\":\"0.000543\",\"quarter\":\"2004-Q4\",\"_id\":2},{\"volume_of_mobile_data\":\"0.00062\",\"quarter\":\"2005-Q1\",\"_id\":3},{\"volume_of_mobile_data\":\"0.000634\",\"quarter\":\"2005-Q2\",\"_id\":4},{\"volume_of_mobile_data\":\"0.000718\",\"quarter\":\"2005-Q3\",\"_id\":5},{\"volume_of_mobile_data\":\"0.000801\",\"quarter\":\"2005-Q4\",\"_id\":6},{\"volume_of_mobile_data\":\"0.00089\",\"quarter\":\"2006-Q1\",\"_id\":7},{\"volume_of_mobile_data\":\"0.001189\",\"quarter\":\"2006-Q2\",\"_id\":8},{\"volume_of_mobile_data\":\"0.001735\",\"quarter\":\"2006-Q3\",\"_id\":9},{\"volume_of_mobile_data\":\"0.003323\",\"quarter\":\"2006-Q4\",\"_id\":10},{\"volume_of_mobile_data\":\"0.012635\",\"quarter\":\"2007-Q1\",\"_id\":11},{\"volume_of_mobile_data\":\"0.029992\",\"quarter\":\"2007-Q2\",\"_id\":12},{\"volume_of_mobile_data\":\"0.053584\",\"quarter\":\"2007-Q3\",\"_id\":13},{\"volume_of_mobile_data\":\"0.100934\",\"quarter\":\"2007-Q4\",\"_id\":14},{\"volume_of_mobile_data\":\"0.171586\",\"quarter\":\"2008-Q1\",\"_id\":15},{\"volume_of_mobile_data\":\"0.248899\",\"quarter\":\"2008-Q2\",\"_id\":16},{\"volume_of_mobile_data\":\"0.439655\",\"quarter\":\"2008-Q3\",\"_id\":17},{\"volume_of_mobile_data\":\"0.683579\",\"quarter\":\"2008-Q4\",\"_id\":18},{\"volume_of_mobile_data\":\"1.066517\",\"quarter\":\"2009-Q1\",\"_id\":19},{\"volume_of_mobile_data\":\"1.357248\",\"quarter\":\"2009-Q2\",\"_id\":20},{\"volume_of_mobile_data\":\"1.695704\",\"quarter\":\"2009-Q3\",\"_id\":21},{\"volume_of_mobile_data\":\"2.109516\",\"quarter\":\"2009-Q4\",\"_id\":22},{\"volume_of_mobile_data\":\"2.3363\",\"quarter\":\"2010-Q1\",\"_id\":23},{\"volume_of_mobile_data\":\"2.777817\",\"quarter\":\"2010-Q2\",\"_id\":24},{\"volume_of_mobile_data\":\"3.002091\",\"quarter\":\"2010-Q3\",\"_id\":25},{\"volume_of_mobile_data\":\"3.336984\",\"quarter\":\"2010-Q4\",\"_id\":26},{\"volume_of_mobile_data\":\"3.466228\",\"quarter\":\"2011-Q1\",\"_id\":27},{\"volume_of_mobile_data\":\"3.380723\",\"quarter\":\"2011-Q2\",\"_id\":28},{\"volume_of_mobile_data\":\"3.713792\",\"quarter\":\"2011-Q3\",\"_id\":29},{\"volume_of_mobile_data\":\"4.07796\",\"quarter\":\"2011-Q4\",\"_id\":30},{\"volume_of_mobile_data\":\"4.679465\",\"quarter\":\"2012-Q1\",\"_id\":31},{\"volume_of_mobile_data\":\"5.331562\",\"quarter\":\"2012-Q2\",\"_id\":32},{\"volume_of_mobile_data\":\"5.614201\",\"quarter\":\"2012-Q3\",\"_id\":33},{\"volume_of_mobile_data\":\"5.903005\",\"quarter\":\"2012-Q4\",\"_id\":34},{\"volume_of_mobile_data\":\"5.807872\",\"quarter\":\"2013-Q1\",\"_id\":35},{\"volume_of_mobile_data\":\"7.053642\",\"quarter\":\"2013-Q2\",\"_id\":36},{\"volume_of_mobile_data\":\"7.970536\",\"quarter\":\"2013-Q3\",\"_id\":37},{\"volume_of_mobile_data\":\"7.664802\",\"quarter\":\"2013-Q4\",\"_id\":38},{\"volume_of_mobile_data\":\"7.73018\",\"quarter\":\"2014-Q1\",\"_id\":39},{\"volume_of_mobile_data\":\"7.907798\",\"quarter\":\"2014-Q2\",\"_id\":40},{\"volume_of_mobile_data\":\"8.629095\",\"quarter\":\"2014-Q3\",\"_id\":41},{\"volume_of_mobile_data\":\"9.327967\",\"quarter\":\"2014-Q4\",\"_id\":42},{\"volume_of_mobile_data\":\"9.687363\",\"quarter\":\"2015-Q1\",\"_id\":43},{\"volume_of_mobile_data\":\"9.98677\",\"quarter\":\"2015-Q2\",\"_id\":44},{\"volume_of_mobile_data\":\"10.902194\",\"quarter\":\"2015-Q3\",\"_id\":45},{\"volume_of_mobile_data\":\"10.677166\",\"quarter\":\"2015-Q4\",\"_id\":46},{\"volume_of_mobile_data\":\"10.96733\",\"quarter\":\"2016-Q1\",\"_id\":47},{\"volume_of_mobile_data\":\"11.38734\",\"quarter\":\"2016-Q2\",\"_id\":48},{\"volume_of_mobile_data\":\"12.14232\",\"quarter\":\"2016-Q3\",\"_id\":49},{\"volume_of_mobile_data\":\"12.86429\",\"quarter\":\"2016-Q4\",\"_id\":50},{\"volume_of_mobile_data\":\"13.29757\",\"quarter\":\"2017-Q1\",\"_id\":51},{\"volume_of_mobile_data\":\"14.54179\",\"quarter\":\"2017-Q2\",\"_id\":52},{\"volume_of_mobile_data\":\"14.88463\",\"quarter\":\"2017-Q3\",\"_id\":53},{\"volume_of_mobile_data\":\"15.77653\",\"quarter\":\"2017-Q4\",\"_id\":54},{\"volume_of_mobile_data\":\"16.47121\",\"quarter\":\"2018-Q1\",\"_id\":55},{\"volume_of_mobile_data\":\"18.47368\",\"quarter\":\"2018-Q2\",\"_id\":56},{\"volume_of_mobile_data\":\"19.97554729\",\"quarter\":\"2018-Q3\",\"_id\":57},{\"volume_of_mobile_data\":\"20.43921113\",\"quarter\":\"2018-Q4\",\"_id\":58},{\"volume_of_mobile_data\":\"20.53504752\",\"quarter\":\"2019-Q1\",\"_id\":59}],\"_links\":{\"start\":\"/api/action/datastore_search?resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f\",\"next\":\"/api/action/datastore_search?offset=100&resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f\"},\"total\":59}}}";

    public static final int START_AT = 2008 , END_AT = 2018;

    /**
     * 允许的一年中有数据的季度数，
     */
    public static final int MAX_QUARTER_OF_YEAR = 4, MIN_QUARTER_OF_YEAR = 1;

    private MobileDateUsageEntity mobileDateUsageEntity;

    @Rule
    public RxSchedulerRule rule = new RxSchedulerRule();

    @Before
    public void setup() {
        mobileDateUsageEntity = new Gson().fromJson(json, MobileDateUsageEntity.class);
        RxJavaPlugins.reset();
        RxJavaPlugins.setIoSchedulerHandler(new Function<Scheduler, Scheduler>() {
            @Override
            public Scheduler apply(Scheduler scheduler) throws Exception {
                return Schedulers.trampoline();
            }
        });
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.witype.SPHTech", appContext.getPackageName());
    }


    /**
     * 测试 {@link com.witype.Dragger.mvp.present.HomePresenter.DataGroupFun} 排序与分组逻辑
     * 测试 {@link com.witype.Dragger.mvp.present.HomePresenter.DataQuarterOffsetFun} 数据差值计算逻辑
     */
    @Test
    public void sortList() {
        TestObserver<List<RecordsBean>> test = Observable.just(mobileDateUsageEntity.getResult().getRecords())
                .flatMap(new HomePresenter.DataGroupFun(START_AT, END_AT))
                .flatMap(new HomePresenter.DataQuarterOffsetFun())
                .test()
                //确保筛选出来的长度和START_AT\END_AT数量匹配
                .assertValueCount(END_AT - START_AT + 1);
        List<List<RecordsBean>> recordsBeans = test.values();
        for (List<RecordsBean> recordsBean : recordsBeans) {
            assertThat(recordsBean.size(), Matchers.greaterThanOrEqualTo(MIN_QUARTER_OF_YEAR));
            assertThat(recordsBean.size(), Matchers.equalTo(MAX_QUARTER_OF_YEAR));

            BigDecimal total = new BigDecimal(0.0);
            //计算一年内所有季度的流量总消耗
            for (RecordsBean value : recordsBean) {
                total = total.add(new BigDecimal(String.valueOf(value.getVolume_of_mobile_data())));
            }
            //对比总流量消耗和实际赋值的数据是否一致
            for (RecordsBean value : recordsBean) {
                assertThat(new BigDecimal(value.getTotal_of_year()).doubleValue(),Matchers.equalTo(total.doubleValue()));
            }

            //1、测试季度增长差值，2、计算是否是asc排序
            Observable.fromIterable(recordsBean)
                    .scan(new BiFunction<RecordsBean, RecordsBean, RecordsBean>() {
                        @Override
                        public RecordsBean apply(RecordsBean recordsBean, RecordsBean recordsBean2) throws Exception {
                            BigDecimal offset = new BigDecimal(recordsBean2.getVolume_of_mobile_data()).subtract(new BigDecimal(recordsBean.getVolume_of_mobile_data()));
                            if (offset.doubleValue() < 0) {
                                //确保当一年中某一个季度数据下降时数据计算正确
                                assertEquals(recordsBean2.getVolume_offset(),offset.doubleValue(),10);
                            }
                            assertEquals(recordsBean2.get_id() - recordsBean.get_id(), 1);
                            return recordsBean2;
                        }
                    })
                    .test()
                    .assertNoErrors();
        }
    }

    @Test
    public void testBaseHttpObserver() {
        Timber.tag(ExampleInstrumentedTest.TAG).d("testBaseHttpObserver");
        TestScheduler testScheduler = new TestScheduler();
        Observable.just(json)
                .map(new Function<String, MobileDateUsageEntity>() {
                    @Override
                    public MobileDateUsageEntity apply(String s) throws Exception {
                        return new Gson().fromJson(json, MobileDateUsageEntity.class);
                    }
                })
                .compose(bindUIEvent())
                .subscribe(new HttpObserver<MobileDateUsageEntity>() {
                    @Override
                    public void onSuccess(MobileDateUsageEntity mobileDateUsageEntity) {
                        Timber.tag(ExampleInstrumentedTest.TAG).d(mobileDateUsageEntity.toString());
                    }
                });
        testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
    }

    @Test
    public void testBaseHttpObserverWithError() {
        Timber.tag(ExampleInstrumentedTest.TAG).d("testBaseHttpObserver");
        HttpObserver<MobileDateUsageEntity> httpObserver = new HttpObserver<MobileDateUsageEntity>() {
            @Override
            public void onSuccess(MobileDateUsageEntity mobileDateUsageEntity) {
                Timber.tag(ExampleInstrumentedTest.TAG).d(mobileDateUsageEntity.toString());
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Timber.tag(ExampleInstrumentedTest.TAG).d(e.getMessage());
            }
        };
        TestObserver<MobileDateUsageEntity> testObserver = new TestObserver<MobileDateUsageEntity>(httpObserver);
        Observable.just(json)
                .map(new Function<String, MobileDateUsageEntity>() {
                    @Override
                    public MobileDateUsageEntity apply(String s) throws Exception {
                        return new Gson().fromJson(s, MobileDateUsageEntity.class);
                    }
                })
                .doOnNext(new Consumer<MobileDateUsageEntity>() {
                    @Override
                    public void accept(MobileDateUsageEntity mobileDateUsageEntity) throws Exception {
                        throw new SocketTimeoutException("mock SocketTimeoutException");
                    }
                })
                .compose(bindUIEvent())
                .subscribe(testObserver);
        testObserver.assertError(SocketTimeoutException.class);
    }

    /**
     * 测试Http请求相应结果统一处理
     */
    @Test
    public void testStructureObserver() {
        Timber.tag(ExampleInstrumentedTest.TAG).d("testStructureObserver");
        HttpObserver<MobileDateUsageEntity> httpObserver = new HttpObserver<MobileDateUsageEntity>() {
            @Override
            public void onSuccess(MobileDateUsageEntity mobileDateUsageEntity) {
                Timber.tag(ExampleInstrumentedTest.TAG).d("onSuccess : %s " ,mobileDateUsageEntity.toString());
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Timber.tag(ExampleInstrumentedTest.TAG).d(e.getMessage());
            }
        };
        TestObserver<MobileDateUsageEntity> testObserver = new TestObserver<MobileDateUsageEntity>(httpObserver);
        Observable.just(structureJson)
                .map(new Function<String, BaseHttpResponseEntity<MobileDateUsageEntity>>() {
                    @Override
                    public BaseHttpResponseEntity<MobileDateUsageEntity> apply(String s) throws Exception {
                        Type type = new TypeToken<BaseHttpResponseEntity<MobileDateUsageEntity>>() {}.getType();
                        return new Gson().fromJson(s, type);
                    }
                })
                .compose(bindUIEvent())
                .map(new StructureFunction<>())
                .subscribe(testObserver);
        testObserver.assertNoErrors();
    }

    /**
     * 测试结构化数据转换，模拟code！=0的情况
     */
    @Test
    public void testStructureObserverWithResponseError() {
        Timber.tag(ExampleInstrumentedTest.TAG).d("testStructureObserverWithResponseError");
        HttpObserver<MobileDateUsageEntity> httpObserver = new HttpObserver<MobileDateUsageEntity>() {
            @Override
            public void onSuccess(MobileDateUsageEntity mobileDateUsageEntity) {
                Timber.tag(ExampleInstrumentedTest.TAG).d(mobileDateUsageEntity.toString());
            }

            @Override
            public void onResponseError(String message, int code) {
                super.onResponseError(message, code);
                Timber.tag(ExampleInstrumentedTest.TAG).d("onResponseError %s(%s)",message,code);
            }

        };
        TestObserver<MobileDateUsageEntity> testObserver = new TestObserver<MobileDateUsageEntity>(httpObserver);
        Observable.just(json)
                .map(new Function<String, MobileDateUsageEntity>() {
                    @Override
                    public MobileDateUsageEntity apply(String s) throws Exception {
                        return new Gson().fromJson(json, MobileDateUsageEntity.class);
                    }
                })
                //将数据转换为 BaseHttpResponseEntity<MobileDateUsageEntity>
                .map(new Function<MobileDateUsageEntity, BaseHttpResponseEntity<MobileDateUsageEntity>>() {
                    @Override
                    public BaseHttpResponseEntity<MobileDateUsageEntity> apply(MobileDateUsageEntity mobileDateUsageEntity) throws Exception {
                        BaseHttpResponseEntity<MobileDateUsageEntity> entity = new BaseHttpResponseEntity<>();
                        entity.setMessage("service internal error");
                        entity.setCode(500);
                        entity.setData(mobileDateUsageEntity);
                        return entity;
                    }
                })
                .compose(bindUIEvent())
                //数据转换
                .map(new StructureFunction<>())
                .subscribe(testObserver);
        testObserver.assertError(ResponseException.class);
    }

    public final <T> ObservableTransformer<T, T> bindUIEvent() {
        return new UIObservableTransformer() {
            @Override
            public void hasSubscribe() {
                Timber.tag(ExampleInstrumentedTest.TAG).d("hasSubscribe");
            }

            @Override
            public void willComplete() {
                Timber.tag(ExampleInstrumentedTest.TAG).d("willComplete");
            }
        };
    }

}
