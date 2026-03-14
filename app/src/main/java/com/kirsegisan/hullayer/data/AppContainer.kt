package com.kirsegisan.hullayer.data

import android.content.Context
import com.kirsegisan.hullayer.data.datastore.SettingDataStoreRepository
import com.kirsegisan.hullayer.data.datastore.dataStore
import com.kirsegisan.hullayer.data.repository.TrackRepository
import com.kirsegisan.hullayer.data.repository.TrackRepositoryImpl


interface AppContainer {
    val settingsRepository: SettingDataStoreRepository
    val trackRepository: TrackRepository
    val settingRepository: SettingDataStoreRepository
}

class DefaultAppContainer(
    private val context: Context,
    override val settingsRepository: SettingDataStoreRepository
): AppContainer {

    override val settingRepository: SettingDataStoreRepository by lazy {
        SettingDataStoreRepository(context.dataStore)
    }

    override val trackRepository: TrackRepository by lazy {
        TrackRepositoryImpl(
            context = context,
            settingsRepository = settingRepository
        )
    }
}