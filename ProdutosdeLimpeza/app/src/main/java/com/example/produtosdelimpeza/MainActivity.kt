package com.example.produtosdelimpeza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.example.produtosdelimpeza.compose.ProdutosLimpezaApp
import com.example.produtosdelimpeza.ui.theme.BluishGreen
import com.example.produtosdelimpeza.ui.theme.LightBluishGray
import com.example.produtosdelimpeza.ui.theme.ProdutosDeLimpezaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProdutosDeLimpezaTheme {
                enableEdgeToEdge(
                    navigationBarStyle = if (isSystemInDarkTheme()) {
                        SystemBarStyle.dark(
                            MaterialTheme.colorScheme.secondary.toArgb() // cor no dark
                        )
                    } else {
                        SystemBarStyle.light(
                            LightBluishGray.toArgb(), // cor no light
                            LightBluishGray.toArgb()  // enforced contrast
                        )
                    }
                )

                ProdutosLimpezaApp()
            }
        }
    }
}
