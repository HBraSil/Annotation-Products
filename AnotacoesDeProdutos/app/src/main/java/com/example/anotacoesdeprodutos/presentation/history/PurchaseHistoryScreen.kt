package com.example.anotacoesdeprodutos.presentation.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anotacoesdeprodutos.presentation.formatter.currencyFormatter

data class PurchaseOrder(
    val id: String,
    val date: String,
    val productsDescription: String,
    val totalValue: Double,
)

data class PurchaseHistoryUiState(
    val clientName: String = "João Silva",
    val orders: List<PurchaseOrder> = emptyList(),
    val totalOrdersCount: Int = 12
)

// 2. Componente CONTÊINER (Stateful)
@Composable
fun PurchaseHistoryScreen(
    onBackClick: () -> Unit = {},
) {
    // Dados imutáveis baseados exatamente na imagem enviada
    val mockOrders = remember {
        listOf(
            PurchaseOrder(
                id = "1",
                date = "15 de Outubro, 2023",
                productsDescription = "2 Sabão, 1 Desinfetante",
                totalValue = 45.00
            ),
            PurchaseOrder(
                id = "2",
                date = "02 de Outubro, 2023",
                productsDescription = "1 Amaciante, 3 Buchas, 1 Detergente",
                totalValue = 32.50
            ),
            PurchaseOrder(
                id = "3",
                date = "22 de Setembro, 2023",
                productsDescription = "5 Água Sanitária, 2 Panos Multiuso",
                totalValue = 58.90,
            )
        )
    }

    val uiState = remember(mockOrders) {
        PurchaseHistoryUiState(
            clientName = "João Silva",
            orders = mockOrders,
            totalOrdersCount = 12
        )
    }

    PurchaseHistoryContent(
        uiState = uiState,
        onBackClick = onBackClick,
    )
}

// 3. Componente de Visualização (Stateless)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseHistoryContent(
    modifier: Modifier = Modifier,
    uiState: PurchaseHistoryUiState,
    onBackClick: () -> Unit,
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "HISTÓRICO DE VENDAS",
                        fontSize = 14.sp, // Fonte menor para evitar quebras com fontes altas do sistema
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onPrimary)
            )
        },
        containerColor = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cabeçalho da Tela
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = uiState.clientName,
                        fontSize = 24.sp, // Fonte reduzida preventivamente
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Listagem de Cards de Histórico (Totalmente Stateless)
            items(uiState.orders, key = { it.id }) { order ->
                OrderHistoryCard(order = order)

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.secondary, thickness = 3.dp)
                Spacer(modifier = Modifier.height(22.dp))
            }
        }
    }
}

// 4. Sub-componente do Card de Histórico (Stateless)
@Composable
fun OrderHistoryCard(order: PurchaseOrder) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Bloco de Data
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Data",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = order.date,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Bloco de Produtos
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "PRODUTOS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 0.5.sp
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "•",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue
                )
                Text(
                    text = order.productsDescription,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 18.sp
                )
            }
        }

        HorizontalDivider(thickness = 0.dp)

        // Bloco de Valor (Sem o ícone de recibo lateral conforme solicitado)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Valor Total",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = currencyFormatter.format(order.totalValue),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// 5. Preview para Validação Real em Dispositivo Comercial
@Preview(showBackground = true, device = "spec:width=1080px,height=2340px,dpi=440")
@Composable
fun PurchaseHistoryScreenPreview() {
    MaterialTheme {
        PurchaseHistoryScreen()
    }
}