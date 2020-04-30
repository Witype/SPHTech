package com.witype.Dragger;

import com.alibaba.fastjson.JSON;
import com.witype.Dragger.entity.MobileDateUsageEntity;
import com.witype.Dragger.entity.RecordsBean;
import com.witype.Dragger.mvp.present.HomePresenter;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class DataSourceFilterTest {

    public static final String json = "{\"help\":\"https://data.gov.sg/api/3/action/help_show?name=datastore_search\",\"success\":true,\"result\":{\"resource_id\":\"a807b7ab-6cad-4aa6-87d0-e283a7353a0f\",\"fields\":[{\"type\":\"int4\",\"id\":\"_id\"},{\"type\":\"text\",\"id\":\"quarter\"},{\"type\":\"numeric\",\"id\":\"volume_of_mobile_data\"}],\"records\":[{\"volume_of_mobile_data\":\"0.000384\",\"quarter\":\"2004-Q3\",\"_id\":1},{\"volume_of_mobile_data\":\"0.000543\",\"quarter\":\"2004-Q4\",\"_id\":2},{\"volume_of_mobile_data\":\"0.00062\",\"quarter\":\"2005-Q1\",\"_id\":3},{\"volume_of_mobile_data\":\"0.000634\",\"quarter\":\"2005-Q2\",\"_id\":4},{\"volume_of_mobile_data\":\"0.000718\",\"quarter\":\"2005-Q3\",\"_id\":5},{\"volume_of_mobile_data\":\"0.000801\",\"quarter\":\"2005-Q4\",\"_id\":6},{\"volume_of_mobile_data\":\"0.00089\",\"quarter\":\"2006-Q1\",\"_id\":7},{\"volume_of_mobile_data\":\"0.001189\",\"quarter\":\"2006-Q2\",\"_id\":8},{\"volume_of_mobile_data\":\"0.001735\",\"quarter\":\"2006-Q3\",\"_id\":9},{\"volume_of_mobile_data\":\"0.003323\",\"quarter\":\"2006-Q4\",\"_id\":10},{\"volume_of_mobile_data\":\"0.012635\",\"quarter\":\"2007-Q1\",\"_id\":11},{\"volume_of_mobile_data\":\"0.029992\",\"quarter\":\"2007-Q2\",\"_id\":12},{\"volume_of_mobile_data\":\"0.053584\",\"quarter\":\"2007-Q3\",\"_id\":13},{\"volume_of_mobile_data\":\"0.100934\",\"quarter\":\"2007-Q4\",\"_id\":14},{\"volume_of_mobile_data\":\"0.171586\",\"quarter\":\"2008-Q1\",\"_id\":15},{\"volume_of_mobile_data\":\"0.248899\",\"quarter\":\"2008-Q2\",\"_id\":16},{\"volume_of_mobile_data\":\"0.439655\",\"quarter\":\"2008-Q3\",\"_id\":17},{\"volume_of_mobile_data\":\"0.683579\",\"quarter\":\"2008-Q4\",\"_id\":18},{\"volume_of_mobile_data\":\"1.066517\",\"quarter\":\"2009-Q1\",\"_id\":19},{\"volume_of_mobile_data\":\"1.357248\",\"quarter\":\"2009-Q2\",\"_id\":20},{\"volume_of_mobile_data\":\"1.695704\",\"quarter\":\"2009-Q3\",\"_id\":21},{\"volume_of_mobile_data\":\"2.109516\",\"quarter\":\"2009-Q4\",\"_id\":22},{\"volume_of_mobile_data\":\"2.3363\",\"quarter\":\"2010-Q1\",\"_id\":23},{\"volume_of_mobile_data\":\"2.777817\",\"quarter\":\"2010-Q2\",\"_id\":24},{\"volume_of_mobile_data\":\"3.002091\",\"quarter\":\"2010-Q3\",\"_id\":25},{\"volume_of_mobile_data\":\"3.336984\",\"quarter\":\"2010-Q4\",\"_id\":26},{\"volume_of_mobile_data\":\"3.466228\",\"quarter\":\"2011-Q1\",\"_id\":27},{\"volume_of_mobile_data\":\"3.380723\",\"quarter\":\"2011-Q2\",\"_id\":28},{\"volume_of_mobile_data\":\"3.713792\",\"quarter\":\"2011-Q3\",\"_id\":29},{\"volume_of_mobile_data\":\"4.07796\",\"quarter\":\"2011-Q4\",\"_id\":30},{\"volume_of_mobile_data\":\"4.679465\",\"quarter\":\"2012-Q1\",\"_id\":31},{\"volume_of_mobile_data\":\"5.331562\",\"quarter\":\"2012-Q2\",\"_id\":32},{\"volume_of_mobile_data\":\"5.614201\",\"quarter\":\"2012-Q3\",\"_id\":33},{\"volume_of_mobile_data\":\"5.903005\",\"quarter\":\"2012-Q4\",\"_id\":34},{\"volume_of_mobile_data\":\"5.807872\",\"quarter\":\"2013-Q1\",\"_id\":35},{\"volume_of_mobile_data\":\"7.053642\",\"quarter\":\"2013-Q2\",\"_id\":36},{\"volume_of_mobile_data\":\"7.970536\",\"quarter\":\"2013-Q3\",\"_id\":37},{\"volume_of_mobile_data\":\"7.664802\",\"quarter\":\"2013-Q4\",\"_id\":38},{\"volume_of_mobile_data\":\"7.73018\",\"quarter\":\"2014-Q1\",\"_id\":39},{\"volume_of_mobile_data\":\"7.907798\",\"quarter\":\"2014-Q2\",\"_id\":40},{\"volume_of_mobile_data\":\"8.629095\",\"quarter\":\"2014-Q3\",\"_id\":41},{\"volume_of_mobile_data\":\"9.327967\",\"quarter\":\"2014-Q4\",\"_id\":42},{\"volume_of_mobile_data\":\"9.687363\",\"quarter\":\"2015-Q1\",\"_id\":43},{\"volume_of_mobile_data\":\"9.98677\",\"quarter\":\"2015-Q2\",\"_id\":44},{\"volume_of_mobile_data\":\"10.902194\",\"quarter\":\"2015-Q3\",\"_id\":45},{\"volume_of_mobile_data\":\"10.677166\",\"quarter\":\"2015-Q4\",\"_id\":46},{\"volume_of_mobile_data\":\"10.96733\",\"quarter\":\"2016-Q1\",\"_id\":47},{\"volume_of_mobile_data\":\"11.38734\",\"quarter\":\"2016-Q2\",\"_id\":48},{\"volume_of_mobile_data\":\"12.14232\",\"quarter\":\"2016-Q3\",\"_id\":49},{\"volume_of_mobile_data\":\"12.86429\",\"quarter\":\"2016-Q4\",\"_id\":50},{\"volume_of_mobile_data\":\"13.29757\",\"quarter\":\"2017-Q1\",\"_id\":51},{\"volume_of_mobile_data\":\"14.54179\",\"quarter\":\"2017-Q2\",\"_id\":52},{\"volume_of_mobile_data\":\"14.88463\",\"quarter\":\"2017-Q3\",\"_id\":53},{\"volume_of_mobile_data\":\"15.77653\",\"quarter\":\"2017-Q4\",\"_id\":54},{\"volume_of_mobile_data\":\"16.47121\",\"quarter\":\"2018-Q1\",\"_id\":55},{\"volume_of_mobile_data\":\"18.47368\",\"quarter\":\"2018-Q2\",\"_id\":56},{\"volume_of_mobile_data\":\"19.97554729\",\"quarter\":\"2018-Q3\",\"_id\":57},{\"volume_of_mobile_data\":\"20.43921113\",\"quarter\":\"2018-Q4\",\"_id\":58},{\"volume_of_mobile_data\":\"20.53504752\",\"quarter\":\"2019-Q1\",\"_id\":59}],\"_links\":{\"start\":\"/api/action/datastore_search?resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f\",\"next\":\"/api/action/datastore_search?offset=100&resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f\"},\"total\":59}}";

    /**
     * 允许的一年中有数据的季度数，
     */
    public static final int MAX_QUARTER_OF_YEAR = 4, MIN_QUARTER_OF_YEAR = 1;

    private MobileDateUsageEntity mobileDateUsageEntity;

    @Before
    public void setup() {
        mobileDateUsageEntity = JSON.parseObject(json, MobileDateUsageEntity.class);
    }

    /**
     * 先排序，避免因为后端数据
     */
    @Test
    public void sortList() {
        long startAt = System.currentTimeMillis();
        Observable.just(mobileDateUsageEntity.getResult().getRecords())
                .flatMap(new HomePresenter.DataGroupFun(2008, 2018))
                .flatMap(new HomePresenter.DataQuarterOffsetFun())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        log(String.valueOf(System.currentTimeMillis() - startAt));
                    }
                })
                .subscribe(new Consumer<List<RecordsBean>>() {
                    @Override
                    public void accept(List<RecordsBean> recordsBeans) throws Exception {
                        log(recordsBeans.toString());
                        assertFilterResult(recordsBeans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void assertFilterResult(List<RecordsBean> recordsBeans) {
        assertThat(recordsBeans.size(), Matchers.greaterThanOrEqualTo(MIN_QUARTER_OF_YEAR));
        assertThat(recordsBeans.size(), Matchers.lessThanOrEqualTo(MAX_QUARTER_OF_YEAR));

        Observable.fromIterable(recordsBeans)
                .scan(new BiFunction<RecordsBean, RecordsBean, RecordsBean>() {
                    @Override
                    public RecordsBean apply(RecordsBean recordsBean, RecordsBean recordsBean2) throws Exception {
                        double offset = recordsBean2.getVolume_of_mobile_data() - recordsBean.getVolume_of_mobile_data();
                        if (offset < 0) {
                            //确保当一年中某一个季度数据下降时数据计算正确
                            assertThat(recordsBean2.getVolume_of_mobile_data(), Matchers.lessThan(0.0));
                        }
                        //确保数据是asc排序
                        assertEquals(recordsBean2.get_id() - recordsBean.get_id(), 1);
                        return recordsBean2;
                    }
                })
                .subscribe(new Consumer<RecordsBean>() {
                    @Override
                    public void accept(RecordsBean recordsBean) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private void log(String log) {
        System.out.println(String.format("%s \n", log));
    }
}
