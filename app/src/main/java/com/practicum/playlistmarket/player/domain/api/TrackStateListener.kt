package com.practicum.playlistmarket.player.domain.api

import com.practicum.playlistmarket.player.domain.StatePlayer

interface TrackStateListener {

    fun getState(state : StatePlayer)

}