package com.example.anotafacil.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onForgotPasswordClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    val uiState by authViewModel.uiState.collectAsState()


    if (uiState.success) {
        onLoginClick()
    }

    LoginContent(
        uiState = uiState,
        onForgotPasswordClick = onForgotPasswordClick,
        onGoogleLoginClick = authViewModel::loginWithGoogle,
        onEmailChange = authViewModel::updateEmail,
        onPasswordChange = authViewModel::updatePassword,
        onLoginClick = authViewModel::loginWithEmailAndPassword,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit = {},
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Ícone do topo (Bússola/Logo)
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Login,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Título e Descrição
            Text(
                text = "Faça o login",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card Principal do Formulário
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Campo de E-mail
                    Text(
                        text = "E-mail",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomTextField(
                        value = uiState.email.field,
                        onValueChange = { onEmailChange(it) },
                        placeholder = "seu.email@exemplo.com",
                        leadingIcon = Icons.Default.Mail,
                        keyboardType = KeyboardType.Email,
                        supportingText = {
                            if (uiState.email.fieldError != null) {
                                Text(
                                    text = uiState.email.fieldError,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de Senha
                    Text(
                        text = "Senha",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomTextField(
                        value = uiState.password.field,
                        onValueChange = { onPasswordChange(it) },
                        placeholder = "••••••••",
                        leadingIcon = Icons.Outlined.Lock,
                        isPassword = true,
                        isPasswordVisible = isPasswordVisible,
                        onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                        keyboardType = KeyboardType.Password,
                        supportingText = {
                            if (uiState.password.fieldError != null) {
                                Text(
                                    text = uiState.password.fieldError,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )

                    // Esqueceu a senha
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        TextButton(
                            onClick = onForgotPasswordClick,
                            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 0.dp)
                        ) {
                            Text(
                                text = "Esqueceu a senha?",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botão Entrar
                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        enabled = uiState.allFieldsValid,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Entrar",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "ou continue com",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botão Google
                    Surface(
                        onClick = onGoogleLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            GoogleIcon(modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Continuar com o Google",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Não tem uma conta? ",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(
                    onClick = {  },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Cadastre-se",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Componente Reutilizável de Input
@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePasswordVisibility: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text,
    supportingText: @Composable (() -> Unit)?
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = "Alternar visibilidade da senha",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        supportingText = supportingText,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
}


@Composable
private fun GoogleIcon(modifier: Modifier = Modifier) {
    Text(
        text = "G",
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}