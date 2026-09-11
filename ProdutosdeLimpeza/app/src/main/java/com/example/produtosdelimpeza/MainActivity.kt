package com.example.produtosdelimpeza

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.produtosdelimpeza.core.domain.model.ProfileMode
import com.example.produtosdelimpeza.core.navigation.LimpOnAppNavigation
import com.example.produtosdelimpeza.core.navigation.route.NavGraph
import com.example.produtosdelimpeza.core.presentation.DeepLinkViewModel
import com.example.produtosdelimpeza.core.presentation.NavigationLastUserModeViewModel
import com.example.produtosdelimpeza.core.theme.ProdutosDeLimpezaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val deepLinkViewModel: DeepLinkViewModel by viewModels()
    private val sessionViewModel: NavigationLastUserModeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.intent
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                sessionViewModel.lastUserMode.value == null
            }
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )
        setContent {
            ProdutosDeLimpezaTheme {
                val startProfile by sessionViewModel.lastUserMode.collectAsState()

                startProfile?.let { profileMode ->
                    val startDestination = when (profileMode) {
                        is ProfileMode.LoggedIn.StoreSection -> NavGraph.STORE_MAIN.route
                        ProfileMode.LoggedIn.CustomerSection -> NavGraph.USER_MAIN.route
                        ProfileMode.LoggedOut -> NavGraph.AUTH.route
                    }

                    LimpOnAppNavigation(startDestination)
                }
            }
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val link = intent.data.toString()
        if (link.isNotEmpty()) deepLinkViewModel.onDeepLinkReceived(link)
    }
}