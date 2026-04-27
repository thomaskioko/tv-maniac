package com.thomaskioko.tvmaniac.testing.integration.bindings

import com.thomaskioko.tvmaniac.util.DefaultDateTimeProvider
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlin.time.Instant

@BindingContainer
@ContributesTo(
    AppScope::class,
    replaces = [
        DefaultDateTimeProvider::class,
        FakeDateTimeProvider::class,
        FakeFormatterUtil::class,
    ],
)
public object TestDateTimeBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideFakeDateTimeProvider(): FakeDateTimeProvider = FakeDateTimeProvider(
        currentTime = Instant.parse("2026-04-19T00:00:00Z"),
    )

    @Provides
    public fun provideDateTimeProvider(fake: FakeDateTimeProvider): DateTimeProvider = fake
}
