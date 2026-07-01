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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.example.anotacoesdeprodutos.presentation.components.AnnotationProductsNothingToShow


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
    val lastScreen by lastScreenViewModel.lastActiveProfile.collectAsState()
    val customerUiState by customersViewModel.customerUiState.collectAsState()

    LaunchedEffect(customerUiState.currentCity?.id) {
        if (customerUiState.currentCity?.id != null) {
            lastScreenViewModel.lastRoute("${Screens.CUSTOMERS.route}/${customerUiState.currentCity?.id}")
            d("CustomersScreen", "CustomersScreen: $lastScreen")
        }
    }

    BackHandler {
        goToHomeScreen()
    }



    ClientManagementContent(
        customersList = customers,
        currentCity = customerUiState.currentCity ?: City(),
        customerUiState = customerUiState,
        onSearchQueryChange = { searchQuery = it },
        onBackClick = onBackClick,
        onNameChange = customersViewModel::updateName,
        onExtraInfoChange = customersViewModel::updateExtraInfo,
        onCreateClientClick = customersViewModel::saveCustomer,
        goToCustomerDetailScreen = goToCustomerDetailScreen,
        showModalCreateCustomer = customersViewModel::showModalCreateCustomer,
        showModalDeleteCustomer = customersViewModel::showModalDeleteCustomer,
        onConfirmDeleteCustomer = customersViewModel::deleteCustomer,
        onDismissModalDeleteCustomer = customersViewModel::onDismissModalDeleteCustomer,
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
    onCreateClientClick: () -> Unit = {},
    onDismissModalDeleteCustomer: () -> Unit = {},
    goToCustomerDetailScreen: (Long) -> Unit = {},
    showModalCreateCustomer: () -> Unit = {},
    onConfirmDeleteCustomer: () -> Unit = {},
    showModalDeleteCustomer: (Long) -> Unit = {},
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
                onClick = showModalCreateCustomer,
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
        if (customersList.isEmpty()) {
            AnnotationProductsNothingToShow(text = "Nenhum cliente encontrado")
        } else {
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
                                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                                    )

                                    Text(
                                        text = customer.extraInfo ?: "nenhuma informação extra",
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
                                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            IconButton(
                                onClick = { showModalDeleteCustomer(customer.id) },
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

        if (customerUiState.showModalDeleteCustomer >= 0) {
            DeleteClientConfirmationDialog(
                onDismissRequest = onDismissModalDeleteCustomer,
                onConfirmClick = onConfirmDeleteCustomer,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
fun DeleteClientConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Cores alinhadas com a identidade visual e o contexto destrutivo
    val alertRed = Color(0xFFC62828)      // Vermelho para o ícone e ação de confirmar
    val actionBlue = Color(0xFF0033CC)    // Azul operacional para a ação neutra/cancelar
    val textPrimary = Color(0xFF111111)   // Título
    val textSecondary = Color(0xFF5F6368) // Corpo do texto
    val dialogBgColor = Color(0xFFFFFFFF)

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(28.dp),
            color = dialogBgColor,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 28.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Ícone de Lixeira (Outline)
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = alertRed,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Título: Excluir Cliente?
                Text(
                    text = "Excluir Cliente?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Texto de Apoio descritivo
                Text(
                    text = "Tem certeza disso? Esta ação não pode ser desfeita.",
                    fontSize = 14.sp,
                    color = textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Botão Confirmar (Ação empilhada verticalmente idêntica à imagem)
                TextButton(
                    onClick = onConfirmClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Confirmar",
                        color = alertRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botão Cancelar
                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cancelar",
                        color = actionBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DeleteClientConfirmationDialogPreview() {
    DeleteClientConfirmationDialog(
        onDismissRequest = {},
        onConfirmClick = {}
    )
}

// 4. Preview da Tela
@Preview(showBackground = true, device = "spec:width=1080px,height=2340px,dpi=440")
@Composable
fun CustomersScreenPreview() {
    MaterialTheme {
        ClientManagementContent()
    }
}