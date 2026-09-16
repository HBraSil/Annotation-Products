import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    onBackClick: () -> Unit = {},
) {
    // Valores iniciais fixos
    val initialName = "Carlos Eduardo Silva"
    val initialEmail = "carlos.vendas@empresa.com"

    // Estados dos campos
    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }

    // Modos de edição
    var isEditingName by remember { mutableStateOf(false) }
    var isEditingEmail by remember { mutableStateOf(false) }

    // Habilita o botão apenas se houver alterações
    val hasChanges = name != initialName || email != initialEmail

    val textMuted = Color(0xFF94A3B8)
    val textDark = Color(0xFF0F172A)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Meu Perfil",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
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
                            tint = textDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                )
            )
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
                // Card Nível de Acesso
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                    Column {
                        Text(
                            text = "Nível de Acesso",
                            fontSize = 12.sp,
                            color = textMuted
                        )
                        Text(
                            text = "Proprietário",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textDark
                        )
                    }
                }

                // Campo Nome Completo (Sem Card)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("NOME COMPLETO") },
                    readOnly = !isEditingName,
                    enabled = isEditingName,
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { isEditingName = !isEditingName }) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Editar Nome",
                                tint = if (isEditingName) MaterialTheme.colorScheme.primary else textMuted
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        disabledBorderColor = Color(0xFFE2E8F0),
                        disabledTextColor = textDark,
                        focusedTextColor = textDark,
                        unfocusedTextColor = textDark,
                        disabledLabelColor = textMuted,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = textMuted
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Campo E-mail Comercial (Sem Card)
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-MAIL COMERCIAL") },
                    readOnly = !isEditingEmail,
                    enabled = isEditingEmail,
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { isEditingEmail = !isEditingEmail }) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Editar E-mail",
                                tint = if (isEditingEmail) MaterialTheme.colorScheme.primary else textMuted
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        disabledBorderColor = Color(0xFFE2E8F0),
                        disabledTextColor = textDark,
                        focusedTextColor = textDark,
                        unfocusedTextColor = textDark,
                        disabledLabelColor = textMuted,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = textMuted
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Ações no Rodapé
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        isEditingName = false
                        isEditingEmail = false
                    },
                    enabled = hasChanges,
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

                TextButton(onClick = {}) {
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
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    MyProfileScreen()
}