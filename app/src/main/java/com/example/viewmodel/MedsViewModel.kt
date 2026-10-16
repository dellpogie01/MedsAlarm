package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarm.AlarmScheduler
import com.example.data.MedicationDatabase
import com.example.data.MedicationEntity
import com.example.data.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MedsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MedicationRepository

    val medications: StateFlow<List<MedicationEntity>>

    private val _currentTime = MutableStateFlow(System.currentTimeMillis())
    val currentTime: StateFlow<Long> = _currentTime.asStateFlow()

    init {
        val dao = MedicationDatabase.getDatabase(application).medicationDao()
        repository = MedicationRepository(dao)
        medications = repository.allMedications.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Ticker for current time / countdowns
        viewModelScope.launch {
            while (true) {
                _currentTime.value = System.currentTimeMillis()
                kotlinx.coroutines.delay(1000L)
            }
        }
    }

    fun addMedication(
        name: String,
        dosage: String,
        instruction: String,
        formType: String,
        firstHour: Int,
        firstMinute: Int,
        amPm: String,
        repeatDays: String,
        doseCountPerDay: Int,
        intervalHours: Double,
        notifyStatusbar: Boolean,
        notifySound: Boolean,
        notifyVibrate: Boolean
    ) {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = dateFormat.format(Date())

            val entity = MedicationEntity(
                name = name,
                dosage = dosage,
                instruction = instruction,
                formType = formType,
                firstHour = firstHour,
                firstMinute = firstMinute,
                amPm = amPm,
                repeatDays = repeatDays,
                doseCountPerDay = doseCountPerDay,
                intervalHours = intervalHours,
                notifyStatusbar = notifyStatusbar,
                notifySound = notifySound,
                notifyVibrate = notifyVibrate,
                currentDoneCount = 0,
                lastResetDate = todayStr
            )
            val newId = repository.insert(entity)

            // Schedule alarms for today
            scheduleAlarmsForMedication(
                context = getApplication(),
                id = newId,
                name = name,
                dosage = dosage,
                firstHour = firstHour,
                firstMinute = firstMinute,
                amPm = amPm,
                doseCountPerDay = doseCountPerDay,
                intervalHours = intervalHours
            )
        }
    }

    fun markDoseTaken(med: MedicationEntity) {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = dateFormat.format(Date())

            val newCount = if (med.lastResetDate != todayStr) {
                1
            } else {
                (med.currentDoneCount + 1).coerceAtMost(med.doseCountPerDay)
            }

            val updated = med.copy(
                currentDoneCount = newCount,
                lastResetDate = todayStr
            )
            repository.update(updated)
        }
    }

    fun deleteMedication(med: MedicationEntity) {
        viewModelScope.launch {
            // Cancel alarms
            for (i in 0 until med.doseCountPerDay) {
                AlarmScheduler.cancelAlarm(getApplication(), (med.id * 100 + i).toInt())
            }
            repository.delete(med)
        }
    }

    private fun scheduleAlarmsForMedication(
        context: android.content.Context,
        id: Long,
        name: String,
        dosage: String,
        firstHour: Int,
        firstMinute: Int,
        amPm: String,
        doseCountPerDay: Int,
        intervalHours: Double
    ) {
        var calendar = Calendar.getInstance().apply {
            var h = firstHour
            if (amPm == "PM" && h < 12) h += 12
            if (amPm == "AM" && h == 12) h = 0
            set(Calendar.HOUR_OF_DAY, h)
            set(Calendar.MINUTE, firstMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If time is in the past for today, push to tomorrow or next interval
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val intervalMillis = (intervalHours * 3600 * 1000).toLong()

        for (i in 0 until doseCountPerDay) {
            val triggerTime = calendar.timeInMillis + (i * intervalMillis)
            AlarmScheduler.scheduleAlarm(
                context = context,
                id = id,
                medName = name,
                dosage = dosage,
                triggerMillis = triggerTime,
                requestCode = (id * 100 + i).toInt()
            )
        }
    }
}
