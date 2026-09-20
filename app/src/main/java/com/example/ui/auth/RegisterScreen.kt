package com.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: GradivaViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedRoleIndex by remember { mutableIntStateOf(0) } // 0: Paciente, 1: Profissional
    val snackbarHostState = remember { SnackbarHostState() }

    // Common fields
    var fullName by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Patient specific fields
    var birthDate by remember { mutableStateOf("") }

    // Professional specific fields
    val professions = listOf("Médico", "Enfermeiro", "Psicólogo", "Fisioterapeuta", "Terapeuta Ocupacional", "Assistente Social")
    var selectedProfession by remember { mutableStateOf("Médico") }
    var professionExpanded by remember { mutableStateOf(false) }
    var councilNumber by remember { mutableStateOf("") }
    var councilState by remember { mutableStateOf("SP") }

    // Calculated council type based on selected profession
    val councilType = when (selectedProfession) {
        "Médico" -> "CRM"
        "Enfermeiro" -> "COREN"
        "Psicólogo" -> "CRP"
        "Fisioterapeuta", "Terapeuta Ocupacional" -> "CREFITO"
        "Assistente Social" -> "CRESS"
        else -> "Registro"
    }

    // LGPD Consents
    var termsAccepted by remember { mutableStateOf(false) }
    var privacyAccepted by remember { mutableStateOf(false) }
    var healthConsentAccepted by remember { mutableStateOf(false) }

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
                            text = "Cadastro Estância Morro Grande",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (selectedRoleIndex == 0) "Novo Cadastro de Paciente" else "Novo Profissional de Saúde",
                            style = MaterialTheme.typography.bodySmall,
                            color = MorroGreenLight
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(GradivaScreen.Login) },
                        modifier = Modifier.testTag("register_back_btn")
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
            // Screen Toggle Tabs: Entrar | Cadastrar-se
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MorroSurface)
                    .padding(4.dp)
            ) {
                Button(
                    onClick = { viewModel.navigateTo(GradivaScreen.Login) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MorroGreenDark
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("reg_tab_login")
                ) {
                    Text("Entrar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Button(
                    onClick = { /* Already on register */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MorroGreenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("reg_tab_register")
                ) {
                    Text("Cadastrar-se", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            // Role Selector Segmented Button
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("role_segmented_button")
            ) {
                SegmentedButton(
                    selected = selectedRoleIndex == 0,
                    onClick = { selectedRoleIndex = 0 },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                ) {
                    Text("Paciente", fontWeight = FontWeight.Bold)
                }
                SegmentedButton(
                    selected = selectedRoleIndex == 1,
                    onClick = { selectedRoleIndex = 1 },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Medication,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                ) {
                    Text("Profissional de Saúde", fontWeight = FontWeight.Bold)
                }
            }

            // Form Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (selectedRoleIndex == 0) "Identificação do Paciente" else "Dados Profissionais & Conselho",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MorroGreenDark
                    )

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nome Completo *") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_fullname_input")
                    )

                    OutlinedTextField(
                        value = cpf,
                        onValueChange = { cpf = it },
                        label = { Text("CPF *") },
                        placeholder = { Text("000.000.000-00") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Badge, contentDescription = null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("reg_cpf_input")
                    )

                    if (selectedRoleIndex == 0) {
                        // Patient Specific: Date of Birth
                        OutlinedTextField(
                            value = birthDate,
                            onValueChange = { birthDate = it },
                            label = { Text("Data de Nascimento *") },
                            placeholder = { Text("DD/MM/AAAA") },
                            leadingIcon = { Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("reg_birthdate_input")
                        )
                    } else {
                        // Professional Specific: Profession Dropdown & Conditional Council
                        ExposedDropdownMenuBox(
                            expanded = professionExpanded,
                            onExpandedChange = { professionExpanded = !professionExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedProfession,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Profissão *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = professionExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .testTag("reg_profession_dropdown")
                            )
                            ExposedDropdownMenu(
                                expanded = professionExpanded,
                                onDismissRequest = { professionExpanded = false }
                            ) {
                                professions.forEach { prof ->
                                    DropdownMenuItem(
                                        text = { Text(prof) },
                                        onClick = {
                                            selectedProfession = prof
                                            professionExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = councilNumber,
                                onValueChange = { councilNumber = it },
                                label = { Text("Número do $councilType *") },
                                placeholder = { Text("Ex: 148290") },
                                singleLine = true,
                                modifier = Modifier.weight(1.8f).testTag("reg_council_number_input")
                            )
                            OutlinedTextField(
                                value = councilState,
                                onValueChange = { councilState = it.take(2).uppercase() },
                                label = { Text("UF *") },
                                placeholder = { Text("SP") },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("reg_council_state_input")
                            )
                        }
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("E-mail *") },
                        placeholder = { Text("exemplo@email.com") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth().testTag("reg_email_input")
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Telefone / Celular *") },
                        placeholder = { Text("(11) 99999-9999") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().testTag("reg_phone_input")
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Senha * (mínimo 6 caracteres)") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth().testTag("reg_password_input")
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmação da Senha *") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth().testTag("reg_confirm_password_input")
                    )
                }
            }

            // LGPD Consents Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = MorroGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Consentimentos Legais & LGPD",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MorroGreenDark
                            )
                        }
                        Text(
                            text = "Ver Termos",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MorroGreenPrimary,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier
                                .clickable { viewModel.showLgpdModal("ALL") }
                                .testTag("view_lgpd_modal_link")
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it },
                            colors = CheckboxDefaults.colors(checkedColor = MorroGreenPrimary),
                            modifier = Modifier.testTag("cb_terms")
                        )
                        Text(
                            text = "Li e aceito os Termos de Uso da Estância Morro Grande.",
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = privacyAccepted,
                            onCheckedChange = { privacyAccepted = it },
                            colors = CheckboxDefaults.colors(checkedColor = MorroGreenPrimary),
                            modifier = Modifier.testTag("cb_privacy")
                        )
                        Text(
                            text = "Li e aceito a Política de Privacidade e Proteção de Dados.",
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = healthConsentAccepted,
                            onCheckedChange = { healthConsentAccepted = it },
                            colors = CheckboxDefaults.colors(checkedColor = MorroGreenPrimary),
                            modifier = Modifier.testTag("cb_health_consent")
                        )
                        Text(
                            text = if (selectedRoleIndex == 0)
                                "Autorizo expressamente o tratamento dos meus dados pessoais sensíveis de saúde mental para fins de urgência, emergência e acompanhamento terapêutico (Art. 11 LGPD)."
                            else
                                "Declaro responsabilidade legal de sigilo ético-assistencial médico no acesso aos prontuários e protocolos da Estância Morro Grande.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    if (selectedRoleIndex == 0) {
                        viewModel.registerPatient(
                            fullName, cpf, birthDate, email, phone,
                            password, confirmPassword,
                            termsAccepted, privacyAccepted, healthConsentAccepted
                        )
                    } else {
                        viewModel.registerProfessional(
                            fullName, cpf, email, phone,
                            password, confirmPassword,
                            selectedProfession, councilType, councilNumber, councilState,
                            termsAccepted, privacyAccepted, healthConsentAccepted
                        )
                    }
                },
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = MorroGreenPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("register_submit_btn")
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        text = if (selectedRoleIndex == 0) "Concluir Cadastro de Paciente" else "Concluir Cadastro Profissional",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
