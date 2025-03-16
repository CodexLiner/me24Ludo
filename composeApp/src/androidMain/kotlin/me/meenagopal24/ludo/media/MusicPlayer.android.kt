package me.meenagopal24.ludo.media


import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer

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
    private var mediaPlayer: MediaPlayer? = null

    override fun play(url: String) {
        try {
            mediaPlayer = MediaPlayer().apply {
                if (url.startsWith("file:///android_asset/")) {
                    val assetPath = url.removePrefix("file:///android_asset/")
                    val assetFileDescriptor = context.assets.openFd(assetPath)

                    setDataSource(
                        assetFileDescriptor.fileDescriptor,
                        assetFileDescriptor.startOffset,
                        assetFileDescriptor.length
                    )
                } else setDataSource(url)

                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setOnPreparedListener { start() }
                setOnCompletionListener { stop() }
                prepareAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }
}
