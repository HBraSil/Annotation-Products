package com.example.anotacoesdeprodutos.presentation.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anotacoesdeprodutos.domain.model.City
import com.example.anotacoesdeprodutos.presentation.LastScreenViewModel
import com.example.anotacoesdeprodutos.presentation.components.AnnotationProductsSearchBar
import com.example.anotacoesdeprodutos.presentation.components.SuccessDialog


@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    lastScreenViewModel: LastScreenViewModel = hiltViewModel(),
    onSearchChange: (String) -> Unit = {},
    onUpdatePricesClick: () -> Unit = {},
    onCityClick: (City) -> Unit = {},
) {

    val homeUiState by homeViewModel.uiState.collectAsState()
    val lastScreen by lastScreenViewModel.lastActiveProfile.collectAsState()

    LaunchedEffect(lastScreen) {

        Log.d("HomeScreen", "actualRoute: $lastScreen")
    }


    HomeContent(
        state = homeUiState,
        onSearchChange = onSearchChange,
        onUpdatePricesClick = onUpdatePricesClick,
        onCityClick = onCityClick,
        addCity = homeViewModel::addCity,
        onDismiss = homeViewModel::dismissDialog
    )
}


@Composable
fun HomeContent(
    state: HomeState,
    onSearchChange: (String) -> Unit,
    onUpdatePricesClick: () -> Unit,
    onCityClick: (City) -> Unit,
    addCity: (City) -> Unit,
    onDismiss: () -> Unit,
) {
    var showAddCityModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Header(
            onUpdatePricesClick = onUpdatePricesClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        AnnotationProductsSearchBar(
            text = "state.searchQuery",
            placeholder = "Pesquisar cidade ou cliente...",
            onSearchQueryChange = onSearchChange
        )

        Spacer(modifier = Modifier.height(30.dp))

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
                text = "${state.cities.size} resultados",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(state.cities) { city ->
                CityCard(
                    city = city,
                    onClick = { onCityClick(city) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(22.dp))
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ){
                    OutlinedButton(
                        onClick = { showAddCityModal = true },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.size(8.dp))

                        Text("Adicionar Cidade")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))
    }

    if (showAddCityModal) {
        ModalAddCityScreen(
            onBackClick = { showAddCityModal = false },
            onSaveClick = addCity
        )
    }

    if (state.success) SuccessDialog(
        text = "Cidade adicionada com sucesso!",
        onDismiss = onDismiss
    )
}

// =====================================================
// HEADER
// =====================================================

@Composable
private fun Header(
    onUpdatePricesClick: () -> Unit,
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "Tela Inicial",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        TextButton(onClick = onUpdatePricesClick) {
            Text(
                text = "Atualizar preços",
                color = Color(0xFF007AFF),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Composable
private fun CityCard(
    city: City,
    onClick: () -> Unit,
) {

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
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

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = city.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Nenhuma venda recente",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

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

// =====================================================
// PREVIEW
// =====================================================

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun HomeScreenPreview() {

    MaterialTheme {
        HomeContent(
            state = HomeState(),
            onSearchChange = {},
            onUpdatePricesClick = {},
            onCityClick = {},
            addCity = {},
            onDismiss = {}
        )
    }
}