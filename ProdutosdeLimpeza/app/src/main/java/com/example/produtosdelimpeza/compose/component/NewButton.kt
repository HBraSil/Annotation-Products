package com.example.produtosdelimpeza.compose.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.produtosdelimpeza.R

@Composable
fun NewButton(text: Int, modifier: Modifier = Modifier, onClickNewBtn: () -> Unit) {
    OutlinedButton(
        onClick = onClickNewBtn,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 100.dp, end = 100.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = White
        ),
        shape = RoundedCornerShape(topStart = 15.dp, bottomEnd = 15.dp)
    ) {
        Text(stringResource(text))
    }
}


@Preview
@Composable
fun NewButtonPreview() {
    NewButton(text = R.string.start, onClickNewBtn = {})
}