package com.wei.vsclock.feature.times

import androidx.lifecycle.viewModelScope
import com.wei.vsclock.core.base.BaseViewModel
import com.wei.vsclock.core.data.repository.TimeRepository
import com.wei.vsclock.core.result.DataSourceResult
import com.wei.vsclock.core.result.asDataSourceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TimesViewModel
@Inject
constructor(
    private val timeRepository: TimeRepository,
) : BaseViewModel<
    TimesViewAction,
    TimesViewState,
    >(
    TimesViewState(),
) {

    init {
        checkApiHealth()
    }

    private fun checkApiHealth() {
        viewModelScope.launch {
            timeRepository.checkHealth()
                .asDataSourceResult()
                .collect { result ->
                    handleApiHealthResult(result)
                }
        }
    }

    private fun handleApiHealthResult(result: DataSourceResult<Int>) {
        when (result) {
            is DataSourceResult.Success -> handleApiHealthSuccess(result.data)
            is DataSourceResult.Error -> handleApiHealthError()
            is DataSourceResult.Loading -> handleLoading()
        }
    }

    private fun handleApiHealthSuccess(statusCode: Int) {
        updateState {
            copy(
                apiHealthUiState = ApiHealthUiState.Success(isHealth = statusCode == 200),
            )
        }
    }

    private fun handleApiHealthError() {
        updateState { copy(apiHealthUiState = ApiHealthUiState.LoadFailed) }
    }

    private fun handleLoading() {
        updateState { copy(apiHealthUiState = ApiHealthUiState.Loading) }
    }

    override fun dispatch(action: TimesViewAction) {
        Timber.d("dispatch $action")
        when (action) {
            else -> {}
        }
    }
}
