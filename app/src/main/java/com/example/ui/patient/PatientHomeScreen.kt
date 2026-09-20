package com.example.ui.patient

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BalancePositiveGreen
import com.example.ui.theme.BalancePositiveLight
import com.example.ui.theme.IncidentRed
import com.example.ui.theme.IncidentRedDark
import com.example.ui.theme.IncidentRedLight
import com.example.ui.theme.MorroAmber
import com.example.ui.theme.MorroAmberLight
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientHomeScreen(
    viewModel: GradivaViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val record = uiState.patientRecord
    val user = uiState.currentUser
    val snackbarHostState = remember { SnackbarHostState() }

    val presentCount = uiState.patientAttendances.count { it.status == "PRESENTE" }
    val totalLectures = uiState.patientAttendances.size
    val rate = if (totalLectures > 0) (presentCount * 100) / totalLectures else 100

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
                            text = "Portal do Paciente • Acompanhamento Terapêutico",
                            style = MaterialTheme.typography.bodySmall,
                            color = MorroGreenLight
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("patient_logout_btn")
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // PATIENT PROFILE & ROOM SUMMARY
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MorroGreenDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_patient_profile")
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(MorroGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user?.name ?: record?.patientName ?: "Beatriz Helena de Souza",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Prontuário: ${user?.recordId ?: record?.recordId ?: "EMG-2026-4081"}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MorroGreenLight
                            )
                            Text(
                                text = "Quarto 12 • Ala Terapêutica Araucária",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "24 Dias", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = "Permanência", fontSize = 10.sp, color = MorroGreenLight)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Fase 2", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = "Ressignificação", fontSize = 10.sp, color = MorroGreenLight)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Estável", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81C784))
                            Text(text = "Evolução", fontSize = 10.sp, color = MorroGreenLight)
                        }
                    }
                }
            }

            // FEATURE 1: PATIENT WALLET & CANTINA BALANCE CARD
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MorroSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MorroBorder)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("patient_wallet_summary_card")
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MorroGoldLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = MorroGold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Conta do Paciente & Cantina",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MorroGreenDark
                                )
                                Text(
                                    text = "Saldo depositado pela família para despesas",
                                    fontSize = 11.sp,
                                    color = MorroTextMuted
                                )
                            }
                        }

                        Text(
                            text = "R$ %.2f".format(Locale.GERMANY, uiState.patientBalance),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BalancePositiveGreen
                        )
                    }

                    // Breakdown preview
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MorroBackground)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Depositado: R$ %.2f".format(Locale.GERMANY, uiState.totalDeposits),
                            fontSize = 11.sp,
                            color = MorroTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Gastos: R$ %.2f".format(Locale.GERMANY, uiState.totalExpenses),
                            fontSize = 11.sp,
                            color = MorroAmber,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = { viewModel.navigateTo(GradivaScreen.PatientWallet) },
                        colors = ButtonDefaults.buttonColors(containerColor = MorroGreenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_open_wallet")
                    ) {
                        Text("Ver Extrato Completo & Lançamentos", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // FEATURE 2: LECTURES & WORKSHOPS CARD
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MorroSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MorroBorder)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("patient_lectures_summary_card")
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MorroGreenLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AssignmentTurnedIn,
                                    contentDescription = null,
                                    tint = MorroGreenPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Palestras & Grupos Terapêuticos",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MorroGreenDark
                                )
                                Text(
                                    text = "Acompanhamento da frequência às atividades",
                                    fontSize = 11.sp,
                                    color = MorroTextMuted
                                )
                            }
                        }

                        Text(
                            text = "$rate% assíduo",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MorroGreenPrimary
                        )
                    }

                    // Attendance summary tag
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(BalancePositiveLight)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = BalancePositiveGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${uiState.patientAttendances.size} palestras avaliadas pela equipe psicológica",
                            fontSize = 12.sp,
                            color = BalancePositiveGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.navigateTo(GradivaScreen.PatientLectures) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_open_lectures")
                    ) {
                        Text("Ver Programação & Minhas Presenças", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // FEATURE 3: INTERCORRÊNCIAS & CLÍNICA
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MorroSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MorroBorder)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("patient_intercorrencias_summary_card")
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MorroAmberLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Healing,
                                    contentDescription = null,
                                    tint = MorroAmber,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Intercorrências & Ocorrências",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MorroGreenDark
                                )
                                Text(
                                    text = "Registros e condutas de acolhimento em crise",
                                    fontSize = 11.sp,
                                    color = MorroTextMuted
                                )
                            }
                        }

                        Text(
                            text = "${uiState.patientIntercorrencias.size} registros",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MorroTextSecondary
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.navigateTo(GradivaScreen.PatientIntercorrencias) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_open_intercorrencias")
                    ) {
                        Text("Ver Histórico de Intercorrências Clínicas", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // SEVERE ALLERGY WARNING
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = IncidentRedLight),
                modifier = Modifier.border(1.5.dp, IncidentRed, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alerta",
                            tint = IncidentRed,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Alergias Severas / Risco de Choque",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = IncidentRedDark
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = record?.severeAllergies ?: "HALOPERIDOL (Distonia severa) | DIPIRONA (Choque anafilático)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IncidentRedDark,
                        lineHeight = 18.sp
                    )
                }
            }

            // PSYCHOLOGICAL & CLINICAL TEAM OF REFERENCE
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MorroSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MorroBorder))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = MorroGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Equipe Terapêutica de Referência",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MorroGreenDark
                        )
                    }

                    // Psicóloga
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Dra. Camila Rodrigues",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MorroTextPrimary
                            )
                            Text(
                                text = "Psicóloga Clínica • CRP 06/142980",
                                fontSize = 12.sp,
                                color = MorroTextSecondary
                            )
                        }
                        IconButton(
                            onClick = { dialNumber(context, "(11) 98877-6655") },
                            modifier = Modifier.testTag("btn_call_psychologist")
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = "Ligar Psicóloga", tint = MorroGreenPrimary)
                        }
                    }

                    // Médico Psiquiatra
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = record?.attendingDoctorName ?: "Dr. Alexandre Castro",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MorroTextPrimary
                            )
                            Text(
                                text = "Médico Psiquiatra • ${record?.attendingDoctorCrm ?: "CRM-SP 148.290"}",
                                fontSize = 12.sp,
                                color = MorroTextSecondary
                            )
                        }
                        IconButton(
                            onClick = { dialNumber(context, record?.attendingDoctorPhone ?: "(11) 99123-8877") },
                            modifier = Modifier.testTag("patient_dial_doc_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = "Ligar Médico", tint = MorroGreenPrimary)
                        }
                    }

                    // Contato Familiar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = record?.familyContactName ?: "Mariana de Souza",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MorroTextPrimary
                            )
                            Text(
                                text = "${record?.familyContactRelationship ?: "Mãe / Curadora"} • ${record?.familyContactPhone ?: "(11) 98765-4321"}",
                                fontSize = 12.sp,
                                color = MorroTextSecondary
                            )
                        }
                        IconButton(
                            onClick = { dialNumber(context, record?.familyContactPhone ?: "(11) 98765-4321") },
                            modifier = Modifier.testTag("patient_dial_mother_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = "Ligar Família", tint = MorroGreenPrimary)
                        }
                    }
                }
            }

            // HOSPITAL MORRO GRANDE CONTACT
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { dialNumber(context, "(11) 4617-8000") },
                    colors = ButtonDefaults.buttonColors(containerColor = MorroGreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_call_morro_grande")
                ) {
                    Icon(imageVector = Icons.Default.LocalHospital, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Central Estância Morro Grande: (11) 4617-8000", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            // LGPD CONSENT AUDIT
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MorroSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MorroBorder))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MorroGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Auditoria e Prontuário Protegidos (LGPD)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MorroGreenDark
                        )
                    }
                    Text(
                        text = "Dados e evolução do paciente sob sigilo médico-hospitalar conforme Artigo 11 da LGPD e código de ética profissional. Estância Morro Grande.",
                        fontSize = 11.sp,
                        color = MorroTextSecondary
                    )
                }
            }
        }
    }
}

private fun dialNumber(context: Context, phone: String) {
    val clean = phone.filter { it.isDigit() || it == '+' }
    if (clean.isNotEmpty()) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$clean")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}

