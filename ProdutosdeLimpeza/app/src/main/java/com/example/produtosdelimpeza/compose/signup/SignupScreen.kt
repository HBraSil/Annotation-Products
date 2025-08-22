package com.example.produtosdelimpeza.compose.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DrawerDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.produtosdelimpeza.R
import com.example.produtosdelimpeza.compose.component.NewButton
import com.example.produtosdelimpeza.compose.component.NewTxtField
import com.example.produtosdelimpeza.ui.theme.GradientSignupMainText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(onBackNavigation: () -> Unit = {}, onToSignupClick: () -> Unit = {}) {
    var passwordHidden by remember { mutableStateOf(true) }
    var txtEmail by remember { mutableStateOf("") }
    var txtPassword by remember { mutableStateOf("") }
    var txtConfirmPassword by remember { mutableStateOf("") }
    val verticalScrollState = rememberScrollState()


    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onBackNavigation) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBackIosNew,
                                contentDescription = stringResource(id = R.string.icon_navigate_back),
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Transparent
                    )
                )
            }
        ) {contentPadding ->
            var nome by remember { mutableStateOf("") }
            var sobrenome by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .padding(top = contentPadding.calculateTopPadding())
                    .verticalScroll(verticalScrollState)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Cadastre-se",
                    modifier = Modifier.fillMaxWidth().padding(start = 30.dp, top = 30.dp),
                    fontSize = 30.sp,
                    textAlign = TextAlign.Start,
                    fontWeight = Bold,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 25.dp, start = 20.dp, end = 20.dp, bottom = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Adiciona espaçamento entre os elementos
                ) {
                    TextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Nome") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                        ),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Transparent,
                            focusedContainerColor = Transparent
                        )
                    )
                    TextField(
                        value = sobrenome,
                        onValueChange = { sobrenome = it },
                        label = { Text("Sobrenome") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                        ),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Transparent,
                            focusedContainerColor = Transparent,
                            focusedTextColor = Black,
                            unfocusedTextColor = Black
                        )
                    )
                }

                NewTxtField(
                    value = txtEmail,
                    onValueChange = { txtEmail = it },
                    label = R.string.email,
                    placeholder = R.string.hint_email,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = stringResource(id = R.string.icon_navigate_back),
                        )
                    },
                )

                NewTxtField(
                    value = txtPassword,
                    onValueChange = { txtPassword = it },
                    label = R.string.password,
                    placeholder = R.string.hint_password,
                    obfuscate = passwordHidden,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = stringResource(id = R.string.icon_navigate_back),
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordHidden = !passwordHidden }) {
                            val image = if (passwordHidden) {
                                Icons.Filled.VisibilityOff
                            } else {
                                Icons.Filled.Visibility
                            }
                            val description = if (passwordHidden) {
                                stringResource(id = R.string.show_password)
                            } else {
                                stringResource(id = R.string.hide_password)
                            }

                            Icon(
                                imageVector = image,
                                contentDescription = description
                            )
                        }
                    }
                )

                NewTxtField(
                    value = txtConfirmPassword,
                    onValueChange = { txtConfirmPassword = it },
                    label = R.string.confirm_password,
                    placeholder = R.string.hint_confirm_password,
                    obfuscate = passwordHidden,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = stringResource(id = R.string.icon_navigate_back),
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordHidden = !passwordHidden }) {
                            val image = if (passwordHidden) {
                                Icons.Filled.VisibilityOff
                            } else {
                                Icons.Filled.Visibility
                            }
                            val description = if (passwordHidden) {
                                stringResource(id = R.string.show_password)
                            } else {
                                stringResource(id = R.string.hide_password)
                            }

                            Icon(
                                imageVector = image,
                                contentDescription = description
                            )
                        }
                    }
                )


                NewButton(
                    R.string.to_signup,
                    modifier = Modifier
                        .padding(top = 100.dp, bottom = 20.dp),
                ) {
                    onToSignupClick()
                }
            }

        }
    }
}



@Preview
@Composable
private fun SignupScreenPreview() {
    SignupScreen()
}