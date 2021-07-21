package com.thomaskioko.tvmaniac.ui.welcome

import androidx.lifecycle.ViewModel
import com.thomaskioko.tvmaniac.core.annotations.DefaultDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    @DefaultDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
}