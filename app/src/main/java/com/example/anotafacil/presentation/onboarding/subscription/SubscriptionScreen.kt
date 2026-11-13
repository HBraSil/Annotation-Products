package com.example.anotafacil.presentation.onboarding.subscription

import android.app.Activity
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Button
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect

/*@Composable
fun SubscriptionScreen(
    subscriptionViewModel: SubscriptionViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    goToHome: () -> Unit = {}
) {
    val uiState by subscriptionViewModel.subscriptionState.collectAsState()

    var selectedPayment by remember {
        mutableStateOf(PaymentMethod.PIX)
    }

    val activity = LocalContext.current as Activity


    }*/

@Composable
fun SubscriptionScreen(
    subscriptionViewModel: SubscriptionViewModel = hiltViewModel(),
    goToHome: () -> Unit,
    onBackClick: () -> Unit,
) {
    val uiState by subscriptionViewModel.subscriptionState.collectAsState()
    val activity = LocalContext.current as Activity

    LifecycleResumeEffect(Unit) {
        subscriptionViewModel.restoreSubscriptionPurchases()

        onPauseOrDispose {
            // O processamento pertence ao BillingManager.
        }
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) goToHome()
    }

    SubscriptionContent(
        uiState = uiState,
        onSubscribeClick = { subscriptionViewModel.testPremiumPurchase(activity = activity) },
        onRetryClick = subscriptionViewModel::loadSubscriptionProduct,
        onBackClick = onBackClick
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionContent(
    uiState: SubscriptionState = SubscriptionState(),
    onSubscribeClick: () -> Unit,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit = {}
) {


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Seja Premium",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBackIosNew, "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        // O scroll é estado visual local: não precisa ir para o ViewModel.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PremiumHeader()
                Spacer(Modifier.height(32.dp))
                PremiumBenefits()
                Spacer(Modifier.height(32.dp))
                SubscriptionPurchaseSection(
                    uiState = uiState,
                    onSubscribeClick = onSubscribeClick,
                    onRetryClick = onRetryClick
                )
                Spacer(Modifier.height(24.dp))
                SubscriptionFooter(
                    onTermsClick = {},
                    onPrivacyClick = {}
                )
            }
        }
    }
}

@Composable
private fun PremiumHeader(modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Box(Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.StarOutline, null, Modifier.size(32.dp))
            }
        }
        Spacer(Modifier.height(20.dp))
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Text(
                text = "ANOTA FÁCIL PREMIUM",
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Desbloqueie o\nGerenciamento Completo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Tenha mais recursos para organizar seus clientes, sua equipe e suas vendas.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PremiumBenefits(modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PremiumBenefitCard(
            icon = Icons.AutoMirrored.Outlined.TrendingUp,
            title = "Relatórios e faturamento",
            description = "Acompanhe suas vendas e os resultados do seu negócio."
        )
        PremiumBenefitCard(
            icon = Icons.Outlined.Groups,
            title = "Gestão de clientes",
            description = "Organize clientes, compras e pagamentos em um só lugar."
        )
        PremiumBenefitCard(
            icon = Icons.Outlined.PersonOutline,
            title = "Códigos para vendedores",
            description = "Vincule vendedores à sua equipe usando códigos de acesso."
        )
        PremiumBenefitCard(
            icon = Icons.Outlined.CloudDone,
            title = "Sincronização na nuvem",
            description = "Envie e baixe seus dados quando precisar."
        )
    }
}

@Composable
private fun PremiumBenefitCard(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Box(Modifier.size(42.dp), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(23.dp))
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SubscriptionPurchaseSection(
    uiState: SubscriptionState,
    onSubscribeClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isProcessing = uiState.status == SubscriptionStatus.PROCESSING
    val isLoading = uiState.status == SubscriptionStatus.LOADING
    val isPremium = uiState.status == SubscriptionStatus.PREMIUM
    //val canSubscribe = uiState.status == SubscriptionStatus.READY && !uiState.formattedMonthlyPrice.isNullOrBlank()

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        when {
            isPremium -> Text(
                "Seu Premium está ativo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            isLoading -> Text(
                "Carregando plano…",
                style = MaterialTheme.typography.bodyLarge
            )

            uiState.status == SubscriptionStatus.READY -> {
                uiState.productName
                    ?.takeIf(String::isNotBlank)
                    ?.let { name ->
                        Text(
                            name,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                uiState.productDescription
                    ?.takeIf(String::isNotBlank)
                    ?.let { description ->
                        Spacer(Modifier.height(4.dp))
                        Text(
                            description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        uiState.formattedMonthlyPrice.orEmpty(),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "/mês",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Cancele a qualquer momento",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        uiState.errorMessage?.let { message ->
            Spacer(Modifier.height(12.dp))
            Text(
                message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }

        if (uiState.status == SubscriptionStatus.ERROR) {
            TextButton(onClick = onRetryClick) { Text("Tentar novamente") }
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onSubscribeClick,
            enabled = uiState.status == SubscriptionStatus.READY,
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            shape = CircleShape,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
        ) {
            // O conteúdo de Button já é um RowScope: texto e seta ficam lado a lado.
            if (isLoading || isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = LocalContentColor.current,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(10.dp))
            }
            Text(
                text = when {
                    isLoading -> "Carregando…"
                    isProcessing -> "Processando…"
                    isPremium -> "Premium ativo"
                    else -> "Assinar agora"
                },
                modifier = Modifier.weight(1f, fill = false),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
            //if (canSubscribe) {
            //    Spacer(Modifier.width(16.dp))
            //    Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, Modifier.size(22.dp))
            //}
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SubscriptionFooter(
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                Icons.Outlined.Lock,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Pagamento pelo Google Play.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Renovação automática. Cancele a qualquer momento nas configurações do Google Play.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = onTermsClick) {
                Text("Termos de uso", textDecoration = TextDecoration.Underline)
            }
            TextButton(onClick = onPrivacyClick) {
                Text("Política de privacidade", textDecoration = TextDecoration.Underline)
            }
        }
    }
}

// Apenas para visualizar as cores da referência no Android Studio.
// Na tela real, use o tema Anota Fácil do seu aplicativo.
@Preview(showBackground = true, widthDp = 393, heightDp = 1100)
@Preview(
    name = "Fonte ampliada",
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    fontScale = 1.5f
)
@Composable
private fun SubscriptionScreenPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF0730CC),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE0E1FF),
            onPrimaryContainer = Color(0xFF0730CC),
            secondaryContainer = Color(0xFFEEEEFF),
            onSecondaryContainer = Color(0xFF0730CC),
            background = Color(0xFFFAFAFA),
            surface = Color(0xFFFAFAFA),
            surfaceContainerLow = Color(0xFFF3F3F3),
            surfaceContainerHighest = Color(0xFFE7E7E7),
            onSurface = Color(0xFF1B1C1E),
            onSurfaceVariant = Color(0xFF55565E)
        )
    ) {
        SubscriptionContent(
            onBackClick = {},
            onSubscribeClick = {}
        )
    }
}