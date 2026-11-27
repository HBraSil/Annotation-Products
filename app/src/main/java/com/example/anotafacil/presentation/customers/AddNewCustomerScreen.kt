package com.example.anotafacil.presentation.customers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anotafacil.domain.formatter.brazilianCurrencyFormatter
import com.example.anotafacil.domain.model.Product
import com.example.anotafacil.ui.components.AnnotationProductsTextField
import com.example.anotafacil.ui.components.AnnotationProductsStatusDialog
import com.example.anotafacil.ui.components.EasyNoteBrazilianCurrencyTextField
import com.example.anotafacil.ui.components.EasyNoteProductQuantityCard
import com.example.anotafacil.ui.components.EasyNotesExposedDropDown
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewCustomerScreen(
    uiState: CustomersUiState = CustomersUiState(),
    title: String = "Novo Cliente",
    submitButtonText: String = "Criar Cliente",
    successMessage: String = "Cliente adicionado com sucesso!",
    onDismissOverlayCreatedCustomer: () -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onExtraInfoChange: (String) -> Unit = {},
    onCreateClientClick: () -> Unit = {},
    onCloseModal: () -> Unit = {},
    onLastPurchaseProductSelected: (Product) -> Unit = {},
    onLastPurchaseDateChange: (Long?) -> Unit = {},
    onOpeningDebtChange: (String) -> Unit = {},
    onIncreaseQuantityClick: (Product) -> Unit = {},
    onDecreaseQuantityClick: (Product) -> Unit = {},
    lastPaymentContent: @Composable () -> Unit = {},
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
                            title,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onCloseModal,
                            modifier = Modifier
                                .padding(start = 12.dp, top = 15.dp)
                                .shadow(2.dp, CircleShape)
                                .background(MaterialTheme.colorScheme.onPrimary, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Voltar",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                )
            },
            containerColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.fillMaxSize().imePadding()
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    //.verticalScroll(rememberScrollState())
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        //.weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 96.dp),
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
                            text = uiState.addNewCustomersField.name,
                            onNameChange = onNameChange,
                            placeholder = "Digite o nome do cliente",
                            capitalization = KeyboardCapitalization.Words
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
                            text = uiState.addNewCustomersField.extraInfo,
                            onNameChange = onExtraInfoChange,
                            placeholder = "Rua, Bairro, Perto de..."
                        )
                    }

                    Spacer(Modifier.height(40.dp))

                    LastPurchaseHistorySection(
                        uiState = uiState.addNewCustomersField,
                        onProductSelected = onLastPurchaseProductSelected,
                        onPurchaseDateChange = onLastPurchaseDateChange,
                        onOpeningDebtChange = onOpeningDebtChange,
                        lastPaymentContent = lastPaymentContent,
                        onIncreaseQuantityClick = onIncreaseQuantityClick,
                        onDecreaseQuantityClick = onDecreaseQuantityClick
                    )

                    Spacer(Modifier.height(24.dp))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Button(
                        onClick = onCreateClientClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = uiState.addNewCustomersField.name.isNotBlank() &&
                            !uiState.isSavingCustomer,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (uiState.isSavingCustomer) "Salvando..." else submitButtonText,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            if (uiState.isSavingCustomer) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
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
    }

    if (uiState.success) {
        AnnotationProductsStatusDialog(
            text = successMessage,
            confirmClick = onDismissOverlayCreatedCustomer,
            onDismiss = onDismissOverlayCreatedCustomer,
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LastPurchaseHistorySection(
    uiState: AddNewCustomersField,
    onProductSelected: (Product) -> Unit,
    onPurchaseDateChange: (Long?) -> Unit,
    onOpeningDebtChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    lastPaymentContent: @Composable () -> Unit = {},
    onDecreaseQuantityClick: (Product) -> Unit,
    onIncreaseQuantityClick: (Product) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val cardShape = RoundedCornerShape(16.dp)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ReceiptLong,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = colors.primary
            )

            Text(
                text = "Histórico da Última Compra",
                modifier = Modifier.weight(1f, fill = false),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface
            )

            HorizontalDivider(
                modifier = Modifier.weight(0.25f),
                color = colors.outlineVariant
            )

            Text(
                text = "• OPCIONAL",
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Preencha apenas se este cliente já possuir registro ou compra anterior.",
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = cardShape,
            color = colors.primaryContainer.copy(0.1f),
            border = BorderStroke(1.dp, colors.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LastPurchaseProductField(
                    products = uiState.availableProducts,
                    selectedProduct = uiState.lastPurchasedProducts,
                    onProductSelected = onProductSelected,
                    onDecreaseQuantityClick = onDecreaseQuantityClick,
                    onIncreaseQuantityClick = onIncreaseQuantityClick
                )

                LastPurchaseDateField(
                    purchaseDateMillis = uiState.lastPurchaseDateMillis,
                    onPurchaseDateChange = onPurchaseDateChange
                )

                lastPaymentContent()

                HorizontalDivider(color = colors.outlineVariant)

                OpeningDebtField(
                    openingDebt = uiState.openingDebt,
                    onOpeningDebtChange = onOpeningDebtChange
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LastPurchaseProductField(
    products: List<Product>,
    selectedProduct: Map<Product, Int>,
    onProductSelected: (Product) -> Unit,
    onDecreaseQuantityClick: (Product) -> Unit,
    onIncreaseQuantityClick: (Product) -> Unit,
) {

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        HistoryFieldLabel("O QUE COMPROU NA ÚLTIMA VEZ?")

        EasyNotesExposedDropDown(
            items = products,
            selectedItem = selectedProduct.keys.lastOrNull()?.name,
            itemText = { it.name },
            leadingIcon = Icons.Default.ShoppingCart,
            containerColor = MaterialTheme.colorScheme.onPrimary,
            trailingContent = { item ->
                Text(
                    text = brazilianCurrencyFormatter.format(item.price.toDouble()),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            },
            onProductSelected = {
                onProductSelected(it)
            }
        )

        selectedProduct.forEach {
            EasyNoteProductQuantityCard(
                item = it.key,
                productName = it.key.name,
                productPrice = it.key.price.toString(),
                quantity = it.value.toString(),
                decreaseQntt = { product ->
                    onDecreaseQuantityClick(product)
                },
                increaseQntt = { product ->
                    onIncreaseQuantityClick(product)
                },
                subtotal = { it.key.price * it.value }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LastPurchaseDateField(
    purchaseDateMillis: Long?,
    onPurchaseDateChange: (Long?) -> Unit,
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    val formattedDate = remember(purchaseDateMillis) {
        purchaseDateMillis?.let { millis ->
            SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))
                .apply { timeZone = TimeZone.getTimeZone("UTC") }
                .format(Date(millis))
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        HistoryFieldLabel("DATA DA ÚLTIMA COMPRA")

        Surface(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.onPrimary
        ) {
            Row(
                modifier = Modifier
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = formattedDate ?: "dd/mm/aaaa",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (formattedDate == null) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = purchaseDateMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    enabled = pickerState.selectedDateMillis != null,
                    onClick = {
                        onPurchaseDateChange(pickerState.selectedDateMillis)
                        showDatePicker = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Row {
                    if (purchaseDateMillis != null) {
                        TextButton(
                            onClick = {
                                onPurchaseDateChange(null)
                                showDatePicker = false
                            }
                        ) {
                            Text("Limpar")
                        }
                    }

                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancelar")
                    }
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun OpeningDebtField(
    openingDebt: TextFieldValue,
    onOpeningDebtChange: (String) -> Unit,
) {
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val accent = if (dark) Color(0xFFFFAA58) else Color(0xFF844300)
    val badge = if (dark) Color(0xFF33280E) else Color(0xFFFFE9BF)


    Column(
        modifier = Modifier.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountBalanceWallet,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = accent
            )

            Text(
                text = "SALDO DEVEDOR ATUAL",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = accent
            )

            Surface(
                shape = CircleShape,
                color = badge
            ) {
                Text(
                    text = "A Cobrar",
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = accent
                )
            }
        }

        Text(
            text = "Valor total pendente a receber deste cliente.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )


        EasyNoteBrazilianCurrencyTextField(
            text = openingDebt,
            onValueChange = onOpeningDebtChange,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}

@Composable
private fun HistoryFieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 0.4.sp
    )
}



// 4. Preview interativo da tela de acordo com as proporções do dispositivo
@Preview(showBackground = true, device = "spec:width=1080px,height=2340px,dpi=440")
@Composable
fun AddNewClientScreenPreview() {
    MaterialTheme {
        AddNewCustomerScreen()
    }
}