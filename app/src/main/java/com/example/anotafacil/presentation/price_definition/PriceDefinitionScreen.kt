package com.example.anotafacil.presentation.price_definition

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anotafacil.domain.formatter.parseBrazilianCurrencyInput
import com.example.anotafacil.domain.model.Product
import com.example.anotafacil.ui.components.AnnotationProductsStatusDialog
import com.example.anotafacil.ui.components.EasyNoteBrazilianCurrencyTextField
import com.example.anotafacil.ui.components.EasyNoteConfirmationDialog
import com.example.anotafacil.ui.components.EasyNoteLoadingOverlay
import kotlin.uuid.Uuid


@Composable
fun PriceDefinitionScreen(
    priceDefinitionViewModel: PriceDefinitionViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by priceDefinitionViewModel.uiState.collectAsState()

    var showLeaveDialog by remember { mutableStateOf(false) }

    BackHandler {
        if (uiState.hasChanges) showLeaveDialog = true
        else onBackClick()
    }


    if (showLeaveDialog) {
        EasyNoteConfirmationDialog(
            title = "Há alterações pendentes",
            subtitle = "Se você sair pederá todos os dados criados ou alterados",
            confirmButtonText = "Salvar",
            cancelButtonText = "Sair sem salvar",
            icon = Icons.Outlined.Info,
            onDismissRequest = { showLeaveDialog = false },
            onCancelButtonClick = {
                showLeaveDialog = false
                onBackClick()
            },
            onConfirmClick = {
                priceDefinitionViewModel.savePrices()
                showLeaveDialog = false
            }
        )
    }
    PriceDefinitionContent(
        uiState = uiState,
        onSaveClick = priceDefinitionViewModel::savePrices,
        onPriceChange = priceDefinitionViewModel::updateProductPrice,
        onDismiss = priceDefinitionViewModel::dismissPriceSaved,
        addManualProduct = priceDefinitionViewModel::addManualProduct,
        onManualProductPriceChange = priceDefinitionViewModel::updateManualProductPrice,
        onManualProductNameChange = priceDefinitionViewModel::updateManualProductName,
        onBackClick = {
            if (uiState.hasChanges) showLeaveDialog = true
            else onBackClick()
        },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceDefinitionContent(
    modifier: Modifier = Modifier,
    uiState: PriceDefinitionUiState,
    onSaveClick: () -> Unit = {},
    onPriceChange: (Uuid, String) -> Unit = { _, _ -> },
    onDismiss: () -> Unit = {},
    addManualProduct: () -> Unit,
    onManualProductPriceChange: (String) -> Unit = {},
    onManualProductNameChange: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
) {


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Definição de Preços",
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
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
                actions = {
                    TextButton(
                        onClick = onSaveClick,
                        enabled = uiState.hasChanges
                    ) {
                        Text(
                            "Salvar",
                            color = if (uiState.hasChanges) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onPrimary)
            )
        },
        containerColor = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier.fillMaxSize().imePadding()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                    color = Color.Transparent
                ) {
                    Text(
                        text = "Itens sem o preço definido não irão aparecer na seleção de produtos.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            item {
                AddProductCard(
                    uiState = uiState,
                    addManualProduct = addManualProduct,
                    onManualProductPriceChange = onManualProductPriceChange,
                    onManualProductNameChange = onManualProductNameChange
                )
            }


            items(uiState.items, key = { it.id }) { item ->
                InventoryPriceCard(
                    item = item,
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
                        text = "Preços atualizados aqui irão refletir, imediatamente, no mercado de compras do cliente. Verifique todos os valores antes de salvar.",
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (uiState.priceSaved) {
        AnnotationProductsStatusDialog(
            text = "Preços salvos com sucesso!",
            confirmClick = onDismiss,
            onDismiss = onDismiss,
        )
    }

    if (uiState.isLoading) {
        EasyNoteLoadingOverlay(
            modifier = Modifier,
            text = "Salvando alterações...",
        )
    }
}


@Composable
fun InventoryPriceCard(
    item: Product,
    onPriceValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var priceText by remember(item.price) {
        mutableStateOf(if (item.price == 0) "" else item.price.toString())
    }


    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
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
                    .background(
                        MaterialTheme.colorScheme.onSecondary,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(item.name.take(1), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )


            OutlinedTextField(
                value = priceText,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        priceText = newValue
                        onPriceValueChange(newValue)
                    }
                },
                prefix = {
                    Text(
                        text = "R$ ",
                        color = MaterialTheme.colorScheme.secondary,
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
                    color = MaterialTheme.colorScheme.onSurface
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}


/*@Composable
fun ConfirmPriceChangesDialog(
    changedItems: List<Product>,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textPrimary = Color(0xFF1A1C1E)
    val textSecondary = Color(0xFF74777F)
    val priceGreen = Color(0xFF2E7D32) // Verde moderno para indicar novos preços de forma positiva

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.onPrimary,
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
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onConfirmClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
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
}*/


@Composable
fun AddProductCard(
    uiState: PriceDefinitionUiState,
    addManualProduct: () -> Unit,
    onManualProductPriceChange: (String) -> Unit,
    onManualProductNameChange: (String) -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircleOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Adicionar Produto Manualmente",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo: Nome do Produto
            Text(
                text = "Nome do produto",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.manualProductName,
                onValueChange = onManualProductNameChange,
                placeholder = {
                    Text(
                        "Ex: Açúcar, Farinha...",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Label,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Preço unitário (R$)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))

            EasyNoteBrazilianCurrencyTextField(
                text = uiState.manuelProductPrice,
                onValueChange = {
                    onManualProductPriceChange(it)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            val manualProductPrice = parseBrazilianCurrencyInput(uiState.manuelProductPrice.text)
            Button(
                onClick = addManualProduct,
                enabled = uiState.manualProductName.isNotBlank() && manualProductPrice > 0.0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text(
                    text = "ADICIONAR À LISTA",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}


/*// 3. Exemplo de Preview Dinâmico do Dialog com dados estáticos
@Preview(showBackground = true)
@Composable
fun ConfirmPriceChangesDialogPreview() {
    val sampleChanges = listOf(
        PriceChangeSummary("1", "Sabão", "12,50", "13,90"),
        PriceChangeSummary("2", "Desinfetante", "8,90", "9,50")
    )

    MaterialTheme {
        *//*TODO: IMPLEMENTAR ESSE DIALOG FUTURAMENTE*//*
        ConfirmPriceChangesDialog(
            changedItems = sampleChanges,
            onDismissRequest = {},
            onConfirmClick = {}
        )
    }
}*/


@Preview(showBackground = true, device = "spec:width=1080px,height=2340px,dpi=440")
@Composable
fun PriceDefinitionScreenPreview() {
    MaterialTheme {
        PriceDefinitionContent(
            uiState = PriceDefinitionUiState(),
            onBackClick = {},
             //onSaveClick = {},
            onPriceChange = { _, _ -> },
            onDismiss = {},
            addManualProduct = {}
        )
    }
}