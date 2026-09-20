package com.example.ui.professional

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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BalancePositiveGreen
import com.example.ui.theme.BalancePositiveLight
import com.example.ui.theme.ExpenseOrange
import com.example.ui.theme.ExpenseOrangeLight
import com.example.ui.theme.IncidentRed
import com.example.ui.theme.IncidentRedDark
import com.example.ui.theme.IncidentRedLight
import com.example.ui.theme.MorroAmber
import com.example.ui.theme.MorroBackground
import com.example.ui.theme.MorroBorder
import com.example.ui.theme.MorroGold
import com.example.ui.theme.MorroGreenDark
import com.example.ui.theme.MorroGreenLight
import com.example.ui.theme.MorroGreenPrimary
import com.example.ui.theme.MorroSurface
import com.example.ui.theme.MorroTextMuted
import com.example.ui.theme.MorroTextPrimary
import com.example.ui.theme.MorroTextSecondary
import com.example.ui.viewmodel.GradivaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalHomeScreen(
    viewModel: GradivaViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.currentUser
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Prontuários", "Palestras", "Intercorrências", "Saldo & Cantina")

    var searchQuery by remember { mutableStateOf("") }

    // Dialog: Nova Intercorrência
    var showIntercorrenciaDialog by remember { mutableStateOf(false) }
    var intercorrenciaTitle by remember { mutableStateOf("Agitação / Ansiedade Noturna") }
    var intercorrenciaSeverity by remember { mutableStateOf("MODERADA") }
    var intercorrenciaConduct by remember { mutableStateOf("Acolhimento psicológico no quarto e técnica de respiração diafragmática") }
    var intercorrenciaVitals by remember { mutableStateOf("PA: 125/85 mmHg | FC: 86 bpm") }

    // Dialog: Lançamento de Saldo / Cantina
    var showWalletDialog by remember { mutableStateOf(false) }
    var walletType by remember { mutableStateOf("DEPOSITO") }
    var walletAmount by remember { mutableStateOf("") }
    var walletDescription by remember { mutableStateOf("") }
    var walletCategory by remember { mutableStateOf("Depósito Familiar") }

    // Dialog: Marcar Frequência de Palestra
    var showAttendanceDialog by remember { mutableStateOf(false) }
    var selectedLectureId by remember { mutableStateOf(1L) }
    var selectedLectureTitle by remember { mutableStateOf("Manejo da Ansiedade e Emoções") }
    var attendanceStatus by remember { mutableStateOf("PRESENTE") }
    var attendanceNotes by remember { mutableStateOf("Participou ativamente do debate e das dinâmicas em grupo.") }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Estância Morro Grande",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Portal Terapêutico • Acesso Multidisciplinar",
                            style = MaterialTheme.typography.bodySmall,
                            color = MorroGreenLight
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("prof_logout_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sair",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MorroGreenDark)
            )
        },
        containerColor = MorroBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // PROFESSIONAL CREDENTIALS HEADER
            Card(
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = MorroGreenPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MorroGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user?.name ?: "Dra. Camila Rodrigues",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${user?.profession ?: "Psicóloga"} • ${user?.councilType ?: "CRP"}-${user?.councilState ?: "SP"} ${user?.councilNumber ?: "06/142980"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MorroGreenLight
                        )
                    }
                }
            }

            // NAVIGATION TABS
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MorroSurface,
                contentColor = MorroGreenDark,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MorroGreenPrimary,
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    )
                }
            }

            // TAB CONTENTS
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        // TAB 0: PRONTUÁRIOS
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MorroSurface),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MorroBorder))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Buscar Paciente no Hospital",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MorroGreenDark
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = {
                                        searchQuery = it
                                        viewModel.updateSearchQuery(it)
                                    },
                                    label = { Text("Nome ou Código EMG (ex: EMG-2026-4081)") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MorroGreenPrimary) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Text(
                            text = "Pacientes Internados:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MorroGreenDark
                        )

                        uiState.searchResults.forEach { patientRecord ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MorroSurface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MorroBorder)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column {
                                            Text(
                                                text = patientRecord.patientName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = MorroGreenDark
                                            )
                                            Text(
                                                text = "Prontuário: ${patientRecord.recordId} • Tipo: ${patientRecord.bloodType}",
                                                fontSize = 12.sp,
                                                color = MorroGreenPrimary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                selectedLectureTitle = "Manejo da Ansiedade e Emoções"
                                                showAttendanceDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MorroGreenPrimary),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Frequência", fontSize = 11.sp)
                                        }
                                    }

                                    // Severe allergy warning
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(IncidentRedLight)
                                            .border(1.dp, IncidentRed, RoundedCornerShape(8.dp))
                                            .padding(8.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = IncidentRed,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Alergias: ${patientRecord.severeAllergies}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = IncidentRedDark
                                            )
                                        }
                                    }

                                    Text(
                                        text = "Diagnóstico: ${patientRecord.psychiatricDiagnoses}",
                                        fontSize = 12.sp,
                                        color = MorroTextPrimary
                                    )
                                    Text(
                                        text = "Gatilhos Conhecidos: ${patientRecord.knownTriggers}",
                                        fontSize = 11.sp,
                                        color = MorroAmber
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        // TAB 1: PALESTRAS TERAPÊUTICAS & OFICINAS
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Palestras & Dinâmicas de Grupo",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MorroGreenDark
                            )
                            Button(
                                onClick = { showAttendanceDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MorroGreenPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Lançar Presença", fontSize = 12.sp)
                            }
                        }

                        // Lectures list
                        uiState.allLectures.forEach { lecture ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MorroSurface),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MorroBorder))
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = lecture.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MorroGreenDark
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(MorroGreenLight)
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = lecture.scheduleTime,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MorroGreenPrimary
                                            )
                                        }
                                    }
                                    Text(
                                        text = lecture.description,
                                        fontSize = 12.sp,
                                        color = MorroTextSecondary
                                    )
                                    Text(
                                        text = "Responsável: ${lecture.speaker} • ${lecture.category} • Local: ${lecture.location}",
                                        fontSize = 11.sp,
                                        color = MorroTextMuted
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Presenças e Participações Avaliadas:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MorroGreenDark
                        )

                        uiState.patientAttendances.forEach { att ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MorroSurface),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MorroBorder))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = "${att.patientName} • ${att.lectureTitle}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MorroGreenDark)
                                        Text(text = "Data: ${att.dateStr} | Por: ${att.registeredBy}", fontSize = 11.sp, color = MorroTextMuted)
                                        if (att.notes.isNotEmpty()) {
                                            Text(text = "Observação: ${att.notes}", fontSize = 11.sp, color = MorroTextSecondary)
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (att.status == "PRESENTE") BalancePositiveLight else ExpenseOrangeLight)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = att.status,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (att.status == "PRESENTE") BalancePositiveGreen else ExpenseOrange
                                        )
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // TAB 2: INTERCORRÊNCIAS CLÍNICAS
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Intercorrências Clínicas & Crises",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MorroGreenDark
                            )
                            Button(
                                onClick = { showIntercorrenciaDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = IncidentRed),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Registrar Crise", fontSize = 12.sp)
                            }
                        }

                        uiState.allIntercorrencias.forEach { inc ->
                            val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(inc.timestamp))
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MorroSurface),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MorroBorder))
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = inc.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = IncidentRedDark)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(IncidentRedLight)
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(text = inc.severity, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = IncidentRed)
                                        }
                                    }
                                    Text(text = "Paciente: ${inc.patientName} • $dateStr", fontSize = 11.sp, color = MorroTextMuted)
                                    Text(text = "Conduta: ${inc.conductAdopted}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MorroGreenDark)
                                    Text(text = "Sinais: ${inc.vitalSigns} | Profissional: ${inc.professionalName} (${inc.professionalRole})", fontSize = 11.sp, color = MorroTextSecondary)
                                }
                            }
                        }
                    }

                    3 -> {
                        // TAB 3: SALDO DO PACIENTE & CANTINA
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Controle Financeiro da Cantina",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MorroGreenDark
                            )
                            Button(
                                onClick = { showWalletDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MorroGreenPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Lançar Transação", fontSize = 12.sp)
                            }
                        }

                        // Summary of current patient balance
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MorroGreenDark)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = "Saldo Atual de Beatriz Helena", color = MorroGreenLight, fontSize = 12.sp)
                                Text(
                                    text = "R$ %.2f".format(Locale.GERMANY, uiState.patientBalance),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Depósitos: R$ %.2f".format(Locale.GERMANY, uiState.totalDeposits), color = MorroGreenLight, fontSize = 11.sp)
                                    Text(text = "Gastos Cantina: R$ %.2f".format(Locale.GERMANY, uiState.totalExpenses), color = Color(0xFFFFCC80), fontSize = 11.sp)
                                }
                            }
                        }

                        Text(
                            text = "Histórico de Transações:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MorroGreenDark
                        )

                        uiState.patientTransactions.forEach { tx ->
                            val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(tx.timestamp))
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MorroSurface),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MorroBorder))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = tx.description, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MorroGreenDark)
                                        Text(text = "${tx.category} • $dateStr", fontSize = 11.sp, color = MorroTextMuted)
                                        Text(text = "Registrado por: ${tx.registeredBy}", fontSize = 10.sp, color = MorroTextSecondary)
                                    }
                                    Text(
                                        text = "${if (tx.type == "DEPOSITO") "+" else "-"} R$ %.2f".format(Locale.GERMANY, tx.amount),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (tx.type == "DEPOSITO") BalancePositiveGreen else ExpenseOrange
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // DIALOG 1: REGISTRAR INTERCORRÊNCIA
    if (showIntercorrenciaDialog) {
        AlertDialog(
            onDismissRequest = { showIntercorrenciaDialog = false },
            title = { Text("Registrar Intercorrência Clínica", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = intercorrenciaTitle,
                        onValueChange = { intercorrenciaTitle = it },
                        label = { Text("Tipo / Título do Episódio") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = intercorrenciaSeverity,
                        onValueChange = { intercorrenciaSeverity = it },
                        label = { Text("Gravidade (LEVE, MODERADA, ALTA)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = intercorrenciaConduct,
                        onValueChange = { intercorrenciaConduct = it },
                        label = { Text("Conduta Adotada (Acolhimento, etc.)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = intercorrenciaVitals,
                        onValueChange = { intercorrenciaVitals = it },
                        label = { Text("Sinais Vitais e Observações") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.recordIntercorrencia(
                            patientUserId = 1L,
                            patientName = "Beatriz Helena de Souza",
                            title = intercorrenciaTitle,
                            severity = intercorrenciaSeverity,
                            description = "Intercorrência registrada pela equipe clínica.",
                            conductAdopted = intercorrenciaConduct,
                            vitalSigns = intercorrenciaVitals
                        )
                        showIntercorrenciaDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MorroGreenPrimary)
                ) {
                    Text("Salvar Registro")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showIntercorrenciaDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DIALOG 2: LANÇAR TRANSAÇÃO (DEPÓSITO OU GASTO)
    if (showWalletDialog) {
        AlertDialog(
            onDismissRequest = { showWalletDialog = false },
            title = { Text("Lançar Transação de Cantina", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                walletType = "DEPOSITO"
                                walletCategory = "Depósito Familiar"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (walletType == "DEPOSITO") BalancePositiveGreen else MorroSurface,
                                contentColor = if (walletType == "DEPOSITO") Color.White else MorroGreenDark
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Depósito (+)")
                        }
                        Button(
                            onClick = {
                                walletType = "GASTO"
                                walletCategory = "Consumo Cantina"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (walletType == "GASTO") ExpenseOrange else MorroSurface,
                                contentColor = if (walletType == "GASTO") Color.White else MorroGreenDark
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Gasto (-)")
                        }
                    }

                    OutlinedTextField(
                        value = walletAmount,
                        onValueChange = { walletAmount = it },
                        label = { Text("Valor (R$)") },
                        placeholder = { Text("ex: 50.00") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = walletDescription,
                        onValueChange = { walletDescription = it },
                        label = { Text("Descrição do Item / Depósito") },
                        placeholder = { Text("ex: Lanche e suco na cantina") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = walletAmount.toDoubleOrNull() ?: 0.0
                        viewModel.recordWalletTransaction(
                            patientUserId = 1L,
                            patientName = "Beatriz Helena de Souza",
                            type = walletType,
                            amount = parsed,
                            description = walletDescription.ifEmpty { if (walletType == "DEPOSITO") "Depósito da Família" else "Consumo na cantina" },
                            category = walletCategory
                        )
                        showWalletDialog = false
                        walletAmount = ""
                        walletDescription = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MorroGreenPrimary)
                ) {
                    Text("Lançar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showWalletDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DIALOG 3: LANÇAR FREQUÊNCIA DE PALESTRA
    if (showAttendanceDialog) {
        AlertDialog(
            onDismissRequest = { showAttendanceDialog = false },
            title = { Text("Lançar Frequência de Palestra", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Paciente: Beatriz Helena de Souza (EMG-2026-4081)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MorroGreenDark)

                    OutlinedTextField(
                        value = selectedLectureTitle,
                        onValueChange = { selectedLectureTitle = it },
                        label = { Text("Palestra / Oficina") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { attendanceStatus = "PRESENTE" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (attendanceStatus == "PRESENTE") BalancePositiveGreen else MorroSurface,
                                contentColor = if (attendanceStatus == "PRESENTE") Color.White else MorroGreenDark
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Presente", fontSize = 12.sp)
                        }
                        Button(
                            onClick = { attendanceStatus = "FALTOU_JUSTIFICADO" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (attendanceStatus == "FALTOU_JUSTIFICADO") MorroAmber else MorroSurface,
                                contentColor = if (attendanceStatus == "FALTOU_JUSTIFICADO") Color.White else MorroGreenDark
                            ),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Text("Justificada", fontSize = 12.sp)
                        }
                    }

                    OutlinedTextField(
                        value = attendanceNotes,
                        onValueChange = { attendanceNotes = it },
                        label = { Text("Observação Psicológica") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                        viewModel.recordLectureAttendance(
                            patientUserId = 1L,
                            patientName = "Beatriz Helena de Souza",
                            lectureId = selectedLectureId,
                            lectureTitle = selectedLectureTitle,
                            dateStr = today,
                            status = attendanceStatus,
                            notes = attendanceNotes
                        )
                        showAttendanceDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MorroGreenPrimary)
                ) {
                    Text("Salvar Presença")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAttendanceDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
