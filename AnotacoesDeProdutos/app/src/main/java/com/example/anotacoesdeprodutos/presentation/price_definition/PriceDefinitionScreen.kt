package com.example.anotacoesdeprodutos.presentation.price_definition

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anotacoesdeprodutos.domain.model.Product


@Composable
fun PriceDefinitionScreen(
    priceDefinitionViewModel: PriceDefinitionViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by priceDefinitionViewModel.uiState.collectAsState()

    PriceDefinitionContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onSaveClick = priceDefinitionViewModel::savePrices,
        onPriceChange = { productId, newPrice ->
            priceDefinitionViewModel.updateProductPrice(productId, newPrice)
        },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceDefinitionContent(
    uiState: PriceDefinitionUiState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onPriceChange: (Long, String) -> Unit,
    modifier: Modifier = Modifier
    ) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Definição de Preços", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Voltar", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                actions = {
                    TextButton(onClick = onSaveClick) {
                        Text("Save", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Definição de Preços",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Atualize os preços dos itens abaixo para aparecer na seleção de produtos. Itens sem o preço definido não irão aparecer na lista de produtos.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }


            items(uiState.items, key = { it.id }) { item ->
                Log.d("PriceDefinitionContent", "Nome: ${item.name} --> ID: ${item.id}")
                InventoryPriceCard(
                    item = item,
                    cardBgColor = MaterialTheme.colorScheme.onPrimary,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    subTextColor = MaterialTheme.colorScheme.secondary,
                    focusedColor = MaterialTheme.colorScheme.primary,
                    onPriceValueChange = { newValue ->
                        onPriceChange(item.id, newValue)
                    }
                )
            }


            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .background(MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Prices updated here will be reflected immediately in the customer-facing marketplace. Ensure all values are checked before saving.",
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// 5. Linha Reutilizável de Produto Totalmente STATELESS (Sem estado interno)
@Composable
fun InventoryPriceCard(
    item: Product,
    onPriceValueChange: (String) -> Unit,
    cardBgColor: Color,
    textColor: Color,
    subTextColor: Color,
    focusedColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.name.take(1), color = subTextColor, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.weight(1f))

            OutlinedTextField(
                value = item.price.toString(),
                onValueChange = onPriceValueChange,
                prefix = {
                    Text(
                        text = "R$ ",
                        color = subTextColor,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                },
                modifier = Modifier.width(110.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = focusedColor,
                    unfocusedBorderColor = Color(0xFFE2E4E8),
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                )
            )
        }
    }
}


// 1. Modelo simples contendo o reflexo de alteração pedido pelo Dialog
data class PriceChangeSummary(
    val id: String,
    val name: String,
    val oldPrice: String,
    val newPrice: String
)

// 2. Componente Totalmente Stateless (State Hoisting Estrito)
@Composable
fun ConfirmPriceChangesDialog(
    changedItems: List<PriceChangeSummary>,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // --- Adaptação para a paleta solicitada (Azuis modernos e Verdes) ---
    val primaryBlue = Color(0xFF0066FF)
    val textPrimary = Color(0xFF1A1C1E)
    val textSecondary = Color(0xFF74777F)
    val priceGreen = Color(0xFF2E7D32) // Verde moderno para indicar novos preços de forma positiva
    val dialogBgColor = Color(0xFFFFFFFF)

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = dialogBgColor,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Título do Dialog
                Text(
                    text = "Confirmar Alterações",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Texto de apoio
                Text(
                    text = "Os seguintes itens terão seus preços atualizados:",
                    fontSize = 15.sp,
                    color = textSecondary,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Lista interna para os itens alterados (limita o tamanho para não quebrar em telas pequenas)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(weight = 1f, fill = false)
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(changedItems, key = { it.id }) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Anterior: R$ ${item.oldPrice}",
                                    fontSize = 14.sp,
                                    color = textSecondary,
                                    textDecoration = TextDecoration.LineThrough // Riscado igual na imagem
                                )
                            }

                            Text(
                                text = "Novo: R$ ${item.newPrice}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = priceGreen // Utilizando variação verde da paleta moderna
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botões de Ação na base do Dialog
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismissRequest,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Cancelar",
                            color = primaryBlue,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onConfirmClick,
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Confirmar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

// 3. Exemplo de Preview Dinâmico do Dialog com dados estáticos
@Preview(showBackground = true)
@Composable
fun ConfirmPriceChangesDialogPreview() {
    val sampleChanges = listOf(
        PriceChangeSummary("1", "Sabão", "12,50", "13,90"),
        PriceChangeSummary("2", "Desinfetante", "8,90", "9,50")
    )

    MaterialTheme {
        ConfirmPriceChangesDialog(
            changedItems = sampleChanges,
            onDismissRequest = {},
            onConfirmClick = {}
        )
    }
}

// 6. Preview para Validação Dinâmica
@Preview(showBackground = true, device = "spec:width=1080px,height=2340px,dpi=440")
@Composable
fun PriceDefinitionScreenPreview() {
    MaterialTheme {
        PriceDefinitionScreen()
    }
}