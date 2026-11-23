package com.example.anotafacil.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp


@Composable
fun EasyNoteButtonWithoutRippleEffect(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    contentColor: Color,
    icon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    CompositionLocalProvider(LocalRippleConfiguration provides null) {

        TextButton(
            onClick = onClick,
            modifier = modifier,
            colors = ButtonDefaults.textButtonColors(
                contentColor = contentColor
            )
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = textStyle
            )
        }
    }
}