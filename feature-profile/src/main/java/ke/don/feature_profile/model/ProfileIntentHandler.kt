package ke.don.feature_profile.model

sealed class ProfileIntentHandler {
    data class SignOut(val onSignOut: () -> Unit): ProfileIntentHandler()
    data class DeleteProfile(val onSignOut: () -> Unit): ProfileIntentHandler()
    data object ToggleSignOutDialog: ProfileIntentHandler()
    data object ToggleBottomSheet: ProfileIntentHandler()
    data object ToggleDeleteDialog: ProfileIntentHandler()
    data object FetchMyProfile: ProfileIntentHandler()
}