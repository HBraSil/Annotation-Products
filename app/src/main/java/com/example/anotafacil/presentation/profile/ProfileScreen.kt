package com.example.anotafacil.presentation.profile

import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    goToAccountProfile: () -> Unit = {},
    onPriceTableClick: () -> Unit = {},
    onManageSellersClick: () -> Unit = {},
) {
    val uiState by profileViewModel.uiState.collectAsState()
    ProfileContent(
        uiState = uiState,
        onPriceTableClick = onPriceTableClick,
        onManageSellersClick = onManageSellersClick,
        onSyncCloudClick = profileViewModel::onSyncCloudClick,
        onSignOutClick = { profileViewModel.signOut() },
        goToAccountProfile = goToAccountProfile
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    onPriceTableClick: () -> Unit,
    onManageSellersClick: () -> Unit,
    onSyncCloudClick: () -> Unit,
    onSignOutClick: () -> Unit,
    goToAccountProfile: () -> Unit = {},
) {
    val context = LocalContext.current


    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            Toast.makeText(
                context,
                "Dados sincronizados com sucesso!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Meu Perfil",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            UserHeaderCard(
                name = uiState.user?.name ?: "Sem nome",
                email = uiState.user?.email ?: "email@gmail.com",
                role = uiState.user?.role?.name ?: "No role",
                goToAccountProfile = goToAccountProfile
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "GESTÃO COMERCIAL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(top = 8.dp, start = 4.dp)
            )


            ManagementOptionCard(
                title = "Tabela de Preços",
                subtitle = "Definir preços dos produtos",
                icon = Icons.Default.LocalOffer,
                iconContainerColor = MaterialTheme.colorScheme.surface.copy(0.2f),
                iconTintColor = MaterialTheme.colorScheme.primaryContainer.copy(0.8f),
                onClick = onPriceTableClick
            )

            ManagementOptionCard(
                title = "Equipe & Códigos de Acesso",
                subtitle = "Gerar código de acesso e gerenciar vendedores",
                icon = Icons.Default.Group,
                iconContainerColor = MaterialTheme.colorScheme.secondary.copy(0.2f),
                iconTintColor = MaterialTheme.colorScheme.onSecondary,
                onClick = onManageSellersClick
            )

            ManagementOptionCard(
                title = "Sincronizar Dados",
                statusDotColor = MaterialTheme.colorScheme.surface,
                icon = Icons.Default.Cloud,
                iconContainerColor = MaterialTheme.colorScheme.onSecondary.copy(0.2f, blue = 0.8f),
                iconTintColor = MaterialTheme.colorScheme.primary,
                showTrailingIcon = false,
                onClick = onSyncCloudClick,
                subtitleContent = {
                    if (uiState.isSyncing) {
                        LoadingDots(color = MaterialTheme.colorScheme.onSecondary)
                    } else {
                        Text(
                            text = "Conexão com internet",
                            fontSize = 12.sp,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            LogoutButton(
                onLogoutClick = onSignOutClick,
            )
        }
    }
}


@Composable
private fun UserHeaderCard(
    name: String,
    email: String,
    role: String,
    modifier: Modifier = Modifier,
    goToAccountProfile: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Surface(
                color = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = role,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4F46E5),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = name,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = email,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }

        IconButton(
            onClick = goToAccountProfile,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.onSecondary.copy(0.4f),
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar Conta",
            )
        }
    }
}


@Composable
private fun ManagementOptionCard(
    title: String,
    subtitle: String = "",
    icon: ImageVector,
    iconContainerColor: Color,
    iconTintColor: Color,
    showTrailingIcon: Boolean = true,
    onClick: () -> Unit,
    statusDotColor: Color = MaterialTheme.colorScheme.onSecondary,
    subtitleContent: (@Composable () -> Unit)? = null,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconContainerColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTintColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F172A)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (subtitleContent != null) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusDotColor)
                        )
                        subtitleContent()
                    } else {
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = statusDotColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            if (showTrailingIcon) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8)
                )
            }
        }
    }
}


@Composable
private fun LogoutButton(
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFEF2F2),
                contentColor = Color(0xFFEF4444)
            ),
            elevation = null
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Sair",
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Sair da Conta",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}


@Composable
fun LoadingDots(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val transition = rememberInfiniteTransition(label = "dots")

            val alpha by transition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 600,
                        delayMillis = index * 150
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot"
            )

            Box(
                modifier = Modifier
                    .size(6.dp)
                    .alpha(alpha)
                    .background(
                        color = color,
                        shape = CircleShape
                    )
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    MaterialTheme {
        ProfileContent(
            uiState = ProfileUiState(),
            onPriceTableClick = {},
            onManageSellersClick = {},
            onSyncCloudClick = {},
            onSignOutClick = {}
        )
    }
}