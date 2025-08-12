package com.example.produtosdelimpeza.compose.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
fun LoginScreen(onBackNavigation: () -> Unit = {}) {

    var a by remember { mutableStateOf("") }
    var b by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.login),
                            modifier = Modifier.padding(start = 30.dp),
                            fontSize = 30.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = ExtraBold,
                        )

                    },
                    navigationIcon = {
                        IconButton(onClick = onBackNavigation) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBackIosNew,
                                contentDescription = stringResource(id = R.string.icon_navigate_back),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        ) { contentPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = contentPadding.calculateTopPadding()),
                color = White
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    NewTxtField(
                        value = b,
                        onValueChange = { b = it },
                        modifier = Modifier.padding(top = 60.dp),
                        label = R.string.email,
                        placeholder = R.string.hint_email
                    )

                    NewTxtField(
                        value = a,
                        onValueChange = { a = it },
                        label = R.string.password,
                        placeholder = R.string.hint_password
                    )

                    Text(
                        text = stringResource(R.string.options_to_access),
                        modifier = Modifier
                            .padding(top = 50.dp),
                        color = Gray,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                    ) {
                        FloatingActionButton(
                            onClick = {},
                            modifier = Modifier.size(30.dp),
                            shape = CircleShape,
                        ){
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = Black,
                                modifier = Modifier
                                    .size(25.dp)
                                    .wrapContentSize(Alignment.Center)
                            )
                        }
                        FloatingActionButton(
                            onClick = {},
                            modifier = Modifier.size(30.dp),
                            shape = CircleShape
                        ){
                            Image(
                                painter = painterResource(id = R.drawable.google_logo), // seu drawable
                                contentDescription = "Google Logo",
                                modifier = Modifier.size(25.dp).wrapContentSize(Alignment.Center).clickable {}
                            )
                        }

                        FloatingActionButton(
                            onClick = {},
                            modifier = Modifier.size(30.dp),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.Facebook,
                                contentDescription = null,
                                tint = Black,
                                modifier = Modifier
                                    .size(25.dp)
                                    .wrapContentSize(Alignment.Center)
                            )
                        }
                        FloatingActionButton(
                            onClick = {},
                            modifier = Modifier.size(30.dp),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Black,
                                modifier = Modifier
                                    .size(25.dp)
                            )
                        }
                    }


                }


                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 80.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    NewButton(R.string.start, onClickNewBtn = {})
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ProdutosDeLimpezaTheme {
        LoginScreen()
    }
}