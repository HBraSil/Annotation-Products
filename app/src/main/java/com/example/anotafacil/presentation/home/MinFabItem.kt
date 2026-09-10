package com.example.anotafacil.presentation.home

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.anotafacil.Screens

data class MinFabItem(
    var icon: ImageVector,
    var name: String,
    var route: Screens? = null,
)