package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patient_wallet_transactions")
data class PatientWalletTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientUserId: Long,
    val patientName: String,
    val type: String, // "DEPOSITO" ou "GASTO"
    val amount: Double,
    val description: String,
    val category: String, // "DEPOSITO_FAMILIA", "CANTINA", "HIGIENE_PESSOAL", "BARBEARIA", "FARMACIA", "OUTROS"
    val registeredBy: String = "Setor Financeiro / Recepção Morro Grande",
    val timestamp: Long = System.currentTimeMillis()
)
