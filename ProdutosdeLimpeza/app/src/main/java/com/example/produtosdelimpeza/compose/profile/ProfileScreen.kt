package com.example.produtosdelimpeza.compose.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.ExtraBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.produtosdelimpeza.R
import com.example.produtosdelimpeza.compose.main.MainBottomNavigation
import com.example.produtosdelimpeza.ui.theme.BluishGreen


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(navController: NavHostController? = null) {
    val verticalScrollState = rememberScrollState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                MainBottomNavigation(navController!!)
            }
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScrollState)
                    .padding(
                        top = contentPadding.calculateTopPadding(),
                        bottom = contentPadding.calculateBottomPadding()
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Card (
                    onClick = {},
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .size(100.dp),
                    shape = CircleShape,
                    elevation = CardDefaults.elevatedCardElevation(3.dp)
                ){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(R.string.image_profile),
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                PersonalData()
                Configurations()
                GeneralInformation()
            }
        }



    }
}


@Composable
fun PersonalData() {
    var isFocused by remember { mutableStateOf(Pair<Int, Boolean>(0, false)) }
    var actualName by remember { mutableStateOf("Hilquias Brasil") }
    var actualPhone by remember { mutableStateOf("Tuntum-Ma") }
    var actualCity by remember { mutableStateOf("(99) 99225-9452") }
    var newName by remember { mutableStateOf(actualName) }
    var isNamechanged by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Dados pessoais",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        IconButton(
            onClick = {
                actualName = newName
                isNamechanged = false
            },
            enabled = isNamechanged,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 5.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = stringResource(R.string.edit_personal_data),
            )
        }
    }

    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Nome",
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 20.dp)
        )
        BasicTextField(
            value = newName,
            onValueChange = { newValue ->
                newName = newValue
                isNamechanged = newName != actualName
            },
            modifier = Modifier
                .padding(end = 20.dp)
                .onFocusChanged { focusState ->
                    isFocused = Pair(1, focusState.isFocused)
                    /*if (focusState.isFocused) {
                        isFocused = false
                    }*/
                },
            textStyle = if (isFocused.first == 1 && isFocused.second) TextStyle(
                fontSize = 16.sp,
                color = Black,
                textAlign = TextAlign.Center,
                fontWeight = Bold
            ) else TextStyle(
                fontSize = 14.sp,
                color = Gray,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
            )
        )
    }

    Divider(color = if (isFocused.first == 1 && isFocused.second) BluishGreen else Gray, thickness = 1.dp)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Cidade",
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 20.dp)
        )
        BasicTextField(
            value = actualCity,
            onValueChange = { newValue ->
                newName = newValue
                isNamechanged = newName != actualName
            },
            modifier = Modifier
                .padding(end = 20.dp)
                .onFocusChanged { focusState ->
                    isFocused = Pair(2, focusState.isFocused)
                    /*if (focusState.isFocused) {
                        isFocused. = false
                    }*/
                },
            textStyle = if (isFocused.first == 2 && isFocused.second) TextStyle(
                fontSize = 16.sp,
                color = Black,
                textAlign = TextAlign.Center,
                fontWeight = Bold
            ) else TextStyle(
                fontSize = 14.sp,
                color = Gray,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
            )
        )
    }

    Divider(color = if (isFocused.first == 2 && isFocused.second) BluishGreen else Gray, thickness = 1.dp)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Celular", modifier = Modifier.padding(start = 20.dp))
        BasicTextField(
            value = actualPhone,
            onValueChange = { newValue ->
                newName = newValue
                isNamechanged = newName != actualName
            },
            modifier = Modifier
                .padding(end = 20.dp)
                .onFocusChanged { focusState ->
                    isFocused = Pair(3, focusState.isFocused)
                    /*if (focusState.isFocused) {
                        isFocused = false
                    }*/
                },
            textStyle = if(isFocused.first == 3 && isFocused.second) TextStyle(
                fontSize = 16.sp,
                color = Black,
                textAlign = TextAlign.Center,
                fontWeight = Bold
            ) else {
                TextStyle(
                    fontSize = 14.sp,
                    color = Gray,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                )
            }
        )
    }

    Divider(color = if (isFocused.first == 3 && isFocused.second) BluishGreen else Gray, thickness = 1.dp)
}


@Composable
fun Configurations() {
    var checked by remember { mutableStateOf(false) }
    var isCustomer by remember { mutableStateOf(false) }

    Text(
        text = "Configurações",
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 50.dp),
        fontWeight = ExtraBold
    )
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(top = 10.dp)
            .fillMaxWidth()
            .clickable{},
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Ativar cupons")
        Icon(
            imageVector = Icons.AutoMirrored.Filled.NavigateNext,
            contentDescription = stringResource(R.string.icon_navigate_next),
        )
    }
    if (isCustomer) {
        Divider(color = Gray, thickness = 1.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Se torne um vendedor", modifier = Modifier.padding(start = 20.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                contentDescription = stringResource(R.string.icon_navigate_next),
                modifier = Modifier.padding(end = 20.dp)
            )
        }
    }
    Divider(color = Gray, thickness = 1.dp)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Ativar modo escuro",
            modifier = Modifier.padding(start = 20.dp),
            fontSize = 15.sp,
        )
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            modifier = Modifier.padding(end = 20.dp)
        )
    }
}


@Composable
fun GeneralInformation() {
    Text(
        text = "Informações Gerais",
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 50.dp),
        fontWeight = ExtraBold
    )

    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(top = 10.dp)
            .clickable {}
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Avalie o App")

        Spacer(Modifier.weight(1f))


            Icon(
                imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                contentDescription = stringResource(R.string.icon_navigate_next_to_feedback),
            )
    }

    Divider(color = Gray, thickness = 1.dp)

    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .clickable {}
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Sobre")

        Spacer(Modifier.weight(1f))


            Icon(
                imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                contentDescription = stringResource(R.string.icon_navigate_next),
            )
    }

    Divider(color = Gray, thickness = 1.dp)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Versão do app", modifier = Modifier.padding(start = 20.dp))
        Text(text = "1.0.0.1", modifier = Modifier.padding(end = 20.dp))
    }

    Spacer(modifier = Modifier.padding(bottom = 20.dp))
}


@Preview
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen()
}