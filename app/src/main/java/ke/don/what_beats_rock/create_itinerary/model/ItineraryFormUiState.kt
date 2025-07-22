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
package ke.don.what_beats_rock.create_itinerary.model

import ke.don.core_datasource.domain.ItineraryItem

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
