package ke.don.what_beats_rock.create_itinerary.model

import ke.don.core_datasource.domain.ItineraryItem
import kotlinx.serialization.Serializable

data class ItineraryFormUiState(
    val posterUrl: String? = null,
    val title: String? = null,
    val description: String? = null,
    val isGeneratingDescription: Boolean = false,
    val descriptionIsError: Boolean = false,
    val descriptionErrorMessage: String? = null,
    val itinerary: List<ItineraryItem> = emptyList(),
    val itineraryItem: ItineraryItem? = null,
    val isGeneratingItinerary: Boolean = false,
    val itineraryIsError: Boolean = false,
    val itineraryErrorMessage: String? = null,
)

