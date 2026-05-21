package com.thomaskioko.tvmaniac.featureflags.testing

import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDate

/**
 * In-memory [FeatureFlag] for unit tests. Holds a single mutable [value] plus an editable source.
 * Tests assign `value = true` to flip the flag and call [setSource] to simulate a debug override
 * being active. Skips the `FeatureFlagsRemoteConfig` / `DefaultFeatureFlagFactory` indirection so
 * presenter and interactor tests stay focused on the consumer's branch logic.
 *
 * For tests that exercise the debug-override decorator end-to-end, construct
 * [FakeFeatureFlagsRemoteConfig] instead and let the production binding container resolve the flag.
 *
 * @param initial Starting value. Read by [observe] until [value] is reassigned.
 * @param key Firebase Remote Config key. Defaulted; override when a test asserts on the rendered row.
 * @param title Human-readable name. Defaulted; override when a test asserts on the rendered row.
 * @param description One-line summary. Defaulted; override when a test asserts on the rendered row.
 * @param dateAdded Date the flag entered the codebase. Defaulted; override when a test asserts on sort order.
 */
public class FakeFeatureFlag<T>(
    initial: T,
    override val key: String = "fake_flag",
    override val title: String = "Fake",
    override val description: String = "Fake flag for tests.",
    override val dateAdded: LocalDate = LocalDate(2026, 1, 1),
) : FeatureFlag<T> {

    private val valueFlow: MutableStateFlow<T> = MutableStateFlow(initial)
    private val sourceFlow: MutableStateFlow<FeatureFlagSource> =
        MutableStateFlow(FeatureFlagSource.Firebase)

    /**
     * Current value. Reassign to flip the flag; observers emit the new value.
     * */
    public var value: T
        get() = valueFlow.value
        set(newValue) {
            valueFlow.value = newValue
        }

    /**
     * Sets the source emitted by [observeSource]. Defaults to [FeatureFlagSource.Firebase].
     * */
    public fun setSource(source: FeatureFlagSource) {
        sourceFlow.value = source
    }

    override fun observe(): Flow<T> = valueFlow

    override fun observeSource(): Flow<FeatureFlagSource> = sourceFlow
}
