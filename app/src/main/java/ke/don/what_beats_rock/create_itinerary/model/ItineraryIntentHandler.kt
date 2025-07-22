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

sealed class ItineraryIntentHandler {
    data class UpdateTitle(val title: String) : ItineraryIntentHandler()
    data class UpdateDescription(val description: String) : ItineraryIntentHandler()
    data class UpdateItineraryText(val text: String) : ItineraryIntentHandler()
    data class MoveItem(val fromIndex: Int, val toIndex: Int) : ItineraryIntentHandler()
    data class RemoveItem(val item: ItineraryItem) : ItineraryIntentHandler()
    data class EditItem(val item: ItineraryItem) : ItineraryIntentHandler()

    data object UpdateItineraryItem : ItineraryIntentHandler()
    data object GenerateDescription : ItineraryIntentHandler()
    data object AddItineraryItem : ItineraryIntentHandler()
    data object GenerateItinerary : ItineraryIntentHandler()
    data object SuggestItineraryItems : ItineraryIntentHandler()
}
