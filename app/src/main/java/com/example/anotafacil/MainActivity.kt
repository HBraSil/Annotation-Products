package com.example.anotafacil

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.anotafacil.data.util.AppForegroundManager
import com.example.anotafacil.presentation.MainActivityViewModel
import com.example.anotafacil.ui.theme.AnotacoesDeProdutosTheme
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @Inject
    lateinit var appForegroundManager: AppForegroundManager

    val mainActivityViewModel: MainActivityViewModel by viewModels()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                mainActivityViewModel.lastActiveProfile.value == null
            }
        }


        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )
            Log.d(
                "APP_LIFECYCLE",
                "MainActivity manager = ${
                    System.identityHashCode(appForegroundManager)
                }"
            )

        setContent {
            val showEmailChangedDialog by appForegroundManager
                .showEmailChangedDialog
                .collectAsStateWithLifecycle()

            val sellerConnection by mainActivityViewModel
                .sellerConnection
                .collectAsStateWithLifecycle()

            LaunchedEffect(showEmailChangedDialog) {
                Log.d(
                    "APP_LIFECYCLE",
                    "MainActivity recebeu: $showEmailChangedDialog"
                )
            }


            AnotacoesDeProdutosTheme {
                val startProfile by mainActivityViewModel.lastActiveProfile.collectAsState()


                startProfile?.let { initialScreen ->
                    val startDestination = remember { initialScreen }

                    ProductsAnnotationApp(
                        sellerConnection = sellerConnection,
                        showEmailChangedDialog = showEmailChangedDialog,
                        mainActivityViewModel = mainActivityViewModel,
                        startDestination = startDestination,
                        dismissEmailChangedDialog = appForegroundManager::dismissEmailChangedDialog,
                        confirmSellerDisconnect = mainActivityViewModel::confirmSellerDisconnect
                    )
                }
            }
        }
    }
}