package com.witype.mvp.integration

import com.witype.mvp.integration.scope.ActivityScope
import javax.inject.Inject

open class CallDataModel @Inject constructor() {

    @set:Inject
    lateinit var iRequestManager : IRequestManager

    fun getRequestManager(): IRequestManager {
        return iRequestManager
    }
}