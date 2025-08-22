package com.example.produtosdelimpeza.compose

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.produtosdelimpeza.compose.initial.InitialScreen
import com.example.produtosdelimpeza.compose.login.LoginScreen
import com.example.produtosdelimpeza.compose.main.MainScreenNavigation
import com.example.produtosdelimpeza.compose.seller.SellerLoginScreen
import com.example.produtosdelimpeza.compose.signup.SignupCodeScreen
import com.example.produtosdelimpeza.compose.signup.SignupScreen


@Composable
fun ProdutosLimpezaApp() {
    val navController = rememberNavController()
    ProdutosLimpezaNavHost(navController = navController)
}

@Composable
fun ProdutosLimpezaNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.INITIAL.route) {
        composable(route = Screen.INITIAL.route) {
            InitialScreen(
                onChoiceClick = { navController.navigate(Screen.LOGIN.route) },
            )
        }

        composable(route = Screen.LOGIN.route) {
            LoginScreen(
                onBackNavigation = { navController.navigateUp() },
                onSignupClick = { navController.navigate(Screen.SIGNUP.route) }
            )
        }

        composable(route = Screen.SIGNUP.route) {
            SignupScreen(
                onBackNavigation = { navController.navigateUp() },
                onToSignupClick = { navController.navigate(Screen.SIGNUP_CODE.route) }
            )
        }

        composable(route = Screen.SIGNUP_CODE.route) {
            SignupCodeScreen(
                onToSignupClick = { navController.navigate(Screen.MAIN.route) }
            )
        }

        composable(route = Screen.MAIN.route) {
            MainScreenNavigation()
        }

        composable(route = Screen.SELLER_LOGIN.route) {
            SellerLoginScreen()
        }
    }
}