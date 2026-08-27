package com.example.anotafacil

import android.util.Log
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.anotafacil.presentation.LastScreenViewModel
import com.example.anotafacil.presentation.customer_detail.CustomerDetailScreen
import com.example.anotafacil.presentation.customers.CustomersScreen
import com.example.anotafacil.presentation.history.PurchaseHistoryScreen
import com.example.anotafacil.presentation.home.HomeScreen
import com.example.anotafacil.presentation.new_purchase.NewPurchaseScreen
import com.example.anotafacil.presentation.price_definition.PriceDefinitionScreen
import com.example.anotafacil.presentation.sales_overview.SalesOverviewScreen

enum class Screens(val route: String) {
    HOME("home"),
    SALES_OVERVIEW("sales_overview"),
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
                onFabClick = {
                    when (it) {
                        Screens.PRICE_DEFINITION.route -> navController.navigate(Screens.PRICE_DEFINITION.route)
                        Screens.SALES_OVERVIEW.route -> navController.navigate(Screens.SALES_OVERVIEW.route)
                    }
                }
            )
        }

        composable(route = Screens.SALES_OVERVIEW.route) {
            SalesOverviewScreen()
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
                    lastScreenViewModel.lastRoute(Screens.HOME.route)
                    Log.d("CustomersScreenNav", "onBackClick: ${lastScreenViewModel.lastActiveProfile.value}")
                    navController.navigateUp()
                },
                goToHomeScreen = {
                    lastScreenViewModel.lastRoute(Screens.HOME.route)
                    Log.d("CustomersScreenNav", "onBackClick: ${lastScreenViewModel.lastActiveProfile.value}")
                    navController.navigate(Screens.HOME.route) {
                        popUpTo(Screens.HOME.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                goToCustomerDetailScreen = {
                    navController.navigate("${Screens.CUSTOMER_DETAIL.route}/$it")
                }
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
                onHistoryClick = { customerId ->
                    navController.navigate("${Screens.PURCHASE_HISTORY.route}/$customerId")
                },
                goToNewPurchaseScreen = {
                    // Pega a entrada atual da Tela B na pilha
                    val currentBackStackEntry = navController.currentBackStackEntry

                    // Verifica se a tela B já terminou de fazer a transição de saída
                    // (targetState é para onde a navegação está indo)
                    val isVisible = currentBackStackEntry
                        ?.lifecycle
                        ?.currentState
                        ?.isAtLeast(Lifecycle.State.RESUMED) == true

                    if (isVisible) {
                        navController.navigate("${Screens.NEW_PURCHASE.route}/$it")
                    }
                },
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

        composable(
            route = "${Screens.PURCHASE_HISTORY.route}/{customerId}",
            arguments = listOf(navArgument("customerId") { type = NavType.LongType })
        ) {
            PurchaseHistoryScreen(onBackClick = { navController.navigateUp() })
        }
    }
}