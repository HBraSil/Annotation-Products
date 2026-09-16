package com.example.anotafacil.presentation.customers

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
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
import com.example.anotafacil.domain.model.City
import com.example.anotafacil.ui.components.AnnotationProductsSearchBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.example.anotafacil.ui.components.AnnotationProductsConfirmationDialog
import com.example.anotafacil.ui.components.AnnotationProductsNothingToShow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import com.example.anotafacil.domain.model.Customer
import com.example.anotafacil.ui.components.AnnotationProductsFab
import com.example.anotafacil.ui.util.currencyFormatter
import kotlin.uuid.Uuid


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomersScreen(
    customersViewModel: CustomersViewModel = hiltViewModel(),
    goToHomeScreen: () -> Unit = {},
    onBackClick: () -> Unit = {},
    goToCustomerDetailScreen: (Uuid?) -> Unit = {},
) {

    val customerUiState by customersViewModel.customerUiState.collectAsState()

    BackHandler {
        goToHomeScreen()
    }

    ClientManagementContent(
        currentCity = customerUiState.currentCity ?: City(),
        customerUiState = customerUiState,
        onBackClick = onBackClick,
        goToCustomerDetailScreen = goToCustomerDetailScreen,
        onCustomerUiEvent = customersViewModel::customersEvent

    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientManagementContent(
    customerUiState: CustomersUiState = CustomersUiState(),
    currentCity: City = City(),
    onBackClick: () -> Unit = {},
    goToCustomerDetailScreen: (Uuid?) -> Unit = {},
    onCustomerUiEvent: (CustomersUiEvent) -> Unit = {}
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentCity.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    Text(
                        text = "Clientes: ${customerUiState.customers.size}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                        fontWeight = FontWeight.W700
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        floatingActionButton = {
            AnnotationProductsFab(
                onClick = { onCustomerUiEvent(CustomersUiEvent.OnShowModalCreateCustomer) },
                text = "Adicionar Cliente",
                icon = Icons.Default.Add
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ServerStatus()
            }


            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    MetricCard(
                        title = "Produtos Vendidos",
                        value = customerUiState.metric.totalProducts.toString(),
                        icon = Icons.Default.ShoppingCart,
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = "Total Vendido",
                        value = currencyFormatter.format(customerUiState.metric.totalAmount),
                        icon = Icons.Default.AttachMoney,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                AnnotationProductsSearchBar(
                    text = customerUiState.searchQuery,
                    placeholder = "Buscar por cliente",
                    onSearchQueryChange = {
                        onCustomerUiEvent(CustomersUiEvent.OnSearchQueryChange(it))
                    }
                )
            }

            item {
                if (customerUiState.customers.isEmpty()) {
                    Spacer(modifier = Modifier.height(40.dp))
                    AnnotationProductsNothingToShow(text = "Nenhum cliente encontrado",)
                } else {
                    customerUiState.customers.forEach { customer ->
                        CardCustomers(
                            customer = customer,
                            onCustomerUiEvent = {
                                CustomersUiEvent.OnShowModalDeleteCustomer(customer.id)
                            },
                            goToCustomerDetailScreen = {
                                goToCustomerDetailScreen(customer.id)
                            }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        if (customerUiState.showModalCreateCustomer) {
            AddNewCustomerScreen(
                uiState = customerUiState,
                onDismissOverlayCreatedCustomer = { onCustomerUiEvent(CustomersUiEvent.OnDismissOverlayCreatedCustomer) },
                onNameChange = { onCustomerUiEvent(CustomersUiEvent.OnNameChange(it)) },
                onExtraInfoChange = { onCustomerUiEvent(CustomersUiEvent.OnExtraInfoChange(it)) },
                onCreateClientClick = { onCustomerUiEvent(CustomersUiEvent.OnCreateCustomerClick) },
                onCloseModal = { onCustomerUiEvent(CustomersUiEvent.OnDismissOverlayCreatedCustomer) },
            )
        }

        if (customerUiState.showModalDeleteCustomer != null) {
            AnnotationProductsConfirmationDialog(
                title = "Excluir Cliente?",
                onDismissRequest = { onCustomerUiEvent(CustomersUiEvent.OnDismissModalDeleteCustomer) },
                onConfirmClick = { onCustomerUiEvent(CustomersUiEvent.OnDeleteCustomerClick) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        customerUiState.errorMessage?.let {
            Toast.makeText(
                LocalContext.current,
                it,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

@Composable
fun ServerStatus(
    modifier: Modifier = Modifier,
    onRefreshClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth()
            .clip(
                RoundedCornerShape(22.dp)

            ).background(
            brush = Brush.linearGradient(
                listOf(
                    MaterialTheme.colorScheme.onBackground,
                    MaterialTheme.colorScheme.secondary.copy(0.6f)
                ),
                start = Offset(30f, 0f),
                //end = Offset(100f, 100f)
            ),
        ),

        color = Color.Transparent,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primaryContainer
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 16.dp,
                    end = 14.dp
                )
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Indicador de conexão
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        Color(0xFF00C98B)
                    )
            )

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            // Textos
            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Atualiza Dados",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )

                Text(
                    text = "Atualizado há instantes",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            // Botão atualizar
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .clickable (
                        onClick = onRefreshClick
                    ),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFF514C59)
                ),
                tonalElevation = 4.dp,
                shadowElevation = 4.dp
            ) {

                Row(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Atualizar",
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "Atualizar",
                        color = MaterialTheme.colorScheme.primaryContainer,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}


@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
) {
    Card(
        modifier = modifier.padding(10.dp)

            .height(116.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Row (
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Normal,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


@Composable
fun CardCustomers(
    customer: Customer,
    onCustomerUiEvent: () -> Unit,
    goToCustomerDetailScreen: () -> Unit
) {
    Card(
        onClick = goToCustomerDetailScreen,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
        ) {
            Column(
                modifier = Modifier.weight(4f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = customer.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primaryContainer
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "info: ",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            0.5f
                        )
                    )

                    Text(
                        text = customer.extraInfo
                            ?: "nenhuma informação extra",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.W500
                    )
                }

                // Última Compra
                customer.lastPurchaseDate?.let {
                    Text(
                        text = "Última compra: $it",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            0.5f
                        )
                    )
                }
            }

            IconButton(
                onClick = onCustomerUiEvent,
                modifier = Modifier.size(44.dp).weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Excluir",
                    tint = Color.LightGray
                )
            }
        }
    }
}

// 4. Preview da Tela
@Preview(showBackground = true, device = "spec:width=1080px,height=2340px,dpi=440")
@Composable
fun CustomersScreenPreview() {
    MaterialTheme {
        ClientManagementContent()
    }
}