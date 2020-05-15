package com.witype.Dragger.mvp.present;

import com.witype.Dragger.RxJavaRule;
import com.witype.Dragger.entity.MobileDateUsageEntity;
import com.witype.Dragger.entity.RecordsBean;
import com.witype.Dragger.entity.ResultBean;
import com.witype.Dragger.integration.HttpModel;
import com.witype.Dragger.integration.HttpObserver;
import com.witype.Dragger.integration.UIObservableTransformer;
import com.witype.Dragger.mvp.contract.HomeView;
import com.witype.Dragger.mvp.model.ApiModel;
import com.witype.mvp.integration.RetrofitRequestManager;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class HomePresenterTest {

    public static final int LIMIT = 100;

    @Rule
    public RxJavaRule rule = new RxJavaRule();

    @Mock
    HomePresenter homePresenter;

    @Mock
    List<RecordsBean> mData;

    @Mock
    MobileDateUsageEntity mobileDateUsageEntity;

    Random random;

    @Before
    public void setUp() throws Exception {
        HomeView homeView = Mockito.mock(HomeView.class);
        HttpModel httpModel = Mockito.mock(HttpModel.class);

        Mockito.doReturn(homeView).when(homePresenter).getView();
        Mockito.doReturn(httpModel).when(homePresenter).getModel();

        random = new Random();
        mData = new ArrayList<>();
        int year = random.nextInt(2000);
        for (int i = 1; i <= LIMIT; i++) {
            RecordsBean spy = new RecordsBean();
            int quarter = Math.max(random.nextInt(4), 1);
            spy.setQuarter(String.format("%s Q%s", year, quarter));
            spy.setVolume_of_mobile_data(random.nextFloat());
            mData.add(spy);
            year = i % 4 == 0 ? random.nextInt(2000) : year;
        }

        // mock
        Observable<MobileDateUsageEntity> just = Observable.just(mobileDateUsageEntity);
        ResultBean resultBean = Mockito.mock(ResultBean.class);
        Mockito.when(resultBean.getRecords()).thenReturn(mData);
        Mockito.when(mobileDateUsageEntity.getResult()).thenReturn(resultBean);
        Mockito.when(homePresenter.getModel().getMobileDataUsage(HomePresenter.RESOURCE_ID,LIMIT)).thenReturn(just);
    }

    /**
     * 测试调用 {@link HomeView#onGetMobileDataUsage()}
     * use RxJava Observer {@link HttpObserver}
     */
    @Test
    public void testHomeViewCallSuccessWithHttpObservable() {
        log("┌─── test testHomeViewCallSuccess ───────────────────────────────────────");
        HttpObserver<MobileDateUsageEntity> httpObserver = new HttpObserver<MobileDateUsageEntity>() {
            @Override
            public void onSuccess(MobileDateUsageEntity mobileDateUsageEntity) {
                log("|\t call onSuccess => onGetMobileDataUsage");
                homePresenter.getView().onGetMobileDataUsage();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                log("|\t call onError => onGetDataError");
                homePresenter.getView().onGetDataError(e.getMessage());
            }
        };
        TestObserver<MobileDateUsageEntity> testObserver = new TestObserver<MobileDateUsageEntity>(httpObserver);

        homePresenter.getModel()
                .getMobileDataUsage(HomePresenter.RESOURCE_ID, LIMIT)
                .subscribe(testObserver);
        testObserver.assertNoErrors();

        log("|\t▊ assert");
        log("|\t assert HomeView#onGetMobileDataUsage atMostOnce call");
        Mockito.verify(homePresenter.getView(),Mockito.atMostOnce()).onGetMobileDataUsage();
        log("|\t assert HomeView#onGetDataError never call");
        Mockito.verify(homePresenter.getView(),Mockito.never()).onGetDataError(Mockito.any());
        log("└──────────────────────────────────────────────────────────────");
    }

    /**
     * 测试调用 {@link HomeView#onGetDataError(String)} ()}
     * use RxJava Observer {@link HttpObserver}
     */
    @Test
    public void testHomeViewCallFailureModelErrorWithHttpObservable() {
        log("┌─── test testHomeViewCallFailureModelErrorWithHttpObservable ────────────────────");
        //mock data
        Mockito.when(mobileDateUsageEntity.getResult().getRecords()).thenReturn(null);

        HttpObserver<MobileDateUsageEntity> httpObserver = new HttpObserver<MobileDateUsageEntity>() {
            @Override
            public void onSuccess(MobileDateUsageEntity mobileDateUsageEntity) {
                log("|\t mock nullpointException");
                int size = mobileDateUsageEntity.getResult().getRecords().size();
                homePresenter.getView().onGetMobileDataUsage();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                log("|\t onError : " + e.getMessage());
                homePresenter.getView().onGetDataError(e.getMessage());
            }
        };
        TestObserver<MobileDateUsageEntity> testObserver = new TestObserver<MobileDateUsageEntity>(httpObserver);

        homePresenter.getModel()
                .getMobileDataUsage(HomePresenter.RESOURCE_ID, LIMIT)
                .subscribe(testObserver);
        testObserver.assertNoErrors();

        log("|\t▊ assert");
        log("|\t assert HomeView#onGetMobileDataUsage never call ");
        Mockito.verify(homePresenter.getView(),Mockito.never()).onGetMobileDataUsage();
        log("|\t assert HomeView#onGetDataError atMostOnce call");
        Mockito.verify(homePresenter.getView(),Mockito.atMostOnce()).onGetDataError(Mockito.any());

        log("└────────────────────────────────────────────────────");
    }

    /**
     * 测试
     * 1、{@link UIObservableTransformer#hasSubscribe()}，当订阅成功后调用，
     * 2、{@link UIObservableTransformer#willComplete()} , 当订阅完成后调用
     * 在UI使用中，可以通过以上两个方法同步界面，如：网络加载时显示等待框，请求完成时隐藏等贷框
     */
    @Test
    public void testUIObservableTransformer() {
        log("┌─── test testUIObservableTransformer ─────────────────────────────────");

        HttpObserver<MobileDateUsageEntity> httpObserver = new HttpObserver<MobileDateUsageEntity>() {
            @Override
            public void onSuccess(MobileDateUsageEntity mobileDateUsageEntity) {
                log("|\t call onSuccess => onGetMobileDataUsage");
                homePresenter.getView().onGetMobileDataUsage();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                log("|\t call onError => onGetDataError");
                homePresenter.getView().onGetDataError(e.getMessage());
            }
        };
        TestObserver<MobileDateUsageEntity> testObserver = new TestObserver<MobileDateUsageEntity>(httpObserver);

        homePresenter.getModel()
                .getMobileDataUsage(HomePresenter.RESOURCE_ID, LIMIT)
                .compose(new UIObservableTransformer<MobileDateUsageEntity>() {
                    @Override
                    public void hasSubscribe() {
                        log("|\t call hasSubscribe => showLoading");
                        homePresenter.getView().showLoading();
                    }

                    @Override
                    public void willComplete() {
                        log("|\t call willComplete => dismissLoading");
                        homePresenter.getView().dismissLoading();
                    }
                })
                .subscribe(testObserver);
        testObserver.assertNoErrors();

        InOrder inOrder = Mockito.inOrder(homePresenter.getView());
        log("|\t┌─── assert ────────────────────────────────────────────────");
        log("|\t▊ assert order showLoading => onGetMobileDataUsage => dismissLoading");
        inOrder.verify(homePresenter.getView()).showLoading();
        inOrder.verify(homePresenter.getView()).onGetMobileDataUsage();
        inOrder.verify(homePresenter.getView()).dismissLoading();

        log("|\t▊ assert");
        log("|\t| assert HomeView#onGetMobileDataUsage atMostOnce call");
        Mockito.verify(homePresenter.getView(),Mockito.atMostOnce()).onGetMobileDataUsage();
        log("|\t| assert HomeView#showLoading atMostOnce call");
        Mockito.verify(homePresenter.getView(),Mockito.atMostOnce()).showLoading();
        log("|\t| assert HomeView#dismissLoading atMostOnce call");
        Mockito.verify(homePresenter.getView(),Mockito.atMostOnce()).dismissLoading();
        log("\t└────────────────────────────────────────────────────────────");

        log("└────────────────────────────────────────────────────");
    }

    /**
     * 测试{@link ApiModel#getMobileDataUsage(String, int)}https://data.gov.sg获取数据异常的情况，模拟{@link HomeView#onGetDataError(String)}
     * see <a>https://data.gov.sg/api/action/datastore_search?resource_id=a807b7ab-6cad-4aa6-87d0-e283a7353a0f&limit=5</a>
     */
    @Test
    public void testGetRemoteDataFailureWithHttpObserver() {
        log("┌─── test testGetRemoteDataFailureWithHttpObserver ────────────────────");
        HttpObserver<MobileDateUsageEntity> httpObserver = new HttpObserver<MobileDateUsageEntity>() {
            @Override
            public void onSuccess(MobileDateUsageEntity mobileDateUsageEntity) throws Exception {
                log("|\t mock exception");
                //mock exception
                throw new NullPointerException("mock exception");
            }

            @Override
            public void onResponseError(String message, int code) {
                super.onResponseError(message, code);
                homePresenter.getView().onGetDataError(message);
            }

            @Override
            public void onNetworkError(Throwable t) {
                super.onNetworkError(t);
                homePresenter.getView().onGetDataError(t.getMessage());
            }
        };
        TestObserver<MobileDateUsageEntity> testObserver = new TestObserver<MobileDateUsageEntity>(httpObserver);

        homePresenter.getModel()
                .getMobileDataUsage(HomePresenter.RESOURCE_ID, LIMIT)
                .compose(new UIObservableTransformer<MobileDateUsageEntity>() {
                    @Override
                    public void hasSubscribe() {
                        log("|\t call hasSubscribe => showLoading");
                        homePresenter.getView().showLoading();
                    }

                    @Override
                    public void willComplete() {
                        log("|\t call willComplete => dismissLoading");
                        homePresenter.getView().dismissLoading();
                    }
                })
                .subscribe(testObserver);
        testObserver.assertNoErrors().assertComplete();

        InOrder inOrder = Mockito.inOrder(homePresenter.getView());
        log("|\t┌─── assert ────────────────────────────────────────────────");
        log("|\t▊ assert order showLoading => onGetMobileDataUsage => dismissLoading");
        inOrder.verify(homePresenter.getView()).showLoading();
        inOrder.verify(homePresenter.getView()).onGetDataError("mock exception");
        inOrder.verify(homePresenter.getView()).dismissLoading();

        log("|\t▊ assert call");
        log("|\t| assert HomeView#showLoading atMostOnce call");
        Mockito.verify(homePresenter.getView(),Mockito.atMostOnce()).showLoading();
        log("|\t| assert HomeView#onGetDataError atMostOnce call");
        Mockito.verify(homePresenter.getView(),Mockito.atMostOnce()).onGetDataError("mock exception");
        log("|\t| assert HomeView#dismissLoading atMostOnce call");
        Mockito.verify(homePresenter.getView(),Mockito.atMostOnce()).dismissLoading();
        log("\t└────────────────────────────────────────────────────────────");

        log("└────────────────────────────────────────────────────");
    }

    private void log(String message) {
        System.out.println(message);
    }
}