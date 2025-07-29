package ke.don.feature_leaderboard.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.core_datasource.domain.repositories.LeaderboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val repository: LeaderboardRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState

    fun handleIntent(intent: LeaderboardIntentHandler){
        when(intent){
            is LeaderboardIntentHandler.FetchLeaderboard -> fetchLeaderboard()
            is LeaderboardIntentHandler.RefreshLeaderboard -> {
                updateState { it.copy(isRefreshing = true) }
                fetchLeaderboard()
            }
            else -> {}
        }
    }

    fun fetchLeaderboard() {
        viewModelScope.launch {
            Log.d("LeaderboardViewModel", "Fetching leaderboard...")
            val result = repository.fetchLeaderboard()
            when{
                result.isSuccess -> {
                    updateState {
                        it.copy(
                            profiles = result.getOrNull() ?: emptyList(),
                            isRefreshing = false,
                            isLoading = false
                        )
                    }
                }

                result.isFailure -> {
                    updateState {
                        it.copy(
                            isError = true,
                            isRefreshing = false,
                            errorMessage = result.exceptionOrNull()?.message ?: "Something went wrong",
                            isLoading = false
                        )
                    }
                }

            }
        }

    }

    fun updateState(transform: (LeaderboardUiState) -> LeaderboardUiState) {
        _uiState.update (transform)
    }
}