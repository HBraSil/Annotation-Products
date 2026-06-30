package com.example.anotacoesdeprodutos.presentation.customers

import android.util.Log.d
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.anotacoesdeprodutos.domain.model.Customer
import com.example.anotacoesdeprodutos.presentation.LastScreenViewModel
import com.example.anotacoesdeprodutos.presentation.add_customer.AddNewCustomerScreen
import com.example.anotacoesdeprodutos.presentation.components.AnnotationProductsSearchBar


@Composable
fun CustomersScreen(
    customersViewModel: CustomersViewModel = hiltViewModel(),
    lastScreenViewModel: LastScreenViewModel,
    goToHomeScreen: () -> Unit = {},
    onBackClick: () -> Unit = {},
    goToCustomerDetailScreen: (Long) -> Unit = {},
) {

    var searchQuery by remember { mutableStateOf("") }

    val customers by customersViewModel.customerList.collectAsState()
    val currentCity by customersViewModel.currentCity.collectAsState()
    val lastScreen by lastScreenViewModel.lastActiveProfile.collectAsState()
    val customerUiState by customersViewModel.customerUiState.collectAsState()

    LaunchedEffect(currentCity?.id) {
        if (currentCity?.id != null) {
            lastScreenViewModel.lastRoute("${Screens.CUSTOMERS.route}/${currentCity?.id}")
            d("CustomersScreen", "CustomersScreen: $lastScreen")
        }
    }

    BackHandler {
        goToHomeScreen()
    }


    ClientManagementContent(
        customersList = customers,
        currentCity = currentCity ?: City(),
        customerUiState = customerUiState,
        onSearchQueryChange = { searchQuery = it },
        onBackClick = onBackClick,
        onNameChange = customersViewModel::updateName,
        onExtraInfoChange = customersViewModel::updateExtraInfo,
        onDeleteClientClick = {},
        onCreateClientClick = { customersViewModel.saveCustomer() },
        goToCustomerDetailScreen = goToCustomerDetailScreen,
        openModalCreateCustomer = customersViewModel::openModal,
        onDismissOverlayCreatedCustomer = customersViewModel::closeModalAndOverlayCreatedCustomer,
        onCloseModal = customersViewModel::closeModalAndOverlayCreatedCustomer
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientManagementContent(
    modifier: Modifier = Modifier,
    customersList: List<Customer> = emptyList(),
    currentCity: City = City(),
    customerUiState: CustomersUiState = CustomersUiState(),
    onSearchQueryChange: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onExtraInfoChange: (String) -> Unit = {},
    onDeleteClientClick: (Customer) -> Unit = {},
    onCreateClientClick: () -> Unit = {},
    goToCustomerDetailScreen: (Long) -> Unit = {},
    openModalCreateCustomer: () -> Unit = {},
    onDismissOverlayCreatedCustomer: () -> Unit = {},
    onCloseModal: () -> Unit = {},
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentCity.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    Text(
                        text = "Total: ${currentCity.customerCount} Clients",
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
                onClick = openModalCreateCustomer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp),
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Text("Adicionar Cliente")
            }
        },
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White
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
                    text = "",
                    placeholder = "Find clients by name...",
                    onSearchQueryChange = onSearchQueryChange
                )
            }

            // Lista de Clientes (Cards)
            items(customersList, key = { it.id }) { customer ->
                Card(
                    onClick = { goToCustomerDetailScreen(customer.id) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row (
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
                            if (customer.extraInfo != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "informações extras: ",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                                    )

                                    Text(
                                        text = customer.extraInfo,
                                        fontSize = 14.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.W500
                                    )
                                }
                            }

                            // Última Compra
                            customer.lastPurchaseDate?.let {
                                Text(
                                    text = "Última compra: $it",
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(
                            onClick = { onDeleteClientClick(customer) },
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

            // Espaçador inferior para a rolagem não ficar colada ao FAB
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        if (customerUiState.showModalCreateCustomer) {
            AddNewCustomerScreen(
                uiState = customerUiState,
                onCreateClientClick = onCreateClientClick,
                onNameChange = onNameChange,
                onExtraInfoChange = onExtraInfoChange,
                onDismissOverlayCreatedCustomer = onDismissOverlayCreatedCustomer,
                onCloseModal = onCloseModal,
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