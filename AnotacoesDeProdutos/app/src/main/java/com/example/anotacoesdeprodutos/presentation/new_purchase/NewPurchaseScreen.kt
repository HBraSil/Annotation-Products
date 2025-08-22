package com.example.anotacoesdeprodutos.presentation.new_purchase

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anotacoesdeprodutos.domain.model.CartItem
import com.example.anotacoesdeprodutos.domain.model.Product
import com.example.anotacoesdeprodutos.presentation.formatter.currencyFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewPurchaseScreen(
    newPurchaseViewModel: NewPurchaseViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
) {
    val uiState by newPurchaseViewModel.uiState.collectAsState()

    Log.d("NewPurchaseScreen", "Selected Products: ${uiState.selectedProducts}")
    NewPurchaseContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onSelectProductClick = { newPurchaseViewModel.onProductSelected(it) },
        decreaseQntt = { newPurchaseViewModel.decreaseQuantity(it) },
        increaseQntt = { newPurchaseViewModel.increaseQuantity(it) },
        onFinalizeClick = { newPurchaseViewModel.finalizePurchase() },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPurchaseContent(
    modifier: Modifier = Modifier,
    uiState: NewPurchaseUiState = NewPurchaseUiState(),
    onBackClick: () -> Unit = {},
    onSelectProductClick: (Product) -> Unit = {},
    decreaseQntt: (Product) -> Unit = { _ -> },
    increaseQntt: (Product) -> Unit = { _ -> },
    onFinalizeClick: () -> Unit = {},
) {
    // --- Nova Paleta de Cores (Azuis, Verdes e Variantes) ---

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nova Compra",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Voltar",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onPrimary)
            )
        },
        containerColor = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp)
                    .background(MaterialTheme.colorScheme.errorContainer.copy(0.5f), RoundedCornerShape(24.dp))
                    .border(1.dp, MaterialTheme.colorScheme.error.copy(0.5f), RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFFFD8D6), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "!",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Column {
                        Text(
                            text = "Saldo Anterior Pendente",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currencyFormatter.format(uiState.pendingDebt),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Seção 2: Campo Seletor de Produto
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Adicionar Produto",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )

                Log.d("NewPurchaseScreen", "Selected Products: ${uiState.selectedProducts}")
                ProductDropdown(
                    products = uiState.allProducts,
                    selectedProduct = uiState.selectedProducts.lastOrNull(),
                    onProductSelected = onSelectProductClick
                )
            }


            if (uiState.selectedProducts.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Nenhum produto selecionado",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Use o menu acima para começar seu pedido",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            } else {
                HorizontalDivider(color = MaterialTheme.colorScheme.secondary, thickness = 0.dp)
                uiState.selectedProducts.forEach { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.product.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Unit: ${currencyFormatter.format(item.product.price.toDouble())}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.background(
                                        Color(0xFFF0F2F5),
                                        RoundedCornerShape(24.dp)
                                    ).padding(horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            decreaseQntt(item.product)
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) { Text("−", fontWeight = FontWeight.Bold) }
                                    Text(
                                        text = item.quantity.toString(),
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(
                                        onClick = {
                                            increaseQntt(item.product)
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) { Text(text = "+", fontWeight = FontWeight.Bold) }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "SUBTOTAL",
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = currencyFormatter.format(item.subtotal()),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Linha 1: Subtotal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Subtotal de Produtos",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 15.sp
                        )
                        Text(
                            text = currencyFormatter.format(uiState.selectedProductsSubtotal),
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Linha 2: Saldo Pendente
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Saldo Anterior Pendente", color = MaterialTheme.colorScheme.secondary, fontSize = 15.sp)
                        Text(
                            text = currencyFormatter.format(uiState.pendingDebt),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(color = Color(0xFFE2E4E8), thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "TOTAL GERAL",
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = currencyFormatter.format(uiState.totalPrice),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black
                            )
                        }


                        Button(
                            onClick = onFinalizeClick,
                            modifier = Modifier
                                .height(52.dp)
                                .padding(start = 12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            enabled = uiState.selectedProducts.isNotEmpty(),
                            shape = RoundedCornerShape(26.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Finalizar",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
                // Seção 4: Card de Fechamento Inferior (Fixo sobreposto)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDropdown(
    products: List<Product>,
    selectedProduct: CartItem?,
    onProductSelected: (Product) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    val bottomRadius by animateDpAsState(
        targetValue = if (expanded) 0.dp else 16.dp,
        animationSpec = tween(250),
        label = ""
    )

    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(250),
        label = "arrowRotation"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        Surface(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = bottomRadius,
                bottomEnd = bottomRadius
            ),
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = Color(0xFF0066FF)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Log.d("ProductDropdown", "Selected Product: ${selectedProduct?.product?.name}")
                Text(
                    text = selectedProduct?.product?.name
                        ?: "Selecione um produto no catálogo...",
                    modifier = Modifier.weight(1f),
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.rotate(arrowRotation)
                )
            }
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.background(Color.White),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            shadowElevation = 0.dp,
        ) {
            products.forEach { product ->
                DropdownMenuItem(
                    text = {
                        Text(product.name)
                    },
                    trailingIcon ={
                        Text(text = currencyFormatter.format(product.price.toDouble()), fontSize = 12.sp, color = Color.Gray)
                    },
                    onClick = {
                        onProductSelected(product)
                        expanded = false
                    }
                )
            }
        }
    }
}

// 4. Preview da Interface para Validação Real-Time
@Preview(showBackground = true, device = "spec:width=1080px,height=2340px,dpi=440")
@Composable
fun NewPurchaseScreenPreview() {
    MaterialTheme {
        NewPurchaseContent(
            uiState = NewPurchaseUiState(
                allProducts = listOf(
                    Product(name = "Produto 1", price = 10),
                    Product(name = "Produto 2", price = 15),
                    Product(name = "Produto 3", price = 20)
                ),
            ),
            //onSelectProductClick = { selectedProduct = it }
        )
    }
}