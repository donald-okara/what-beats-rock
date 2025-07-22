package ke.don.core_datasource.domain

import kotlinx.serialization.Serializable


@Serializable
data class ItineraryItem(
    val id: String? = null,
    val title: String = "",
    val isLocked: Boolean = false,
    val isGenerated: Boolean = false,
)

@Serializable
data class InsertionSuggestion(
    val id: String,
    val title: String,
    val isLocked: Boolean,
    val isGenerated: Boolean,
    val position: Int
)
