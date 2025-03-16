package me.meenagopal24.ludo.media


import android.annotation.SuppressLint
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

@SuppressLint("StaticFieldLeak")
object AndroidAudioPlayerProvider {
    var context: Context? = null
        set(value) {
            if (field == null) {
                field = value
            }
        }
}


actual fun createAudioPlayer(): AudioPlayer {
    if (AndroidAudioPlayerProvider.context == null) {
        throw IllegalStateException("Context not initialized")
    }
    return AndroidAudioPlayer(AndroidAudioPlayerProvider.context!!)
}


class AndroidAudioPlayer(private val context: Context) : AudioPlayer {
    private var player: ExoPlayer? = null

    override fun play(url: String) {
        player = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            play()
        }
    }

    override fun stop() {
        player?.release()
        player = null
    }

    override fun isPlaying(): Boolean {
        return player?.isPlaying == true
    }
}