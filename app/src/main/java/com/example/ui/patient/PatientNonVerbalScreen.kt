package com.example.ui.patient

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.GradivaNavy
import com.example.ui.theme.GradivaTealPrimary
import com.example.ui.viewmodel.GradivaScreen
import com.example.ui.viewmodel.GradivaViewModel

data class AssistiveCardItem(
    val id: String,
    val title: String,
    val message: String,
    val icon: ImageVector,
    val color: Color,
    val emergencyLevel: String = "INFO"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientNonVerbalScreen(
    viewModel: GradivaViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var selectedItem by remember { mutableStateOf<AssistiveCardItem?>(null) }

    val cards = listOf(
        AssistiveCardItem(
            id = "mutism",
            title = "NÃO CONSIGO FALAR AGORA",
            message = "Estou em crise de mutismo / paralisia da fala. Eu entendo o que você diz, por favor tenha paciência e não me force a falar.",
            icon = Icons.AutoMirrored.Filled.VolumeOff,
            color = EmergencyRed,
            emergencyLevel = "CRITICAL"
        ),
        AssistiveCardItem(
            id = "hallucination",
            title = "ESTOU ASSUSTADO(A) COM ALUCINAÇÕES",
            message = "Estou vendo ou ouvindo coisas que me causam pânico. Por favor, fale com calma, em voz baixa e me diga que estou seguro(a).",
            icon = Icons.Default.Psychology,
            color = AlertAmber,
            emergencyLevel = "WARNING"
        ),
        AssistiveCardItem(
            id = "no_touch",
            title = "POR FAVOR, NÃO ME TOQUE",
            message = "O toque físico agora pode disparar um surto de defesa ou medo extremo. Mantenha distância respeitosa.",
            icon = Icons.Default.PanTool,
            color = EmergencyRed,
            emergencyLevel = "CRITICAL"
        ),
        AssistiveCardItem(
            id = "quiet_place",
            title = "PRECISO DE UM LUGAR SILENCIOSO",
            message = "O excesso de barulho, luz forte ou pessoas falando ao mesmo tempo está agravando minha crise. Me leve a uma sala calma.",
            icon = Icons.Default.VolumeMute,
            color = GradivaTealPrimary
        ),
        AssistiveCardItem(
            id = "water",
            title = "PRECISO DE UM COPO DE ÁGUA",
            message = "Beber água fresca e respirar fundo vai me ajudar a reduzir a taquicardia e a ansiedade aguda.",
            icon = Icons.Default.LocalDrink,
            color = GradivaTealPrimary
        ),
        AssistiveCardItem(
            id = "rescue_med",
            title = "PRECISO DA MEDICAÇÃO DE RESGATE",
            message = "Tenho medicação prescrita pelo Dr. Alexandre (Estância Morro Grande) para crises como esta. Olhe a Ficha SOS.",
            icon = Icons.Default.Medication,
            color = AlertAmber
        ),
        AssistiveCardItem(
            id = "call_family",
            title = "LIGUE PARA O MEU CONTATO DE RESGATE",
            message = "Por favor, ligue para minha mãe / curadora legal. O contato telefônico está salvo no aplicativo Estância Morro Grande.",
            icon = Icons.Default.Call,
            color = GradivaNavy
        ),
        AssistiveCardItem(
            id = "listening",
            title = "ESTOU OUVINDO E ENTENDENDO",
            message = "Não estou surdo(a) ou confuso(a), apenas meu corpo não consegue articular respostas verbais neste momento.",
            icon = Icons.Default.Hearing,
            color = GradivaTealPrimary
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Comunicação Assistiva Não-Verbal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Toque no cartão para ampliar a mensagem",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (uiState.currentUser?.role == "PATIENT") {
                                viewModel.navigateTo(GradivaScreen.PatientHome)
                            } else if (uiState.currentUser?.role == "PROFESSIONAL") {
                                viewModel.navigateTo(GradivaScreen.ProfessionalHome)
                            } else {
                                viewModel.navigateTo(GradivaScreen.EmergencyQuickAccess)
                            }
                        },
                        modifier = Modifier.testTag("nonverbal_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GradivaNavy)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Expanded Banner if an item is selected
            AnimatedVisibility(visible = selectedItem != null) {
                selectedItem?.let { item ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = item.color.copy(alpha = 0.15f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedItem = null }
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = item.color,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = item.title,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = item.color,
                                lineHeight = 24.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = item.message,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = GradivaNavy,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Toque para recolher",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Text(
                text = "Cartões de Emergência para Apontar:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = GradivaNavy
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(cards) { card ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                triggerHaptic(context)
                                selectedItem = card
                            }
                            .testTag("assistive_card_${card.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(card.color.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = card.icon,
                                    contentDescription = null,
                                    tint = card.color,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = card.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = card.color
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = card.message,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun triggerHaptic(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.vibrate(50L)
        }
    } catch (_: Exception) {}
}
