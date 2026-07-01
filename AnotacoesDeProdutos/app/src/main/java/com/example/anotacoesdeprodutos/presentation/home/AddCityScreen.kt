package com.example.anotacoesdeprodutos.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.anotacoesdeprodutos.domain.model.City

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalAddCityScreen(onBackClick: () -> Unit, onSaveClick: (City) -> Unit) {

    var cityName by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onBackClick,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = null,
        modifier = Modifier.statusBarsPadding()
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Add City",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 48.dp)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBackClick,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Voltar",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.onPrimary,
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(0.3f))

                // 1. Ícone do Prédio/Cidade Centralizado
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(color = Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Substitua pelo seu R.drawable.ic_building se tiver um SVG próprio
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Cidade",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 2. Textos Centrais
                Text(
                    text = "LOCATION ENTRY",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Where next?",
                    color = Color.Black,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(48.dp))

                TextField(
                    value = cityName,
                    onValueChange = { cityName = it },
                    placeholder = { Text("Nome da cidade") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = MaterialTheme.shapes.medium),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = Color.Gray,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                    )
                )

                Spacer(modifier = Modifier.weight(0.2f))

                // 3. Botão de Salvar (Substituindo o antigo "+")
                ElevatedButton(
                    onClick = {
                        onSaveClick(City(id = 0, name = cityName, lastSale = null))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Usando o ícone clássico de "Check" dentro de um box/disquete simulado
                        // Para ficar idêntico à imagem 16216262004334515125.jpeg, o ideal é usar Icons.Default.CheckCircle ou um SVG customizado
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Salvar nome",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )

                        Text(
                            text = "Confirmar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(0.4f))
            }
        }
    }
}

@Preview
@Composable
fun ModalAddCityScreenPreview() {
    ModalAddCityScreen(onBackClick = {}, onSaveClick = {})
}