package com.example.repository

import com.example.data.AppDatabase
import com.example.data.CrisisEvolutionEntity
import com.example.data.IntercorrenciaEntity
import com.example.data.LectureAttendanceEntity
import com.example.data.LgpdConsentEntity
import com.example.data.MedicalRecordEntity
import com.example.data.PatientWalletTransactionEntity
import com.example.data.TherapeuticLectureEntity
import com.example.data.UserEntity
import kotlinx.coroutines.flow.Flow

class HospitalRepository(private val database: AppDatabase) {

    private val userDao = database.userDao()
    private val consentDao = database.lgpdConsentDao()
    private val medicalRecordDao = database.medicalRecordDao()
    private val crisisEvolutionDao = database.crisisEvolutionDao()
    private val lectureDao = database.lectureDao()
    private val attendanceDao = database.attendanceDao()
    private val intercorrenciaDao = database.intercorrenciaDao()
    private val walletDao = database.walletDao()

    suspend fun login(email: String, passwordRaw: String): Result<UserEntity> {
        val cleanEmail = email.trim().lowercase()
        val user = userDao.getUserByEmail(cleanEmail)
            ?: return Result.failure(Exception("E-mail não encontrado na Estância Morro Grande."))

        val inputHash = AppDatabase.hashPassword(passwordRaw)
        if (user.passwordHash != inputHash) {
            return Result.failure(Exception("Senha incorreta. Verifique suas credenciais."))
        }

        return Result.success(user)
    }

    suspend fun registerPatient(
        name: String,
        cpf: String,
        birthDate: String,
        email: String,
        phone: String,
        passwordRaw: String,
        termsAccepted: Boolean,
        privacyAccepted: Boolean,
        healthDataAccepted: Boolean
    ): Result<UserEntity> {
        if (!termsAccepted || !privacyAccepted || !healthDataAccepted) {
            return Result.failure(Exception("É obrigatório aceitar os Termos, Política de Privacidade e Consentimento de Saúde (LGPD)."))
        }

        val cleanEmail = email.trim().lowercase()
        val existing = userDao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return Result.failure(Exception("Este e-mail já está cadastrado no sistema."))
        }

        val generatedRecordId = "EMG-2026-${(1000..9999).random()}"
        val user = UserEntity(
            name = name.trim(),
            cpf = cpf.trim(),
            birthDate = birthDate.trim(),
            email = cleanEmail,
            phone = phone.trim(),
            passwordHash = AppDatabase.hashPassword(passwordRaw),
            role = "PATIENT",
            recordId = generatedRecordId
        )

        val newUserId = userDao.insertUser(user)
        val createdUser = user.copy(id = newUserId)

        // Record audited LGPD Consents
        consentDao.insertConsent(
            LgpdConsentEntity(
                userId = newUserId,
                userEmail = cleanEmail,
                consentType = "TERMS_OF_USE",
                purpose = "Termos Gerais de Acompanhamento Estância Morro Grande"
            )
        )
        consentDao.insertConsent(
            LgpdConsentEntity(
                userId = newUserId,
                userEmail = cleanEmail,
                consentType = "PRIVACY_POLICY",
                purpose = "Política de Privacidade e Proteção de Dados Pessoais"
            )
        )
        consentDao.insertConsent(
            LgpdConsentEntity(
                userId = newUserId,
                userEmail = cleanEmail,
                consentType = "SENSITIVE_HEALTH_DATA_ART11",
                purpose = "Autorização Específica para Tratamento de Dados de Saúde Mental e Terapêutica (Art. 11 LGPD)"
            )
        )

        // Create blank/initial medical record for patient
        val initialRecord = MedicalRecordEntity(
            userId = newUserId,
            patientName = createdUser.name,
            recordId = generatedRecordId,
            bloodType = "Não informado",
            severeAllergies = "Nenhuma alergia grave relatada no momento",
            psychiatricDiagnoses = "Em avaliação inicial na Estância Morro Grande",
            continuousMedication = "Não informado",
            rescueProtocolMedication = "Sob prescrição médica da equipe",
            knownTriggers = "Ruídos intensos, confronto verbal",
            calmingTechniques = "Acolhimento calmo, caminhada no jardim, escuta psicológica",
            familyContactName = "Contato Familiar Principal",
            familyContactPhone = phone,
            nonVerbalAssistiveNote = "Paciente em acompanhamento terapêutico"
        )
        medicalRecordDao.insertOrUpdateRecord(initialRecord)

        return Result.success(createdUser)
    }

    suspend fun registerProfessional(
        name: String,
        cpf: String,
        email: String,
        phone: String,
        passwordRaw: String,
        profession: String,
        councilType: String,
        councilNumber: String,
        councilState: String,
        termsAccepted: Boolean,
        privacyAccepted: Boolean,
        healthDataAccepted: Boolean
    ): Result<UserEntity> {
        if (!termsAccepted || !privacyAccepted || !healthDataAccepted) {
            return Result.failure(Exception("É obrigatório o aceite dos Termos de Uso e Sigilo Profissional de Saúde (LGPD)."))
        }

        val cleanEmail = email.trim().lowercase()
        val existing = userDao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return Result.failure(Exception("Este e-mail já está cadastrado no sistema."))
        }

        val user = UserEntity(
            name = name.trim(),
            cpf = cpf.trim(),
            email = cleanEmail,
            phone = phone.trim(),
            passwordHash = AppDatabase.hashPassword(passwordRaw),
            role = "PROFESSIONAL",
            profession = profession,
            councilType = councilType,
            councilNumber = councilNumber.trim(),
            councilState = councilState.trim().uppercase()
        )

        val newUserId = userDao.insertUser(user)

        // Audit Professional LGPD & Professional Confidentiality
        consentDao.insertConsent(
            LgpdConsentEntity(
                userId = newUserId,
                userEmail = cleanEmail,
                consentType = "PROFESSIONAL_CONFIDENTIALITY_TERMS",
                purpose = "Termo de Sigilo Médico-Assistencial e Psicológico na Estância Morro Grande (Art. 11 LGPD e Códigos de Ética Profissional)"
            )
        )

        return Result.success(user.copy(id = newUserId))
    }

    fun getMedicalRecordFlow(userId: Long): Flow<MedicalRecordEntity?> {
        return medicalRecordDao.getRecordByUserIdFlow(userId)
    }

    suspend fun getMedicalRecordByRecordId(recordId: String): MedicalRecordEntity? {
        return medicalRecordDao.getRecordByRecordId(recordId)
    }

    fun searchPatientRecords(query: String): Flow<List<MedicalRecordEntity>> {
        return if (query.isBlank()) {
            medicalRecordDao.getAllRecords()
        } else {
            medicalRecordDao.searchRecords(query.trim())
        }
    }

    suspend fun saveMedicalRecord(record: MedicalRecordEntity) {
        medicalRecordDao.insertOrUpdateRecord(record)
    }

    suspend fun addCrisisEvolution(evolution: CrisisEvolutionEntity): Long {
        return crisisEvolutionDao.insertEvolution(evolution)
    }

    fun getEvolutionsForPatient(patientUserId: Long): Flow<List<CrisisEvolutionEntity>> {
        return crisisEvolutionDao.getEvolutionsForPatient(patientUserId)
    }

    fun getRecentCrisisLogs(): Flow<List<CrisisEvolutionEntity>> {
        return crisisEvolutionDao.getRecentCrisisLogs()
    }

    fun getConsents(userId: Long): Flow<List<LgpdConsentEntity>> {
        return consentDao.getConsentsByUser(userId)
    }

    // --- Lectures and Workshops ---
    fun getAllLectures(): Flow<List<TherapeuticLectureEntity>> {
        return lectureDao.getAllLectures()
    }

    suspend fun addLecture(lecture: TherapeuticLectureEntity): Long {
        return lectureDao.insertLecture(lecture)
    }

    fun getLectureAttendanceForPatient(patientUserId: Long): Flow<List<LectureAttendanceEntity>> {
        return attendanceDao.getAttendanceForPatient(patientUserId)
    }

    fun getAllRecentAttendance(): Flow<List<LectureAttendanceEntity>> {
        return attendanceDao.getAllRecentAttendance()
    }

    suspend fun recordLectureAttendance(attendance: LectureAttendanceEntity): Long {
        return attendanceDao.insertAttendance(attendance)
    }

    // --- Intercorrências ---
    fun getIntercorrenciasForPatient(patientUserId: Long): Flow<List<IntercorrenciaEntity>> {
        return intercorrenciaDao.getIntercorrenciasForPatient(patientUserId)
    }

    fun getAllIntercorrencias(): Flow<List<IntercorrenciaEntity>> {
        return intercorrenciaDao.getAllIntercorrencias()
    }

    suspend fun recordIntercorrencia(intercorrencia: IntercorrenciaEntity): Long {
        return intercorrenciaDao.insertIntercorrencia(intercorrencia)
    }

    // --- Patient Wallet & Cantina ---
    fun getPatientWalletTransactions(patientUserId: Long): Flow<List<PatientWalletTransactionEntity>> {
        return walletDao.getTransactionsForPatient(patientUserId)
    }

    fun getAllWalletTransactions(): Flow<List<PatientWalletTransactionEntity>> {
        return walletDao.getAllTransactions()
    }

    suspend fun recordWalletTransaction(transaction: PatientWalletTransactionEntity): Long {
        return walletDao.insertTransaction(transaction)
    }

    suspend fun ensureSeededData() {
        AppDatabase.populateInitialHospitalData(database)
    }
}

