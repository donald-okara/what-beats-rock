package ke.don.feature_profile.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.core_datasource.domain.models.Profile
import ke.don.core_datasource.domain.repositories.ProfileRepository
import ke.don.core_designsystem.material_theme.components.SnackManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val profileRepository: ProfileRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> get() = _uiState

    private val _eventChannel = Channel<SnackManager>()
    val eventFlow = _eventChannel.receiveAsFlow()


    fun handleIntent(intent: ProfileIntentHandler){
        when(intent){
            is ProfileIntentHandler.FetchMyProfile -> fetchMyProfile()
            is ProfileIntentHandler.DeleteProfile -> deleteProfile(intent.onSignOut)
            is ProfileIntentHandler.SignOut -> signOut(intent.onSignOut)
            is ProfileIntentHandler.ToggleSignOutDialog -> {
                updateState {
                    it.copy(
                        showSignOutDialog = !it.showSignOutDialog
                    )
                }
            }
            is ProfileIntentHandler.ToggleBottomSheet -> {
                updateState {
                    it.copy(
                        showSheet = !it.showSheet
                    )
                }
            }
            is ProfileIntentHandler.ToggleDeleteDialog -> {
                updateState {
                    it.copy(
                        showDeleteDialog = !it.showSignOutDialog
                    )
                }
            }
        }
    }

    private fun updateState(transform: (ProfileUiState) -> ProfileUiState) {
        _uiState.update(transform)
    }

    fun showSnackbar(message: String) {
        viewModelScope.launch {
            _eventChannel.send(SnackManager.ShowSnackbar(message))
        }
    }

    fun fetchMyProfile() {
        viewModelScope.launch {
            updateState { state ->
                state.copy(
                    isMyProfile = true,
                    isLoading = true
                )
            }
            val result = profileRepository.fetchMyProfile()

            when{
                result.isSuccess -> {
                    updateState { state ->
                        state.copy(
                            isLoading = false,
                            profile = result.getOrNull() ?: Profile()
                        )
                    }

                }
                result.isFailure -> {
                    updateState { state ->
                        state.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = result.exceptionOrNull()?.message
                        )
                    }
                    showSnackbar(result.exceptionOrNull()?.message ?: "Something went wrong")
                }
            }

        }
    }

    fun signOut(onSignOut: () -> Unit){
        viewModelScope.launch {
            val result = profileRepository.signOut()
            when{
                result.isSuccess -> {
                    updateState {
                        it.copy(
                            showSignOutDialog = !it.showSignOutDialog
                        )
                    }
                    onSignOut()
                }
                result.isFailure -> {
                    updateState {
                        it.copy(
                            isSignoutError = true,
                            signoutErrorMessage = result.exceptionOrNull()?.message
                        )
                    }
                    showSnackbar(result.exceptionOrNull()?.message ?: "Something went wrong")
                }
            }
        }
    }

    fun deleteProfile(onSignOut: () -> Unit){
        viewModelScope.launch {
            val result = profileRepository.deleteOwnProfile()
            when{
                result.isSuccess -> {
                    updateState {
                        it.copy(
                            showSignOutDialog = false
                        )
                    }
                    signOut(onSignOut)
                }
                result.isFailure -> {
                    updateState {
                        it.copy(
                            isDeleteError = true,
                            deleteErrorMessage = result.exceptionOrNull()?.message
                        )
                    }
                    showSnackbar(result.exceptionOrNull()?.message ?: "Something went wrong")
                }
            }
        }
    }
}