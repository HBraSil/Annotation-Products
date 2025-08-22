package com.example.produtosdelimpeza.compose.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NewTxtField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    obfuscate: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    @StringRes label: Int,
    @StringRes placeholder: Int,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit) = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {Text(text = stringResource(label)) /*color = MaterialTheme.colorScheme.secondary)*/},
        placeholder = {Text(text = stringResource(placeholder))},
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp),
        shape = RoundedCornerShape(15.dp),
        textStyle = TextStyle(
            fontSize = 19.sp,
            fontWeight = Normal,
            //color = Black
        ),
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            /*focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = White,
            unfocusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = White,
            focusedTextColor = White*/
        ),
        visualTransformation = if (obfuscate)
            PasswordVisualTransformation()
        else
            VisualTransformation.None
    )
}