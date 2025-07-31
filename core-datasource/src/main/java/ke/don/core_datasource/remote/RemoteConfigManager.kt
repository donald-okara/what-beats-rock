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
                "store_link" to "Default from module"
            )
        )
        remoteConfig.fetchAndActivate()
    }

    fun getString(key: String): String = remoteConfig.getString(key)
}
