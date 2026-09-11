package com.example.anotafacil.presentation.onboarding.verification_code

import android.widget.Toast
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anotafacil.presentation.auth.FieldState


@Composable
fun VerificationCodeScreen(
    verificationCodeViewModel: VerificationCodeViewModel = hiltViewModel(),
    onContinue: () -> Unit = {},
    onBack: () -> Unit = {}
) {

    val uiState by verificationCodeViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onContinue()
        }
    }


    VerificationCodeContent(
        uiState = uiState,
        onVerifyCode = verificationCodeViewModel::verifyCode,
        onBack = onBack,
        onNameChange = verificationCodeViewModel::updateSellerName,
        onCodeChange = verificationCodeViewModel::updateCode
    )
}

@Composable
fun VerificationCodeContent(
    uiState: VerificationCodeUiState = VerificationCodeUiState(),
    onVerifyCode: () -> Unit = {},
    onBack: () -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onCodeChange: (String) -> Unit = {}
) {

    val context = LocalContext.current
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FF))
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            VerificationIcon()

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Digite o código",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111111)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Insira o código de 6 dígitos enviado para\n" +
                        "verificação e liberação de acesso.",
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = Color(0xFF5F6878)
            )

            Spacer(Modifier.height(34.dp))

            CodeInputCard(
                name = uiState.sellerName,
                code = uiState.code,
                onCodeChange = onCodeChange,
                onNameChange = onNameChange
            )
        }

        BottomButtons(
            uiState = uiState,
            onBack = onBack,
            onVerifyCode = onVerifyCode
        )
    }
}

@Composable
private fun VerificationIcon() {
    Box(
        modifier = Modifier
            .size(78.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFEFF2FF)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Numbers,
            contentDescription = null,
            modifier = Modifier.size(34.dp),
            tint = Color(0xFF2145E8)
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(26.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF2145E8)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color.White
            )
        }
    }
}


@Composable
private fun CodeInputCard(
    name: FieldState,
    code: String,
    onNameChange: (String) -> Unit,
    onCodeChange: (String) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 430.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(
            1.dp,
            Color(0xFFE6E9F0)
        ),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "NOME DO VENDEDOR",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111111)
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = name.field,
                onValueChange = onNameChange,
                label = {
                    Text(
                        text = "Digite seu nome",
                        color = MaterialTheme.colorScheme.secondary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                supportingText = {
                    name.fieldError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
            )

            //
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "CÓDIGO DE 6 DÍGITOS",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF687386)
            )

            Spacer(Modifier.height(8.dp))

            CodeInput(
                code = code,
                onCodeChange = onCodeChange
            )
        }
    }
}

@Composable
private fun CodeInput(
    code: String,
    onCodeChange: (String) -> Unit
) {
    BasicTextField(
        value = code,
        onValueChange = { value ->
            if (value.length <= 6 && value.all(Char::isDigit)) {
                onCodeChange(value)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        singleLine = true,
        textStyle = TextStyle(
            color = Color.Transparent,
            fontSize = 1.sp
        ),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(6) { index ->

                    val digit = code.getOrNull(index)
                    val isActive = index == code.length

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = if (isActive) 2.dp else 1.dp,
                                color = if (isActive)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color(0xFFD8DEE9),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .background(Color(0xFFFBFCFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = digit?.toString() ?: "",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF111111)
                        )

                        if (digit == null && !isActive) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFFC9D1DF))
                            )
                        }
                    }
                }
            }
        }
    )
}




@Composable
private fun BottomButtons(
    uiState: VerificationCodeUiState,
    onBack: () -> Unit,
    onVerifyCode: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp)
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                onClick = onBack,
                modifier = Modifier
                    .weight(0.8f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF0F2F8),
                    contentColor = Color(0xFF4B5563)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Voltar",
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = onVerifyCode,
                enabled = uiState.sellerName.isValid && uiState.code.length <= 6 && !uiState.isLoading,
                modifier = Modifier.weight(1.2f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2947E8),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (uiState.isLoading) "Validando..." else "Prosseguir",
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.width(8.dp))

                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(19.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun VerificationCodeScreenPreview() {
    VerificationCodeContent(
        uiState = VerificationCodeUiState(
            code = "123456",
            sellerName = FieldState(
                field = "João da Silva",
                fieldError = null,
                isValid = true
            ),
            isLoading = false,
            errorMessage = null,
            success = true
        ),
    )
}