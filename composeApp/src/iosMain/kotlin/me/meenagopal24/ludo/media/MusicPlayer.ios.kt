package me.meenagopal24.ludo.media

import platform.AVFoundation.*
import platform.Foundation.NSURL

actual fun createAudioPlayer(): AudioPlayer = IOSAudioPlayer()


class IOSAudioPlayer : AudioPlayer {
    private var player: AVPlayer? = null

    override fun play(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: return
        player = AVPlayer(nsUrl)
        player?.play()
    }

    override fun stop() {
        player?.pause()
        player = null
    }

    override fun isPlaying(): Boolean {
        return player?.timeControlStatus == AVPlayerTimeControlStatusPlaying
    }
}
