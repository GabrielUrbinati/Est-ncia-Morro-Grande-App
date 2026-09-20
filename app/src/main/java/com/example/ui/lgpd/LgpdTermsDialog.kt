package com.example.ui.lgpd

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MorroGreenDark
import com.example.ui.theme.MorroGreenPrimary

@Composable
fun LgpdTermsDialog(
    topic: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Segurança LGPD",
                        tint = MorroGreenPrimary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Termos & LGPD Estância Morro Grande",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MorroGreenDark
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_lgpd_dialog")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Lei Geral de Proteção de Dados (Lei nº 13.709/2018)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MorroGreenDark
                        )
                        Text(
                            text = "Versão dos Termos: v2.4-2026.09 | Estância Morro Grande",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Section 1: Health Sensitive Data
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = null,
                        tint = MorroGreenPrimary,
                        modifier = Modifier.padding(end = 8.dp, top = 2.dp)
                    )
                    Column {
                        Text(
                            text = "1. Tratamento de Dados Sensíveis de Saúde (Art. 11)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MorroGreenDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "O aplicativo da Estância Morro Grande coleta dados de saúde estritamente necessários para acompanhamento terapêutico, salvaguarda da integridade e socorro em crises: diagnósticos, adesão ao plano de tratamento, registros de intercorrências e controle financeiro de cantina. O tratamento baseia-se no Art. 11, II, 'e' (tutela da saúde) e consentimento expresso do titular ou responsável legal.",
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }

                // Section 2: Purpose & Access
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MorroGreenDark,
                        modifier = Modifier.padding(end = 8.dp, top = 2.dp)
                    )
                    Column {
                        Text(
                            text = "2. Finalidade e Controle de Acesso",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MorroGreenDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Os dados destinam-se exclusivamente ao acompanhamento da evolução terapêutica, notas de adesão ao tratamento, registro de intercorrências clínicas, e gestão transparente de saldo e despesas familiares na Estância Morro Grande. O acesso é restrito ao próprio paciente, familiares autorizados e equipe multidisciplinar de saúde devidamente cadastrada.",
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }

                // Section 3: Retention & Rights
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = MorroGreenPrimary,
                        modifier = Modifier.padding(end = 8.dp, top = 2.dp)
                    )
                    Column {
                        Text(
                            text = "3. Direitos do Titular & Retenção Legal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MorroGreenDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "O titular ou responsável legal possui o direito de solicitar confirmação, correção e prestação de contas dos lançamentos e prontuários nos termos da LGPD. Contato do Encarregado de Dados (DPO): dpo@estanciamorrogrande.com.br.",
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MorroGreenPrimary),
                modifier = Modifier.testTag("confirm_read_lgpd_btn")
            ) {
                Text("Entendi e Estou Ciente")
            }
        }
    )
}
