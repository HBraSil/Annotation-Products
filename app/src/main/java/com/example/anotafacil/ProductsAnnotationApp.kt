package com.example.anotafacil

import AccountProfileScreen
import android.util.Log
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.anotafacil.presentation.LastScreenViewModel
import com.example.anotafacil.presentation.onboarding.verification_code.VerificationCodeScreen
import com.example.anotafacil.presentation.onboarding.RoleSectionScreen
import com.example.anotafacil.presentation.auth.LoginScreen
import com.example.anotafacil.presentation.auth.SignUpScreen
import com.example.anotafacil.ui.components.AnimatedBottomBar
import com.example.anotafacil.presentation.customer_detail.CustomerDetailScreen
import com.example.anotafacil.presentation.customers.OwnerCustomersScreen
import com.example.anotafacil.presentation.customers.SellerCustomersScreen
import com.example.anotafacil.presentation.history.PurchaseHistoryScreen
import com.example.anotafacil.presentation.home.OwnerHomeScreen
import com.example.anotafacil.presentation.home.SellerHomeScreen
import com.example.anotafacil.presentation.new_purchase.NewPurchaseScreen
import com.example.anotafacil.presentation.onboarding.subscription.SubscriptionScreen
import com.example.anotafacil.presentation.price_definition.PriceDefinitionScreen
import com.example.anotafacil.presentation.profile.manage_sellers.ManageSellersScreen
import com.example.anotafacil.presentation.profile.ProfileScreen
import com.example.anotafacil.presentation.sales_overview.SalesOverviewScreen

enum class Screens(val route: String) {
    ROLE_SECTION("role_section"),
    LOGIN("login"),
    SIGN_UP("sign_up"),
    SUBSCRIPTION("subscription"),
    CODE_VERIFICATION("code_verification"),

    OWNER_HOME("owner_home"),
    SELLER_HOME("seller_home"),

    OWNER_CUSTOMERS("owner_customers"),
    SELLER_CUSTOMERS("seller_customers"),

    SALES_OVERVIEW("sales_overview"),
    CUSTOMER_DETAIL("customer_detail"),
    NEW_PURCHASE("new_purchase"),
    PRICE_DEFINITION("price_definition"),
    PURCHASE_HISTORY("purchase_history"),
    PROFILE("profile"),
    MANAGE_SELLERS("manage_sellers"),
    ACCOUNT_PROFILE("account_profile")
}


@Composable
fun ProductsAnnotationApp(
    lastScreenViewModel: LastScreenViewModel = hiltViewModel(),
    startDestination: String
) {
    val navController = rememberNavController()

    val backStackEntry = navController.currentBackStackEntryAsState()
    val backStackRoute = backStackEntry.value?.destination?.route

    Scaffold(
        bottomBar = {
            if (
                backStackRoute == Screens.OWNER_HOME.route ||
                backStackRoute == Screens.SALES_OVERVIEW.route ||
                backStackRoute == Screens.PROFILE.route ||
                backStackRoute == Screens.PRICE_DEFINITION.route
            ) {
                AnimatedBottomBar(
                    currentRoute = backStackRoute,
                    onItemClick = { route ->
                        navController.navigate(route) {
                            popUpTo(Screens.OWNER_HOME.route) { inclusive = false }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        ProductsAnnotationApp(
            startDestination = startDestination,
            navController = navController,
            innerPadding = innerPadding,
            lastRoute = {
                Log.d("ProductsAnnotationApp", "lastRoute: $it")
                lastScreenViewModel.lastRoute(it)
            }
        )
    }
}

@Composable
fun ProductsAnnotationApp(
    startDestination: String,
    navController: NavHostController,
    innerPadding: PaddingValues = PaddingValues(),
    lastRoute: (String) -> Unit = {},
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {

        composable(route = Screens.LOGIN.route) {
            LaunchedEffect(Unit) {
                lastRoute(Screens.LOGIN.route)
            }
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screens.ROLE_SECTION.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onSignUpClick = {
                    navController.navigate(Screens.SIGN_UP.route)
                }
            )
        }


        composable(route = Screens.SIGN_UP.route) {
            SignUpScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        composable(route = Screens.ROLE_SECTION.route) {
            LaunchedEffect(Unit){
                lastRoute(Screens.ROLE_SECTION.route)
            }
            RoleSectionScreen {
                when (it) {
                    0 -> navController.navigate(Screens.SUBSCRIPTION.route)
                    1 -> navController.navigate(Screens.CODE_VERIFICATION.route)
                }
            }
        }

        composable(route = Screens.SUBSCRIPTION.route) {
            SubscriptionScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                goToHome = {
                    navController.navigate(Screens.OWNER_HOME.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(route = Screens.CODE_VERIFICATION.route) {
            VerificationCodeScreen(
                onContinue = {
                    navController.navigate(Screens.SELLER_HOME.route) {

                        popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onBack = {
                    navController.navigateUp()
                },
            )
        }



        composable(route = Screens.OWNER_HOME.route) { navBackStack ->
            LaunchedEffect(Unit) {
                lastRoute(Screens.OWNER_HOME.route)
            }

            OwnerHomeScreen(
                innerPadding = innerPadding,
                onCityClick = {
                    navController.navigate("${Screens.OWNER_CUSTOMERS.route}/${it.id}")
                }
            )
        }


        composable(route = Screens.SELLER_HOME.route) { navBackStack ->
            LaunchedEffect(Unit) {
                lastRoute(Screens.SELLER_HOME.route)
            }

            SellerHomeScreen(
                innerPadding = innerPadding,
                onCityClick = {
                    navController.navigate("${Screens.SELLER_CUSTOMERS.route}/${it.id}")
                }
            )
        }


        composable(
            route = "${Screens.OWNER_CUSTOMERS.route}/{cityId}",
            arguments = listOf(navArgument("cityId") { type = NavType.StringType }),
            popExitTransition = { ExitTransition.None }
        ) { navBackStackEntry ->
            val currentCity = navBackStackEntry.arguments?.getString("cityId")

            LaunchedEffect(Unit){
                currentCity?.let {
                    lastRoute("${Screens.OWNER_CUSTOMERS.route}/${currentCity}")
                }
            }

            OwnerCustomersScreen(
                onBackClick = {
                    val popped = navController.popBackStack()
                    if (!popped) {
                        navController.navigate(Screens.OWNER_HOME.route) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true } // Limpa a pilha para a Home virar a raiz
                        }
                    }
                },
                goToHomeScreen = {
                    val popped = navController.popBackStack()
                    if (!popped) {
                        navController.navigate(Screens.OWNER_HOME.route) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        }
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
            route = "${Screens.SELLER_CUSTOMERS.route}/{cityId}",
            arguments = listOf(navArgument("cityId") { type = NavType.StringType }),
            popExitTransition = { ExitTransition.None }
        ) { navBackStackEntry ->
            val currentCity = navBackStackEntry.arguments?.getString("cityId")

            LaunchedEffect(Unit){
                currentCity?.let {
                    lastRoute("${Screens.SELLER_CUSTOMERS.route}/${currentCity}")
                }
            }

            SellerCustomersScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                goToHomeScreen = {
                    navController.navigate(Screens.SELLER_HOME.route) {
                        popUpTo(Screens.SELLER_HOME.route) { inclusive = false }
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


        composable(route = Screens.SALES_OVERVIEW.route) {
            SalesOverviewScreen(
                onBackClick = {}
            )
        }


        composable(route = Screens.PROFILE.route) {
            ProfileScreen(
                goToAccountProfile = {
                    navController.navigate(Screens.ACCOUNT_PROFILE.route)
                },
                onPriceTableClick = { navController.navigate(Screens.PRICE_DEFINITION.route) },
                onManageSellersClick = { navController.navigate(Screens.MANAGE_SELLERS.route) },
                onSignOutClick = {
                    navController.navigate(Screens.LOGIN.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screens.ACCOUNT_PROFILE.route) {
            AccountProfileScreen(
                onBackClick = { navController.navigateUp() },
                goToLoginScreen = {
                    navController.navigate(Screens.LOGIN.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screens.MANAGE_SELLERS.route) {
            ManageSellersScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
    }
}