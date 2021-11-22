package com.thomaskioko.tvmaniac.search

import androidx.lifecycle.ViewModel
import com.thomaskioko.tvmaniac.core.annotations.DefaultDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    @DefaultDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel()
