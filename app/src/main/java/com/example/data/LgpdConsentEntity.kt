package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lgpd_consents")
data class LgpdConsentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val userEmail: String,
    val consentType: String, // "TERMS_OF_USE", "PRIVACY_POLICY", "SENSITIVE_HEALTH_DATA_ART11"
    val termVersion: String = "v2.4-2026.09",
    val acceptedAt: Long = System.currentTimeMillis(),
    val ipOrDeviceMetadata: String = "Android Device Cryptographic Keystore (App Local Secure Storage)",
    val purpose: String = "Acompanhamento Terapêutico, Histórico Clínico e Gestão do Paciente Estância Morro Grande",
    val isRevoked: Boolean = false
)
