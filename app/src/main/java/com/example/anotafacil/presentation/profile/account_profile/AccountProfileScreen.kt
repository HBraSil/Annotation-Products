import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anotafacil.presentation.profile.account_profile.AccountProfileViewModel
import com.example.anotafacil.presentation.profile.account_profile.ProfileDetailUiState
import com.example.anotafacil.ui.components.AnnotationProductsConfirmationDialog
import com.example.anotafacil.ui.components.EasyNotesTextField


@Composable
fun AccountProfileScreen(
    accountProfileViewModel: AccountProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    goToLoginScreen: () -> Unit = {},
) {
    val uiState by accountProfileViewModel.uiState.collectAsState()

    AccountProfileContent(
        uiState = uiState,
        onUpdateName = accountProfileViewModel::updateName,
        onUpdateEmail = accountProfileViewModel::updateEmail,
        goToLoginScreen = goToLoginScreen,
        deleteAccount = accountProfileViewModel::deleteAccount,
        saveChanges = accountProfileViewModel::saveChanges,
        onBackClick = onBackClick,
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountProfileContent(
    uiState: ProfileDetailUiState = ProfileDetailUiState(),
    onUpdateName: (String) -> Unit = {},
    onUpdateEmail: (String) -> Unit = {},
    goToLoginScreen: () -> Unit = {},
    deleteAccount: () -> Unit = {},
    onBackClick: () -> Unit = {},
    saveChanges: () -> Unit = {},
) {
    var showAccountConfirmationDialog by remember {
        mutableStateOf<Pair<Int, String?>>(
            Pair(
                0,
                null
            )
        )
    }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.emailSentMessage) {
        uiState.emailSentMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Long
            )
        }
    }

    LaunchedEffect(uiState.successfullyDeleted) {
        if (uiState.successfullyDeleted) {
            goToLoginScreen()
        }
    }



    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Meu Perfil",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .shadow(2.dp, CircleShape)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost ={
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = MaterialTheme.colorScheme.onPrimary
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Nível de Acesso",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = uiState.user?.role?.name ?: "Nível de Acesso",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                /*// Campo Nome Completo (Sem Card)
                OutlinedTextField(
                    value = uiState.name.field,
                    onValueChange = onUpdateName,
                    singleLine = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Editar Nome",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.onPrimary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                        disabledBorderColor = MaterialTheme.colorScheme.onPrimary,
                        disabledTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        disabledLabelColor = MaterialTheme.colorScheme.secondary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )*/
                EasyNotesTextField(
                    value = uiState.name.field,
                    onValueChange = onUpdateName,
                    leadingIcon = Icons.Outlined.Edit,
                    supportingText = {
                        uiState.name.fieldError?.let {
                            Text(
                                text = uiState.name.fieldError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                )

                // Campo E-mail Comercial (Sem Card)
                /*OutlinedTextField(
                    value = uiState.email.field,
                    onValueChange = onUpdateEmail,
                    singleLine = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Editar E-mail",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.onPrimary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                        disabledBorderColor = MaterialTheme.colorScheme.onPrimary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        *//*disabledTextColor = textDark,
                        unfocusedTextColor = textDark,
                        disabledLabelColor = textMuted,
                        unfocusedLabelColor = textMuted*//*
                    ),
                    modifier = Modifier.fillMaxWidth()
                )*/
                EasyNotesTextField(
                    value = uiState.email.field,
                    onValueChange = onUpdateEmail,
                    leadingIcon = Icons.Outlined.Edit,
                    supportingText = {
                        uiState.email.fieldError?.let {
                            Text(
                                text = uiState.email.fieldError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                )
            }

            FooterActions(
                uiState = uiState,
                saveChanges = {
                    showAccountConfirmationDialog = Pair(
                        first = 1,
                        second = "Tem certeza que deseja salvar as alterações?"
                    )
                },
                deleteAccount = {
                    showAccountConfirmationDialog = Pair(
                        first = 2,
                        second = "Tem certeza que deseja excluir sua conta?"
                    )
                }
            )
        }

        showAccountConfirmationDialog.second?.let {
            AnnotationProductsConfirmationDialog(
                title = it,
                onDismissRequest = {
                    showAccountConfirmationDialog = Pair(first = 0, second = null)
                },
                onConfirmClick = {
                    when (showAccountConfirmationDialog.first) {
                        1 -> saveChanges()
                        2 -> deleteAccount()
                    }
                }
            )
        }

    }

    if(uiState.isDeleting) {
        DeletingDataOverlay()
    }

}


@Composable
fun FooterActions(
    uiState: ProfileDetailUiState = ProfileDetailUiState(),
    saveChanges: () -> Unit = {},
    deleteAccount: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = saveChanges,
            enabled = uiState.wasNameChanged || uiState.wasEmailChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Salvar Alterações",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }
        }

        TextButton(onClick = deleteAccount) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Excluir minha conta",
                color = Color(0xFFEF4444),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Composable
private fun DeletingDataOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Excluindo todos os seus dados...",
            color = Color.White
        )
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    AccountProfileContent()
}