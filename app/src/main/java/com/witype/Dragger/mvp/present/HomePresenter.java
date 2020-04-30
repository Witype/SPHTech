package com.witype.Dragger.mvp.present;

import android.util.Log;

import com.witype.Dragger.entity.MobileDateUsageEntity;
import com.witype.Dragger.entity.RecordsBean;
import com.witype.Dragger.integration.scope.ActivityScope;
import com.witype.Dragger.mvp.contract.HomeView;

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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MobileDateUsageEntity>() {
                    @Override
                    public void accept(MobileDateUsageEntity mobileDateUsageEntity) throws Exception {
                        Log.d("", "getMobileDataUsage");
                        onGetData(mobileDateUsageEntity.getResult().getRecords());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void onGetData(List<RecordsBean> recordsBeans) {
        Observable.just(recordsBeans)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .flatMap(new DataGroupFun(startQuarter, endQuarter))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<RecordsBean>>() {
                    @Override
                    public void accept(List<RecordsBean> recordsBeans) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private int getQuarterFilter(String quarter) {
        Pattern pattern = Pattern.compile("20[0-9]{2}");
        Matcher matcher = pattern.matcher(quarter);
        return matcher.find() ? Integer.parseInt(matcher.group(0)) : 0;
    }

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
                            return recordsBeans.get(0).getCompareKey().compareTo(t1.get(0).getCompareKey());
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
                return recordsBean.getCompareKey().compareTo(t1.getCompareKey());
            }
        };

        private int getQuarterFilter(String quarter) {
            Pattern pattern = Pattern.compile("20[0-9]{2}");
            Matcher matcher = pattern.matcher(quarter);
            return matcher.find() ? Integer.parseInt(matcher.group(0)) : 0;
        }
    }

    public static class DataQuarterOffsetFun implements Function<List<RecordsBean>, ObservableSource<List<RecordsBean>>> {

        @Override
        public ObservableSource<List<RecordsBean>> apply(List<RecordsBean> recordsBeans) throws Exception {
            return Observable.fromIterable(recordsBeans)
                    .scan(new BiFunction<RecordsBean, RecordsBean, RecordsBean>() {
                        @Override
                        public RecordsBean apply(RecordsBean recordsBean, RecordsBean recordsBean2) throws Exception {
                            double offset = recordsBean2.getVolume_of_mobile_data() - recordsBean.getVolume_of_mobile_data();
                            recordsBean2.setVolume_offset(offset);
                            return recordsBean2;
                        }
                    })
                    .toList()
                    .toObservable();
        }
    }
}