package com.example.anotafacil.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.ShortNavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.anotafacil.Screens
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.ShapeCornerRadius


data class BottomBarItem(
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector,
    val label: String,
    val router: String
)


@Composable
fun BottomAnimatedBar(
    currentRoute: String? = "",
    onItemClick: (String) -> Unit = {},
) {
    val bottomNavigationItems = listOf(
        BottomBarItem(
            iconSelected = Icons.Default.EditNote,
            iconUnselected = Icons.Outlined.EditNote,
            label = "Home",
            router = Screens.HOME.route
        ),
        BottomBarItem(
            iconSelected = Icons.Default.QueryStats,
            iconUnselected = Icons.Outlined.QueryStats,
            label = "Ver Análise",
            router = Screens.SALES_OVERVIEW.route
        ),
        BottomBarItem(
            iconSelected = Icons.Default.Person,
            iconUnselected = Icons.Outlined.Person,
            label = "Perfil",
            router = Screens.PROFILE.route
        )
    )

    val selectedIndex = remember(currentRoute) {
        val index = bottomNavigationItems.indexOfFirst { it.router == currentRoute }
        if (index != -1) index else 0
    }
    Box(
        modifier = Modifier
            .padding(horizontal = 40.dp)
            .padding(bottom = 40.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                clip = false
            )
            .background(
                color = Color.White,
                shape = CircleShape
            )
    ) {
        AnimatedNavigationBar(
            selectedIndex = selectedIndex,
            barColor = MaterialTheme.colorScheme.onPrimary,
            ballColor = MaterialTheme.colorScheme.primary,
            cornerRadius = ShapeCornerRadius(180f, 180f, 180f, 180f),
            ballAnimation = Parabolic(tween()),
            indentAnimation = Height(tween())
        ) {
            bottomNavigationItems.forEachIndexed { index, item ->
                val isSelected = selectedIndex == index
                ShortNavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != item.router) onItemClick(item.router)
                    },
                    icon = {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.iconSelected else item.iconUnselected,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    label = {
                        Text(
                            item.label
                        )
                    },
                    colors = ShortNavigationBarItemDefaults.colors(
                        selectedIndicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BottomAnimatedBarPreview() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
    ) {
        BottomAnimatedBar()
    }
}