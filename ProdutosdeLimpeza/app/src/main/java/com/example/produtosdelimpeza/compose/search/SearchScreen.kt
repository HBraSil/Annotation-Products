package com.example.produtosdelimpeza.compose.search

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.produtosdelimpeza.compose.main.MainBottomNavigation
import com.example.produtosdelimpeza.viewmodels.SearchHistoryViewModel


@Composable
fun SearchScreen(
    navControler: NavHostController,
    searchScreenViewModel: SearchHistoryViewModel = viewModel(
        factory = SearchHistoryViewModel.Factory(LocalContext.current).factory
    ),
) {
    Scaffold(
        bottomBar = {
            MainBottomNavigation(navControler)
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = contentPadding.calculateBottomPadding())
                .background(MaterialTheme.colorScheme.onPrimary),
            verticalArrangement = Arrangement.Top
        ) {
            SearchBarContainer(searchScreenViewModel)

            Text("Testando")
            Text("Testando")
            Text("Testando")
            Text("Testando")

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarContainer(searchScreenViewModel: SearchHistoryViewModel) {
    var query by remember { mutableStateOf(("")) }
    var expanded by remember { mutableStateOf(false) }
    var viewModel = searchScreenViewModel

    val products =
        listOf("Sabão", "Desinfetante", "Amaciante", "Detergente", "Limpa Alumínio", "Kiboa")
    val keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(expanded) {
        if (!expanded) {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }

    SearchBar(
        inputField = {
            Row {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(20.dp), // Use a mesma forma da SearchBar
                    elevation = if (expanded) 4.dp else 4.dp, // Adicione a elevação aqui!
                ) {
                    TextField(
                        value = TextFieldValue(text = query, selection = TextRange(query.length)),
                        onValueChange = { query = it.text },
                        placeholder = {
                            Text(
                                text = "Busque por vendedor/produto",
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    expanded = true
                                }
                            },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        searchScreenViewModel.addSearchItem(query)
                                        expanded = false
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Send,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        leadingIcon = {
                            if (!expanded && query.isEmpty()) {
                                IconButton(onClick = {}) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else {
                                if (query.isNotEmpty()) {
                                    IconButton(onClick = { query = "" }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                } else {
                                    IconButton(onClick = { expanded = false }) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardVoice,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        },
                        // ... outras propriedades do TextField ...
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                searchScreenViewModel.addSearchItem(query)
                                expanded = false
                            }
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Transparent,
                            unfocusedContainerColor = Transparent,
                            focusedIndicatorColor = Transparent,
                            unfocusedIndicatorColor = Transparent,
                        )
                    )
                }
                if (expanded) {
                    ElevatedButton(
                        onClick = { expanded = false },
                        modifier = Modifier
                            .padding(start = 5.dp, end = 5.dp)
                            .height(25.dp)
                            .width(85.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = "Cancelar",
                            modifier = Modifier.align(Alignment.CenterVertically),
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        },
        shadowElevation = 1.dp,
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth().background(Transparent),
        colors = SearchBarDefaults.colors(
            dividerColor = Transparent // Remove o fundo do inputField
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        // Conteúdo dos resultados quando expandido
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(start = 8.dp, end = 8.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            products.forEach { product ->
                Card(
                    onClick = {},
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = product,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(start = 30.dp, top = 15.dp, bottom = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Buscas Recentes",
                modifier = Modifier,
                fontWeight = Bold,
                fontSize = 20.sp,
                color = Black
            )

            TextButton(
                onClick = {
                    viewModel.clearSearchHistory()
                },
                modifier = Modifier.padding(start = 40.dp, end = 10.dp),
            ) {
                Text(
                    text = "Limpar histórico de navegação",
                    fontWeight = Bold,
                    fontSize = 10.sp,
                    // color = MaterialTheme.colorScheme.onSecondary
                )
            }

        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            if (viewModel.history.isEmpty()) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Sem histórico de navegação",
                        style = MaterialTheme.typography.displayMedium,
                        fontSize = 20.sp,
                        color = Gray,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                }
            } else {
                viewModel.history.forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Button(
                            onClick = {
                                query = it
                                expanded = false // Fecha ao selecionar
                            },
                            modifier = Modifier
                                .weight(1f) // <-- O Button vai expandir para ocupar todo o espaço
                                .padding(start = 8.dp, end = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Transparent,
                                contentColor = Black
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            // Conteúdo do botão em uma Row para alinhamento
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.Start)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.History,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text(
                                    text = it,
                                    color = Black
                                )
                            }
                        }

                        IconButton(onClick = {
                            query = it
                            keyboardController?.show()
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowOutward,
                                contentDescription = "Seta para a esquerda (rotacionada)",
                                // Aplica a rotação de 180 graus no ícone
                                modifier = Modifier.rotate(275f)
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
private fun SearchScreenPreview() {
    SearchScreen(navControler = NavHostController(LocalContext.current))
}