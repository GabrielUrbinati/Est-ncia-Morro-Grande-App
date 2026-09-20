package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "therapeutic_lectures")
data class TherapeuticLectureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val speaker: String,
    val scheduleTime: String,
    val location: String = "Auditório Terapêutico Morro Grande",
    val category: String,
    val description: String
)

@Entity(tableName = "lecture_attendance")
data class LectureAttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientUserId: Long,
    val patientName: String,
    val lectureId: Long,
    val lectureTitle: String,
    val dateStr: String,
    val status: String, // "PRESENTE", "AUSENTE", "JUSTIFICADO"
    val notes: String = "",
    val registeredBy: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
