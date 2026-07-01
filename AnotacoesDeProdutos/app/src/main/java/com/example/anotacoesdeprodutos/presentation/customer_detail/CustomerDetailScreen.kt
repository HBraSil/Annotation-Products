package com.example.anotacoesdeprodutos.presentation.customer_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anotacoesdeprodutos.presentation.components.AnnotationProductsNothingToShow
import com.example.anotacoesdeprodutos.presentation.formatter.currencyFormatter
import com.example.anotacoesdeprodutos.presentation.formatter.toBrazilianDate


@Composable
fun CustomerDetailScreen(
    customerDetailViewModel: CustomerDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onHistoryClick: (Long) -> Unit = {},
    goToNewPurchaseScreen: (Long) -> Unit = {},
) {
    var partialPayment by remember { mutableStateOf("") }

    val uiState by customerDetailViewModel.uiState.collectAsState()

    ClientDetailsContent(
        uiState = uiState,
        onPartialPaymentChange = { partialPayment = it },
        onBackClick = onBackClick,
        onHistoryClick = onHistoryClick,
        goToNewPurchaseScreen = goToNewPurchaseScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailsContent(
    uiState: CustomerDetailUiState = CustomerDetailUiState(),
    onPartialPaymentChange: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onHistoryClick: (Long) -> Unit = {},
    goToNewPurchaseScreen: (Long) -> Unit = {},
) {
    var isPartialPaymentExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Cliente") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { onHistoryClick(uiState.customer.id) }) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Histórico",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { goToNewPurchaseScreen(uiState.customer.id) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Adicionar",
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "NOVA COMPRA"
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = uiState.customer.name,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar cliente",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    uiState.customer.extraInfo?.let {
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    if (uiState.purchase.purchaseDate > 0L) {
                        Text(
                            text = "Última compra: ${uiState.purchase.purchaseDate.toBrazilianDate()}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.surface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Seção 2: Cabeçalho da Listagem Mensal
            item {
                Text(
                    "Compras do Último Mês",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (uiState.purchaseItems == null) {
                    AnnotationProductsNothingToShow(
                        modifier = Modifier.padding(vertical = 40.dp),
                        text = "Nenhuma compra feita",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Seção 3: Itens Comprados no Último Mês (Histórico Meio da Tela)
            items(uiState.purchaseItems ?: emptyList(), key = { it.id }) { item ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Pequeno marcador redondo para simular o bullet point elegante do app
                            Box(modifier = Modifier.size(6.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                            Column {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        item.product.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    HorizontalDivider(color = Color(0xFFB8B7B7), thickness = 1.dp, modifier = Modifier.width(10.dp))
                                    Text("unit: R$ ${item.product.price}", fontSize = 8.sp, color = Color.Gray)
                                }
                                Text("Quantidade: ${item.quantity}x", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("TOTAL", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                            Text(text = currencyFormatter.format(item.subtotal()), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                }
            }

            // Seção 4: O CARD DE SALDO TOTAL EM ABERTO COM ANIMAÇÃO
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent) // Gerenciado internamente pelas seções
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(vertical = 24.dp, horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    "SALDO TOTAL EM ABERTO",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = currencyFormatter.format(uiState.purchase.total),
                                    fontSize = 32.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            // Botão Quitar Total
                            Button(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                enabled = uiState.purchase.total > 0,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.onPrimary,
                                    contentColor = MaterialTheme.colorScheme.primary,
                                    disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                    )
                                    Text(
                                        text = "Quitar Total",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        // Aba Inferior de Pagamento Parcial (Gatilho da Animação)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.secondary.copy(0.4f))
                        ) {
                            // Cabeçalho clicável que dispara a expansão
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        isPartialPaymentExpanded = !isPartialPaymentExpanded
                                    }
                                    .padding(vertical = 16.dp, horizontal = 20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "PAGAMENTO PARCIAL",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = if (isPartialPaymentExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (isPartialPaymentExpanded) "Recolher" else "Expandir",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }

                            // COMPONENTE ANIMADO: Desce suavemente ao clicar
                            AnimatedVisibility(
                                visible = isPartialPaymentExpanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = "uiState.customer.partialPayment",
                                        onValueChange = onPartialPaymentChange,
                                        placeholder = { Text("R$ 0,00", color = MaterialTheme.colorScheme.secondary) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                                            focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                                            unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                    Button(
                                        onClick = { /* Ação de confirmação */ },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.height(54.dp)
                                    ) {
                                        Text("Confirmar", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(60.dp)) // Espaço extra para não cobrir o conteúdo com o FAB
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1080px,height=2340px,dpi=440")
@Composable
fun CustomerDetailScreenPreview() {
    MaterialTheme {
        ClientDetailsContent()
    }
}