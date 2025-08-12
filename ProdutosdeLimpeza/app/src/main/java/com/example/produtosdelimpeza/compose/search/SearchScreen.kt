package com.example.produtosdelimpeza.compose.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.produtosdelimpeza.compose.main.MainBottomNavigation


@Composable
fun SearchScreen(navControler: NavHostController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                MainBottomNavigation(navControler)
            }
        ) { contentPadding ->
            Column(modifier = Modifier.padding(bottom = contentPadding.calculateBottomPadding())) {
                Text(text = "Tela de Pesquisa")
            }
       }
    }
}