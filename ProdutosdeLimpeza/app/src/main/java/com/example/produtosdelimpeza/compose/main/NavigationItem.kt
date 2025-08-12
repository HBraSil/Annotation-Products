package com.example.produtosdelimpeza.compose.main

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.produtosdelimpeza.compose.Screen

data class NavigationItem(
    @StringRes val title: Int,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector,
    val router: Screen
)
