package com.example.produtosdelimpeza.compose.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.ExtraBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.produtosdelimpeza.R
import com.example.produtosdelimpeza.compose.component.NewButton
import com.example.produtosdelimpeza.compose.component.NewTxtField
import com.example.produtosdelimpeza.ui.theme.ProdutosDeLimpezaTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onBackNavigation: () -> Unit = {}, onSignupClick: () -> Unit = {}) {
    val verticalScrollState = rememberScrollState()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {},
                    actions = {
                        TextButton (
                            onClick = onSignupClick,
                            modifier = Modifier.padding(end = 20.dp)
                        ) {
                            Text(text = "Registre-se aqui",
                                fontSize = 14.sp,
                                fontWeight = ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
        ) { contentPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = contentPadding.calculateTopPadding())
                        .verticalScroll(verticalScrollState)
                        .navigationBarsPadding()
                        .padding(bottom = 50.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.limp_on_logo),
                        contentDescription = stringResource(R.string.login_image),
                        modifier = Modifier.padding(top = 20.dp)
                    )

                    Text(
                        text = "Faça seu login",
                        modifier = Modifier.padding(start = 30.dp, top = 30.dp),
                        fontSize = 20.sp,
                        fontWeight = Bold,
                    )
                    ContentLoginScreen()

                }
            }
}

@Composable
fun ContentLoginScreen() {
    var txtEmail by remember { mutableStateOf("") }
    var txtPassword by remember { mutableStateOf("") }
    var passwordHidden by remember { mutableStateOf(true) }

    NewTxtField(
        modifier = Modifier.padding(top = 10.dp),
        value = txtEmail,
        onValueChange = { txtEmail = it },
        label = R.string.email,
        placeholder = R.string.hint_email,
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = stringResource(id = R.string.type_email),
                tint = MaterialTheme.colorScheme.primary
            )
        },
    )

    NewTxtField(
        value = txtPassword,
        onValueChange = { txtPassword = it },
        label = R.string.password,
        placeholder = R.string.hint_password,
        obfuscate = passwordHidden,
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = stringResource(id = R.string.type_password),
                tint = MaterialTheme.colorScheme.primary
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

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Esqueceu a senha?",
            modifier = Modifier.padding(end = 30.dp)
        )
    }

    NewButton(
        text = R.string.start,
        modifier = Modifier.padding(top = 30.dp, bottom = 30.dp),
    ){}

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            10.dp,
            Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FloatingActionButton(
            onClick = {},
            modifier = Modifier.padding(start = 30.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Image(
                painter = painterResource(id = R.drawable.light_google_logo), // seu drawable
                contentDescription = stringResource(R.string.google_logo),
                modifier = Modifier
                    .size(30.dp)
                    .wrapContentSize(Alignment.Center)
                    .clickable {}
            )
        }

        HorizontalDivider(modifier = Modifier.padding(start = 10.dp, end = 10.dp).weight(1f), 0.dp, Black)

        Text(
            text = stringResource(R.string.options_to_access),
            color = Gray,
        )

        HorizontalDivider(modifier = Modifier.padding(start = 10.dp, end = 10.dp).weight(1f), 0.dp, Black)

        FloatingActionButton(
            onClick = {},
            modifier = Modifier.padding(end = 30.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Image(
                painter = painterResource(R.drawable.light_facebook_logo),
                contentDescription = stringResource(R.string.facebook_logo),
                modifier = Modifier
                    .size(30.dp)
                    .wrapContentSize(Alignment.Center)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ProdutosDeLimpezaTheme {
        LoginScreen(onSignupClick = {})
    }
}