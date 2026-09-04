package com.example.anotafacil

import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.anotafacil.presentation.LastScreenViewModel
import com.example.anotafacil.presentation.auth.CodeVerificationScreen
import com.example.anotafacil.presentation.auth.InitialScreen
import com.example.anotafacil.presentation.auth.LoginScreen
import com.example.anotafacil.presentation.auth.SignUpScreen
import com.example.anotafacil.presentation.customer_detail.CustomerDetailScreen
import com.example.anotafacil.presentation.customers.CustomersScreen
import com.example.anotafacil.presentation.history.PurchaseHistoryScreen
import com.example.anotafacil.presentation.home.HomeScreen
import com.example.anotafacil.presentation.new_purchase.NewPurchaseScreen
import com.example.anotafacil.presentation.price_definition.PriceDefinitionScreen
import com.example.anotafacil.presentation.sales_overview.SalesOverviewScreen
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

enum class Screens(val route: String) {
    INITIAL("initial"),
    LOGIN("login"),
    SIGN_UP("sign_up"),
    CODE_VERIFICATION("code_verification"),
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
        composable(route = Screens.INITIAL.route) {
            val lastScreenViewModel: LastScreenViewModel = hiltViewModel()

            lastScreenViewModel.lastRoute(Screens.INITIAL.route)
            InitialScreen {
                when (it) {
                    0 -> navController.navigate(Screens.LOGIN.route)
                    1 -> navController.navigate(Screens.CODE_VERIFICATION.route)
                }
            }
        }

        composable(route = Screens.LOGIN.route) {
            LoginScreen(
                onLoginClick = { email, password ->
                    navController.navigate(Screens.HOME.route)
                },
                onSignUpClick = {
                    navController.navigate(Screens.SIGN_UP.route)
                }
            )
        }

        composable(route = Screens.SIGN_UP.route) {
            SignUpScreen()
        }

        composable(route = Screens.CODE_VERIFICATION.route) {
            CodeVerificationScreen(
                onContinue = {
                    navController.navigate(Screens.HOME.route)
                },
                onBack = {
                    navController.navigateUp()
                },
            )
        }

        composable(route = Screens.HOME.route) { navBackStack ->
            val lastScreenViewModel: LastScreenViewModel = hiltViewModel(navBackStack)
            LaunchedEffect(Unit) {
                lastScreenViewModel.lastRoute(Screens.HOME.route)
            }
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
            SalesOverviewScreen(
                onBackClick = {}
            )
        }

        composable(
            route = "${Screens.CUSTOMERS.route}/{cityId}",
            arguments = listOf(navArgument("cityId") { type = NavType.StringType }),
            popExitTransition = { ExitTransition.None }
        ) { navBackStackEntry ->
            val currentCity = navBackStackEntry.arguments?.getString("cityId")
            val lastScreenViewModel: LastScreenViewModel = hiltViewModel(navBackStackEntry)

            LaunchedEffect(Unit){
                currentCity?.let {
                    lastScreenViewModel.lastRoute("${Screens.CUSTOMERS.route}/${currentCity}")
                }
            }

            CustomersScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                goToHomeScreen = {
                    navController.navigate(Screens.HOME.route) {
                        popUpTo(Screens.HOME.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                goToCustomerDetailScreen = { uuid ->
                    uuid?.let {
                        println("UUID: $it")
                        navController.navigate("${Screens.CUSTOMER_DETAIL.route}/$it")
                    }
                }
            )
        }

        composable(
            route = "${Screens.CUSTOMER_DETAIL.route}/{customerId}",
            arguments = listOf(navArgument("customerId") { type = NavType.StringType })
        ) {
            CustomerDetailScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onHistoryClick = { customerId ->
                    navController.navigate("${Screens.PURCHASE_HISTORY.route}/$customerId")
                },
                goToNewPurchaseScreen = {
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
            arguments = listOf(navArgument("customerId") { type = NavType.StringType })
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
            arguments = listOf(navArgument("customerId") { type = NavType.StringType })
        ) {
            PurchaseHistoryScreen(onBackClick = { navController.navigateUp() })
        }
    }
}