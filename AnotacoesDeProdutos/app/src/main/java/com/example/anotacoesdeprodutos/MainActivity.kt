package com.example.anotacoesdeprodutos

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.anotacoesdeprodutos.presentation.LastScreenViewModel
import com.example.anotacoesdeprodutos.ui.theme.AnotacoesDeProdutosTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val sessionViewModel: LastScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                sessionViewModel.lastActiveProfile.value == null
            }
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )

        setContent {
            AnotacoesDeProdutosTheme {
                val startProfile by sessionViewModel.lastActiveProfile.collectAsState()

                startProfile?.let { lastScreen ->
                    Log.d("MainActivityOn", "onCreate: $lastScreen")
                    ProductsAnnotationApp(lastScreen)
                }
            }
        }
    }
}