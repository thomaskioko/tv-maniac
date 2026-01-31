package com.thomaskioko.tvmaniac.domain.library

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.library.model.LibraryItem
import com.thomaskioko.tvmaniac.data.library.LibraryRepository
import com.thomaskioko.tvmaniac.data.library.model.LibrarySortOption
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
public class ObserveLibraryInteractor(
    private val repository: LibraryRepository,
) : SubjectInteractor<ObserveLibraryInteractor.Params, List<LibraryItem>>() {

    override fun createObservable(params: Params): Flow<List<LibraryItem>> {
        return repository.observeLibrary(
            query = params.query,
            sortOption = params.sortOption,
            followedOnly = params.followedOnly,
        )
    }

    public data class Params(
        val query: String = "",
        val sortOption: LibrarySortOption = LibrarySortOption.LAST_WATCHED,
        val followedOnly: Boolean = false,
    )
}
