package com.thomaskioko.tvmaniac.viewmodel

import com.thomaskioko.tvmaniac.core.usecase.scope.CoroutineScopeOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

open class BaseViewModel : CoroutineScopeOwner {

    private val job = SupervisorJob()

    override val coroutineScope: CoroutineScope = CoroutineScope(job + Dispatchers.Main)

    fun onDestroy() {
        job.cancel()
    }
}
