package com.thomaskioko.tvmaniac

import androidx.lifecycle.ViewModel
import com.thomaskioko.tvmaniac.core.networkutil.NetworkRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import me.tatarka.inject.annotations.Inject

@Inject
class MainActivityViewModel(
    private val datastoreRepository: DatastoreRepository,
    private val observeNetwork: NetworkRepository
) : ViewModel()