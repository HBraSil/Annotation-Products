package com.example.anotafacil.presentation.auth


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import com.hilquias.anotafacil.R


@Composable
fun InitialScreen(
    onChoiceClick: (Int) -> Unit = {}
) {

    InitialContent(
        onChoiceClick = onChoiceClick
    )
}

@Composable
fun InitialContent(
    onChoiceClick: (Int) -> Unit = {}
) {
    var selectedProfileIndex by remember { mutableStateOf<Int?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Image(
                painter = painterResource(R.drawable.background_image_initial_screen),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimary.copy(0.95f),
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .systemBarsPadding()
            ) {
                Text(
                    text = "Bem Vindo",
                    modifier = Modifier
                        .padding(start = 20.dp),
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    fontFamily = FontFamily(
                        Font(R.font.montserrat_extrabold_italic)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))
                ProfileSelectionSection(
                    selectedProfileIndex = selectedProfileIndex,
                    onSelectProfileIndex = { index -> selectedProfileIndex = index }
                )
                Spacer(modifier = Modifier.height(30.dp))

                ElevatedButton(
                    onClick = { selectedProfileIndex?.let { onChoiceClick(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = selectedProfileIndex != null,
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Continuar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "prosseguir para escolha",
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun ProfileSelectionSection(
    selectedProfileIndex: Int? = null,
    onSelectProfileIndex: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileOptionCard(
            badgeText = "Gestão Completa",
            title = "Proprietário do Negócio",
            icon = Icons.Default.Storefront,
            isSelected = selectedProfileIndex == 0,
            onClick = { onSelectProfileIndex(0) }
        )

        ProfileOptionCard(
            badgeText = "Vendas e Notas",
            title = "Vendedor",
            icon = Icons.Default.PointOfSale,
            isSelected = selectedProfileIndex == 1,
            onClick = { onSelectProfileIndex(1) }
        )
    }
}

@Composable
fun ProfileOptionCard(
    badgeText: String,
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = Color(0xFF0038FF)
    val inactiveIconBg = Color(0xFFF2F3F5)
    val activeBadgeBg = MaterialTheme.colorScheme.background
    val inactiveBadgeBg = Color(0xFFEEEEEE)
    val inactiveTextColor = Color(0xFF6C707A)
    val inactiveIconTint = Color(0xFF8E8E93)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected) 6.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .background(
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.secondary.copy(0.1f),
                shape = RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (isSelected) MaterialTheme.colorScheme.onBackground.copy(0.8f) else inactiveIconBg,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else inactiveIconTint
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .background(
                        color = if (isSelected) activeBadgeBg else inactiveBadgeBg,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) primaryColor else inactiveTextColor
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = Color(0xFFE0E0E0)
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun InitialScreenPreview() {
    InitialScreen()
}