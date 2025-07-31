package ke.don.feature_share.models

import ke.don.core_datasource.domain.models.PodiumProfile
import ke.don.core_datasource.domain.models.SpotlightModel

sealed class SharableScreenModel {
    class Profile (val profile: PodiumProfile): SharableScreenModel()
    class GameSpotlight (val spotlight: SpotlightModel): SharableScreenModel()
}