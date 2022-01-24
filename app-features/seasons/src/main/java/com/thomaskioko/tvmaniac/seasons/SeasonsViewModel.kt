package com.thomaskioko.tvmaniac.seasons

import androidx.lifecycle.ViewModel
import com.thomaskioko.tvmaniac.core.annotations.DefaultDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class SeasonsViewModel @Inject constructor(
    @DefaultDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel()
