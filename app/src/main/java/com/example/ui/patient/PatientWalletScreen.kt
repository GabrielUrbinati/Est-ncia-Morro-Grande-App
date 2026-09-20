package com.example.ui.patient

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SmokingRooms
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PatientWalletTransactionEntity
import com.example.ui.theme.BalancePositiveGreen
import com.example.ui.theme.BalancePositiveLight
import com.example.ui.theme.ExpenseOrange
import com.example.ui.theme.ExpenseOrangeLight
import com.example.ui.theme.MorroBackground
import com.example.ui.theme.MorroBorder
import com.example.ui.theme.MorroGold
import com.example.ui.theme.MorroGoldLight
import com.example.ui.theme.MorroGreenDark
import com.example.ui.theme.MorroGreenLight
import com.example.ui.theme.MorroGreenPrimary
import com.example.ui.theme.MorroSurface
import com.example.ui.theme.MorroTextMuted
import com.example.ui.theme.MorroTextPrimary
import com.example.ui.theme.MorroTextSecondary
import com.example.ui.viewmodel.GradivaScreen
import com.example.ui.viewmodel.GradivaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientWalletScreen(
    viewModel: GradivaViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val patient = uiState.currentUser

    var selectedFilter by remember { mutableStateOf("TODOS") }
    var showDepositDialog by remember { mutableStateOf(false) }
    var showExpenseDialog by remember { mutableStateOf(false) }
    var showOrderDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Conta & Saldo do Paciente",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Estância Morro Grande • Cantina & Despesas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MorroGreenLight
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(GradivaScreen.PatientHome) },
                        modifier = Modifier.testTag("btn_back_from_wallet")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MorroGreenDark
                )
            )
        },
        containerColor = MorroBackground,
        modifier = modifier
    ) { paddingValues ->
        val filteredList = when (selectedFilter) {
            "DEPOSITOS" -> uiState.patientTransactions.filter { it.type == "DEPOSITO" }
            "MERCADO" -> uiState.patientTransactions.filter { it.category == "MERCADO" }
            "CIGARRO" -> uiState.patientTransactions.filter { it.category == "CIGARRO" }
            "CANTINA" -> uiState.patientTransactions.filter { it.category == "CANTINA" }
            "HIGIENE" -> uiState.patientTransactions.filter { it.category == "HIGIENE_PESSOAL" || it.category == "BARBEARIA" || it.category == "FARMACIA" }
            else -> uiState.patientTransactions
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Main Balance Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_patient_balance"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MorroGreenDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "SALDO DISPONÍVEL",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = MorroGreenLight
                                )
                                Text(
                                    text = "R$ %.2f".format(Locale.GERMANY, uiState.patientBalance),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MorroGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        // Breakdown row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF81C784),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Depósitos Família",
                                        fontSize = 11.sp,
                                        color = MorroGreenLight
                                    )
                                }
                                Text(
                                    text = "+ R$ %.2f".format(Locale.GERMANY, uiState.totalDeposits),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.RemoveCircleOutline,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB74D),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Total Gasto",
                                        fontSize = 11.sp,
                                        color = MorroGreenLight
                                    )
                                }
                                Text(
                                    text = "- R$ %.2f".format(Locale.GERMANY, uiState.totalExpenses),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD180)
                                )
                            }
                        }

                        // Action Buttons: Deposits, Expenses, Market/Cigarette Orders
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showDepositDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MorroGold),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("btn_add_family_deposit")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Depósito Família", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { showExpenseDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.2f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("btn_add_cantina_expense")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalCafe,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Lançar Gasto", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Prominent Order Button: Pedido à Equipe (Mercado / Cigarro)
                        Button(
                            onClick = { showOrderDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MorroGreenPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("btn_order_market_cigar_team")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Fazer Pedido à Equipe (Mercado / Cigarro / Cantina)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Info notice on patient purchases and family deposits
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MorroGoldLight),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MorroGold.copy(alpha = 0.3f)))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MorroGold,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "O paciente não circula fora da estância. Todos os gastos são intermediados pela equipe mediante pedidos supervisionados (mercado na cidade, cigarro controlado, cantina e itens de higiene), com prestação de contas integral e saldo transferido pela família.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MorroTextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Filter Chips
            item {
                Text(
                    text = "Extrato Detalhado de Lançamentos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MorroGreenDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf(
                        "TODOS" to "Todos os Lançamentos",
                        "DEPOSITOS" to "Depósitos Família",
                        "MERCADO" to "Pedidos de Mercado",
                        "CIGARRO" to "Cigarro / Tabacaria",
                        "CANTINA" to "Cantina & Lanches",
                        "HIGIENE" to "Higiene Pessoal"
                    )
                    items(filters) { (key, label) ->
                        FilterChip(
                            selected = selectedFilter == key,
                            onClick = { selectedFilter = key },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MorroGreenPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_$key")
                        )
                    }
                }
            }

            // Transactions List
            if (filteredList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MorroSurface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhum lançamento nesta categoria.",
                                color = MorroTextMuted,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredList) { tx ->
                    TransactionItemCard(transaction = tx)
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal: Informar Depósito da Família
    if (showDepositDialog && patient != null) {
        var depositAmountStr by remember { mutableStateOf("") }
        var depositFamilyName by remember { mutableStateOf("Mariana de Souza (Mãe)") }
        var depositMethod by remember { mutableStateOf("PIX Bancário") }

        AlertDialog(
            onDismissRequest = { showDepositDialog = false },
            title = {
                Text("Registrar Depósito da Família", fontWeight = FontWeight.Bold, color = MorroGreenDark)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Adicione saldo à conta de conveniência do paciente ${patient.name} na Estância Morro Grande.",
                        fontSize = 12.sp,
                        color = MorroTextSecondary
                    )
                    OutlinedTextField(
                        value = depositAmountStr,
                        onValueChange = { depositAmountStr = it },
                        label = { Text("Valor (R$)") },
                        placeholder = { Text("Ex: 150.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_deposit_amount")
                    )
                    OutlinedTextField(
                        value = depositFamilyName,
                        onValueChange = { depositFamilyName = it },
                        label = { Text("Familiar Depositante") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_deposit_family")
                    )
                    OutlinedTextField(
                        value = depositMethod,
                        onValueChange = { depositMethod = it },
                        label = { Text("Forma de Envio") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = depositAmountStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            viewModel.recordWalletTransaction(
                                patientUserId = patient.id,
                                patientName = patient.name,
                                type = "DEPOSITO",
                                amount = amount,
                                description = "Depósito via $depositMethod por $depositFamilyName",
                                category = "DEPOSITO_FAMILIA"
                            )
                            showDepositDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MorroGreenPrimary),
                    modifier = Modifier.testTag("btn_confirm_deposit")
                ) {
                    Text("Confirmar Depósito")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDepositDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal: Lançar Débito / Compra na Cantina
    if (showExpenseDialog && patient != null) {
        var expenseAmountStr by remember { mutableStateOf("") }
        var expenseDescription by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf("CANTINA") }

        AlertDialog(
            onDismissRequest = { showExpenseDialog = false },
            title = {
                Text("Lançar Débito / Despesa", fontWeight = FontWeight.Bold, color = MorroGreenDark)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Descreva os itens consumidos ou debitados do saldo do paciente.",
                        fontSize = 12.sp,
                        color = MorroTextSecondary
                    )
                    OutlinedTextField(
                        value = expenseAmountStr,
                        onValueChange = { expenseAmountStr = it },
                        label = { Text("Valor Total (R$)") },
                        placeholder = { Text("Ex: 18.50") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_expense_amount")
                    )
                    OutlinedTextField(
                        value = expenseDescription,
                        onValueChange = { expenseDescription = it },
                        label = { Text("Descrição detalhada") },
                        placeholder = { Text("Ex: Suco de laranja, água e torrada") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_expense_desc")
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCategory == "CANTINA",
                                onClick = { selectedCategory = "CANTINA" },
                                label = { Text("Cantina", fontSize = 11.sp) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedCategory == "MERCADO",
                                onClick = { selectedCategory = "MERCADO" },
                                label = { Text("Mercado", fontSize = 11.sp) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedCategory == "CIGARRO",
                                onClick = { selectedCategory = "CIGARRO" },
                                label = { Text("Cigarro", fontSize = 11.sp) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedCategory == "HIGIENE_PESSOAL",
                                onClick = { selectedCategory = "HIGIENE_PESSOAL" },
                                label = { Text("Higiene", fontSize = 11.sp) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedCategory == "BARBEARIA",
                                onClick = { selectedCategory = "BARBEARIA" },
                                label = { Text("Cuidados", fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = expenseAmountStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                        if (amount > 0 && expenseDescription.isNotBlank()) {
                            viewModel.recordWalletTransaction(
                                patientUserId = patient.id,
                                patientName = patient.name,
                                type = "GASTO",
                                amount = amount,
                                description = expenseDescription.trim(),
                                category = selectedCategory
                            )
                            showExpenseDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseOrange),
                    modifier = Modifier.testTag("btn_confirm_expense")
                ) {
                    Text("Lançar Débito")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExpenseDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal: Fazer Pedido de Mercado / Cigarro / Cantina à Equipe
    if (showOrderDialog && patient != null) {
        var orderAmountStr by remember { mutableStateOf("") }
        var orderDescription by remember { mutableStateOf("") }
        var selectedOrderCategory by remember { mutableStateOf("MERCADO") }

        AlertDialog(
            onDismissRequest = { showOrderDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = MorroGreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Pedido de Compras à Equipe",
                        fontWeight = FontWeight.Bold,
                        color = MorroGreenDark,
                        fontSize = 17.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Os pacientes não circulam fora da estância. Faça o pedido para que a equipe compre na cidade (mercado, tabacaria/cigarro, cantina ou itens de higiene).",
                        fontSize = 12.sp,
                        color = MorroTextSecondary,
                        lineHeight = 16.sp
                    )

                    // Category chips
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedOrderCategory == "MERCADO",
                                onClick = { selectedOrderCategory = "MERCADO" },
                                label = { Text("Mercado Cidade", fontSize = 11.sp) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedOrderCategory == "CIGARRO",
                                onClick = { selectedOrderCategory = "CIGARRO" },
                                label = { Text("Cigarro / Tabacaria", fontSize = 11.sp) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedOrderCategory == "CANTINA",
                                onClick = { selectedOrderCategory = "CANTINA" },
                                label = { Text("Cantina Interna", fontSize = 11.sp) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = selectedOrderCategory == "HIGIENE_PESSOAL",
                                onClick = { selectedOrderCategory = "HIGIENE_PESSOAL" },
                                label = { Text("Higiene Pessoal", fontSize = 11.sp) }
                            )
                        }
                    }

                    // Quick suggestion presets
                    Text(
                        text = "Sugestões rápidas de pedidos comuns:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MorroGreenDark
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = false,
                                onClick = {
                                    selectedOrderCategory = "CIGARRO"
                                    orderDescription = "1 Maço de cigarros (marca autorizada)"
                                    orderAmountStr = "15.00"
                                },
                                label = { Text("1x Cigarro (R$ 15)", fontSize = 10.sp) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = {
                                    selectedOrderCategory = "CIGARRO"
                                    orderDescription = "2 Maços de cigarros (controle semanal)"
                                    orderAmountStr = "30.00"
                                },
                                label = { Text("2x Cigarros (R$ 30)", fontSize = 10.sp) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = {
                                    selectedOrderCategory = "MERCADO"
                                    orderDescription = "Pedido mercado: Bolacha, achocolatado e café solúvel"
                                    orderAmountStr = "35.00"
                                },
                                label = { Text("Mercado Lanches (R$ 35)", fontSize = 10.sp) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = {
                                    selectedOrderCategory = "HIGIENE_PESSOAL"
                                    orderDescription = "Kit Higiene: Shampoo, sabonete e desodorante roll-on"
                                    orderAmountStr = "25.00"
                                },
                                label = { Text("Kit Higiene (R$ 25)", fontSize = 10.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = orderDescription,
                        onValueChange = { orderDescription = it },
                        label = { Text("Descrição do Pedido") },
                        placeholder = { Text("Ex: 2 maços de cigarro e 1 pacote de bolacha") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_order_desc")
                    )

                    OutlinedTextField(
                        value = orderAmountStr,
                        onValueChange = { orderAmountStr = it },
                        label = { Text("Valor Estimado ou Real (R$)") },
                        placeholder = { Text("Ex: 30.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_order_amount")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = orderAmountStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                        if (amount > 0 && orderDescription.isNotBlank()) {
                            viewModel.recordWalletTransaction(
                                patientUserId = patient.id,
                                patientName = patient.name,
                                type = "GASTO",
                                amount = amount,
                                description = orderDescription.trim(),
                                category = selectedOrderCategory
                            )
                            showOrderDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MorroGreenPrimary),
                    modifier = Modifier.testTag("btn_confirm_team_order")
                ) {
                    Text("Confirmar Pedido")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOrderDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun TransactionItemCard(
    transaction: PatientWalletTransactionEntity,
    modifier: Modifier = Modifier
) {
    val isDeposit = transaction.type == "DEPOSITO"
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(transaction.timestamp))

    val (categoryLabel, categoryColor) = when {
        isDeposit -> "Depósito Família" to BalancePositiveGreen
        transaction.category == "MERCADO" -> "Pedido Mercado" to MorroGreenDark
        transaction.category == "CIGARRO" -> "Cigarro / Tabacaria" to ExpenseOrange
        transaction.category == "CANTINA" -> "Cantina & Lanches" to MorroGold
        transaction.category == "HIGIENE_PESSOAL" -> "Higiene Pessoal" to MorroGreenPrimary
        else -> "Despesa Geral" to ExpenseOrange
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tx_item_${transaction.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MorroSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MorroBorder))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDeposit) BalancePositiveLight else ExpenseOrangeLight
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        isDeposit -> Icons.Default.Payments
                        transaction.category == "MERCADO" -> Icons.Default.ShoppingCart
                        transaction.category == "CIGARRO" -> Icons.Default.SmokingRooms
                        transaction.category == "CANTINA" -> Icons.Default.LocalCafe
                        transaction.category == "HIGIENE_PESSOAL" -> Icons.Default.ShoppingBag
                        else -> Icons.Default.Receipt
                    },
                    contentDescription = null,
                    tint = if (isDeposit) BalancePositiveGreen else ExpenseOrange,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(categoryColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = categoryLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MorroTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formattedDate,
                        fontSize = 11.sp,
                        color = MorroTextMuted
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${transaction.registeredBy}",
                        fontSize = 11.sp,
                        color = MorroGreenPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount
            Text(
                text = "${if (isDeposit) "+" else "-"} R$ %.2f".format(Locale.GERMANY, transaction.amount),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDeposit) BalancePositiveGreen else ExpenseOrange
            )
        }
    }
}
