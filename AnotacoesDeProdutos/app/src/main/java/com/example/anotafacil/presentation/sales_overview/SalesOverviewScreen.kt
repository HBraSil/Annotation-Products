package com.example.anotafacil.presentation.sales_overview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.anotafacil.presentation.components.SalesChart
import com.patrykandpatrick.vico.compose.cartesian.axis.BaseAxis

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesOverviewScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sales Overview") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back navigation */ }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Ícone de voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
                .fillMaxSize(),
        ) {
            SalesChart(
                sales = listOf(
                    850.0,
                    1100.0,
                    950.0,
                    1400.0,
                    1750.0,
                    1500.0,
                    1900.0,
                )
            )
        }
    }
}


@Preview
@Composable
fun SalesOverviewScreenPreview() {
    SalesOverviewScreen()
}