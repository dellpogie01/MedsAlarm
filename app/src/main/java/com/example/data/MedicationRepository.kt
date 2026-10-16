package com.example.data

import kotlinx.coroutines.flow.Flow

class MedicationRepository(private val dao: MedicationDao) {
    val allMedications: Flow<List<MedicationEntity>> = dao.getAllMedications()

    suspend fun insert(medication: MedicationEntity): Long {
        return dao.insertMedication(medication)
    }

    suspend fun update(medication: MedicationEntity) {
        dao.updateMedication(medication)
    }

    suspend fun delete(medication: MedicationEntity) {
        dao.deleteMedication(medication)
    }

    suspend fun getById(id: Long): MedicationEntity? {
        return dao.getMedicationById(id)
    }
}
