package com.thomaskioko.tvmaniac.traktauth

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTraktAuthStateInteractor @Inject constructor(
    private val traktManager: TraktManager
) : FlowInteractor<Unit, TraktAuthState>() {

    override fun run(params: Unit): Flow<TraktAuthState> = traktManager.state
}

