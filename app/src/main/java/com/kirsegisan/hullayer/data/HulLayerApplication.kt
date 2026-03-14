package com.kirsegisan.hullayer.data

import android.app.Application
import com.kirsegisan.hullayer.data.datastore.SettingDataStoreRepository
import com.kirsegisan.hullayer.data.datastore.dataStore

class HulLayerApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(
            this,
            SettingDataStoreRepository(dataStore)
        )
    }
}