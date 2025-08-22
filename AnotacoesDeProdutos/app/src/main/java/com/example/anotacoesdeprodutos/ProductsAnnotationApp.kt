package com.example.anotacoesdeprodutos

import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.anotacoesdeprodutos.presentation.LastScreenViewModel
import com.example.anotacoesdeprodutos.presentation.customer_detail.CustomerDetailScreen
import com.example.anotacoesdeprodutos.presentation.customers.CustomersScreen
import com.example.anotacoesdeprodutos.presentation.history.PurchaseHistoryScreen
import com.example.anotacoesdeprodutos.presentation.home.HomeScreen
import com.example.anotacoesdeprodutos.presentation.new_purchase.NewPurchaseScreen
import com.example.anotacoesdeprodutos.presentation.price_definition.PriceDefinitionScreen

enum class Screens(val route: String) {
    HOME("home"),
    CUSTOMER_DETAIL("customer_detail"),
    CUSTOMERS("customers"),
    NEW_PURCHASE("new_purchase"),
    PRICE_DEFINITION("price_definition"),
    PURCHASE_HISTORY("purchase_history")
}

@Composable
fun ProductsAnnotationApp(startDestination: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screens.HOME.route) {
            HomeScreen(
                onCityClick = {
                    navController.navigate("${Screens.CUSTOMERS.route}/${it.id}")
                },
                onUpdatePricesClick = {
                    navController.navigate(Screens.PRICE_DEFINITION.route)
                }
            )
        }

        composable(
            route = "${Screens.CUSTOMERS.route}/{cityId}",
            arguments = listOf(navArgument("cityId") { type = NavType.LongType }),
            popExitTransition = { ExitTransition.None }
        ) { navBackStackEntry ->
            val lastScreenViewModel: LastScreenViewModel = hiltViewModel(navBackStackEntry)

            CustomersScreen(
                lastScreenViewModel = lastScreenViewModel,
                onBackClick = {
                    lastScreenViewModel.currentRoute(Screens.HOME.route)
                    navController.navigateUp()
                },
                goToHomeScreen = {
                    navController.navigate(Screens.HOME.route) {
                        popUpTo(Screens.HOME.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(
            route = "${Screens.CUSTOMER_DETAIL.route}/{customerId}",
            arguments = listOf(navArgument("customerId") { type = NavType.LongType })
        ) {
            CustomerDetailScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onHistoryClick = { navController.navigate(Screens.PURCHASE_HISTORY.route) },
                goToNewPurchaseScreen = {
                    navController.navigate("${Screens.NEW_PURCHASE.route}/$it")
                }
            )
        }

        composable(
            route = "${Screens.NEW_PURCHASE.route}/{customerId}",
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 600)
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = 600)
                )
            },
            arguments = listOf(navArgument("customerId") { type = NavType.LongType })
        ) {
            NewPurchaseScreen(
                onBackClick = { navController.navigateUp() }
            )
        }

        composable(route = Screens.PRICE_DEFINITION.route) {
            PriceDefinitionScreen(onBackClick = { navController.navigateUp() })
        }

        composable(route = Screens.PURCHASE_HISTORY.route) {
            PurchaseHistoryScreen(onBackClick = { navController.navigateUp() })
        }
    }
}