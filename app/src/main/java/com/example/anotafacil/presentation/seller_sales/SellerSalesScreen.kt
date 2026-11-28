package com.example.anotafacil.presentation.seller_sales

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlin.uuid.Uuid
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale



data class SellerSalesUi(
    val sellerId: String,
    val sellerName: String,
    val totalSalesCents: Long,
    val customers: List<SellerCustomerUi>
)

data class SellerCustomerUi(
    val id: Uuid,
    val name: String
)


@Composable
fun SellerSalesList(
    sellers: List<SellerSalesUi>,
    expandedSellerId: String?,
    onSellerClick: (String) -> Unit,
    onCustomerClick: (Uuid) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vendedores vinculados (${sellers.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (sellers.isNotEmpty()) {
                    Text(
                        text = "Toque para ver clientes",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (sellers.isEmpty()) {
            item {
                Text(
                    text = "Nenhum vendedor vinculado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(
            items = sellers,
            key = { seller -> seller.sellerId }
        ) { seller ->
            SellerSalesCard(
                seller = seller,
                formattedTotal = currencyFormat.format(
                    BigDecimal.valueOf(seller.totalSalesCents, 2)
                ),
                expanded = expandedSellerId == seller.sellerId,
                onSellerClick = { onSellerClick(seller.sellerId) },
                onCustomerClick = onCustomerClick
            )
        }
    }
}

@Composable
private fun SellerSalesCard(
    seller: SellerSalesUi,
    formattedTotal: String,
    expanded: Boolean,
    onSellerClick: () -> Unit,
    onCustomerClick: (Uuid) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val customerCount = seller.customers.size

    val initials = remember(seller.sellerName) {
        val names = seller.sellerName.trim().split(Regex("\\s+"))
        val first = names.first().take(1)
        val last = if (names.size > 1) names.last().take(1) else ""
        (first + last).uppercase()
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colors.surface,
        border = BorderStroke(
            width = 1.dp,
            color = if (expanded) colors.primary else colors.outlineVariant
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        role = Role.Button,
                        onClickLabel = if (expanded) "Ocultar clientes" else "Ver clientes",
                        onClick = onSellerClick
                    )
                    .semantics {
                        stateDescription = if (expanded) "Expandido" else "Recolhido"
                    }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(38.dp),
                    shape = CircleShape,
                    color = colors.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = initials,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.onPrimaryContainer
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = seller.sellerName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = when (customerCount) {
                            0 -> "Nenhum cliente"
                            1 -> "1 cliente único"
                            else -> "$customerCount clientes únicos"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    text = formattedTotal,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (expanded) colors.primary else colors.onSurface
                )

                Spacer(Modifier.width(4.dp))

                Icon(
                    imageVector = if (expanded) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = colors.onSurfaceVariant
                )
            }

            if (expanded) {
                HorizontalDivider(color = colors.outlineVariant)

                Surface(color = colors.surfaceContainerLow) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Clientes atendidos no período",
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.onSurfaceVariant
                            )

                            Text(
                                text = customerCount.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.onSurfaceVariant
                            )
                        }

                        seller.customers.forEachIndexed { index, customer ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        role = Role.Button,
                                        onClickLabel = "Ver detalhes de ${customer.name}",
                                        onClick = { onCustomerClick(customer.id) }
                                    )
                                    .heightIn(min = 48.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = customer.name,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = colors.onSurfaceVariant
                                )
                            }

                            if (index < seller.customers.lastIndex) {
                                HorizontalDivider(color = colors.outlineVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SellerSalesScreen(
    sellers: List<SellerSalesUi>,
    onNavigateToCustomerDetail: (Uuid) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedSellerId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    SellerSalesList(
        sellers = sellers,
        expandedSellerId = expandedSellerId,
        onSellerClick = { clickedId ->
            expandedSellerId = if (clickedId == expandedSellerId) {
                null
            } else {
                clickedId
            }
        },
        onCustomerClick = onNavigateToCustomerDetail,
        modifier = modifier
    )
}




@Preview(
    name = "Vendas por vendedor",
    showBackground = true,
    widthDp = 390,
    heightDp = 720
)
@Composable
private fun SellerSalesListPreview() {
    val sellers = remember {
        fun customer(name: String) = SellerCustomerUi(
            id = Uuid.random(),
            name = name
        )

        listOf(
            SellerSalesUi(
                sellerId = "mariana",
                sellerName = "Mariana Costa",
                totalSalesCents = 1_485_000L,
                customers = listOf(
                    customer("Padaria Pão Dourado"),
                    customer("Mercado Bom Preço"),
                    customer("Restaurante Sabor da Terra"),
                    customer("Cafeteria Grão & Arte"),
                    customer("Hortifruti Vila Nova")
                )
            ),
            SellerSalesUi(
                sellerId = "rafael",
                sellerName = "Rafael Oliveira",
                totalSalesCents = 1_143_000L,
                customers = listOf(
                    customer("Mercado Central"),
                    customer("Padaria São José"),
                    customer("Cantina da Praça"),
                    customer("Mercearia do João")
                )
            ),
            SellerSalesUi(
                sellerId = "camila",
                sellerName = "Camila Ferreira",
                totalSalesCents = 968_000L,
                customers = listOf(
                    customer("Café da Vila"),
                    customer("Empório Primavera"),
                    customer("Mercado Nova Era"),
                    customer("Doceria da Ana")
                )
            ),
            SellerSalesUi(
                sellerId = "lucas",
                sellerName = "Lucas Mendes",
                totalSalesCents = 754_000L,
                customers = listOf(
                    customer("Padaria Imperial"),
                    customer("Mercado Aurora"),
                    customer("Lanchonete Avenida")
                )
            ),
            SellerSalesUi(
                sellerId = "beatriz",
                sellerName = "Beatriz Almeida",
                totalSalesCents = 542_000L,
                customers = listOf(
                    customer("Bistrô do Centro"),
                    customer("Mercearia Santa Luzia")
                )
            )
        )
    }

    var expandedSellerId by remember { mutableStateOf<String?>("mariana") }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(modifier = Modifier.fillMaxSize().padding(top = 96.dp)) {
            SellerSalesList(
                sellers = sellers,
                expandedSellerId = expandedSellerId,
                onSellerClick = { sellerId ->
                    expandedSellerId = if (expandedSellerId == sellerId) null else sellerId
                },
                onCustomerClick = {}
            )
        }
    }
}