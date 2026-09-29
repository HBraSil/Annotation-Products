package com.example.anotafacil.presentation.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.anotafacil.domain.model.City
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.ui.components.AnnotationProductsConfirmationDialog
import com.example.anotafacil.ui.components.AnnotationProductsNothingToShow
import com.example.anotafacil.ui.components.AnnotationProductsSearchBar
import com.example.anotafacil.ui.components.AnnotationProductsSuccessDialog
import com.hilquias.anotafacil.R


@Composable
fun OwnerHomeScreen(
    innerPadding: PaddingValues = PaddingValues(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    onCityClick: (City) -> Unit,
) {
    val homeUiState by homeViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.loadOwnerUser()
    }

    HomeContent(
        homeState = homeUiState,
        innerPadding = innerPadding,
        isOwner = true,
        onSearchChange = homeViewModel::updateSearchQuery,
        addCity = homeViewModel::addCity,
        onCityClick = onCityClick,
        closeSuccessDialog = homeViewModel::closeSuccessDialog,
        refreshHome = homeViewModel::refreshHome,
    )
}


@Composable
fun SellerHomeScreen(
    innerPadding: PaddingValues = PaddingValues(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    onCityClick: (City) -> Unit,
    onSignOutSellerClick: () -> Unit,
    goToAccountProfile: () -> Unit,
    goToRoleSection: () -> Unit
) {
    val homeUiState by homeViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showSuccessDialog by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(homeUiState.isSellerDisconnected) {
        homeUiState.isSellerDisconnected?.let {
            showSuccessDialog = it
        }
    }

    LaunchedEffect(Unit) {
        homeViewModel.loadSellerUser()
    }

    LaunchedEffect(homeUiState.message) {
        homeUiState.message?.let {
            Toast.makeText(
                context,
                it,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        floatingActionButton = {
            ElevatedButton(
                onClick = { homeViewModel.syncCloudClick() },
                modifier = Modifier
                    .width(150.dp)
                    .height(60.dp)
                    .padding(bottom = 20.dp),
                enabled = !homeUiState.isUploading,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                if (homeUiState.isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp)
                    )
                    return@ElevatedButton
                }

                Text(text = "Enviar dados")
            }
        },
    ) { paddingValues ->
        HomeContent(
            modifier = Modifier.padding(paddingValues),
            homeState = homeUiState,
            onSearchChange = homeViewModel::updateSearchQuery,
            addCity = homeViewModel::addCity,
            onCityClick = onCityClick,
            closeSuccessDialog = homeViewModel::closeSuccessDialog,
            refreshHome = homeViewModel::refreshHome,
            onSignOutSellerClick = {
                onSignOutSellerClick()
                homeViewModel.signOutSeller()
            },
            goToAccountProfile = goToAccountProfile,
            onDisconnectSellerClick = goToRoleSection
        )

        showSuccessDialog?.let {
            AnnotationProductsSuccessDialog(
                text = it,
                confirmButtonText = "Sair",
                icon = Icons.Default.Close,
                iconColor = MaterialTheme.colorScheme.error,
                containerIconColor = MaterialTheme.colorScheme.errorContainer,
                confirmButtonTextColor = MaterialTheme.colorScheme.error.copy(0.8f),
                confirmButtonContainerColor = MaterialTheme.colorScheme.onPrimary,
                confirmClick = {
                    goToRoleSection()
                    showSuccessDialog = null
                },
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    homeState: HomeState,
    innerPadding: PaddingValues = PaddingValues(),
    isOwner: Boolean = false,
    onSearchChange: (String) -> Unit = {},
    addCity: (String) -> Unit = {},
    onCityClick: (City) -> Unit = {},
    closeSuccessDialog: () -> Unit = {},
    refreshHome: () -> Unit = {},
    onSignOutSellerClick: () -> Unit = {},
    goToAccountProfile: () -> Unit = {},
    onDisconnectSellerClick: () -> Unit = {}
) {
    var showAddCityDialog by rememberSaveable { mutableStateOf(false) }
    var signOutSellerDialog by rememberSaveable { mutableStateOf(false) }
    var disconnectSellerDialog by rememberSaveable { mutableStateOf(false) }



    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        if (isOwner) {
            OwnerTopAppBar(
                uiState = homeState,
                modifier = Modifier.weight(1f),
                showAddCityDialog = { showAddCityDialog = true }
            )
        } else {
            SellerTopAppBar(
                uiState = homeState,
                onEditProfileClick = goToAccountProfile,
                onDisconnectSellerClick = { disconnectSellerDialog = true },
                onExitClick = { signOutSellerDialog = true }
            )
        }



        AnnotationProductsSearchBar(
            text = homeState.searchQuery,
            placeholder = "Pesquisar cidade",
            onSearchQueryChange = onSearchChange
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Cidades",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "${homeState.cities.size} resultados",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }


        PullToRefreshBox(
            isRefreshing = false,
            onRefresh = refreshHome,
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {

                    if (homeState.isSyncing) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            SyncAnimation()
                        }
                    } else {
                        if (homeState.cities.isEmpty()) {
                            AnnotationProductsNothingToShow(
                                text = "Nenhum cidade encontrada",
                                modifier = Modifier.padding(vertical = 20.dp)
                            )
                        } else {
                            homeState.cities.forEach { city ->
                                Log.d("HomeScreen", "${city.name} -> ${city.customerCount}")
                                CityCard(
                                    city = city,
                                    onClick = { onCityClick(city) },
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }


        if (signOutSellerDialog) {
            AnnotationProductsConfirmationDialog(
                title = "Tem certeza que deseja sair do app?",
                subtitle = null,
                onDismissRequest = { signOutSellerDialog = false },
                onConfirmClick = {
                    onSignOutSellerClick()
                    signOutSellerDialog = false
                }
            )
        }
        if (disconnectSellerDialog) {
            AnnotationProductsConfirmationDialog(
                title = "Tem certeza que deseja desconectar do vendedor?",
                subtitle = null,
                onDismissRequest = { signOutSellerDialog = false },
                onConfirmClick = {
                    onDisconnectSellerClick()
                    disconnectSellerDialog = false
                }
            )
        }
    }

    if (showAddCityDialog) {
        ModalAddCityScreen(
            onBackClick = { showAddCityDialog = false },
            onSaveClick = { cityName ->
                addCity(cityName)
            }
        )
    }

    if (homeState.success) {
        AnnotationProductsSuccessDialog(
            text = "Cidade adicionada com sucesso!",
            confirmClick = {
                showAddCityDialog = false
                closeSuccessDialog()
            },
            onDismiss = {
                showAddCityDialog = false
                closeSuccessDialog()
            },
        )
    }
}


@Composable
fun OwnerTopAppBar(
    uiState: HomeState,
    modifier: Modifier = Modifier,
    showAddCityDialog: () -> Unit
) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ){
        Text(
            text = uiState.ownerUser.name,
            maxLines = 1,
            modifier = modifier,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            overflow = TextOverflow.Ellipsis,
        )

        TextButton(
            onClick = showAddCityDialog,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Adicionar Cidade",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}



@Composable
fun SellerTopAppBar(
    uiState: HomeState = HomeState(),
    onEditProfileClick: () -> Unit,
    onDisconnectSellerClick: () -> Unit,
    onExitClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Raimundo",
                maxLines = 1,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "vendendo para: ",
                    maxLines = 1,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = uiState.ownerUser.name,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        ProfileOptionsMenu(
            onEditProfileClick = onEditProfileClick,
            onDisconnectSellerClick = onDisconnectSellerClick,
            onExitClick = onExitClick
        )
    }
}


@Composable
private fun CityCard(
    modifier: Modifier = Modifier,
    city: City,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.onPrimary)
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.background
            ) {

                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(14.dp))

            Text(
                text = city.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 6.dp
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "${city.customerCount}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "clientes",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}


@Composable
fun SyncAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading_data_home)
    )

    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = modifier.size(60.dp)
    )
}


@Composable
fun ProfileOptionsMenu(
    onEditProfileClick: () -> Unit,
    onDisconnectSellerClick: () -> Unit,
    onExitClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Mais opções"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.onPrimary
        ) {
            DropdownMenuItem(
                text = {
                    Text("Editar perfil")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )
                },
                onClick = {
                    expanded = false
                    onEditProfileClick()
                }
            )

            DropdownMenuItem(
                text = {
                    Text("Desconectar do vendedor")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LinkOff,
                        contentDescription = null
                    )
                },
                onClick = {
                    expanded = false
                    onDisconnectSellerClick()
                }
            )

            DropdownMenuItem(
                text = {
                    Text("Sair do app")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null
                    )
                },
                onClick = {
                    expanded = false
                    onExitClick()
                }
            )
        }
    }
}



@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeContent(
            homeState = HomeState(
                ownerUser = User(name = "João"),
            )
        )
    }
}