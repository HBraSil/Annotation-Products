package com.example.anotacoesdeprodutos.presentation.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anotacoesdeprodutos.domain.model.PurchaseWithItemsDomain
import com.example.anotacoesdeprodutos.presentation.components.AnnotationProductsNothingToShow
import com.example.anotacoesdeprodutos.presentation.formatter.currencyFormatter
import com.example.anotacoesdeprodutos.presentation.formatter.toBrazilianDate


@Composable
fun PurchaseHistoryScreen(
    purchaseHistoryViewModel: PurchaseHistoryViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
) {

    val uiState by purchaseHistoryViewModel.uiState.collectAsState()


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
    uiState: PurchaseHistoryUiState = PurchaseHistoryUiState(),
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
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            item {
                if (uiState.orders.isEmpty()) {
                    AnnotationProductsNothingToShow(
                        modifier = Modifier.padding(top = 30.dp),
                        text = "Nenhuma compra feita",
                    )
                } else {
                    uiState.orders.forEach { order ->

                        OrderHistoryCard(order = order)

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.secondary, thickness = 3.dp)
                        Spacer(modifier = Modifier.height(22.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun OrderHistoryCard(order: PurchaseWithItemsDomain) {

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
                text = order.purchase.purchaseDate.toBrazilianDate(),
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
                LazyRow {
                    items(order.items) { item ->
                        Text(
                            text = "${item.cartItem.quantity} ${item.product.name}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 18.sp
                        )
                        if (item != order.items.last()) {
                            Text(
                                text = ", ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
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
                text = currencyFormatter.format(order.purchase.total),
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
        PurchaseHistoryContent(
            uiState = PurchaseHistoryUiState(
                clientName = "Cliente Teste",
            ),
            onBackClick = {}
        )
    }
}