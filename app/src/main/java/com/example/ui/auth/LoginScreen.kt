package com.example.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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
import com.example.ui.viewmodel.GradivaScreen
import com.example.ui.viewmodel.GradivaViewModel

@Composable
fun LoginScreen(
    viewModel: GradivaViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

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
        containerColor = MorroBackground,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Image Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_morro_grande_hero_1789924960199),
                    contentDescription = "Estância Morro Grande Hospital",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MorroGreenDark.copy(alpha = 0.70f))
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MorroGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "ESTÂNCIA MORRO GRANDE",
                            fontWeight = FontWeight.Black,
                            fontSize = 19.sp,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Hospital & Centro Terapêutico de Saúde Mental",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MorroGreenLight,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Login Form Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MorroSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MorroBorder))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Prominent Tabs: Entrar | Cadastrar-se
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MorroBackground)
                                .padding(4.dp)
                        ) {
                            Button(
                                onClick = { /* Already on login */ },
                                colors = ButtonDefaults.buttonColors(containerColor = MorroGreenPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("tab_login")
                            ) {
                                Text("Entrar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Button(
                                onClick = { viewModel.navigateTo(GradivaScreen.Register) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = MorroGreenDark
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("tab_register")
                            ) {
                                Text("Cadastrar-se", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }

                        Text(
                            text = "Portal de Acompanhamento Terapêutico",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MorroGreenDark
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("E-mail Cadastrado") },
                            placeholder = { Text("exemplo@morrogrande.com.br") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = MorroGreenPrimary)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_input")
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Senha") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = MorroGreenPrimary)
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Ocultar" else "Exibir"
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input")
                        )

                        Button(
                            onClick = { viewModel.login(email, password) },
                            enabled = !uiState.isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = MorroGreenPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_submit_btn")
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Acessar Portal", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }

                        // Direct Register Link
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.navigateTo(GradivaScreen.Register) }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Não possui cadastro? ",
                                fontSize = 13.sp,
                                color = MorroTextSecondary
                            )
                            Text(
                                text = "Cadastre-se aqui",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MorroGreenPrimary
                            )
                        }

                        // Demo shortcuts for quick testing
                        Text(
                            text = "Acessos Demonstrativos Rápidos:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MorroTextSecondary
                        )

                        // 3 Demo Buttons: Paciente, Psicóloga, Médico
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    email = "paciente@morrogrande.com.br"
                                    password = "123456"
                                    viewModel.fillDemoLogin("PATIENT")
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("demo_patient_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = MorroGreenPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Paciente: Beatriz Helena (Saldo, Palestras & Intercorrências)", fontSize = 12.sp, color = MorroGreenDark)
                            }

                            OutlinedButton(
                                onClick = {
                                    email = "psicologa@morrogrande.com.br"
                                    password = "123456"
                                    viewModel.fillDemoLogin("PSYCHOLOGIST")
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("demo_psychologist_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp), tint = MorroGold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Psicóloga: Dra. Camila Rodrigues (CRP 06/142980)", fontSize = 12.sp, color = MorroGreenDark)
                            }

                            OutlinedButton(
                                onClick = {
                                    email = "medico@morrogrande.com.br"
                                    password = "123456"
                                    viewModel.fillDemoLogin("DOCTOR")
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("demo_doctor_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Medication, contentDescription = null, modifier = Modifier.size(16.dp), tint = MorroGreenPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Médico Psiquiatra: Dr. Alexandre Castro (CRM-SP)", fontSize = 12.sp, color = MorroGreenDark)
                            }
                        }
                    }
                }

                // Register Link Button
                Button(
                    onClick = { viewModel.navigateTo(GradivaScreen.Register) },
                    colors = ButtonDefaults.buttonColors(containerColor = MorroGreenDark),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("go_to_register_btn")
                ) {
                    Text("Cadastrar Novo Paciente ou Profissional", fontSize = 14.sp)
                }

                // LGPD Transparency Footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "LGPD",
                        tint = MorroGreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LGPD Lei 13.709/2018: Proteção de Dados de Saúde Mental",
                        fontSize = 11.sp,
                        color = MorroTextMuted
                    )
                }
            }
        }
    }
}

