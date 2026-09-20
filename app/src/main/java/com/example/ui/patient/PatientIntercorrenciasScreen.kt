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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.IntercorrenciaEntity
import com.example.ui.theme.BalancePositiveGreen
import com.example.ui.theme.BalancePositiveLight
import com.example.ui.theme.IncidentRed
import com.example.ui.theme.IncidentRedLight
import com.example.ui.theme.MorroAmber
import com.example.ui.theme.MorroAmberLight
import com.example.ui.theme.MorroBackground
import com.example.ui.theme.MorroBorder
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
fun PatientIntercorrenciasScreen(
    viewModel: GradivaViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val intercorrencias = uiState.patientIntercorrencias

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Histórico de Intercorrências",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Estância Morro Grande • Registro Clínico",
                            style = MaterialTheme.typography.bodySmall,
                            color = MorroGreenLight
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(GradivaScreen.PatientHome) },
                        modifier = Modifier.testTag("btn_back_from_intercorrencias")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MorroGreenDark)
            )
        },
        containerColor = MorroBackground,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Overview card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_intercorrencia_overview"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MorroGreenDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "STATUS DE ACOMPANHAMENTO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = MorroGreenLight
                                )
                                Text(
                                    text = "Quadro Estável e Monitorado",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MorroGreenPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Healing,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Text(
                            text = "Todas as intercorrências são acolhidas de forma humanizada pela equipe multidisciplinar com registro imediato de condutas e sinais vitais.",
                            fontSize = 12.sp,
                            color = MorroGreenLight,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Ocorrências & Acolhimentos Registrados (${intercorrencias.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MorroGreenDark
                )
            }

            if (intercorrencias.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MorroSurface)
                    ) {
                        Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Nenhuma intercorrência clínica registrada para o paciente.",
                                color = MorroTextMuted,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(intercorrencias) { item ->
                    IntercorrenciaCard(intercorrencia = item)
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun IntercorrenciaCard(
    intercorrencia: IntercorrenciaEntity,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(intercorrencia.timestamp))

    val severityColor = when (intercorrencia.severity.uppercase()) {
        "GRAVE" -> IncidentRed
        "MODERADA" -> MorroAmber
        else -> MorroGreenPrimary
    }
    val severityBg = when (intercorrencia.severity.uppercase()) {
        "GRAVE" -> IncidentRedLight
        "MODERADA" -> MorroAmberLight
        else -> MorroGreenLight
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("intercorrencia_item_${intercorrencia.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MorroSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MorroBorder))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Badge and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(severityBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ReportProblem,
                        contentDescription = null,
                        tint = severityColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Gravidade: ${intercorrencia.severity}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = severityColor
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BalancePositiveLight)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = BalancePositiveGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (intercorrencia.resolved) "Resolvida" else "Em Observação",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BalancePositiveGreen
                    )
                }
            }

            Text(
                text = intercorrencia.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MorroGreenDark
            )

            // Description
            Text(
                text = intercorrencia.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MorroTextPrimary,
                lineHeight = 18.sp
            )

            // Conduct Box
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MorroBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = MorroGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Conduta Terapêutica Adotada:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MorroGreenDark
                        )
                    }
                    Text(
                        text = intercorrencia.conductAdopted,
                        fontSize = 12.sp,
                        color = MorroTextSecondary,
                        lineHeight = 16.sp
                    )

                    if (intercorrencia.vitalSigns.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = IncidentRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sinais Vitais: ${intercorrencia.vitalSigns}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MorroTextPrimary
                            )
                        }
                    }
                }
            }

            // Footer: Responsible professional and timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = MorroGreenPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${intercorrencia.professionalRole}: ${intercorrencia.professionalName}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MorroTextPrimary
                    )
                }

                Text(
                    text = formattedDate,
                    fontSize = 11.sp,
                    color = MorroTextMuted
                )
            }
        }
    }
}
