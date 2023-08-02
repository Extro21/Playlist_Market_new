package com.practicum.playlistmarket.domain.impl

import com.practicum.playlistmarket.domain.api.PlayerInteractor
import com.practicum.playlistmarket.domain.repository.PlayerRepository


class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {

    override fun playbackControl() {
        repository.playbackControl()
    }

    override fun startPlayer() {
        repository.startPlayer()
    }

    override fun pausePlayer() {
        repository.pausePlayer()
    }

    override fun preparePlayer(trackUrl: String) {
        repository.preparePlayer(trackUrl)
    }

    override fun releasePlayer() {
        repository.releasePlayer()
    }

//    fun getTime() {
//        val time = repository.getTime()
//
//        trackTimeListener.onTimeChanged(time)
//
//    }



}
