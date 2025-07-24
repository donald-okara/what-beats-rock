package ke.don.core_datasource.domain.models

data class Profile(
    var uid: String? = null,
    var displayName: String? = null,
    var email: String? = null,
    var photoUrl: String? = null,
    var createdAt: String? = null,
    var highScore: Int? = null,
    var onboarded: Boolean? = null,
    val lastPlayed: Long? = null
)
