package com.thomaskioko.tvmaniac.domain.featureflags

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.featureflags.FeatureFlagProvider
import com.thomaskioko.tvmaniac.featureflags.FeatureFlags
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSortDescriptor
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Inject
public class ObserveFeatureFlagRowsInteractor(
    private val featureFlags: FeatureFlags,
    private val provider: FeatureFlagProvider,
) : SubjectInteractor<ObserveFeatureFlagRowsInteractor.Param, ObserveFeatureFlagRowsInteractor.Result>() {

    override fun createObservable(params: Param): Flow<Result> {
        val filtered = provider.flags(params.sort, params.ascending).filter { it.matches(params.query) }
        if (filtered.isEmpty()) return flowOf(Result(params, persistentListOf()))
        return combine(filtered.map(::rowFor)) { rows ->
            Result(params, rows.asList().toImmutableList())
        }
    }

    private fun rowFor(flag: FeatureFlag): Flow<FeatureFlagRow> = combine(
        featureFlags.isEnabled(flag),
        featureFlags.source(flag),
    ) { value, source ->
        FeatureFlagRow(featureFlag = flag, value = value, featureFlagSource = source)
    }

    private fun FeatureFlag.matches(query: String): Boolean =
        query.isBlank() ||
            title.contains(query, ignoreCase = true) ||
            key.contains(query, ignoreCase = true) ||
            description.contains(query, ignoreCase = true)

    public data class Param(
        val sort: FeatureFlagSortDescriptor = FeatureFlagSortDescriptor.Date,
        val ascending: Boolean = false,
        val groupByType: Boolean = false,
        val query: String = "",
    )

    public data class Result(
        val params: Param,
        val rows: ImmutableList<FeatureFlagRow>,
    )
}
