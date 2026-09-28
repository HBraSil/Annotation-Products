package com.example.anotafacil.presentation.profile.manage_sellers

import android.content.ClipData
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.anotafacil.domain.model.OwnerCode
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.ui.components.AnnotationProductsConfirmationDialog
import kotlinx.coroutines.launch

@Composable
fun ManageSellersScreen(
    manageSellersViewModel: ManageSellersViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
) {

    val uiState by manageSellersViewModel.uiState.collectAsState()

    ManageSellersContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onShowDisconnectionConfirmationDialog = manageSellersViewModel::showDisconnectionConfirmationDialog,
        onGenerateNewCodeClick = manageSellersViewModel::generateOwnerCode,
        disconnectSeller = manageSellersViewModel::disconnectSeller
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSellersContent(
    uiState: ManageSellersUiState,
    onBackClick: () -> Unit = {},
    onShowDisconnectionConfirmationDialog: (Boolean) -> Unit = {},
    onGenerateNewCodeClick: () -> Unit = {},
    disconnectSeller: (String) -> Unit = {}
) {

    var sellerUid by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current
    val context = LocalContext.current
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CÓDIGOS E VENDEDORES",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Voltar",
                            tint = Color(0xFF0F172A)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF8FAFC))
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            AccessExplanationCard()

            PinCodeCard(
                uiState = uiState,
                onCopyCodeClick = {
                    scope.launch {
                        clipboard.setClipEntry(
                            ClipEntry(
                                ClipData.newPlainText(
                                    "Código",
                                    uiState.ownerCode.code
                                )
                            )
                        )
                    }

                    Toast.makeText(context, "Código copiado", Toast.LENGTH_SHORT).show()
                },
                onGenerateNewCodeClick = onGenerateNewCodeClick
            )

            Spacer(modifier = Modifier.height(4.dp))

            ConnectedSellersSection(
                activeSellersCount = uiState.sellers.size,
                sellers = uiState.sellers,
                onDisconnectSellerClick = {
                    onShowDisconnectionConfirmationDialog(true)
                    sellerUid = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
        
        if (uiState.showDisconnectionConfirmationDialog) {
            AnnotationProductsConfirmationDialog(
                title = "Tem certeza que deseja desconectar esse vendedor?",
                onDismissRequest = { onShowDisconnectionConfirmationDialog(false) },
                onConfirmClick = { disconnectSeller(sellerUid) }
            )
        }
    }
}



@Composable
private fun AccessExplanationCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Acesso de Vendedores",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Compartilhe este código PIN seguro com sua equipe. Os vendedores utilizam esta chave para sincronizar o catálogo e registrar pedidos diretamente na sua central de vendas.",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun PinCodeCard(
    uiState: ManageSellersUiState,
    onCopyCodeClick: () -> Unit,
    onGenerateNewCodeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabeçalho Interno (Badge e Expiração)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = "PIN de Sincronização",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (uiState.isCodeExpired) "Código expirado" else "Expira em ${uiState.remainingTime}",
                        fontSize = 12.sp,
                        color = if(uiState.isCodeExpired) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    uiState.ownerCode.code.forEach { char ->
                        BoxNumericCode(char)
                    }
                }
            }

            // Botões de Ação do PIN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onCopyCodeClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF5B46E0)
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Copiar Código",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Button(
                    onClick = onGenerateNewCodeClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f),
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Autorenew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Gerar Novo",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun BoxNumericCode(char: Char) {
    Box(
        modifier = Modifier
            .size(height = 48.dp, width = 38.dp)
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char.toString(),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}


@Composable
private fun ConnectedSellersSection(
    activeSellersCount: Int,
    sellers: List<User>,
    onDisconnectSellerClick: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Seus Vendedores",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "Vendedores que estão sincronizados com sua conta",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Surface(
                color = Color(0xFFEEF2FF),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "$activeSellersCount ativo",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4F46E5),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        sellers.forEach { seller ->
            SellerItemCard(
                sellerName = seller.name,
                sellerEmail = seller.email,
                onDisconnectClick = { onDisconnectSellerClick(seller.uid) }
            )
        }
    }
}

@Composable
private fun SellerItemCard(
    sellerName: String,
    sellerEmail: String,
    onDisconnectClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.onSurface.copy(0.1f),
                RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = sellerName,
                    fontSize = 15.sp,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = sellerEmail,
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.8f)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Surface(
                onClick = onDisconnectClick,
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                contentColor =MaterialTheme.colorScheme.error
            ) {
                Text(
                    text = "Desconectar vendedor",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ManageSellersScreenPreview() {
    ManageSellersContent(
        uiState = ManageSellersUiState(
            ownerCode = OwnerCode(
                ownerId = "123456789",
                code = "123456",
                createdAt = 39453094,
                expiresAt = 3945309457567546456
            ),
            sellers = listOf(
                User(
                    name = "Rafael Lima",
                    email = "john.c.calhoun@examplepetstore.com",
                    ownerId = "123456789"
                ),
                User(
                    name = "Lucas Santana",
                    email = "james.wiwegerg4543grfgsdfvlson@example-pet-store.com",
                    ownerId = "123456789"
                )
            ),
        ),
    )
}