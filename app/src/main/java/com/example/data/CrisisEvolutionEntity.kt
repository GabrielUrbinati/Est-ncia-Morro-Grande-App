package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crisis_evolutions")
data class CrisisEvolutionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientUserId: Long,
    val patientRecordId: String,
    val patientName: String,
    val authorName: String,
    val authorCouncil: String, // e.g. "Médico CRM-SP 148290"
    val timestamp: Long = System.currentTimeMillis(),
    val crisisType: String, // e.g. "Surto Psicótico Agudo", "Agitação Psicomotora", "Catatonia / Mutismo"
    val conductAdopted: String, // e.g. "Desescalada Verbal Humanizada", "Medicação SOS Administrada"
    val vitalSigns: String, // e.g. "PA: 130/80 | FC: 92 | SatO2: 98%"
    val notes: String
)
