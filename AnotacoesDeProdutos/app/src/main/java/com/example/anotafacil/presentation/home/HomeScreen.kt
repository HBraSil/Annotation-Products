package com.example.anotafacil.presentation.home

import android.util.Log
import android.widget.Button
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.InsertChart
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PriceChange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anotafacil.Screens
import com.example.anotafacil.domain.model.City
import com.example.anotafacil.presentation.components.AnnotationProductsNothingToShow
import com.example.anotafacil.presentation.components.AnnotationProductsSearchBar
import com.example.anotafacil.presentation.components.AnnotationProductsSuccessDialog


@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onCityClick: (City) -> Unit,
    onFabClick: (String) -> Unit = {},
) {
    val homeUiState by homeViewModel.uiState.collectAsState()

    HomeContent(
        homeUiState = homeUiState,
        onSearchChange = homeViewModel::updateSearchQuery,
        addCity = homeViewModel::addCity,
        onCityClick = onCityClick,
        onFabClick = onFabClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    homeUiState: HomeState,
    onSearchChange: (String) -> Unit,
    addCity: (String) -> Unit,
    onCityClick: (City) -> Unit,
    onFabClick: (String) -> Unit,
) {
    var showAddCityModalBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tela Inicial",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            Column(
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp),
            ) {
                MultiFloatingButtons {
                    if (it != null) onFabClick(it.route)
                    else showAddCityModalBottomSheet = true
                }

            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .background(MaterialTheme.colorScheme.background)
                .padding(it)
                .padding(horizontal = 20.dp)
        ) {
            AnnotationProductsSearchBar(
                text = homeUiState.searchQuery,
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
                            text = "${homeUiState.cities.size} resultados",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    if (homeUiState.cities.isEmpty()) {
                        AnnotationProductsNothingToShow(
                            text = "Nenhum cidade encontrada",
                            modifier = Modifier.padding(vertical = 20.dp)
                        )
                    } else {
                        homeUiState.cities.forEach { city ->
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
    }

    if (showAddCityModalBottomSheet) {
        ModalAddCityScreen(
            onBackClick = { showAddCityModalBottomSheet = false },
            onSaveClick = addCity,
        )
    }

    if (homeUiState.success) AnnotationProductsSuccessDialog(
        text = "Cidade adicionada com sucesso!",
        onDismiss = { showAddCityModalBottomSheet = false }
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


@Composable
fun MultiFloatingButtons(onClick: (Screens?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val minFabList = listOf(
        MinFabItem(Icons.Outlined.PriceChange, "Atualizar Preços", Screens.PRICE_DEFINITION),
        MinFabItem(Icons.Outlined.InsertChart, "Ver Relatório", Screens.SALES_OVERVIEW),
        MinFabItem(Icons.Outlined.LocationCity, "Adicionar Cidade")
    )

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }) + expandVertically(),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }) + shrinkVertically(),
        ) {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                minFabList.forEach { item ->
                    MinFab(item) {
                        onClick(item.route)
                    }
                }
            }
        }


        val transition = updateTransition(targetState = expanded, label = "transition")
        val rotate by transition.animateFloat(label = "rotate") {
            if (it) 90f else 0f
        }

        ElevatedCard(
            onClick = { expanded = !expanded },
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.MoreVert,
                contentDescription = null,
                modifier = Modifier.rotate(rotate).padding(18.dp)
            )
        }
    }
}



@Composable
fun MinFab(item: MinFabItem, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(10.dp))
                .padding(4.dp),
        ) {
            Text(
                text = item.name, modifier = Modifier.wrapContentWidth(),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
        }


        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier
                .padding(start = 10.dp, bottom = 7.dp)
                .size(40.dp),
            containerColor = MaterialTheme.colorScheme.onBackground
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.name,
                tint = MaterialTheme.colorScheme.onPrimary
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
            homeUiState = HomeState(),
            onSearchChange = {},
            onCityClick = {},
            addCity = {},
            onFabClick = {}
        )
    }
}