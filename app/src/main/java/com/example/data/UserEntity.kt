package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val cpf: String,
    val birthDate: String = "",
    val email: String,
    val phone: String,
    val passwordHash: String,
    val role: String, // "PATIENT" or "PROFESSIONAL"
    val profession: String = "", // "Médico", "Enfermeiro", "Psicólogo", "Fisioterapeuta", etc.
    val councilType: String = "", // "CRM", "COREN", "CRP", "CREFITO", etc.
    val councilNumber: String = "",
    val councilState: String = "",
    val recordId: String = "", // e.g. "GDV-2026-9042"
    val createdAt: Long = System.currentTimeMillis()
)
