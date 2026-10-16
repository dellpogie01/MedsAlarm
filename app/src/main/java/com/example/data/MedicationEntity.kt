package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val dosage: String,
    val instruction: String,
    val formType: String, // "tablet", "capsule", "injection"
    val firstHour: Int,
    val firstMinute: Int,
    val amPm: String, // "AM", "PM"
    val repeatDays: String, // comma separated e.g. "S,M,T,W,T,F,S"
    val doseCountPerDay: Int,
    val intervalHours: Double,
    val notifyStatusbar: Boolean,
    val notifySound: Boolean,
    val notifyVibrate: Boolean,
    val currentDoneCount: Int = 0,
    val lastResetDate: String = ""
)
