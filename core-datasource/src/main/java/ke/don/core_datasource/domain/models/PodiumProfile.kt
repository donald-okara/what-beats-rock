package ke.don.core_datasource.domain.models

data class PodiumProfile(
    val position: Int = 0, // 1, 2, 3 (not necessarily in order)
    val score: Int = 0,
    val profileUrl: String? = null,
    val userName: String = "",
    val id: String = ""
)