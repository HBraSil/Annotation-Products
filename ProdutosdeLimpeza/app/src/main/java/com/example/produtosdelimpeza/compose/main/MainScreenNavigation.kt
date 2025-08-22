package com.example.produtosdelimpeza.compose.main

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.produtosdelimpeza.R
import androidx.navigation.compose.rememberNavController
import com.example.produtosdelimpeza.compose.Screen
import com.example.produtosdelimpeza.compose.home.HomeScreen
import com.example.produtosdelimpeza.compose.profile.ProfileScreen
import com.example.produtosdelimpeza.compose.search.SearchScreen
import com.example.produtosdelimpeza.compose.seller.SellerScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenNavigation() {
    val navController = rememberNavController()

        NavHost(navController = navController, startDestination = Screen.HOME.route,  modifier = Modifier.background(White)) {
            composable(Screen.HOME.route) {
                HomeScreen(navController) {nameSeller ->
                    navController.navigate("${Screen.SELLER.route}/$nameSeller")
                }
            }
            composable(Screen.SEARCH.route) {
                SearchScreen(navController)
            }
            composable(Screen.PROFILE.route) {
                ProfileScreen(navController)
            }
            composable(Screen.PRODUCT.route) {
                //Área do vendedor(navController)
            }

            composable("${Screen.SELLER.route}/{nameSeller}") {
                val nameSeller = it.arguments?.getString("nameSeller") ?: ""
                SellerScreen(nameSeller) {
                    navController.navigateUp()
                }
            }
        }
}



@Composable
fun MainBottomNavigation(
    navController: NavHostController,
) {
    val navigationItems = listOf(
        NavigationItem(
            title = R.string.home,
            iconSelected = Icons.Filled.Home,
            iconUnselected = Icons.Outlined.Home,
            router = Screen.HOME
        ),
        NavigationItem(
            title = R.string.search,
            iconSelected = Icons.Filled.Search,
            iconUnselected = Icons.Outlined.Search,
            router = Screen.SEARCH
        ),
        NavigationItem(
            title = R.string.profile,
            iconSelected = Icons.Filled.Person,
            iconUnselected = Icons.Outlined.Person,
            router = Screen.PROFILE
        ),
    )


    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        navigationItems.forEach { item ->
            NavigationBarItem(
                //modifier = Modifier.background(if (currentRoute == item.router.route) DarkGray else White),
                selected = currentRoute == item.router.route,
                onClick = {
                    if (currentRoute != item.router.route) {
                        navController.navigate(item.router.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (currentRoute == item.router.route) item.iconSelected else item.iconUnselected,
                        contentDescription = item.title.toString(),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = item.title),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = NavigationBarItemDefaults.colors(indicatorColor = White)
            )
        }
    }
}


@Preview
@Composable
private fun HomeScreenPreview() {
    MainScreenNavigation()
}
