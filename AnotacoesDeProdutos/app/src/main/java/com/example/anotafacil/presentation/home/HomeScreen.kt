package com.example.anotafacil.presentation.home

import android.util.Log
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anotafacil.domain.model.City
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.ui.components.AnnotationProductsNothingToShow
import com.example.anotafacil.ui.components.AnnotationProductsSearchBar
import com.example.anotafacil.ui.components.AnnotationProductsSuccessDialog


@Composable
fun HomeScreen(
    innerPadding: PaddingValues = PaddingValues(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    onCityClick: (City) -> Unit,
) {
    val homeUiState by homeViewModel.uiState.collectAsState()

    HomeContent(
        homeState = homeUiState,
        innerPadding = innerPadding,
        onSearchChange = homeViewModel::updateSearchQuery,
        addCity = homeViewModel::addCity,
        onCityClick = onCityClick,
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    homeState: HomeState,
    innerPadding: PaddingValues = PaddingValues(),
    onSearchChange: (String) -> Unit = {},
    addCity: (String) -> Unit = {},
    onCityClick: (City) -> Unit = {},
) {
    var showAddCityDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = homeState.user.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextButton(
                onClick = { showAddCityDialog = true },
                modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterVertically)
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

        Spacer(modifier = Modifier.height(16.dp))

        AnnotationProductsSearchBar(
            text = homeState.searchQuery,
            placeholder = "Pesquisar cidade",
            onSearchQueryChange = onSearchChange
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
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
                        text = "${homeState.cities.size} resultados",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
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

    if (homeState.success) AnnotationProductsSuccessDialog(
        text = "Cidade adicionada com sucesso!",
        onDismiss = { showAddCityDialog = false }
    )
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


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeContent(
            homeState = HomeState(
                user = User(name = "João"),
            ),
            onSearchChange = {},
            addCity = {},
            onCityClick = {},
        )
    }
}