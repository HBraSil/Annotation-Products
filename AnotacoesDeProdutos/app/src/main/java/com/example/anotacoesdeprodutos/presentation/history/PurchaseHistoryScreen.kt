package com.example.anotacoesdeprodutos.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anotacoesdeprodutos.domain.model.Payment
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
                        fontSize = 14.sp,
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
                .padding(horizontal = 10.dp),
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

            if (uiState.history.isEmpty()) {
                item {
                    AnnotationProductsNothingToShow(
                        modifier = Modifier.padding(top = 30.dp),
                        text = "Nenhuma compra feita",
                    )
                }
            } else {
                uiState.history.forEach { (month, history) ->
                    item(month) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Text(
                                text = month,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            HorizontalDivider(modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                    items(history) { movement ->
                        when (movement) {
                            is HistoryMovement.UiPurchase -> {
                                PurchaseHistoryCard(movement.purchase)
                            }

                            is HistoryMovement.UiPayment -> {
                                if (!movement.payment.isTotalPayment) {
                                    PaymentHistoryCard(
                                        payment = movement.payment,
                                        isTotalPayment = false
                                    )
                                }
                                else {
                                    PaymentHistoryCard(
                                        payment = movement.payment,
                                        amountColor = MaterialTheme.colorScheme.surface.copy(green = 0.8f),
                                    )
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.secondary, thickness = 3.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun PurchaseHistoryCard(purchase: PurchaseWithItemsDomain) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Compras",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "COMPRAS",
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 16.dp), // Adicionei um end padding pro preço não colar na borda
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Envolvemos a lista de produtos em uma Column ou Row com weight(1f)
                // Isso garante que eles só usem o espaço que sobrar, protegendo o preço de ser cortado
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Para o texto não quebrar feio, podemos transformar a lista de produtos em uma única String inteligente
                    val produtosFormatados = remember(purchase.items) {
                        purchase.items.joinToString(separator = ", ") {
                            "${it.cartItem.quantity} ${it.product.name}"
                        }
                    }

                    Text(
                        text = produtosFormatados,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // 2. Um pequeno espaço de segurança entre os produtos e o preço
                Spacer(modifier = Modifier.width(8.dp))

                // 3. O preço agora está seguro, pois o weight(1f) lá de cima deu prioridade para o que sobra
                Text(
                    text = currencyFormatter.format(purchase.purchase.totalAmount),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}


@Composable
fun PaymentHistoryCard(
    payment: Payment,
    isTotalPayment: Boolean = true,
    amountColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(0.2f)
        ),
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Money,
                contentDescription = "Dinheiro",
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "PAGAMENTO",
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = payment.paymentDate.toBrazilianDate(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.width(6.dp))
            HorizontalDivider(modifier = Modifier.width(12.dp), color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isTotalPayment) "QUITADO" else currencyFormatter.format(payment.amount),
                color = amountColor,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PurchaseHistoryCardPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
        ) {

            Spacer(modifier = Modifier.height(10.dp))
            //PaymentHistoryCard()
        }
    }
}

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