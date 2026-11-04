package com.example.anotafacil.presentation.sales_overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.anotafacil.domain.formatter.brazilianCurrencyFormatter
import com.example.anotafacil.domain.model.MonthlySalesData
import com.example.anotafacil.ui.components.EasyNotesExposedDropDown
import com.example.anotafacil.ui.components.SalesChart
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

enum class SalesMetric(val label: String) {
    TOTAL_SALES("Vendas"),
    TOTAL_RECEIVED("Recebimentos"),
    TOTAL_PRODUCTS_SOLD("Produtos")
}

data class MonthSales(
    val month: String,
    val revenue: Double,
    val received: Double,
    val quantity: Int
)

@Composable
fun SalesOverviewScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    salesOverviewViewModel: SalesOverviewViewModel = hiltViewModel(),
) {
    val uiState by salesOverviewViewModel.uiState.collectAsStateWithLifecycle()
    var selectedMetric by rememberSaveable { mutableStateOf(SalesMetric.TOTAL_RECEIVED) }

    val cities = remember(uiState.availableCities) {
        listOf("Todas") + uiState.availableCities.map { it.name }
    }

    val months = remember(uiState.monthlySales) {
        buildLastSixMonths(uiState.monthlySales)
    }

    SalesOverviewContent(
        selectedCity = uiState.selectedCity,
        selectedMetric = selectedMetric,
        cities = cities,
        months = months,
        isLoading = uiState.isLoading,
        onCitySelected = salesOverviewViewModel::onCitySelected,
        onMetricSelected = { selectedMetric = it },
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

private fun buildLastSixMonths(monthlySales: List<MonthlySalesData>): List<MonthSales> {
    val dataByMonth = monthlySales.associateBy { it.month }
    val currentMonth = YearMonth.now()

    return (0..5).map { offset ->
        val month = currentMonth.minusMonths((5 - offset).toLong())
        val key = month.toString()
        val summary = dataByMonth[key]

        MonthSales(
            month = month.month.getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
                .replaceFirstChar { it.titlecase(Locale("pt", "BR")) },
            revenue = summary?.totalSales ?: 0.0,
            received = summary?.totalReceived ?: 0.0,
            quantity = summary?.totalProducts ?: 0,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SalesOverviewContent(
    selectedCity: String,
    selectedMetric: SalesMetric,
    cities: List<String>,
    months: List<MonthSales>,
    isLoading: Boolean,
    onCitySelected: (String) -> Unit,
    onMetricSelected: (SalesMetric) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Visão de desempenho") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Voltar",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.onPrimary
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(26.dp)
        ) {
            item {
                CityFilterSection(
                    cities = cities,
                    selectedCity = selectedCity,
                    onCitySelected = onCitySelected
                )
            }

            item {
                MetricSelectorSection(
                    selectedMetric = selectedMetric,
                    onMetricSelected = onMetricSelected
                )
            }

            item {
                SummaryCard(
                    selectedMetric = selectedMetric,
                    months = months,
                )
            }

            item {
                SalesChart(
                    sales = when (selectedMetric) {
                        SalesMetric.TOTAL_RECEIVED -> months.map { it.received }
                        SalesMetric.TOTAL_PRODUCTS_SOLD -> months.map { it.quantity.toDouble() }
                        else -> months.map { it.revenue }
                    },
                    labels = months.map { it.month },
                    valueFormatter = { value ->
                        when (selectedMetric) {
                            SalesMetric.TOTAL_PRODUCTS_SOLD -> "${value.toInt()}"
                            else -> brazilianCurrencyFormatter.format(value)
                        }
                    },
                    axisFormatter = { value ->
                        when (selectedMetric) {
                            SalesMetric.TOTAL_PRODUCTS_SOLD -> "${value.toInt()}"
                            else -> "R$ ${value.toInt()}"
                        }
                    },
                )
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CityFilterSection(
    cities: List<String>,
    selectedCity: String,
    onCitySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Filtro de cidade",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        EasyNotesExposedDropDown(
            items = cities,
            itemText = { it },
            selectedItem = selectedCity,
            leadingIcon = Icons.Default.LocationCity,
            containerColor = MaterialTheme.colorScheme.background,
            onProductSelected = onCitySelected,
            trailingContent = {}
        )
    }
}

@Composable
private fun MetricSelectorSection(
    selectedMetric: SalesMetric,
    onMetricSelected: (SalesMetric) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Visualizar por:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SalesMetric.entries.forEachIndexed { index, metric ->
                SegmentedButton(
                    selected = selectedMetric == metric,
                    onClick = { onMetricSelected(metric) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = SalesMetric.entries.size
                    )
                ) {
                    Text(
                        text = metric.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = when (selectedMetric) {
                SalesMetric.TOTAL_SALES -> "Soma do valor de todas as vendas realizadas no período."
                SalesMetric.TOTAL_RECEIVED -> "Total em dinheiro recebido no período."
                SalesMetric.TOTAL_PRODUCTS_SOLD -> "Total de produtos vendidos no período."
            },
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
        )
    }
}

@Composable
private fun SummaryCard(
    selectedMetric: SalesMetric,
    months: List<MonthSales>,
) {
    val total = when (selectedMetric) {
        SalesMetric.TOTAL_SALES -> months.sumOf { it.revenue }
        SalesMetric.TOTAL_RECEIVED -> months.sumOf { it.received }
        SalesMetric.TOTAL_PRODUCTS_SOLD -> months.sumOf { it.quantity.toDouble() }
    }

    val average = when (selectedMetric) {
        SalesMetric.TOTAL_SALES -> months.map { it.revenue }.average()
        SalesMetric.TOTAL_RECEIVED -> months.map { it.received }.average()
        SalesMetric.TOTAL_PRODUCTS_SOLD -> months.map { it.quantity.toDouble() }.average()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ÚLTIMOS 6 MESES",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = when (selectedMetric) {
                SalesMetric.TOTAL_RECEIVED -> brazilianCurrencyFormatter.format(total)
                SalesMetric.TOTAL_PRODUCTS_SOLD -> "${total.toInt()} produtos"
                else -> brazilianCurrencyFormatter.format(total)
            },
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.background,
                    RoundedCornerShape(26.dp)
                )
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Média mensal: ",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = when (selectedMetric) {
                    SalesMetric.TOTAL_RECEIVED -> brazilianCurrencyFormatter.format(average)
                    SalesMetric.TOTAL_PRODUCTS_SOLD -> "${average.toInt()} produtos"
                    else -> brazilianCurrencyFormatter.format(average)
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview
@Composable
fun SalesOverviewScreenPreview() {
    SalesOverviewContent(
        selectedCity = "Todas",
        selectedMetric = SalesMetric.TOTAL_RECEIVED,
        cities = listOf("Todas", "São Paulo", "Rio"),
        months = listOf(
            MonthSales("Jan", 3000.0, 2400.0, 40),
            MonthSales("Fev", 3500.0, 2800.0, 55),
            MonthSales("Mar", 4200.0, 3100.0, 63),
            MonthSales("Abr", 3900.0, 2900.0, 58),
            MonthSales("Mai", 4700.0, 3600.0, 72),
            MonthSales("Jun", 5100.0, 4200.0, 81),
        ),
        isLoading = false,
        onCitySelected = {},
        onMetricSelected = {},
        onBackClick = {},
    )
}

