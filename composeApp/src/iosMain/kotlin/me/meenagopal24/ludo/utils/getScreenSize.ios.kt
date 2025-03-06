package me.meenagopal24.ludo.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIScreen

@OptIn(ExperimentalForeignApi::class)
actual fun getScreenSize(): ScreenSize {
    val screen = UIScreen.mainScreen
    val width = screen.bounds.useContents { size.width }.toFloat()
    val height = screen.bounds.useContents { size.height }.toFloat()
    return ScreenSize(width, height)
}