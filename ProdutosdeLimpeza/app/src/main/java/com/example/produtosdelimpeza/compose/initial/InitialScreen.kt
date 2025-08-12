package com.example.produtosdelimpeza.compose.initial

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.produtosdelimpeza.R
import com.example.produtosdelimpeza.ui.theme.IntenseBlue
import com.example.produtosdelimpeza.ui.theme.ProdutosDeLimpezaTheme
import com.example.produtosdelimpeza.ui.theme.SmoothBlue

@Composable
fun InitialScreen(onLoginButtonClick: () -> Unit = {}, onSignupButtonClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Image(
                painter = painterResource(R.drawable.clean_products),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop

            )

            Column(
                modifier = Modifier
                    .padding(bottom = 60.dp, start = 50.dp, end = 50.dp)
                    .fillMaxWidth()
                ,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                FilledTonalButton(
                    onClick = onSignupButtonClick,
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = IntenseBlue,
                        contentColor = White
                    )
                ) {
                    Text(
                        text = "Fazer cadastro",
                        fontSize = 15.sp
                    )
                }

                FilledTonalButton(
                    onClick = onLoginButtonClick,
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = SmoothBlue,
                        contentColor = White
                    )
                ) {
                    Text(
                        text = "Já sou cliente",
                        fontSize = 15.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.padding(top = 15.dp),
                        text = stringResource(id = R.string.is_seller),
                        fontWeight = Bold,
                        fontSize = 13.sp
                    )
                    TextButton(
                        onClick = {/*TODO*/ }
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 15.dp, end = 15.dp),
                            text = stringResource(id = R.string.click_here),
                            fontWeight = Bold,
                            fontSize = 13.sp
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