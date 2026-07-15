package com.example.anotacoesdeprodutos.presentation.customers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anotacoesdeprodutos.presentation.components.AnnotationProductsTextField
import com.example.anotacoesdeprodutos.presentation.components.AnnotationProductsSuccessDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewCustomerScreen(
    uiState: CustomersUiState = CustomersUiState(),
    onDismissOverlayCreatedCustomer: () -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onExtraInfoChange: (String) -> Unit = {},
    onCreateClientClick: () -> Unit = {},
    onCloseModal: () -> Unit = {},
) {
    ModalBottomSheet(
        onDismissRequest = onCloseModal,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = null,
        modifier = Modifier.statusBarsPadding()
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "New Client",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onCloseModal,
                            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Voltar",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onPrimary)
                )
            },
            containerColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(48.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "NOME DO CLIENTE",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            letterSpacing = 0.5.sp
                        )
                        AnnotationProductsTextField(
                            text = uiState.name,
                            onNameChange = onNameChange,
                            placeholder = "Digite o nome do cliente"
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Campo 2: ADDRESS COMPLEMENT (Totalmente Stateless)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "INFORMAÇÃO EXTRA",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            letterSpacing = 0.5.sp
                        )
                        AnnotationProductsTextField(
                            text = uiState.extraInfo ?: "",
                            onNameChange = onExtraInfoChange,
                            placeholder = "Rua, Bairro, Perto de..."
                        )
                    }
                }

                // Botão "Create Client" posicionado de forma fixa na base da tela
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    Button(
                        onClick = onCreateClientClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = uiState.name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Criar Cliente",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.customerCreatedWithSuccess) {
        AnnotationProductsSuccessDialog(
            text = "Cliente adicionado com sucesso!",
            onDismiss = onDismissOverlayCreatedCustomer
        )
    }
}

// 4. Preview interativo da tela de acordo com as proporções do dispositivo
@Preview(showBackground = true, device = "spec:width=1080px,height=2340px,dpi=440")
@Composable
fun AddNewClientScreenPreview() {
    MaterialTheme {
        AddNewCustomerScreen(
            
        )
    }
}