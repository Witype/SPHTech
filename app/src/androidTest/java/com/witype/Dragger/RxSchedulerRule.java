package com.witype.Dragger;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;

public class RxSchedulerRule implements TestRule {

    private TestScheduler testScheduler = new TestScheduler();

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                RxJavaPlugins.setIoSchedulerHandler(new Function<Scheduler, Scheduler>() {
                    @Override
                    public Scheduler apply(Scheduler scheduler) throws Exception {
                        return Schedulers.trampoline();
                    }
                });

                RxJavaPlugins.setNewThreadSchedulerHandler(new Function<Scheduler, Scheduler>() {
                    @Override
                    public Scheduler apply(Scheduler scheduler) throws Exception {
                        return Schedulers.trampoline();
                    }
                });

                RxJavaPlugins.setComputationSchedulerHandler(new Function<Scheduler, Scheduler>() {
                    @Override
                    public Scheduler apply(Scheduler scheduler) throws Exception {
                        return Schedulers.trampoline();
                    }
                });

                try {
                    base.evaluate();
                } finally {
                    RxJavaPlugins.reset();
                }
            }
        };
    }

}
