package ke.don.feature_profile.model

import ke.don.core_datasource.domain.models.Profile

data class ProfileUiState (
    val profile: Profile = Profile(),
    val isMyProfile: Boolean = false,
    val showSheet: Boolean = false,
    val showSignOutDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isSignoutError: Boolean = false,
    val isDeleteError: Boolean = false,
    val errorMessage: String? = null,
    val signoutErrorMessage: String? = null,
    val deleteErrorMessage: String? = null
)
