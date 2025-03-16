package me.meenagopal24.ludo.media

interface AudioPlayer {
    fun play(url: String)
    fun stop()
    fun isPlaying() : Boolean
}

expect fun createAudioPlayer(): AudioPlayer
