package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medical_records")
data class MedicalRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val patientName: String,
    val recordId: String, // e.g. "GDV-2026-9042"
    val bloodType: String = "O+",
    val severeAllergies: String, // High-risk allergy warning!
    val psychiatricDiagnoses: String,
    val continuousMedication: String,
    val rescueProtocolMedication: String,
    val knownTriggers: String,
    val calmingTechniques: String,
    val emergencyPhoneSamu: String = "192",
    val emergencyPhoneHospital: String = "(11) 4617-8000",
    val familyContactName: String = "",
    val familyContactRelationship: String = "",
    val familyContactPhone: String = "",
    val attendingDoctorName: String = "",
    val attendingDoctorCrm: String = "",
    val attendingDoctorPhone: String = "",
    val nonVerbalAssistiveNote: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
