package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CrisisEvolutionEntity
import com.example.data.IntercorrenciaEntity
import com.example.data.LectureAttendanceEntity
import com.example.data.LgpdConsentEntity
import com.example.data.MedicalRecordEntity
import com.example.data.PatientWalletTransactionEntity
import com.example.data.TherapeuticLectureEntity
import com.example.data.UserEntity
import com.example.repository.HospitalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class GradivaScreen {
    object Login : GradivaScreen()
    object Register : GradivaScreen()
    object PatientHome : GradivaScreen()
    object PatientLectures : GradivaScreen()
    object PatientIntercorrencias : GradivaScreen()
    object PatientWallet : GradivaScreen()
    object EmergencyQuickAccess : GradivaScreen()
    object PatientNonVerbalCards : GradivaScreen()
    object ProfessionalHome : GradivaScreen()
    data class ProfessionalPatientDetail(val recordId: String) : GradivaScreen()
    object LgpdAuditScreen : GradivaScreen()
}

data class GradivaUiState(
    val currentScreen: GradivaScreen = GradivaScreen.Login,
    val currentUser: UserEntity? = null,
    val patientRecord: MedicalRecordEntity? = null,
    val selectedPatientRecord: MedicalRecordEntity? = null,
    val searchResults: List<MedicalRecordEntity> = emptyList(),
    val searchQuery: String = "",
    val patientEvolutions: List<CrisisEvolutionEntity> = emptyList(),
    val userConsents: List<LgpdConsentEntity> = emptyList(),
    // New Treatment Tracking States
    val allLectures: List<TherapeuticLectureEntity> = emptyList(),
    val patientAttendances: List<LectureAttendanceEntity> = emptyList(),
    val patientIntercorrencias: List<IntercorrenciaEntity> = emptyList(),
    val allIntercorrencias: List<IntercorrenciaEntity> = emptyList(),
    val patientTransactions: List<PatientWalletTransactionEntity> = emptyList(),
    val patientBalance: Double = 0.0,
    val totalDeposits: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showLgpdDialog: Boolean = false,
    val lgpdDialogType: String = "ALL" // "TERMS", "PRIVACY", "HEALTH_DATA", "ALL"
)

class GradivaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HospitalRepository
    private val _uiState = MutableStateFlow(GradivaUiState())
    val uiState: StateFlow<GradivaUiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = HospitalRepository(db)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.ensureSeededData()
            } catch (e: Throwable) {
                android.util.Log.e("GradivaViewModel", "Database seed error", e)
            }
            try {
                // Observe patient catalog
                repository.searchPatientRecords("")
                    .catch { e -> android.util.Log.e("GradivaViewModel", "Error in searchPatientRecords", e) }
                    .collect { records ->
                        _uiState.update { it.copy(searchResults = records) }
                    }
            } catch (e: Throwable) {
                android.util.Log.e("GradivaViewModel", "Error observing patients", e)
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getAllLectures()
                    .catch { e -> android.util.Log.e("GradivaViewModel", "Error in getAllLectures", e) }
                    .collect { lectures ->
                        _uiState.update { it.copy(allLectures = lectures) }
                    }
            } catch (e: Throwable) {
                android.util.Log.e("GradivaViewModel", "Error observing lectures", e)
            }
        }
    }

    fun navigateTo(screen: GradivaScreen) {
        _uiState.update { it.copy(currentScreen = screen, errorMessage = null, successMessage = null) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun showLgpdModal(type: String) {
        _uiState.update { it.copy(showLgpdDialog = true, lgpdDialogType = type) }
    }

    fun dismissLgpdModal() {
        _uiState.update { it.copy(showLgpdDialog = false) }
    }

    fun login(email: String, passwordRaw: String) {
        if (email.isBlank() || passwordRaw.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Preencha o e-mail e a senha.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.login(email, passwordRaw)
            result.fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            currentUser = user,
                            isLoading = false,
                            currentScreen = if (user.role == "PATIENT") GradivaScreen.PatientHome else GradivaScreen.ProfessionalHome
                        )
                    }
                    if (user.role == "PATIENT") {
                        loadPatientData(user.id)
                    } else {
                        loadProfessionalData()
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Erro ao realizar login."
                        )
                    }
                }
            )
        }
    }

    fun fillDemoLogin(role: String) {
        when (role) {
            "PATIENT" -> login("paciente@morrogrande.com.br", "123456")
            "PSYCHOLOGIST" -> login("psicologa@morrogrande.com.br", "123456")
            "DOCTOR" -> login("medico@morrogrande.com.br", "123456")
            else -> login("paciente@morrogrande.com.br", "123456")
        }
    }

    fun registerPatient(
        name: String,
        cpf: String,
        birthDate: String,
        email: String,
        phone: String,
        passwordRaw: String,
        passwordConfirm: String,
        termsAccepted: Boolean,
        privacyAccepted: Boolean,
        healthDataAccepted: Boolean
    ) {
        if (name.isBlank() || cpf.isBlank() || email.isBlank() || phone.isBlank() || passwordRaw.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Todos os campos obrigatórios devem ser preenchidos.") }
            return
        }
        if (passwordRaw != passwordConfirm) {
            _uiState.update { it.copy(errorMessage = "A confirmação de senha não confere.") }
            return
        }
        if (passwordRaw.length < 6) {
            _uiState.update { it.copy(errorMessage = "A senha deve ter pelo menos 6 caracteres.") }
            return
        }
        if (!termsAccepted || !privacyAccepted || !healthDataAccepted) {
            _uiState.update { it.copy(errorMessage = "Para cadastrar um paciente, é obrigatório aceitar os Termos, Política e Autorização de Saúde (LGPD).") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.registerPatient(
                name, cpf, birthDate, email, phone, passwordRaw,
                termsAccepted, privacyAccepted, healthDataAccepted
            )
            result.fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            currentUser = user,
                            isLoading = false,
                            successMessage = "Cadastro na Estância Morro Grande realizado! Prontuário ${user.recordId} ativado.",
                            currentScreen = GradivaScreen.PatientHome
                        )
                    }
                    loadPatientData(user.id)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.localizedMessage ?: "Erro no cadastro.") }
                }
            )
        }
    }

    fun registerProfessional(
        name: String,
        cpf: String,
        email: String,
        phone: String,
        passwordRaw: String,
        passwordConfirm: String,
        profession: String,
        councilType: String,
        councilNumber: String,
        councilState: String,
        termsAccepted: Boolean,
        privacyAccepted: Boolean,
        healthDataAccepted: Boolean
    ) {
        if (name.isBlank() || cpf.isBlank() || email.isBlank() || phone.isBlank() || passwordRaw.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Preencha todos os campos obrigatórios.") }
            return
        }
        if (councilNumber.isBlank() || councilState.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Informe o número e estado da sua inscrição no conselho profissional ($councilType).") }
            return
        }
        if (passwordRaw != passwordConfirm) {
            _uiState.update { it.copy(errorMessage = "As senhas não coincidem.") }
            return
        }
        if (!termsAccepted || !privacyAccepted || !healthDataAccepted) {
            _uiState.update { it.copy(errorMessage = "É obrigatório aceitar os termos de sigilo profissional e LGPD.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.registerProfessional(
                name, cpf, email, phone, passwordRaw, profession,
                councilType, councilNumber, councilState,
                termsAccepted, privacyAccepted, healthDataAccepted
            )
            result.fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            currentUser = user,
                            isLoading = false,
                            successMessage = "Profissional registrado na Estância Morro Grande. Acesso liberado.",
                            currentScreen = GradivaScreen.ProfessionalHome
                        )
                    }
                    loadProfessionalData()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.localizedMessage ?: "Erro no cadastro profissional.") }
                }
            )
        }
    }

    fun loadPatientData(userId: Long) {
        viewModelScope.launch {
            repository.getMedicalRecordFlow(userId).collect { record ->
                _uiState.update { it.copy(patientRecord = record) }
            }
        }
        viewModelScope.launch {
            repository.getEvolutionsForPatient(userId).collect { evolutions ->
                _uiState.update { it.copy(patientEvolutions = evolutions) }
            }
        }
        viewModelScope.launch {
            repository.getConsents(userId).collect { consents ->
                _uiState.update { it.copy(userConsents = consents) }
            }
        }
        // Load Lectures Attendance
        viewModelScope.launch {
            repository.getLectureAttendanceForPatient(userId).collect { attendances ->
                _uiState.update { it.copy(patientAttendances = attendances) }
            }
        }
        // Load Intercorrências
        viewModelScope.launch {
            repository.getIntercorrenciasForPatient(userId).collect { inters ->
                _uiState.update { it.copy(patientIntercorrencias = inters) }
            }
        }
        // Load Patient Wallet & Calculate Balance
        viewModelScope.launch {
            repository.getPatientWalletTransactions(userId).collect { txs ->
                val deposits = txs.filter { it.type == "DEPOSITO" }.sumOf { it.amount }
                val expenses = txs.filter { it.type == "GASTO" }.sumOf { it.amount }
                val balance = deposits - expenses
                _uiState.update {
                    it.copy(
                        patientTransactions = txs,
                        totalDeposits = deposits,
                        totalExpenses = expenses,
                        patientBalance = balance
                    )
                }
            }
        }
    }

    fun loadProfessionalData() {
        viewModelScope.launch {
            repository.getRecentCrisisLogs().collect { logs ->
                _uiState.update { it.copy(patientEvolutions = logs) }
            }
        }
        viewModelScope.launch {
            repository.getAllIntercorrencias().collect { inters ->
                _uiState.update { it.copy(allIntercorrencias = inters) }
            }
        }
        _uiState.value.currentUser?.let { user ->
            viewModelScope.launch {
                repository.getConsents(user.id).collect { consents ->
                    _uiState.update { it.copy(userConsents = consents) }
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        viewModelScope.launch {
            repository.searchPatientRecords(query).collect { results ->
                _uiState.update { it.copy(searchResults = results) }
            }
        }
    }

    fun selectPatientForDetail(recordId: String) {
        viewModelScope.launch {
            val record = repository.getMedicalRecordByRecordId(recordId)
            if (record != null) {
                _uiState.update {
                    it.copy(
                        selectedPatientRecord = record,
                        currentScreen = GradivaScreen.ProfessionalPatientDetail(recordId)
                    )
                }
                loadPatientData(record.userId)
            } else {
                _uiState.update { it.copy(errorMessage = "Prontuário $recordId não localizado.") }
            }
        }
    }

    // --- Record Attendance for Lecture (Psychologist / Team) ---
    fun recordLectureAttendance(
        patientUserId: Long,
        patientName: String,
        lectureId: Long,
        lectureTitle: String,
        dateStr: String,
        status: String,
        notes: String
    ) {
        val author = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            val attendance = LectureAttendanceEntity(
                patientUserId = patientUserId,
                patientName = patientName,
                lectureId = lectureId,
                lectureTitle = lectureTitle,
                dateStr = dateStr,
                status = status,
                notes = notes,
                registeredBy = "${author.profession} ${author.name}"
            )
            repository.recordLectureAttendance(attendance)
            _uiState.update { it.copy(successMessage = "Presença registrada com sucesso: $status.") }
        }
    }

    // --- Record Intercorrência (Psychologist / Doctor / Nursing) ---
    fun recordIntercorrencia(
        patientUserId: Long,
        patientName: String,
        title: String,
        severity: String,
        description: String,
        conductAdopted: String,
        vitalSigns: String
    ) {
        val author = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            val inter = IntercorrenciaEntity(
                patientUserId = patientUserId,
                patientName = patientName,
                title = title,
                severity = severity,
                description = description,
                conductAdopted = conductAdopted,
                vitalSigns = vitalSigns,
                professionalName = author.name,
                professionalRole = author.profession
            )
            repository.recordIntercorrencia(inter)
            _uiState.update { it.copy(successMessage = "Intercorrência registrada com sucesso.") }
        }
    }

    // --- Record Wallet Transaction (Deposit or Expense) ---
    fun recordWalletTransaction(
        patientUserId: Long,
        patientName: String,
        type: String, // "DEPOSITO" ou "GASTO"
        amount: Double,
        description: String,
        category: String
    ) {
        val author = _uiState.value.currentUser ?: return
        if (amount <= 0) {
            _uiState.update { it.copy(errorMessage = "O valor deve ser maior que zero.") }
            return
        }
        viewModelScope.launch {
            val tx = PatientWalletTransactionEntity(
                patientUserId = patientUserId,
                patientName = patientName,
                type = type,
                amount = amount,
                description = description,
                category = category,
                registeredBy = "${author.profession} ${author.name}"
            )
            repository.recordWalletTransaction(tx)
            val msg = if (type == "DEPOSITO") "Depósito de R$ %.2f registrado com sucesso.".format(amount)
                      else "Débito/Gasto de R$ %.2f registrado com sucesso.".format(amount)
            _uiState.update { it.copy(successMessage = msg) }
        }
    }

    fun addCrisisLog(
        crisisType: String,
        conductAdopted: String,
        vitalSigns: String,
        notes: String
    ) {
        val selected = _uiState.value.selectedPatientRecord ?: return
        val author = _uiState.value.currentUser ?: return

        viewModelScope.launch {
            val evolution = CrisisEvolutionEntity(
                patientUserId = selected.userId,
                patientRecordId = selected.recordId,
                patientName = selected.patientName,
                authorName = author.name,
                authorCouncil = "${author.profession} ${author.councilType}-${author.councilState} ${author.councilNumber}",
                crisisType = crisisType,
                conductAdopted = conductAdopted,
                vitalSigns = vitalSigns,
                notes = notes
            )
            repository.addCrisisEvolution(evolution)
            _uiState.update { it.copy(successMessage = "Evolução clínica registrada no prontuário.") }
        }
    }

    fun logout() {
        _uiState.update {
            it.copy(
                currentUser = null,
                patientRecord = null,
                selectedPatientRecord = null,
                currentScreen = GradivaScreen.Login,
                errorMessage = null,
                successMessage = "Sessão encerrada com segurança."
            )
        }
    }
}

