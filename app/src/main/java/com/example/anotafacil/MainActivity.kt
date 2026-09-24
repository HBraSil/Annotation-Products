package com.hilquias.anotafacil

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
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.anotafacil.ProductsAnnotationApp
import com.example.anotafacil.presentation.LastScreenViewModel
import com.example.anotafacil.ui.theme.AnotacoesDeProdutosTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val lastScreenViewModel: LastScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                lastScreenViewModel.lastActiveProfile.value == null
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
                val startProfile by lastScreenViewModel.lastActiveProfile.collectAsState()

                startProfile?.let { initialScreen ->
                    val startDestination = remember { initialScreen }
                    ProductsAnnotationApp(lastScreenViewModel, startDestination)
                }
            }
        }
    }
}