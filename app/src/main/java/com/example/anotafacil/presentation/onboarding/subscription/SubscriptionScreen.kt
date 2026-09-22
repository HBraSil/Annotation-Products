package com.example.anotafacil.presentation.onboarding.subscription

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val TextPrimary = Color(0xFF111111)
private val TextSecondary = Color(0xFF606575)
private val Background = Color(0xFFF9F9FA)

@Composable
fun SubscriptionScreen(
    subscriptionViewModel: SubscriptionViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    goToHome: () -> Unit = {}
) {
    var selectedPayment by remember {
        mutableStateOf(PaymentMethod.PIX)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Voltar",
                    tint = TextPrimary
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFE9EBFF),
                                    Color(0xFFF3F3F5)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(
                            horizontal = 20.dp,
                            vertical = 22.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(29.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = Color(0xFFD9DFFF)
                    ) {

                        Text(
                            text = "ϟ  Upgrade Imediato de Proprietário",
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 7.dp
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "Desbloqueie o\nGerenciamento\nCompleto",
                        fontSize = 25.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "Ative agora todas as\nferramentas necessárias para sua gestão\ncomercial através de uma taxa única.",
                        fontSize = 13.sp,
                        lineHeight = 21.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFECECF1)
                        ) {
                            Text(
                                text = "Taxa única de ativação",
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 5.dp
                                ),
                                fontSize = 12.sp,
                                color = Color(0xFF444651)
                            )
                        }

                        Spacer(
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(
                            modifier = Modifier.size(4.dp)
                        )

                        Text(
                            text = "Vitalício",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {

                        Text(
                            text = "R$",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(
                                bottom = 9.dp
                            )
                        )

                        Spacer(
                            modifier = Modifier.size(8.dp)
                        )

                        Text(
                            text = "10,00",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )

                        Spacer(
                            modifier = Modifier.size(8.dp)
                        )

                        Column(
                            modifier = Modifier.padding(
                                bottom = 5.dp
                            )
                        ) {

                            Text(
                                text = "/ pagamento",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )

                            Text(
                                text = "único",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Sem cobranças recorrentes ou\nmensalidades surpresa. Acesso liberado\nno mesmo segundo para a sua conta.",
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = TextSecondary
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp),
                        color = Color(0xFFF7F7F8)
                    ) {

                        Row(
                            modifier = Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 13.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(
                                modifier = Modifier.size(10.dp)
                            )

                            Text(
                                text = "Liberação automática\nem segundos",
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = TextPrimary
                            )

                            Spacer(
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = "100%\nDigital",
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            /*
             * SECTION TITLE
             */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.Top
            ) {

                Text(
                    text = "O que você ganha com o\nacesso total",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = TextPrimary
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "4 recursos\nchave",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            /*
             * FEATURES
             */

            FeatureCard(
                icon = Icons.Default.BarChart,
                title = "Gráficos & Relatórios Completos",
                description = "Acompanhe vendas por produto, curva de faturamento e lucro líquido detalhado dos últimos meses.",
                iconBackground = Color(0xFFE0E4FF)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            FeatureCard(
                icon = Icons.Default.PersonAdd,
                title = "Gestão Ilimitada de Clientes",
                description = "Cadastre, edite e exclua clientes, visualize histórico de compras e controle saldos em aberto e pagamentos parciais.",
                iconBackground = Color(0xFFE6E4EE)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            FeatureCard(
                icon = Icons.Default.CardMembership,
                title = "Códigos para Vendedores Ilimitados",
                description = "Gere e compartilhe novos códigos de acesso sem restrições para toda a sua equipe de vendas em campo.",
                iconBackground = Color(0xFFE0E4FF)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            FeatureCard(
                icon = Icons.Default.Cloud,
                title = "Sincronização em Nuvem Segura",
                description = "Seus dados, cadastros e anotações protegidos com criptografia e sempre sincronizados em tempo real.",
                iconBackground = Color(0xFFFFE1DA)
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            /*
             * PAYMENT METHODS
             */
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF0F0F2)
            ) {

                Column(
                    modifier = Modifier.padding(14.dp)
                ) {

                    Text(
                        text = "Formas de pagamento integradas",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        PaymentOption(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.QrCode,
                            title = "Pix",
                            subtitle = "Instantâneo",
                            selected = selectedPayment == PaymentMethod.PIX,
                            onClick = {
                                selectedPayment = PaymentMethod.PIX
                            }
                        )

                        PaymentOption(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.CreditCard,
                            title = "Cartão",
                            subtitle = "Crédito à vista",
                            selected = selectedPayment == PaymentMethod.CARD,
                            onClick = {
                                selectedPayment = PaymentMethod.CARD
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            /*
             * PAYMENT BUTTON
             */
            Button(
                onClick = {
                    goToHome()
                    subscriptionViewModel.finalizeSubscription()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {

                Text(
                    text = "Pagar R$ 10,00 e Desbloquear",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.size(8.dp)
                )

                Text(
                    text = "→",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    modifier = Modifier.size(13.dp),
                    tint = TextSecondary
                )

                Spacer(
                    modifier = Modifier.size(5.dp)
                )

                Text(
                    text = "Pagamento 100% seguro • Liberação\ninstantânea via Pix",
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }
    }
}

private enum class PaymentMethod {
    PIX,
    CARD
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    iconBackground: Color
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(19.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.size(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = description,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun PaymentOption(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Surface(
        modifier = modifier
            .height(64.dp)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(12.dp)
            ),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color.White
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 9.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (selected) {
                            Color(0xFFE1E5FF)
                        } else {
                            Color(0xFFEAEAEA)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color(0xFF454545)
                    }
                )
            }

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            Column {

                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = if (selected) {
                        FontWeight.Medium
                    } else {
                        FontWeight.Normal
                    },
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        TextPrimary
                    }
                )

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        TextSecondary
                    }
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 326,
    heightDp = 1600
)
@Composable
private fun SubscriptionScreenPreview() {
    MaterialTheme {
        SubscriptionScreen()
    }
}