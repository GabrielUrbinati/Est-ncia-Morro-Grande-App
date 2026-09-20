package com.example.ui.emergency

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MedicalRecordEntity
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.AlertAmberLight
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedDark
import com.example.ui.theme.EmergencyRedLight
import com.example.ui.theme.MorroBackground
import com.example.ui.theme.MorroBorder
import com.example.ui.theme.MorroGreenDark
import com.example.ui.theme.MorroGreenLight
import com.example.ui.theme.MorroGreenPrimary
import com.example.ui.viewmodel.GradivaScreen
import com.example.ui.viewmodel.GradivaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyQuickAccessScreen(
    viewModel: GradivaViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var searchInput by remember { mutableStateOf("GDV-2026-9042") }
    var displayedRecord by remember { mutableStateOf<MedicalRecordEntity?>(null) }

    LaunchedEffect(uiState.searchResults, searchInput) {
        val found = uiState.searchResults.firstOrNull {
            it.recordId.equals(searchInput.trim(), ignoreCase = true) ||
            it.patientName.contains(searchInput.trim(), ignoreCase = true)
        }
        displayedRecord = found ?: uiState.searchResults.firstOrNull()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Ficha de Emergência SOS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Acesso Rápido para Socorristas e SAMU",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(GradivaScreen.Login) },
                        modifier = Modifier.testTag("emergency_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.navigateTo(GradivaScreen.PatientNonVerbalCards) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("emergency_nonverbal_shortcut_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cartões Não-Verbal", fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MorroGreenDark)
            )
        },
        containerColor = MorroBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Emergency Search Bar
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Localizar Paciente em Surto / Crise",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MorroGreenDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchInput,
                        onValueChange = {
                            searchInput = it
                            viewModel.updateSearchQuery(it)
                        },
                        label = { Text("Código Morro Grande ou Nome do Paciente") },
                        placeholder = { Text("Ex: EMG-2026-4081 ou Beatriz") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        },
                        trailingIcon = {
                            Button(
                                onClick = { viewModel.updateSearchQuery(searchInput) },
                                colors = ButtonDefaults.buttonColors(containerColor = MorroGreenPrimary),
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .testTag("emergency_search_action_btn")
                            ) {
                                Text("Buscar", fontSize = 12.sp)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("emergency_search_input")
                    )
                }
            }

            displayedRecord?.let { record ->
                // Patient Card Header
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(MorroGreenPrimary),
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
                                    text = record.patientName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MorroGreenDark
                                )
                                Text(
                                    text = "Prontuário Estância Morro Grande: ${record.recordId}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MorroGreenPrimary
                                )
                                Text(
                                    text = "Tipo Sanguíneo: ${record.bloodType}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // CRITICAL ALLERGY ALERT (RED HIGHLIGHT)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmergencyRedLight),
                    modifier = Modifier.border(2.dp, EmergencyRed, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Alerta de Alergia",
                                tint = EmergencyRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ALERTA DE ALERGIAS GRAVES",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = EmergencyRedDark
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = record.severeAllergies,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyRedDark,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "ATENÇÃO EQUIPE: Não administrar neurolépticos ou fármacos sem checar o histórico acima para evitar distonia laríngea ou choque!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = EmergencyRedDark
                        )
                    }
                }

                // NON-VERBAL / PSYCHIATRIC SURTO ALERT
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AlertAmberLight),
                    modifier = Modifier.border(1.dp, AlertAmber, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = AlertAmber,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Condição em Crise: Mutismo e Desorientação",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AlertAmber
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = record.nonVerbalAssistiveNote,
                            fontSize = 13.sp,
                            color = MorroGreenDark
                        )
                    }
                }

                // RESCUE PROTOCOL
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Medication,
                                contentDescription = null,
                                tint = MorroGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Protocolo de Resgate Prescrito para Surto",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MorroGreenDark
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = record.rescueProtocolMedication,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MorroGreenPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Medicações de Uso Contínuo:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MorroGreenDark
                        )
                        Text(
                            text = record.continuousMedication,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // CALMING & HUMANIZED TECHNIQUES
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = MorroGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Técnicas de Desescalada & Acalmia",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MorroGreenDark
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Como Acalmar: ${record.calmingTechniques}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Gatilhos a Evitar: ${record.knownTriggers}",
                            fontSize = 13.sp,
                            color = EmergencyRedDark
                        )
                    }
                }

                // QUICK DIAL BUTTONS
                Text(
                    text = "Discagem Rápida de Emergência",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MorroGreenDark
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { dialPhone(context, "192") },
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dial_samu_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SAMU 192", fontSize = 13.sp)
                    }

                    Button(
                        onClick = { dialPhone(context, record.emergencyPhoneHospital) },
                        colors = ButtonDefaults.buttonColors(containerColor = MorroGreenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("dial_morro_btn")
                    ) {
                        Icon(imageVector = Icons.Default.LocalHospital, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Plantão Estância", fontSize = 13.sp)
                    }
                }

                // Family & Attending Doctor contacts
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = record.familyContactName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${record.familyContactRelationship} • ${record.familyContactPhone}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { dialPhone(context, record.familyContactPhone) },
                                modifier = Modifier.testTag("dial_family_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Call, contentDescription = "Ligar", tint = MorroGreenPrimary)
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = record.attendingDoctorName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${record.attendingDoctorCrm} • ${record.attendingDoctorPhone}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { dialPhone(context, record.attendingDoctorPhone) },
                                modifier = Modifier.testTag("dial_doctor_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Call, contentDescription = "Ligar Médico", tint = MorroGreenPrimary)
                            }
                        }
                    }
                }
            } ?: run {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CrisisAlert,
                            contentDescription = null,
                            tint = AlertAmber,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Nenhum prontuário encontrado com o termo pesquisado.",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tente buscar por 'Beatriz' ou 'GDV-2026-9042'.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun dialPhone(context: Context, phoneNumber: String) {
    val clean = phoneNumber.filter { it.isDigit() || it == '+' }
    if (clean.isNotEmpty()) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$clean")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
