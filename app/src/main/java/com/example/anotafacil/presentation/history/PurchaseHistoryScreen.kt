package com.example.anotafacil.presentation.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anotafacil.domain.formatter.brazilianCurrencyFormatter
import com.example.anotafacil.domain.formatter.toBrazilianDate
import com.example.anotafacil.domain.model.CartItem
import com.example.anotafacil.domain.model.CartItemWithProductDomain
import com.example.anotafacil.domain.model.Payment
import com.example.anotafacil.domain.model.Product
import com.example.anotafacil.domain.model.Purchase
import com.example.anotafacil.domain.model.PurchaseWithItemsDomain
import com.example.anotafacil.ui.components.AnnotationProductsNothingToShow
import kotlin.uuid.Uuid


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
                        text = uiState.clientName,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primaryContainer
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
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 10.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (uiState.history.isEmpty()) {
                item {
                    AnnotationProductsNothingToShow(
                        modifier = Modifier.padding(top = 30.dp),
                        text = "Nenhuma compra feita",
                    )
                }
            } else {
                uiState.history.forEach { (month, history) ->
                    item(key = month) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = month,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                uiState.monthlyBalances[month]?.let { balance ->
                                    MonthBalanceSummary(
                                        openingBalance = brazilianCurrencyFormatter.format(
                                            balance.openingBalance
                                        ),
                                        closingBalance = brazilianCurrencyFormatter.format(
                                            balance.closingBalance
                                        )
                                    )
                                }
                            }

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                history.forEach { movement ->
                                    when (movement) {
                                        is HistoryMovement.UiPurchase -> {
                                            PurchaseHistoryCard(
                                                movement.purchaseWithItemsDomain
                                            )
                                        }

                                        is HistoryMovement.UiPayment -> {
                                            if (movement.payment.isTotalPayment) {
                                                PaymentHistoryCard(
                                                    payment = movement.payment,
                                                    amountColor = MaterialTheme.colorScheme.surface
                                                        .copy(green = 0.8f)
                                                )
                                            } else {
                                                PaymentHistoryCard(
                                                    payment = movement.payment,
                                                    isTotalPayment = false
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun MonthBalanceSummary(
    openingBalance: String,
    closingBalance: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.onPrimary,

    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Início: ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(openingBalance)
                    }
                },
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "→",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = buildAnnotatedString {
                    append("Saldo final: ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(closingBalance)
                    }
                },
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodySmall
            )
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
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Purchase Icon",
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "COMPRA",
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.width(6.dp))
                    HorizontalDivider(
                        modifier = Modifier.width(14.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = purchase.purchase.purchaseDate.toBrazilianDate(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                        text = brazilianCurrencyFormatter.format(purchase.purchase.totalAmount),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }

    }
}


@Composable
fun PaymentHistoryCard(
    payment: Payment,
    isTotalPayment: Boolean = true,
    amountColor: Color = MaterialTheme.colorScheme.primaryContainer,
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
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PAGAMENTO",
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.width(6.dp))
                    HorizontalDivider(
                        modifier = Modifier.width(14.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = payment.paymentDate.toBrazilianDate(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isTotalPayment) "QUITADO" else brazilianCurrencyFormatter.format(
                        payment.amount
                    ),
                    color = amountColor,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
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
                history = mapOf(
                    "1" to listOf(
                        HistoryMovement.UiPayment(
                            Payment(
                                id = Uuid.random(),
                                customerId = Uuid.random(),
                                amount = 100.0,
                                paymentDate = System.currentTimeMillis(),
                                isTotalPayment = false
                            )
                        ),
                        HistoryMovement.UiPurchase(
                            PurchaseWithItemsDomain(
                                purchase = Purchase(
                                    id = Uuid.random(),
                                    customerId = Uuid.random(),
                                    totalAmount = 100.0,
                                    purchaseDate = System.currentTimeMillis(),
                                ),
                                items = listOf(
                                    CartItemWithProductDomain(
                                        cartItem = CartItem(
                                            id = 1L,
                                            productId = Uuid.random(),
                                            quantity = 1
                                        ),
                                        product = Product(
                                            name = "Produto Teste",
                                            price = 100,
                                        )
                                    )
                                )
                            )
                        )
                    )
                ),
                monthlyBalances = mapOf(
                    "1" to MonthlyBalance(openingBalance = 100.0, closingBalance = 100.0)
                )
            ),
            onBackClick = {}
        )
    }
}