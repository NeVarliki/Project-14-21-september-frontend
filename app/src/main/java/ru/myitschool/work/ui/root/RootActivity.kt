package ru.myitschool.work.ui.root

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import ru.myitschool.work.BuildConfig
import ru.myitschool.work.ui.screen.AppNavHost
import ru.myitschool.work.ui.theme.WorkTheme

class RootActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!BuildConfig.DEBUG) window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        enableEdgeToEdge()
        setContent {
            WorkTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}
