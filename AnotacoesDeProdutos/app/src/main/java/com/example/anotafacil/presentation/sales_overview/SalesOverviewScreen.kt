package com.example.anotafacil.presentation.sales_overview

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.LocationCity
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.anotafacil.presentation.components.EasyNotesExposedDropDown
import com.example.anotafacil.presentation.components.SalesChart
import com.example.anotafacil.presentation.formatter.currencyFormatter


enum class SalesMetric {
    RECEIVED,
    SALES_QUANTITY
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
    modifier: Modifier = Modifier
) {
    var selectedCity by remember {
        mutableStateOf("Todas")
    }

    var selectedMetric by remember {
        mutableStateOf(SalesMetric.RECEIVED)
    }

    val cities = listOf(
        "Todas",
        "São Paulo",
        "Rio de Janeiro",
        "Belo Horizonte",
        "Brasília"
    )

    val months = remember {
        listOf(
            MonthSales(
                month = "Fev",
                revenue = 5200.0,
                received = 4100.0,
                quantity = 83
            ),
            MonthSales(
                month = "Mar",
                revenue = 5800.0,
                received = 4600.0,
                quantity = 91
            ),
            MonthSales(
                month = "Abr",
                revenue = 6100.0,
                received = 4800.0,
                quantity = 97
            ),
            MonthSales(
                month = "Mai",
                revenue = 5700.0,
                received = 4400.0,
                quantity = 89
            ),
            MonthSales(
                month = "Jun",
                revenue = 6900.0,
                received = 5500.0,
                quantity = 108
            ),
            MonthSales(
                month = "Jul",
                revenue = 4800.0,
                received = 3900.0,
                quantity = 76
            ),
            MonthSales(
                month = "Ago",
                revenue = 8400.0,
                received = 6700.0,
                quantity = 126
            )
        )
    }

    SalesOverviewContent(
        selectedCity = selectedCity,
        selectedMetric = selectedMetric,
        cities = cities,
        months = months,
        onCitySelected = {
            selectedCity = it
        },
        onMetricSelected = {
            selectedMetric = it
        },
        onBackClick = onBackClick,
        modifier = modifier
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SalesOverviewContent(
    selectedCity: String,
    selectedMetric: SalesMetric,
    cities: List<String>,
    months: List<MonthSales>,
    onCitySelected: (String) -> Unit,
    onMetricSelected: (SalesMetric) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("Visão de desempenho")
                },
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
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
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
                    total = 1000.40,
                    months = months
                )
            }

            item {
                SalesChart(
                    sales = when (selectedMetric) {
                        SalesMetric.RECEIVED ->
                            months.map { it.received }

                        SalesMetric.SALES_QUANTITY ->
                            months.map { it.quantity.toDouble() }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CityFilterSection(
    cities: List<String>,
    selectedCity: String,
    onCitySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        Text(
            text = "Filtro de cidade",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Seletor de métrica",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            SalesMetric.entries.forEachIndexed { index, metric ->

                SegmentedButton(
                    selected = selectedMetric == metric,
                    onClick = {
                        onMetricSelected(metric)
                    },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = SalesMetric.entries.size
                    )
                ) {
                    Text(
                        text = when (metric) {
                            SalesMetric.RECEIVED ->
                                "Recebido"

                            SalesMetric.SALES_QUANTITY ->
                                "Qtd. produtos"
                        }
                    )
                }
            }
        }
    }
}



@Composable
private fun SummaryCard(
    selectedMetric: SalesMetric,
    total: Double,
    months: List<MonthSales>,
    modifier: Modifier = Modifier
) {
    val average = when (selectedMetric) {
        SalesMetric.RECEIVED ->
            months.map { it.received }.average()

        SalesMetric.SALES_QUANTITY ->
            months.map { it.quantity }.average()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ÚLTIMOS 7 MESES",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = when (selectedMetric) {
                SalesMetric.RECEIVED ->
                    currencyFormatter.format(total)

                SalesMetric.SALES_QUANTITY ->
                    "${total.toInt()} produtos"
            },
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

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
            Spacer(
                modifier = Modifier.width(4.dp)
            )
            Text(
                text = when (selectedMetric) {
                    SalesMetric.RECEIVED ->
                        currencyFormatter.format(average)

                    SalesMetric.SALES_QUANTITY ->
                        "${average.toInt()} produtos"
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
    SalesOverviewScreen(
        onBackClick = {}
    )
}