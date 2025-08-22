package com.example.produtosdelimpeza.compose.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.ExtraBold
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.font.FontWeight.Companion.W900
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.produtosdelimpeza.R
import com.example.produtosdelimpeza.compose.component.NewButton



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupCodeScreen(modifier: Modifier = Modifier, onToSignupClick: () -> Unit = {}) {
    var code by remember { mutableStateOf(List(6) { "" }) }
    val focusRequesters = List(6) { FocusRequester() }
    val focusManager = LocalFocusManager.current // fazer um teste depois removendo essa linha



    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onToSignupClick) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBackIosNew,
                                contentDescription = stringResource(id = R.string.icon_navigate_back),
                            )
                        }
                    }
                )
            }
        ) {
            Column(
                modifier = modifier
                    .padding(top = it.calculateTopPadding())
                    .fillMaxSize()
            ) {

                Text(
                    text = "Código de verificação",
                    modifier = Modifier.padding(start = 12.dp),
                    fontSize = 30.sp,
                    maxLines = 1,
                    softWrap = false,
                    fontWeight = ExtraBold,
                    //color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Digite o código de 6 dígitos enviado para o seu e-mail",
                    modifier = Modifier.padding(start = 12.dp, top = 40.dp),
                    color = DarkGray,
                    fontWeight = Normal,
                )

                Row(
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                ) {
                    code.forEachIndexed { index, value ->
                        OutlinedTextField(
                            value = value,
                            onValueChange = { newValue ->
                                // aceita apenas dígitos e no máximo 1 char
                                val filtered = newValue.filter { it.isDigit() }.take(1)
                                if (filtered.isNotEmpty()) {
                                    // Aqui estamos transformando a lista em um mutableList e, com also, atribuindo a ela um novo valor, que vem da variável filtered
                                    code = code.toMutableList().also {it[index] = filtered}

                                    if (index < 5)
                                        focusRequesters[index + 1].requestFocus()
                                    else
                                        focusManager.clearFocus()
                                }
                            },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                               // color = Color.Black
                            ),
                            modifier = Modifier.size(52.dp)
                                .focusRequester(focusRequesters[index])
                                .onKeyEvent{ keyEvent ->
                                    if (keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.Backspace) {
                                        when {
                                            value.isNotEmpty() -> {
                                                code = code.toMutableList().also { it[index] = "" }
                                                focusRequesters[index].requestFocus()
                                                true
                                            }
                                            // Campo atual vazio: apaga o anterior e foca nele
                                            index > 0 -> {
                                                code = code.toMutableList().also { it[index - 1] = "" }
                                                focusRequesters[index - 1].requestFocus()
                                                true
                                            }
                                            else -> false
                                        }
                                    } else {
                                        false
                                    }
                                },
                            shape = RoundedCornerShape(6.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Gray,
                                focusedBorderColor = Color.Black
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }


                LaunchedEffect(Unit) {
                    focusRequesters[0].requestFocus()
                }


                Text(
                    text = "Não recebi meu código de verificação",
                    modifier = Modifier
                        .padding(top = 50.dp, end = 12.dp)
                        .align(Alignment.End),
                    fontSize = 12.sp,
                    color = Blue,
                    fontWeight = W900,
                )

                NewButton( // DEIXAR O BOTÃO DISABLED ATÉ Q SEJA VERIFICADO O CÓDIGO, E SÓ DEPOIS TORNA-LO ENABLED
                    text = R.string.move_on,
                    modifier = Modifier.padding(top = 100.dp).imePadding(),
                ) {
                    onToSignupClick()
                }
            }
        }
    }


}



@Preview
@Composable
private fun SignupCodeScreenPreview() {
    SignupCodeScreen()
}