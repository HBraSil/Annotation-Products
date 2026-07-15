package com.example.anotacoesdeprodutos.presentation.customers

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import com.example.anotacoesdeprodutos.Screens
import com.example.anotacoesdeprodutos.domain.model.City
import com.example.anotacoesdeprodutos.presentation.LastScreenViewModel
import com.example.anotacoesdeprodutos.presentation.components.AnnotationProductsSearchBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.anotacoesdeprodutos.presentation.components.AnnotationProductsConfirmationDialog
import com.example.anotacoesdeprodutos.presentation.components.AnnotationProductsNothingToShow


@Composable
fun CustomersScreen(
    customersViewModel: CustomersViewModel = hiltViewModel(),
    lastScreenViewModel: LastScreenViewModel,
    goToHomeScreen: () -> Unit = {},
    onBackClick: () -> Unit = {},
    goToCustomerDetailScreen: (Long) -> Unit = {},
) {

    val customerUiState by customersViewModel.customerUiState.collectAsState()

    LaunchedEffect(customerUiState.currentCity?.id) {
        if (customerUiState.currentCity?.id != null) {
            lastScreenViewModel.lastRoute("${Screens.CUSTOMERS.route}/${customerUiState.currentCity?.id}")
        }
    }

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
    goToCustomerDetailScreen: (Long) -> Unit = {},
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
                        text = "Total: ${customerUiState.customers.size} Clients",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                        fontWeight = FontWeight.W700
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onCustomerUiEvent(CustomersUiEvent.OnShowModalCreateCustomer) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp),
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Text("Adicionar Cliente")
            }
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

                // Campo de Busca
                item {
                    AnnotationProductsSearchBar(
                        text = customerUiState.searchQuery,
                        placeholder = "Buscar por cliente",
                        onSearchQueryChange = {
                            onCustomerUiEvent(CustomersUiEvent.OnSearchQueryChange(it))
                        }
                    )
                }

                // Lista de Clientes (Cards)
                item {
                    if (customerUiState.customers.isEmpty()) {
                        Spacer(modifier = Modifier.height(40.dp))
                        AnnotationProductsNothingToShow(text = "Nenhum cliente encontrado",)
                    } else {
                        customerUiState.customers.forEach { customer ->
                            Card(
                                onClick = { goToCustomerDetailScreen(customer.id) },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Nome do Cliente
                                        Text(
                                            text = customer.name,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,

                                            )

                                        // Informação Extra (Se houver)
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

                                    Spacer(modifier = Modifier.weight(1f))

                                    IconButton(
                                        onClick = { onCustomerUiEvent(CustomersUiEvent.OnShowModalDeleteCustomer(customer.id)) },
                                        modifier = Modifier.size(44.dp)
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

        if (customerUiState.showModalDeleteCustomer >= 0) {
            AnnotationProductsConfirmationDialog(
                title = "Excluir Cliente?",
                onDismissRequest = { onCustomerUiEvent(CustomersUiEvent.OnDismissModalDeleteCustomer) },
                onConfirmClick = { onCustomerUiEvent(CustomersUiEvent.OnDeleteCustomerClick) },
                modifier = Modifier.fillMaxWidth()
            )
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