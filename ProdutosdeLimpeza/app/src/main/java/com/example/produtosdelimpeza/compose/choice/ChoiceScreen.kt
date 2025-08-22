package com.example.produtosdelimpeza.compose.choice

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChoiceScreen(onChoiceClick: (Int) -> Unit) {
    var listSellerClient by remember { mutableStateOf(listOf("Vendedor", "Cliente")) }
    var num by remember { mutableIntStateOf(0) } // Inicialmente, o índice é 0

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Escolha uma opção",
            modifier = Modifier
                .padding(top = 60.dp, start = 30.dp)
                .align(Alignment.Start),
            style = MaterialTheme.typography.headlineLarge,
        )

        Spacer(modifier = Modifier.weight(2f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Você é:",
                textAlign = TextAlign.Start,
                fontSize = 24.sp,
            )
            Button(
                onClick = { if (num > 0) num-- },
                enabled = num > 0,
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Voltar"
                )
            }

            AnimatedContent(
                targetState = num,
                label = "text_animation",
                modifier = Modifier
                    .weight(0.5f)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                transitionSpec = {
                    if (targetState > initialState) {
                        ContentTransform(
                            targetContentEnter = slideInHorizontally { h -> h } + fadeIn(),
                            initialContentExit = slideOutHorizontally { h -> -h } + fadeOut()
                        )
                    } else {
                        ContentTransform(
                            targetContentEnter = slideInHorizontally { h -> -h } + fadeIn(),
                            initialContentExit = slideOutHorizontally { h -> h } + fadeOut()
                        )
                    }
                }
            ) { number ->
                Text(
                    text = listSellerClient[number],
                    modifier = Modifier.padding(4.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    fontWeight = Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Button(
                onClick = { if (num < 1) num++ },
                enabled = num < listSellerClient.size - 1,
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Próximo"
                )
            }
        }

        ElevatedButton(
            onClick = { onChoiceClick(num) },
            modifier = Modifier.padding(top = 150.dp),
            colors = ButtonDefaults.buttonColors(
            ),
        ) {
            Text("Continuar")
        }
        // Espaço até o rodapé
        Spacer(modifier = Modifier.weight(1f))
    }
}



@Preview(showBackground = true)
@Composable
private fun ChoiceScreenPreview() {
    ChoiceScreen(onChoiceClick = {})
}