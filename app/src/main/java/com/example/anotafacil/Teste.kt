import  androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anotafacil.presentation.customers.ServerStatus

// --- PALETA DE CORES ---
private val DarkBackground = Color(0xFF0F172A)
private val SurfaceDark = Color(0xFF1E293B)
private val SurfaceCard = Color(0xFF182232)
private val AccentGreen = Color(0xFF10B981)
private val AccentGreenContainer = Color(0xFF064E3B)
private val AccentBlue = Color(0xFF3B82F6)
private val TextPrimary = Color(0xFFF8FAFC)
private val TextSecondary = Color(0xFF94A3B8)

// --- MODELO DE DADOS ---
data class ClienteModel(
    val id: String,
    val iniciais: String,
    val nome: String,
    val status: String,
    val ultimaCompra: String
)

val clientesMock = listOf(
    ClienteModel("1", "MS", "Marcos Silva", "CONCLUÍDO", "2x Bermudas Térmicas..."),
    ClienteModel("2", "CD", "Camila Duarte", "EM ANDAMENTO", "1x Kit Suplementos"),
    ClienteModel("3", "RM", "Roberto Mendes", "CONCLUÍDO", "5x Camisetas DryFit"),
    ClienteModel("4", "ML", "Mariana Lopes", "PENDENTE", "1x Tênis Corrida"),
    ClienteModel("5", "LP", "Lucas Prado", "CONCLUÍDO", "3x Meias Esportivas")
)

// --- TELA PRINCIPAL ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaClientesCustom(
    cidade: String = "Campinas - SP",
    totalClientesCidade: Int = 48,
    lucroHoje: String = "R$ 1.845,50",
    variacaoLucro: String = "+14.2% vs ontem",
    vendasHoje: Int = 38,
    variacaoVendasMesAnterior: String = "+8% vs mês anterior",
    onBackClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onAddClienteClick: () -> Unit = {},
    onDeleteCliente: (ClienteModel) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    val clientesFiltrados = clientesMock.filter {
        it.nome.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            // Header Topo: Botão de voltar + Título da tela
            TopAppBar(
                title = {
                    Text(
                        text = "Graça Aranha",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    Text(
                        text = "total de clientes: $totalClientesCidade",
                        color = AccentBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                )
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ServerStatus {  }
            }

            // 1. Linha da Imagem 2: Sincronizado + Novo Cliente
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Botão Novo Cliente
                    Button(
                        onClick = onAddClienteClick,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = DarkBackground,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Novo Cliente",
                            color = DarkBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // 3. Cards da Imagem 1 (com a alteração no Card de Vendas Hoje)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card Lucro Hoje
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceDark
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "LUCRO HOJE",
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = AccentGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = lucroHoje,
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = variacaoLucro,
                                color = AccentGreen,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Card Vendas Hoje (Com alteração: Comparativo com o mês anterior no lugar da Meta)
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceDark
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "VENDAS HOJE",
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.ShoppingBag,
                                    contentDescription = null,
                                    tint = AccentBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$vendasHoje unidades",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            // Alteração feita conforme pedido: Comparativo de vendas vs mês anterior
                            Text(
                                text = variacaoVendasMesAnterior,
                                color = AccentBlue,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // 4. Barra de Pesquisa (Foco exclusivo por Nome)
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Buscar por nome...",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Pesquisar",
                            tint = TextSecondary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            // 5. Cards de Clientes (Idênticos ao Layout da Imagem 3)
            items(clientesFiltrados) { cliente ->
                ClienteCardImagem3(
                    cliente = cliente,
                    onDeleteClick = { onDeleteCliente(cliente) }
                )
            }
        }
    }
}

// --- CARD DE CLIENTE ESTILO IMAGEM 3 ---
@Composable
fun ClienteCardImagem3(
    cliente: ClienteModel,
    onDeleteClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        color = SurfaceCard
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Circular com Iniciais
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(SurfaceDark),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cliente.iniciais,
                    color = AccentBlue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Informações do Cliente (Nome, Status Tag e Última Compra)
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = cliente.nome,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Badge/Tag de Status
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AccentGreenContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = cliente.status,
                            color = AccentGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Última compra: ${cliente.ultimaCompra}",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Ações da Direita (Lixeira e Seta de navegação)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Excluir Cliente",
                    tint = TextSecondary.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onDeleteClick() }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Abrir Detalhes",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true, device = "id:pixel_7")
@Composable
fun TelaClientesCustomPreview() {
    MaterialTheme {
        TelaClientesCustom()
    }
}