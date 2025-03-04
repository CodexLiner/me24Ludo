import androidx.compose.ui.window.ComposeUIViewController
import me.meenagopal24.ludo.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
