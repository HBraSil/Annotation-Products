package com.example.anotafacil.presentation.auth

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anotafacil.ui.components.EasyNotesTextField
import com.hilquias.anotafacil.R

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ForgotPasswordContent(
        uiState = uiState,
        isLoading = uiState.isLoading,
        onEmailChange = viewModel::updateEmail,
        onSendLinkClick = viewModel::resetPassword,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun ForgotPasswordContent(
    uiState: AuthUiState = AuthUiState(),
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onSendLinkClick: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(start = 16.dp, top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Voltar"
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Text(
                text = "Recuperar senha",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Digite seu e-mail para receber as instruções e redefinir seu acesso.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "E-mail cadastrado",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            EasyNotesTextField(
                value = uiState.email.field,
                onValueChange = onEmailChange,
                placeholder = "nome@exemplo.com",
                keyboardType = KeyboardType.Email,
                leadingIcon = Icons.Default.Mail,
                supportingText = {
                    uiState.email.fieldError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                /*keyboardActions = KeyboardActions(
                    onDone = {
                        if (!isLoading) onSendLinkClick()
                    }
                )*/
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onSendLinkClick,
                enabled = !isLoading && uiState.email.field.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Enviar link",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}



@Preview
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordContent(
        uiState = AuthUiState(),
        isLoading = false,
        onEmailChange = {},
        onSendLinkClick = {},
        onNavigateBack = {}
    )
}