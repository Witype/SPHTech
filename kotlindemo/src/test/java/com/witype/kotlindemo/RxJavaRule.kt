package com.witype.kotlindemo

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class RxJavaRule : TestRule {

    override fun apply(base: Statement?, description: Description?): Statement {
        return object : Statement() {

            @Throws(Throwable::class)
            override fun evaluate() {
                RxJavaPlugins.setIoSchedulerHandler {
                    Schedulers.trampoline()
                }
                RxJavaPlugins.setNewThreadSchedulerHandler {
                    Schedulers.trampoline()
                }
                RxJavaPlugins.setComputationSchedulerHandler {
                    Schedulers.trampoline()
                }
                RxAndroidPlugins.setMainThreadSchedulerHandler() {
                    Schedulers.trampoline()
                }
                base?.evaluate()
            }
        }
    }
}