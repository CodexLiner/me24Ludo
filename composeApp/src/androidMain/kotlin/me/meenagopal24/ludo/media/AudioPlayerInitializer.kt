package me.meenagopal24.ludo.media

import android.content.Context
import androidx.startup.Initializer

class AudioPlayerInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        AndroidAudioPlayerProvider.context = context.applicationContext
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}