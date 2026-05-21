package com.thomaskioko.tvmaniac.domain.featureflags

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSortDescriptor
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

@Inject
public class ObserveFeatureFlagRowsInteractor(
    private val flags: Set<FeatureFlag<Boolean>>,
) : SubjectInteractor<ObserveFeatureFlagRowsInteractor.Param, ObserveFeatureFlagRowsInteractor.Result>() {

    override fun createObservable(params: Param): Flow<Result> {
        val sorted = flags.sortedWith(comparator(params.sort, params.ascending))
        val filtered = sorted.filter { it.matches(params.query) }
        if (filtered.isEmpty()) return flowOf(Result(params, persistentListOf()))
        return combine(filtered.map(::rowFor)) { rows ->
            Result(params, rows.asList().toImmutableList())
        }
    }

    private fun rowFor(flag: FeatureFlag<Boolean>): Flow<FeatureFlagRow> = combine(
        flag.observe(),
        flag.observeSource(),
    ) { value, source ->
        FeatureFlagRow(featureFlag = flag, value = value, featureFlagSource = source)
    }

    private fun comparator(sort: FeatureFlagSortDescriptor, ascending: Boolean): Comparator<FeatureFlag<Boolean>> {
        val base: Comparator<FeatureFlag<Boolean>> = when (sort) {
            FeatureFlagSortDescriptor.Title -> compareBy { it.title }
            FeatureFlagSortDescriptor.Key -> compareBy { it.key }
            FeatureFlagSortDescriptor.Date -> compareBy { it.dateAdded }
        }
        return if (ascending) base else base.reversed()
    }

    private fun FeatureFlag<Boolean>.matches(query: String): Boolean =
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
