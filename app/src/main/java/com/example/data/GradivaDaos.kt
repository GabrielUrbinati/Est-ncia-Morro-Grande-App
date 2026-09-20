package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE cpf = :cpf LIMIT 1")
    suspend fun getUserByCpf(cpf: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = 'PATIENT' ORDER BY name ASC")
    fun getAllPatients(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}

@Dao
interface LgpdConsentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsent(consent: LgpdConsentEntity): Long

    @Query("SELECT * FROM lgpd_consents WHERE userId = :userId ORDER BY acceptedAt DESC")
    fun getConsentsByUser(userId: Long): Flow<List<LgpdConsentEntity>>

    @Query("SELECT * FROM lgpd_consents WHERE userId = :userId AND consentType = :type LIMIT 1")
    suspend fun getConsentByType(userId: Long, type: String): LgpdConsentEntity?
}

@Dao
interface MedicalRecordDao {
    @Query("SELECT * FROM medical_records WHERE userId = :userId LIMIT 1")
    fun getRecordByUserIdFlow(userId: Long): Flow<MedicalRecordEntity?>

    @Query("SELECT * FROM medical_records WHERE userId = :userId LIMIT 1")
    suspend fun getRecordByUserId(userId: Long): MedicalRecordEntity?

    @Query("SELECT * FROM medical_records WHERE recordId = :recordId LIMIT 1")
    suspend fun getRecordByRecordId(recordId: String): MedicalRecordEntity?

    @Query("SELECT * FROM medical_records WHERE patientName LIKE '%' || :query || '%' OR recordId LIKE '%' || :query || '%'")
    fun searchRecords(query: String): Flow<List<MedicalRecordEntity>>

    @Query("SELECT * FROM medical_records ORDER BY patientName ASC")
    fun getAllRecords(): Flow<List<MedicalRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRecord(record: MedicalRecordEntity): Long

    @Update
    suspend fun updateRecord(record: MedicalRecordEntity)
}

@Dao
interface CrisisEvolutionDao {
    @Query("SELECT * FROM crisis_evolutions WHERE patientUserId = :patientUserId ORDER BY timestamp DESC")
    fun getEvolutionsForPatient(patientUserId: Long): Flow<List<CrisisEvolutionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvolution(evolution: CrisisEvolutionEntity): Long

    @Query("SELECT * FROM crisis_evolutions ORDER BY timestamp DESC LIMIT 30")
    fun getRecentCrisisLogs(): Flow<List<CrisisEvolutionEntity>>
}

@Dao
interface TherapeuticLectureDao {
    @Query("SELECT * FROM therapeutic_lectures ORDER BY id ASC")
    fun getAllLectures(): Flow<List<TherapeuticLectureEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLecture(lecture: TherapeuticLectureEntity): Long

    @Query("SELECT COUNT(*) FROM therapeutic_lectures")
    suspend fun getLectureCount(): Int
}

@Dao
interface LectureAttendanceDao {
    @Query("SELECT * FROM lecture_attendance WHERE patientUserId = :patientUserId ORDER BY timestamp DESC")
    fun getAttendanceForPatient(patientUserId: Long): Flow<List<LectureAttendanceEntity>>

    @Query("SELECT * FROM lecture_attendance ORDER BY timestamp DESC LIMIT 50")
    fun getAllRecentAttendance(): Flow<List<LectureAttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: LectureAttendanceEntity): Long
}

@Dao
interface IntercorrenciaDao {
    @Query("SELECT * FROM intercorrencias WHERE patientUserId = :patientUserId ORDER BY timestamp DESC")
    fun getIntercorrenciasForPatient(patientUserId: Long): Flow<List<IntercorrenciaEntity>>

    @Query("SELECT * FROM intercorrencias ORDER BY timestamp DESC LIMIT 50")
    fun getAllIntercorrencias(): Flow<List<IntercorrenciaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntercorrencia(intercorrencia: IntercorrenciaEntity): Long
}

@Dao
interface PatientWalletDao {
    @Query("SELECT * FROM patient_wallet_transactions WHERE patientUserId = :patientUserId ORDER BY timestamp DESC")
    fun getTransactionsForPatient(patientUserId: Long): Flow<List<PatientWalletTransactionEntity>>

    @Query("SELECT * FROM patient_wallet_transactions ORDER BY timestamp DESC LIMIT 50")
    fun getAllTransactions(): Flow<List<PatientWalletTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: PatientWalletTransactionEntity): Long
}

