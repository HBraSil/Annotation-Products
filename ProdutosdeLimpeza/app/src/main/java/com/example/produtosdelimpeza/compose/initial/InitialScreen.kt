package com.example.produtosdelimpeza.compose.initial

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.produtosdelimpeza.R
import com.example.produtosdelimpeza.ui.theme.LightDarkBlue
import com.example.produtosdelimpeza.ui.theme.ProdutosDeLimpezaTheme


@Composable
fun InitialScreen(onChoiceClick: () -> Unit = {}) {
    val verticalScrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Image(
                painter = painterResource(R.drawable.background_initial_screen_hd),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        color = MaterialTheme.colorScheme.onSecondary,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .verticalScroll(verticalScrollState)
            ) {
                Text(
                    text = "Seja bem vindo a ",
                    modifier = Modifier
                        .padding(top = 40.dp, start = 20.dp),
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = FontFamily(
                        Font(R.font.montserrat_extralight)
                    )
                )

                Text(
                    text = "LimpOn",
                    modifier = Modifier.padding(bottom = 60.dp, start = 40.dp),
                    fontSize = 40.sp,
                    color = LightDarkBlue,
                    fontFamily = FontFamily(
                        Font(R.font.montserrat_extrabold_italic)
                    )
                )

                Text(
                    text = stringResource(R.string.welcome_message),
                    modifier = Modifier
                        .fillMaxWidth(0.5f) // metade da largura da tela
                        .align(Alignment.Start)
                        .padding(start = 20.dp, bottom = 60.dp),
                    color = DarkGray
                )

                Card(
                    onClick = onChoiceClick,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .height(70.dp)
                        .align(Alignment.End)
                        .width(160.dp),
                    shape = RoundedCornerShape(
                        topStart = 30.dp,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp
                    ),
                    elevation = CardDefaults.cardElevation(2.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Começar",
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                           // color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun InitialScreenPreview() {
    ProdutosDeLimpezaTheme {
        InitialScreen()
    }
}