package com.thomaskioko.tvmaniac.domain.traktlists

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.traktlists.api.TraktListRepository
import dev.zacsweers.metro.Inject

@Inject
public class CreateTraktListInteractor(
    private val repository: TraktListRepository,
    private val userRepository: UserRepository,
) : Interactor<CreateTraktListInteractor.Params>() {

    override suspend fun doWork(params: Params) {
        require(params.name.isNotBlank()) { "List name cannot be empty" }
        require(params.name.length <= MAX_NAME_LENGTH) { "List name cannot exceed $MAX_NAME_LENGTH characters" }
        val slug = userRepository.getCurrentUser()?.slug ?: throw Exception("User not logged in")
        repository.createList(slug = slug, name = params.name.trim())
    }

    public data class Params(val name: String)

    private companion object {
        private const val MAX_NAME_LENGTH = 50
    }
}
