package ke.don.core_datasource.domain.models

import java.util.UUID

data class Session(
    val id: String? = UUID.randomUUID().toString(),
    val score: Int = 0,
    val time: Long = System.currentTimeMillis(),
    val started: Boolean = false
)
