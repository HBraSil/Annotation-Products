package com.example.anotafacil.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> EasyNotesExposedDropDown(
    items: List<T>,
    itemText: (T) -> String,
    leadingIcon: ImageVector,
    selectedItem: String?,
    containerColor: Color,
    trailingContent: (@Composable (T) -> Unit)?,
    onProductSelected: (T) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val bottomRadius by animateDpAsState(
        targetValue = if (expanded) 0.dp else 16.dp,
        animationSpec = tween(250),
        label = ""
    )

    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(250),
        label = "arrowRotation"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        Surface(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = bottomRadius,
                bottomEnd = bottomRadius
            ),
            color = containerColor
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = selectedItem ?: "Selecione um produto",
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "opened or closed ExposedDropDown icon",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.rotate(arrowRotation)
                )
            }
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.background(Color.White),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            shadowElevation = 0.dp,
        ) {
            if (items.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Nenhum item disponível") },
                    onClick = { expanded = false }
                )
            } else {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = itemText(item),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        },
                        trailingIcon = { trailingContent?.invoke(item) },
                        onClick = {
                            onProductSelected(item)
                            expanded = false
                        },
                        /*colors = MenuItemColors(
                            textColor = MaterialTheme.colorScheme.onBackground,
                            leadingIconColor = MaterialTheme.colorScheme.onBackground,
                            trailingIconColor = MaterialTheme.colorScheme.onBackground,
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onBackground,
                        )*/
                    )
                }
            }
        }
    }
}