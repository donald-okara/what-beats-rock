/*
 * Copyright © 2025 Donald O. Isoe (isoedonald@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ke.don.feature_profile.model

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
    val profileRepository: ProfileRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> get() = _uiState

    private val _eventChannel = Channel<SnackManager>()
    val eventFlow = _eventChannel.receiveAsFlow()

    fun handleIntent(intent: ProfileIntentHandler) {
        when (intent) {
            is ProfileIntentHandler.FetchMyProfile -> fetchMyProfile()
            is ProfileIntentHandler.DeleteProfile -> deleteProfile(intent.onSignOut)
            is ProfileIntentHandler.SignOut -> signOut(intent.onSignOut)
            is ProfileIntentHandler.ClearState -> updateState { ProfileUiState() }
            is ProfileIntentHandler.ToggleSignOutDialog -> {
                updateState {
                    it.copy(
                        showSignOutDialog = !it.showSignOutDialog,
                    )
                }
            }
            is ProfileIntentHandler.ToggleBottomSheet -> {
                updateState {
                    it.copy(
                        showSheet = !it.showSheet,
                    )
                }
            }
            is ProfileIntentHandler.ToggleDeleteDialog -> {
                updateState {
                    it.copy(
                        showDeleteDialog = !it.showSignOutDialog,
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
                    isLoading = true,
                )
            }
            val result = profileRepository.fetchMyProfile()

            when {
                result.isSuccess -> {
                    updateState { state ->
                        state.copy(
                            isLoading = false,
                            profile = result.getOrNull() ?: Profile(),
                        )
                    }
                }
                result.isFailure -> {
                    updateState { state ->
                        state.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = result.exceptionOrNull()?.message,
                        )
                    }
                    showSnackbar(result.exceptionOrNull()?.message ?: "Something went wrong")
                }
            }
        }
    }

    fun signOut(onSignOut: () -> Unit) {
        viewModelScope.launch {
            val result = profileRepository.signOut()
            when {
                result.isSuccess -> {
                    updateState {
                        it.copy(
                            showSignOutDialog = !it.showSignOutDialog,
                        )
                    }
                    onSignOut()
                }
                result.isFailure -> {
                    updateState {
                        it.copy(
                            isSignoutError = true,
                            signoutErrorMessage = result.exceptionOrNull()?.message,
                        )
                    }
                    showSnackbar(result.exceptionOrNull()?.message ?: "Something went wrong")
                }
            }
        }
    }

    fun deleteProfile(onSignOut: () -> Unit) {
        viewModelScope.launch {
            val result = profileRepository.deleteOwnProfile()
            when {
                result.isSuccess -> {
                    updateState {
                        it.copy(
                            showSignOutDialog = false,
                        )
                    }
                    signOut(onSignOut)
                }
                result.isFailure -> {
                    updateState {
                        it.copy(
                            isDeleteError = true,
                            deleteErrorMessage = result.exceptionOrNull()?.message,
                        )
                    }
                    showSnackbar(result.exceptionOrNull()?.message ?: "Something went wrong")
                }
            }
        }
    }
}
