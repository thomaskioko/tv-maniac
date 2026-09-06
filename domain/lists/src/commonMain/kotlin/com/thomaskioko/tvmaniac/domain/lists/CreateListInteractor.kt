package com.thomaskioko.tvmaniac.domain.lists

import com.thomaskioko.tvmaniac.accountmanager.api.ProviderFeatures
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.lists.api.ListRepository
import dev.zacsweers.metro.Inject

@Inject
public class CreateListInteractor(
    private val repository: ListRepository,
    private val userRepository: UserRepository,
    private val activeProviderFeatures: () -> ProviderFeatures,
) : Interactor<CreateListInteractor.Params>() {

    override suspend fun doWork(params: Params) {
        require(params.name.isNotBlank()) { "List name cannot be empty" }
        require(params.name.length <= MAX_NAME_LENGTH) { "List name cannot exceed $MAX_NAME_LENGTH characters" }
        val traktSlug = if (activeProviderFeatures().supportsLists) userRepository.getCurrentUser()?.slug else null
        repository.createList(name = params.name.trim(), traktSlug = traktSlug)
    }

    public data class Params(val name: String)

    private companion object {
        private const val MAX_NAME_LENGTH = 50
    }
}
