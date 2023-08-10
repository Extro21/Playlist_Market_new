package com.practicum.playlistmarket.Creator

import android.content.Context
import com.practicum.playlistmarket.player.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmarket.player.domain.api.TrackStateListener
import com.practicum.playlistmarket.player.domain.api.TrackTimeListener
import com.practicum.playlistmarket.player.domain.api.PlayerInteractor
import com.practicum.playlistmarket.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmarket.player.domain.repository.PlayerRepository
import com.practicum.playlistmarket.search.data.dto.TrackRepositoryImpl
import com.practicum.playlistmarket.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmarket.search.domain.api.TrackInteractor
import com.practicum.playlistmarket.search.domain.api.TrackRepository
import com.practicum.playlistmarket.search.domain.impl.TrackInteraktorImpl
import com.practicum.playlistmarket.settings.data.SettingRepositoryImpl
import com.practicum.playlistmarket.settings.data.SharedPreferencesThemeSettings
import com.practicum.playlistmarket.settings.domain.SettingInteractor
import com.practicum.playlistmarket.settings.domain.SettingInteractorImpl
import com.practicum.playlistmarket.settings.domain.SettingRepository


object Creator {


    private fun getPlayerRepository(trackTimeListener: TrackTimeListener, stateListener: TrackStateListener): PlayerRepository {
        return PlayerRepositoryImpl(trackTimeListener, stateListener)
    }


    fun providePlayerInteractor(trackTimeListener: TrackTimeListener, stateListener: TrackStateListener): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository(trackTimeListener, stateListener))
    }


    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteraktorImpl(getTrackRepository())
    }

    private fun getSettingRepository(context: Context): SettingRepository {
        return SettingRepositoryImpl(context, SharedPreferencesThemeSettings(context))
    }

    fun provideSettingInteractor(context: Context): SettingInteractor{
        return SettingInteractorImpl(getSettingRepository(context))
    }


}