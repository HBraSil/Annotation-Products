package com.example.anotafacil.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anotafacil.domain.formatter.brazilianCurrencyFormatter
import com.example.anotafacil.domain.model.Product


@Composable
fun EasyNoteProductQuantityCard(
    item: Product,
    productName: String,
    productPrice: String,
    quantity: String,
    decreaseQntt: (Product) -> Unit,
    increaseQntt: (Product) -> Unit,
    subtotal: () -> Int,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = productName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Unit: ${brazilianCurrencyFormatter.format(productPrice.toDouble())}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.onPrimary,
                        RoundedCornerShape(24.dp)
                    ).padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            decreaseQntt(item)
                        },
                        modifier = Modifier.size(28.dp)
                    ) { Text("−", fontWeight = FontWeight.Bold) }
                    Text(
                        text = quantity,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            increaseQntt(item)
                        },
                        modifier = Modifier.size(28.dp)
                    ) { Text(text = "+", fontWeight = FontWeight.Bold) }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "SUBTOTAL",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = brazilianCurrencyFormatter.format(subtotal()),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}