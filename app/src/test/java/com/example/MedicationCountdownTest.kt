package com.example

import com.example.data.MedicationEntity
import com.example.ui.getNextDoseMillis
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class MedicationCountdownTest {

    @Test
    fun testGetNextDoseMillisReturnsFutureTime() {
        val currentMillis = System.currentTimeMillis()
        val med = MedicationEntity(
            id = 1L,
            name = "Aspirin",
            dosage = "100 mg",
            instruction = "Take after meal",
            formType = "tablet",
            firstHour = 8,
            firstMinute = 0,
            amPm = "AM",
            repeatDays = "0,1,2,3,4,5,6",
            doseCountPerDay = 3,
            intervalHours = 8.0,
            notifyStatusbar = true,
            notifySound = true,
            notifyVibrate = true,
            currentDoneCount = 1,
            lastResetDate = "2026-10-17"
        )

        val nextMillis = getNextDoseMillis(med, currentMillis)
        assertTrue(nextMillis >= currentMillis)
    }

    @Test
    fun testIntervalCalculation() {
        val doseCount = 2
        val interval = if (doseCount > 0) 24.0 / doseCount else 24.0
        org.junit.Assert.assertEquals(12.0, interval, 0.001)

        val doseCount3 = 3
        val interval3 = if (doseCount3 > 0) 24.0 / doseCount3 else 24.0
        org.junit.Assert.assertEquals(8.0, interval3, 0.001)
    }
}
