package com.example.anotacoesdeprodutos.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnnotationProductsTextField(
    text: String,
    onNameChange: (String) -> Unit,
    placeholder: String
) {
    TextField(
        value = text,
        onValueChange = onNameChange,
        placeholder = {
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.secondary.copy(0.5f),
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = MaterialTheme.shapes.medium),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = Color.Gray,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
        )
    )
}