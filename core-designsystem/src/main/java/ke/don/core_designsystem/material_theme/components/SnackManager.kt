package ke.don.core_designsystem.material_theme.components

sealed class SnackManager {
    data class ShowSnackbar(val message: String) : SnackManager()
}
