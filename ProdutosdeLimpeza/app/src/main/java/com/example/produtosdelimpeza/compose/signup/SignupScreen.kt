package com.example.produtosdelimpeza.compose.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.ExtraBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.produtosdelimpeza.R
import com.example.produtosdelimpeza.compose.component.NewButton
import com.example.produtosdelimpeza.compose.component.NewTxtField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(onBackNavigation: () -> Unit = {}, onToSignupClick: () -> Unit = {}) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.signup),
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
        ) {contentPadding ->
            Column(
                modifier = Modifier
                    .padding(top = contentPadding.calculateTopPadding())
                    .fillMaxSize()
                    .background(White),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                NewTxtField(
                    value = "",
                    onValueChange = { },
                    modifier = Modifier.padding(top = 60.dp),
                    label = R.string.name_lastname,
                    placeholder = R.string.hint_name_lastname
                )

                NewTxtField(
                    value = "",
                    onValueChange = { },
                    label = R.string.email,
                    placeholder = R.string.hint_email
                )

                NewTxtField(
                    value = "",
                    onValueChange = { },
                    label = R.string.password,
                    placeholder = R.string.hint_password
                )

                NewTxtField(
                    value = "",
                    onValueChange = { },
                    label = R.string.confirm_password,
                    placeholder = R.string.hint_confirm_password
                )


                NewButton(
                    R.string.to_signup,
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
private fun SignupScreenPreview() {
    SignupScreen()
}