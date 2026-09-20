package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "intercorrencias")
data class IntercorrenciaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientUserId: Long,
    val patientName: String,
    val title: String,
    val severity: String, // "LEVE", "MODERADA", "GRAVE"
    val description: String,
    val conductAdopted: String,
    val vitalSigns: String = "",
    val professionalName: String,
    val professionalRole: String, // "Psicólogo(a)", "Médico(a) Psiquiatra", "Enfermagem"
    val timestamp: Long = System.currentTimeMillis(),
    val resolved: Boolean = true
)
