package com.witype.Dragger.mvp.present;

import com.trello.rxlifecycle3.android.ActivityEvent;
import com.witype.Dragger.entity.MobileDateUsageEntity;
import com.witype.Dragger.entity.RecordsBean;
import com.witype.Dragger.integration.HttpObserver;
import com.witype.Dragger.mvp.activity.QuarterAdapter;
import com.witype.Dragger.mvp.contract.HomeView;
import com.witype.mvp.integration.scope.ActivityScope;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

@ActivityScope
public class HomePresenter extends BasePresenter<HomeView> {

    public static final String RESOURCE_ID = "a807b7ab-6cad-4aa6-87d0-e283a7353a0f";

    public static final int DEFAULT_START_YEAR = 2008, DEFAULT_END_YEAR = 2018;

    private int startQuarter = DEFAULT_START_YEAR, endQuarter = DEFAULT_END_YEAR;

    @Inject
    QuarterAdapter quarterAdapter;

    @Inject
    public HomePresenter(HomeView view) {
        super(view);
    }

    public void setEndQuarter(int endQuarter) {
        this.endQuarter = endQuarter;
    }

    public void setStartQuarter(int startQuarter) {
        this.startQuarter = startQuarter;
    }

    public void getMobileDataUsage(int limit) {
        getModel().getMobileDataUsage(RESOURCE_ID, limit)
                //需要和ActivityEvent绑定，这样在Activity销毁时能够取消订阅
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUIEvent())
                .observeOn(Schedulers.single())
                .map(new Function<MobileDateUsageEntity, List<RecordsBean>>() {
                    @Override
                    public List<RecordsBean> apply(MobileDateUsageEntity mobileDateUsageEntity) throws Exception {
                        return mobileDateUsageEntity.getResult().getRecords();
                    }
                })
                .flatMap(new DataRangeFilter(startQuarter, endQuarter))
                .flatMap(new DataGroupFun())
                .flatMap(new DataQuarterOffsetFun())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpObserver<List<RecordsBean>>() {
                    @Override
                    public void onSuccess(List<RecordsBean> recordsBeans) {
                        quarterAdapter.addData(recordsBeans);
                    }

                    @Override
                    public void onHttpError(String message, int httpCode) {
                        super.onHttpError(message, httpCode);
                        getView().onGetDataError(message);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        quarterAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * 通过年份过滤数据
     * 从startAt—>endAt
     */
    public static class DataRangeFilter implements Function<List<RecordsBean>, ObservableSource<List<RecordsBean>>> {

        private int startAt, endAt;

        public DataRangeFilter(int startAt, int endAt) {
            this.startAt = Math.min(startAt,endAt);
            this.endAt = Math.max(endAt,startAt);
        }

        @Override
        public ObservableSource<List<RecordsBean>> apply(List<RecordsBean> recordsBeans) throws Exception {
            return Observable.fromIterable(recordsBeans)
                    .filter(new Predicate<RecordsBean>() {
                        @Override
                        public boolean test(RecordsBean recordsBean) throws Exception {
                            int quarterFilter = recordsBean.getQuarterYearNum();
                            return quarterFilter >= startAt && quarterFilter <= endAt;
                        }
                    })
                    .toList()
                    .toObservable();
        }
    }

    /**
     * 将数据按照年进行排序和分组
     * step 1： 对数据按照年份进行分组；
     * step 2： 对分组的数据进行排序，去list[0]的数据，ps：通过分组的数据不会为空，
     * step 3： 对整个List<List>分组数据进行排序；
     * 使用RxJava
     * {@link Observable#groupBy(Function)}     @see #http://reactivex.io/documentation/operators/groupby.html
     * {@link Observable#toSortedList()}        @see #http://reactivex.io/documentation/operators/to.html
     * {@link Observable#flatMap(Function)}     @see #http://reactivex.io/documentation/operators/flatmap.html
     */
    public static class DataGroupFun implements Function<List<RecordsBean>, ObservableSource<List<RecordsBean>>> {

        @Override
        public ObservableSource<List<RecordsBean>> apply(List<RecordsBean> recordsBeans) throws Exception {
            return Observable.fromIterable(recordsBeans)
                    .groupBy(new Function<RecordsBean, Integer>() {
                        @Override
                        public Integer apply(RecordsBean recordsBean) throws Exception {
                            return recordsBean.getQuarterYearNum();
                        }
                    })
                    .flatMap(new Function<GroupedObservable<Integer, RecordsBean>, ObservableSource<List<RecordsBean>>>() {
                        @Override
                        public ObservableSource<List<RecordsBean>> apply(GroupedObservable<Integer, RecordsBean> integerRecordsBeanGroupedObservable) throws Exception {
                            return integerRecordsBeanGroupedObservable.toSortedList(sortListFun).toObservable();
                        }
                    })
                    .toSortedList(new Comparator<List<RecordsBean>>() {
                        @Override
                        public int compare(List<RecordsBean> recordsBeans, List<RecordsBean> t1) {
                            return ascCompare(recordsBeans.get(0),t1.get(0));
                        }
                    })
                    .toObservable()
                    .flatMap(new Function<List<List<RecordsBean>>, ObservableSource<List<RecordsBean>>>() {
                        @Override
                        public ObservableSource<List<RecordsBean>> apply(List<List<RecordsBean>> lists) throws Exception {
                            return Observable.fromIterable(lists)
                                    .window(1)
                                    .flatMap(new Function<Observable<List<RecordsBean>>, ObservableSource<List<RecordsBean>>>() {
                                        @Override
                                        public ObservableSource<List<RecordsBean>> apply(Observable<List<RecordsBean>> listObservable) throws Exception {
                                            return listObservable;
                                        }
                                    });
                        }
                    });

        }

        /**
         * 对分组出来的数据进行排序
         */
        private Comparator<RecordsBean> sortListFun = new Comparator<RecordsBean>() {

            @Override
            public int compare(RecordsBean recordsBean, RecordsBean t1) {
                return ascCompare(recordsBean,t1);
            }
        };

    }

    /**
     * 排序的执行方法
     * @param recordsBean
     * @param t1
     * @return
     */
    private static int ascCompare(RecordsBean recordsBean, RecordsBean t1) {
        return recordsBean.getCompareKey() - t1.getCompareKey();
    }

    /**
     *计算年度自己与上一个季度的差值和去年的综合
     * scale    @see http://reactivex.io/documentation/operators/scan.html
     */
    public static class DataQuarterOffsetFun implements Function<List<RecordsBean>, ObservableSource<List<RecordsBean>>> {

        private BigDecimal total;


        @Override
        public ObservableSource<List<RecordsBean>> apply(List<RecordsBean> recordsBeans) throws Exception {
            total = new BigDecimal(0.0);
            return Observable.fromIterable(recordsBeans)
                    .doOnNext(new Consumer<RecordsBean>() {
                        @Override
                        public void accept(RecordsBean recordsBean) throws Exception {
                            recordsBean.getQuarterQuaterNum();
                            recordsBean.getQuarterYearNum();
                            total = total.add(new BigDecimal(recordsBean.getVolume_of_mobile_data()));
                        }
                    })
                    .scan(new BiFunction<RecordsBean, RecordsBean, RecordsBean>() {
                        @Override
                        public RecordsBean apply(RecordsBean recordsBean, RecordsBean recordsBean2) throws Exception {
                            double offset = new BigDecimal(recordsBean2.getVolume_of_mobile_data()).subtract(new BigDecimal(recordsBean.getVolume_of_mobile_data())).doubleValue();
                            recordsBean2.setVolume_offset(offset);
//                            BigDecimal add = new BigDecimal(recordsBean.getTotal_of_year()).add(new BigDecimal(recordsBean2.getTotal_of_year()));
//                            recordsBean.setTotal_of_year(add.floatValue());
//                            recordsBean2.setTotal_of_year(add.floatValue());
                            return recordsBean2;
                        }
                    })
                    .toList()
                    .toObservable()
                    .doOnNext(new Consumer<List<RecordsBean>>() {
                        @Override
                        public void accept(List<RecordsBean> recordsBeans) throws Exception {
                            for (RecordsBean recordsBean : recordsBeans) {
                                recordsBean.setTotal_of_year(total.doubleValue());
                            }
                        }
                    });
        }
    }

}
