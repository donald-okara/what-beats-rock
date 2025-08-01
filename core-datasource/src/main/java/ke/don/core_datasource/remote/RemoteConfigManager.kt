package ke.don.core_datasource.remote

import android.annotation.SuppressLint
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

object RemoteConfigManager {
    @SuppressLint("StaticFieldLeak")
    private val remoteConfig = Firebase.remoteConfig

    fun init() {
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            }
        )
        remoteConfig.setDefaultsAsync(
            mapOf(
                "store_link" to "https://youtu.be/dQw4w9WgXcQ?si=vDg-LTXiZk_j8W9Z"
            )
        )
        remoteConfig.fetchAndActivate()
    }

    fun getString(key: String): String = remoteConfig.getString(key)
}
