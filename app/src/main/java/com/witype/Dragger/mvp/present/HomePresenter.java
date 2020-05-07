package com.witype.Dragger.mvp.present;

import com.trello.rxlifecycle3.android.ActivityEvent;
import com.witype.Dragger.entity.MobileDateUsageEntity;
import com.witype.Dragger.entity.RecordsBean;
import com.witype.Dragger.integration.HttpObserver;
import com.witype.Dragger.integration.scope.ActivityScope;
import com.witype.Dragger.mvp.activity.QuarterAdapter;
import com.witype.Dragger.mvp.contract.HomeView;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        model.getMobileDataUsage(RESOURCE_ID, limit)
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
                .flatMap(new DataGroupFun(startQuarter, endQuarter))
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
                        view.onGetDataError(message);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        quarterAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * 将数据按照年进行分组，
     * 使用RxJava
     * groupBy      @see #http://reactivex.io/documentation/operators/groupby.html
     * toSortedList @see #http://reactivex.io/documentation/operators/to.html
     * flatMap      @see #http://reactivex.io/documentation/operators/flatmap.html
     */
    public static class DataGroupFun implements Function<List<RecordsBean>, ObservableSource<List<RecordsBean>>> {

        private int startAt, endAt;

        public DataGroupFun(int startAt, int endAt) {
            this.startAt = startAt;
            this.endAt = endAt;
        }

        @Override
        public ObservableSource<List<RecordsBean>> apply(List<RecordsBean> recordsBeans) throws Exception {
            return Observable.fromIterable(recordsBeans)
                    .toSortedList(sortListFun)
                    .toObservable()
                    .flatMap(new Function<List<RecordsBean>, ObservableSource<RecordsBean>>() {
                        @Override
                        public ObservableSource<RecordsBean> apply(List<RecordsBean> recordsBeans) throws Exception {
                            return Observable.fromIterable(recordsBeans);
                        }
                    })
                    .filter(new Predicate<RecordsBean>() {
                        @Override
                        public boolean test(RecordsBean recordsBean) throws Exception {
                            int quarterFilter = getQuarterFilter(recordsBean.getQuarter());
                            return quarterFilter >= startAt && quarterFilter <= endAt;
                        }
                    })
                    .groupBy(new Function<RecordsBean, Integer>() {
                        @Override
                        public Integer apply(RecordsBean recordsBean) throws Exception {
                            return getQuarterFilter(recordsBean.getQuarter());
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
                            return Observable.fromIterable(lists);
                        }
                    });

        }

        private Comparator<RecordsBean> sortListFun = new Comparator<RecordsBean>() {

            @Override
            public int compare(RecordsBean recordsBean, RecordsBean t1) {
                return ascCompare(recordsBean,t1);
            }
        };

        private int getQuarterFilter(String quarter) {
            Pattern pattern = Pattern.compile("20[0-9]{2}");
            Matcher matcher = pattern.matcher(quarter);
            return matcher.find() ? Integer.parseInt(matcher.group(0)) : 0;
        }
    }

    private static int ascCompare(RecordsBean recordsBean, RecordsBean t1) {
        return recordsBean.getCompareKey().compareTo(t1.getCompareKey());
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
                            recordsBean.getQuarterQStr();
                            recordsBean.getQuarterYStr();
                            total = total.add(new BigDecimal(recordsBean.getVolume_of_mobile_data()));
                        }
                    })
                    .scan(new BiFunction<RecordsBean, RecordsBean, RecordsBean>() {
                        @Override
                        public RecordsBean apply(RecordsBean recordsBean, RecordsBean recordsBean2) throws Exception {
                            double offset = new BigDecimal(recordsBean2.getVolume_of_mobile_data()).subtract(new BigDecimal(recordsBean.getVolume_of_mobile_data())).doubleValue();
                            recordsBean2.setVolume_offset(offset);
//                            double result = new BigDecimal(Double.toString(recordsBean.getVolume_of_mobile_data())).add(new BigDecimal(recordsBean2.getVolume_of_mobile_data())).doubleValue();
//                            recordsBean2.setVolume(result);
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
