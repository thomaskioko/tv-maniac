package com.thomaskioko.tvmaniac.domain.user

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
public class UserDataSyncInitializer(
    private val observeAuthAndSyncUserInteractor: ObserveAuthAndSyncUserInteractor,
    private val coroutineScope: AppCoroutineScope,
) : AppInitializer {
    override fun init() {
        observeAuthAndSyncUserInteractor(Unit)
        coroutineScope.io.launch {
            observeAuthAndSyncUserInteractor.flow.collect()
        }
    }
}
