package com.thomaskioko.tvmaniac.shared

import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Component

@Component
@ApplicationScope
abstract class ApplicationComponent : SharedComponent() {
    companion object
}
