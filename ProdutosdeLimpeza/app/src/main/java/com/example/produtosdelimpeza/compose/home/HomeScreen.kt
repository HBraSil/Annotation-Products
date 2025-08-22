package com.example.produtosdelimpeza.compose.home

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import com.example.produtosdelimpeza.R
import com.example.produtosdelimpeza.compose.main.MainBottomNavigation
import com.example.produtosdelimpeza.ui.theme.LightGreenCircle
import com.example.produtosdelimpeza.ui.theme.RedCircle


data class ItemInitialCard(
    val image: Int,
    val name: String,
    val colorIcon: Color,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, onCardSellerClick: (String) -> Unit) {

    var showDialog by remember { mutableStateOf(false) }

    val items = listOf(
        ItemInitialCard(R.drawable.highlight, "Raimundo", LightGreenCircle, "Este vendedor passa na sua cidade"),
        ItemInitialCard(R.drawable.highlight, "Iran", LightGreenCircle, "Este vendedor passa na sua cidade"),
        ItemInitialCard(R.drawable.highlight, "Francialdo", RedCircle, "Este vendedor não passa na sua cidade"),
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "LimpOn", /*color = Black*/) },
                    actions = {
                        IconButton(onClick = { showDialog = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = stringResource(R.string.icon_info)
                            )
                        }
                    }
                )
            },
            bottomBar = {
                MainBottomNavigation(navController)
            },
            modifier = Modifier.navigationBarsPadding()
        ) { contentPadding ->
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = contentPadding.calculateTopPadding(),
                    )
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(items.size) {
                        ItemCard(item = items[it]) {
                            onCardSellerClick(items[it].name)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        HomeInfoDialog { showDialog = false }
    }
}

@Composable
fun HomeInfoDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(White, shape = RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF8FB9A8),
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Informações Importantes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.padding(top = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Circle,
                        contentDescription = null,
                        modifier = Modifier
                            .size(15.dp)
                            .padding(top = 4.dp),
                        tint = LightGreenCircle,
                    )
                    Text(
                        text = "Vendedor passa na sua cidade",
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Outlined.Circle,
                        contentDescription = null,
                        modifier = Modifier
                            .size(15.dp)
                            .padding(top = 4.dp),
                        tint = RedCircle,
                    )
                    Text(
                        text = "Vendedor NÃO passa na sua cidade",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@Composable
fun ItemCard(item: ItemInitialCard, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .size(150.dp)
            .clickable { onClick() }
            .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(100.dp)
                .border(0.dp, Black, shape = CircleShape),
        ) {
            Image(
                painter = painterResource(id = item.image),
                contentDescription = item.name,
                modifier = Modifier.clip(CircleShape),
                contentScale = ContentScale.FillBounds
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {

            Icon(
                imageVector  = Icons.Outlined.Circle,
                contentDescription = item.name,
                modifier = Modifier
                    .size(15.dp)
                    .padding(top = 2.dp),
                tint = item.colorIcon
            )


            Text(
                text = item.name,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    //HomeScreen() {}
}