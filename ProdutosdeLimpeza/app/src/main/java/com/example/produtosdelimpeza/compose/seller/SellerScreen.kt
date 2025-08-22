package com.example.produtosdelimpeza.compose.seller

import androidx.compose.foundation.Image
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.produtosdelimpeza.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerScreen(nameSeller: String = "", onBackNavigation: () -> Unit = {}) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Aurubu", color = Black)},
                    navigationIcon = {
                        IconButton(onClick = { onBackNavigation() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = stringResource(R.string.icon_navigate_back),
                                tint = Black
                            )
                        }
                    }
                )
            }
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = contentPadding.calculateTopPadding())
            ) {
                Card(
                    onClick = {},
                    modifier = Modifier
                        .padding(top = 30.dp, start = 30.dp, end = 30.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Black
                    ),
                    elevation = CardDefaults.elevatedCardElevation(4.dp)
                ) {
                    Row {
                        Card(
                            onClick = {},
                            modifier = Modifier
                                .padding(top = 10.dp, start = 10.dp, bottom = 10.dp)
                                .size(100.dp),
                            shape = CircleShape,
                            elevation = CardDefaults.elevatedCardElevation(3.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = stringResource(R.string.icon_navigate_back),
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterVertically),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            Text(
                                text = nameSeller,
                                fontSize = 18.sp,
                                fontWeight = Bold,
                                color = White
                            )

                            Text(
                                text = "Tuntum-Ma",
                                fontSize = 13.sp,
                                color = White
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun SellerScreenPreview() {
    SellerScreen()
}