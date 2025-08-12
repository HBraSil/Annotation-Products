package com.example.produtosdelimpeza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.produtosdelimpeza.compose.ProdutosLimpezaApp
import com.example.produtosdelimpeza.ui.theme.ProdutosDeLimpezaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProdutosDeLimpezaTheme {
                  ProdutosLimpezaApp()
            }
        }
    }
}


